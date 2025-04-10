package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.ConfigurationException;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.CSB;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefRenderElement;
import net.minecraftforge.fml.earlydisplay.ElementShader;
import net.minecraftforge.fml.earlydisplay.QuadHelper;
import net.minecraftforge.fml.earlydisplay.RenderElement;
import net.minecraftforge.fml.earlydisplay.SimpleBufferBuilder;
import org.jline.utils.Log;
import org.lwjgl.opengl.GL32C;

import java.io.FileNotFoundException;

import static org.lwjgl.opengl.GL32C.GL_TEXTURE_2D;

/**
 * Renders a single static texture.
 */
public class StaticTextureElement implements ElementSupplier {
    public static final int COLOR = (255 << 24) | 0xFFFFFF;
    private static final int DEFAULT_TEXTURE_SIZE = 34881;

    private final int textureId;
    private final boolean absolute;
    private final float[] coords;

    public StaticTextureElement(String texture, boolean absolute, float[] coords) {
        this.coords = coords;
        this.absolute = absolute;
        try {
            textureId = StaticSTBHelper.resolveAndBindTexture(texture, DEFAULT_TEXTURE_SIZE);
        } catch (FileNotFoundException e) {
            Log.error("Failed to load texture: ", e.getMessage());
            throw new ConfigurationException(e);
        }
    }

    public static float[] relativeCoords(float[] coords, CSB csb) {
        final float width = csb.ctx().scaledWidth() / 100f;
        final float height = csb.ctx().scaledHeight() / 100f;
        return new float[]{
                coords[0] * width,
                coords[1] * width,
                coords[2] * height,
                coords[3] * height
        };
    }

    @Override
    public void render(CSB csb, int frame) {
        final float[] coords = absolute ? this.coords : relativeCoords(this.coords, csb);
        csb.ctx().elementShader().updateTextureUniform(0);
        csb.ctx().elementShader().updateRenderTypeUniform(ElementShader.RenderType.TEXTURE);
        GL32C.glBindTexture(GL_TEXTURE_2D, textureId);
        csb.buffer().begin(SimpleBufferBuilder.Format.POS_TEX_COLOR, SimpleBufferBuilder.Mode.QUADS);
        QuadHelper.loadQuad(csb.buffer(), coords[0], coords[1], coords[2], coords[3], 0f, 1f, 0f, 1f, COLOR);
        csb.buffer().draw();
        GL32C.glBindTexture(GL_TEXTURE_2D, 0);
    }

}
