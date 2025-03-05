package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.CSB;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefRenderElement;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefSimpleFont;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.TextureRenderer;
import net.neoforged.fml.earlydisplay.ColourScheme;
import net.neoforged.fml.earlydisplay.RenderElement;
import net.neoforged.fml.earlydisplay.SimpleBufferBuilder;
import net.neoforged.fml.earlydisplay.SimpleFont;
import net.neoforged.fml.loading.progress.ProgressMeter;
import net.neoforged.fml.loading.progress.StartupNotificationManager;

import java.util.List;
import java.util.function.Supplier;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefRenderElement.getGlobalAlpha;
import static java.lang.Math.clamp;

public class StartupProgressBar extends ProgressBar implements Supplier<RenderElement> {
    private final int lineSpacing;
    private final int descent;
    private final boolean absolute;
    private final float[] coords;

    public StartupProgressBar(SimpleFont font, boolean absolute, float[] coords) {
        super(font);
        final RefSimpleFont fontAccessor = new RefSimpleFont(font);
        lineSpacing = fontAccessor.lineSpacing();
        descent = fontAccessor.descent();
        this.absolute = absolute;
        this.coords = coords;
    }

    private static float[] indeterminateBar(int frame, boolean isActive) {
        if (getGlobalAlpha() != 0xFF || !isActive) {
            return new float[]{0f, 1f};
        } else {
            var progress = frame % 100;
            return new float[]{clamp((progress - 2) / 100f, 0f, 1f), clamp((progress + 2) / 100f, 0f, 1f)};
        }
    }

    public static float[] relativeCoords(float[] coords, CSB csb) {
        final float width = csb.ctx().scaledWidth() / 100f;
        final float height = csb.ctx().scaledHeight() / 100f;
        return new float[]{
                coords[0] * width,
                coords[1] * height,
        };
    }

    private static int[] toIntArray(float[] arr) {
        int[] result = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = (int) arr[i];
        }
        return result;
    }

    @Override
    public RenderElement get() {
        return RefRenderElement.constructor(this::render);
    }

    private void render(CSB csb, int frameNumber) {
        List<ProgressMeter> currentProgress = StartupNotificationManager.getCurrentProgress();
        final int size = currentProgress.size();
        final int barCount = 3;
        for (int i = 0; i < barCount && i < size; i++) {
            final ProgressMeter pm = currentProgress.get(i);
            renderBar(csb, frameNumber, i, pm);
        }
    }

    private void renderBar(final CSB csb, final int frame, int cnt, ProgressMeter pm) {
        final RenderElement.DisplayContext ctx = csb.ctx();
        final int[] coords = toIntArray(absolute ? this.coords : relativeCoords(this.coords, csb));
        final int barSpacing = (lineSpacing - descent + BAR_HEIGHT) * ctx.scale();
        final int y = coords[1] + cnt * barSpacing;
        final int alpha = 0xFF;
        final int colour = ColourScheme.BLACK.foreground().packedint(alpha);
        TextureRenderer textureRenderer;
        if (pm.steps() == 0) {
            textureRenderer = progressBar(ct -> new float[]{(coords[0] - BAR_WIDTH / 2f * ct.scale()), y + lineSpacing - descent, BAR_WIDTH * ct.scale()}, f -> colour, f -> indeterminateBar(f, cnt == 0));
        } else {
            textureRenderer = progressBar(ct -> new float[]{(coords[0] - BAR_WIDTH / 2f * ct.scale()), y + lineSpacing - descent, BAR_WIDTH * ct.scale()}, f -> colour, f -> new float[]{0f, pm.progress()});
        }
        textureRenderer.accept(csb, frame);
        renderText(text((coords[0] - BAR_WIDTH / 2 * ctx.scale()), y, pm.label().getText(), colour), csb);
    }

    public interface TextGenerator {
        void accept(SimpleBufferBuilder bb, SimpleFont fh, RenderElement.DisplayContext ctx);
    }
}
