package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements;

import cz.lukaskabc.minecraft.mod_loader.loading.stargate_early_loading.dialing.DialingStrategy;
import cz.lukaskabc.minecraft.mod_loader.loading.stargate_early_loading.reflection.RefRenderElement;
import cz.lukaskabc.minecraft.mod_loader.loading.stargate_early_loading.reflection.RefSimpleFont;
import cz.lukaskabc.minecraft.mod_loader.loading.stargate_early_loading.reflection.TextureRenderer;
import cz.lukaskabc.minecraft.mod_loader.loading.stargate_early_loading.utils.ContextSimpleBuffer;
import net.neoforged.fml.earlydisplay.ColourScheme;
import net.neoforged.fml.earlydisplay.RenderElement;
import net.neoforged.fml.earlydisplay.SimpleBufferBuilder;
import net.neoforged.fml.earlydisplay.SimpleFont;
import net.neoforged.fml.loading.progress.ProgressMeter;
import net.neoforged.fml.loading.progress.StartupNotificationManager;

import java.util.List;
import java.util.function.Supplier;

import static cz.lukaskabc.minecraft.mod_loader.loading.stargate_early_loading.StargateEarlyLoadingWindow.MEMORY_BAR_HEIGHT;
import static cz.lukaskabc.minecraft.mod_loader.loading.stargate_early_loading.StargateEarlyLoadingWindow.getGlobalAlpha;
import static cz.lukaskabc.minecraft.mod_loader.loading.stargate_early_loading.elements.MojangLogo.LOGO_NEGATIVE_HEIGHT_OFFSET;
import static cz.lukaskabc.minecraft.mod_loader.loading.stargate_early_loading.elements.MojangLogo.LOGO_WIDTH;
import static java.lang.Math.clamp;

public class StartupProgressBar extends ProgressBar implements Supplier<RenderElement> {
    private final int lineSpacing;
    private final int descent;


    public StartupProgressBar(SimpleFont font, DialingStrategy dialingStrategy) {
        super(font);
        this.dialingStrategy = dialingStrategy;
        final RefSimpleFont fontAccessor = new RefSimpleFont(font);
        lineSpacing = fontAccessor.lineSpacing();
        descent = fontAccessor.descent();
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

    private void render(ContextSimpleBuffer bb, int frameNumber) {
        List<ProgressMeter> currentProgress = StartupNotificationManager.getCurrentProgress();
        dialingStrategy.updateProgress(currentProgress, frameNumber);
        final int size = currentProgress.size();
        final int barCount = 3;
        for (int i = 0; i < barCount && i < size; i++) {
            final ProgressMeter pm = currentProgress.get(i);
            renderBar(bb, frameNumber, i, pm);
        }
    }

    private void renderBar(final ContextSimpleBuffer bb, final int frame, int cnt, ProgressMeter pm) {
        final RenderElement.DisplayContext ctx = bb.context();
        final int barSpacing = (lineSpacing - descent + BAR_HEIGHT) * ctx.scale();
        final int mojangLogoHeight = ctx.scale() * ((LOGO_WIDTH / 2) - LOGO_NEGATIVE_HEIGHT_OFFSET);
        final int y = ctx.scaledHeight() / 2 + cnt * barSpacing + MEMORY_BAR_HEIGHT + mojangLogoHeight;
        final int alpha = 0xFF;
        final int colour = ColourScheme.BLACK.foreground().packedint(alpha);
        TextureRenderer textureRenderer;
        if (pm.steps() == 0) {
            textureRenderer = progressBar(ct -> new int[]{(ct.scaledWidth() - BAR_WIDTH * ct.scale()) / 2, y + lineSpacing - descent, BAR_WIDTH * ct.scale()}, f -> colour, f -> indeterminateBar(f, cnt == 0));
        } else {
            textureRenderer = progressBar(ct -> new int[]{(ct.scaledWidth() - BAR_WIDTH * ct.scale()) / 2, y + lineSpacing - descent, BAR_WIDTH * ct.scale()}, f -> colour, f -> new float[]{0f, pm.progress()});
        }
        textureRenderer.accept(bb, frame);
        renderText(text((ctx.scaledWidth() - BAR_WIDTH * ctx.scale()) / 2, y, pm.label().getText(), colour), bb);
    }

    public interface TextGenerator {
        void accept(SimpleBufferBuilder bb, SimpleFont fh, RenderElement.DisplayContext ctx);
    }
}
