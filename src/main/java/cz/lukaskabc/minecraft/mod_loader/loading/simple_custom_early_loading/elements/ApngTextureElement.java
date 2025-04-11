package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.ConfigurationException;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.CSB;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.stb.ApngSTBHelper;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.stb.ApngTexture;
import net.ellerton.japng.error.PngException;
import net.neoforged.fml.earlydisplay.ElementShader;
import net.neoforged.fml.earlydisplay.QuadHelper;
import net.neoforged.fml.earlydisplay.SimpleBufferBuilder;
import org.jline.utils.Log;
import org.lwjgl.opengl.GL32C;

import java.io.FileNotFoundException;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements.StaticTextureElement.COLOR;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;

public class ApngTextureElement implements ElementSupplier {
    private static final int DEFAULT_TEXTURE_SIZE = 34881;

    private final ApngTexture apngTexture;
    private final boolean absolute;
    private final float[] coords;

    public ApngTextureElement(String texture, boolean absolute, float[] coords) {
        this.coords = coords;
        this.absolute = absolute;
        try {
            apngTexture = ApngSTBHelper.resolveAndBindApngTexture(texture, DEFAULT_TEXTURE_SIZE);
        } catch (FileNotFoundException | PngException e) {
            Log.error("Failed to load texture: ", e.getMessage());
            throw new ConfigurationException(e);
        }
    }

    @Override
    public void render(CSB csb, int frame) {
        final float[] coords = absolute ? this.coords : StaticTextureElement.relativeCoords(this.coords, csb);
        final float[] uvs = getUV();
        csb.ctx().elementShader().updateTextureUniform(0);
        csb.ctx().elementShader().updateRenderTypeUniform(ElementShader.RenderType.TEXTURE);
        GL32C.glBindTexture(GL_TEXTURE_2D, apngTexture.getCurrentTextureId());
        csb.buffer().begin(SimpleBufferBuilder.Format.POS_TEX_COLOR, SimpleBufferBuilder.Mode.QUADS);
        QuadHelper.loadQuad(csb.buffer(), coords[0], coords[1], coords[2], coords[3], uvs[0], uvs[1], uvs[2], uvs[3], COLOR);
        csb.buffer().draw();
        GL32C.glBindTexture(GL_TEXTURE_2D, 0);
        apngTexture.nextFrame();
    }

    private float[] getUV() {
        final float[] uv = new float[4];
        final int[] currentTextureSize = apngTexture.getCurrentTextureSize();
        uv[0] = apngTexture.getCurrentTextureXOffset() / (float) currentTextureSize[0];
        uv[1] = (apngTexture.getCurrentTextureXOffset() + apngTexture.getCurrentFrameWidth()) / (float) currentTextureSize[0];
        uv[2] = apngTexture.getCurrentTextureYOffset() / (float) currentTextureSize[1];
        uv[3] = (apngTexture.getCurrentTextureYOffset() + apngTexture.getCurrentFrameHeight()) / (float) currentTextureSize[1];
        return uv;
    }
}
