package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements;


import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.CSB;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefSimpleFont;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.TextureRenderer;
import net.neoforged.fml.earlydisplay.*;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefRenderElement.getGlobalAlpha;


public class ProgressBar {
    public static final int BAR_HEIGHT = 20;
    public static final int BAR_WIDTH = 400;
    protected final int fontTextureId;
    protected final SimpleFont font;

    public ProgressBar(SimpleFont font) {
        this.font = font;
        final RefSimpleFont fontAccessor = new RefSimpleFont(font);
        fontTextureId = fontAccessor.textureNumber();
    }

    public static TextureRenderer progressBar(BarPosition position, ColorFunction colourFunction, ProgressDisplay progressDisplay) {
        return (csb, frame) -> {
            final RenderElement.DisplayContext context = csb.ctx();
            final SimpleBufferBuilder bb = csb.buffer();

            var colour = colourFunction.color(frame);
            var alpha = (colour & 0xFF000000) >> 24;
            context.elementShader().updateTextureUniform(0);
            context.elementShader().updateRenderTypeUniform(ElementShader.RenderType.BAR);
            var progress = progressDisplay.progress(frame);
            bb.begin(SimpleBufferBuilder.Format.POS_TEX_COLOR, SimpleBufferBuilder.Mode.QUADS);
            var inset = 2;
            var pos = position.location(context);
            var x0 = pos[0];
            var x1 = pos[0] + pos[2] + 4 * inset;
            var y0 = pos[1];
            var y1 = y0 + BAR_HEIGHT;
            QuadHelper.loadQuad(bb, x0, x1, y0, y1, 0f, 0f, 0f, 0f, context.colourScheme().foreground().packedint(alpha));

            x0 += inset;
            x1 -= inset;
            y0 += inset;
            y1 -= inset;
            QuadHelper.loadQuad(bb, x0, x1, y0, y1, 0f, 0f, 0f, 0f, context.colourScheme().background().packedint(getGlobalAlpha()));

            x1 = x0 + inset + (int) (progress[1] * pos[2]);
            x0 += (int) (inset + progress[0] * pos[2]);
            y0 += inset;
            y1 -= inset;
            QuadHelper.loadQuad(bb, x0, x1, y0, y1, 0f, 0f, 0f, 0f, colour);
            bb.draw();
        };
    }

    protected void renderText(final StartupProgressBar.TextGenerator textGenerator, final CSB csb) {
        csb.ctx().elementShader().updateTextureUniform(fontTextureId);
        csb.ctx().elementShader().updateRenderTypeUniform(ElementShader.RenderType.FONT);
        csb.buffer().begin(SimpleBufferBuilder.Format.POS_TEX_COLOR, SimpleBufferBuilder.Mode.QUADS);
        textGenerator.accept(csb.buffer(), font, csb.ctx());
        csb.buffer().draw();
    }

    protected StartupProgressBar.TextGenerator text(int x, int y, String text, int colour) {
        return (bb, font, context) -> font.generateVerticesForTexts(x, y, bb, new SimpleFont.DisplayText(text, colour));
    }

    public interface ColorFunction {
        int color(int frame);
    }

    public interface ProgressDisplay {
        float[] progress(int frame);
    }

    public interface BarPosition {
        float[] location(RenderElement.DisplayContext context);
    }
}
