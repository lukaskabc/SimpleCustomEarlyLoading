package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.BoundsResolver;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.ElementPosition;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.StartupProgressBarConfig;
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

    private final int barCount;
    private final BoundsResolver boundsResolver;

    public StartupProgressBar(SimpleFont font, StartupProgressBarConfig barConfig) {
        super(font);
        final RefSimpleFont fontAccessor = new RefSimpleFont(font);
        lineSpacing = fontAccessor.lineSpacing();
        descent = fontAccessor.descent();
        barCount = barConfig.getBarCount();
        barConfig.getPosition().setSizeUnit(ElementPosition.Unit.PIXELS);
        barConfig.getPosition().setWidth(BAR_WIDTH);
        barConfig.getPosition().setHeight(BAR_HEIGHT);
        this.boundsResolver = barConfig.getPosition();
    }

    private static float[] indeterminateBar(int frame, boolean isActive) {
        if (getGlobalAlpha() != 0xFF || !isActive) {
            return new float[]{0f, 1f};
        } else {
            var progress = frame % 100;
            return new float[]{clamp((progress - 2) / 100f, 0f, 1f), clamp((progress + 2) / 100f, 0f, 1f)};
        }
    }

    @Override
    public RenderElement get() {
        return RefRenderElement.constructor(this::render);
    }

    private void render(CSB csb, int frameNumber) {
        List<ProgressMeter> currentProgress = StartupNotificationManager.getCurrentProgress();
        final int size = currentProgress.size();
        for (int i = 0; i < barCount && i < size; i++) {
            final ProgressMeter pm = currentProgress.get(i);
            renderBar(csb, frameNumber, i, pm);
        }
    }

    private void renderBar(final CSB csb, final int frame, int cnt, ProgressMeter pm) {
        final RenderElement.DisplayContext ctx = csb.ctx();
        final int[] coords = boundsResolver.resolveBounds(BAR_WIDTH, BAR_HEIGHT, ctx.scaledWidth(), ctx.scaledHeight());
        final int barSpacing = (lineSpacing - descent + BAR_HEIGHT) * ctx.scale();
        coords[1] += cnt * barSpacing;
        coords[3] += cnt * barSpacing;

        final int alpha = 0xFF;
        final int colour = ColourScheme.BLACK.foreground().packedint(alpha);
        TextureRenderer textureRenderer;
        if (pm.steps() == 0) {
            textureRenderer = progressBar(coords, f -> colour, f -> indeterminateBar(f, cnt == 0));
        } else {
            textureRenderer = progressBar(coords, f -> colour, f -> new float[]{0f, pm.progress()});
        }
        textureRenderer.accept(csb, frame);
        renderText(text(coords[0], coords[3], pm.label().getText(), colour), csb);
    }

    public interface TextGenerator {
        void accept(SimpleBufferBuilder bb, SimpleFont fh, RenderElement.DisplayContext ctx);
    }
}
