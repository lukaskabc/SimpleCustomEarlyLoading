package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.BoundsResolver;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.ConfigurationException;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.helper.ApngSTBHelper;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.helper.ApngTexture;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.CSB;
import net.ellerton.japng.error.PngException;
import net.neoforged.fml.earlydisplay.ElementShader;
import net.neoforged.fml.earlydisplay.QuadHelper;
import net.neoforged.fml.earlydisplay.SimpleBufferBuilder;
import org.jline.utils.Log;
import org.lwjgl.opengl.GL32C;

import java.io.FileNotFoundException;
import java.util.Set;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements.StaticTextureElement.COLOR;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;

public class ApngTextureElement implements ElementSupplier {
    public static final Set<String> SUPPORTED_EXTENSIONS = Set.of("apng");

    private final ApngTexture apngTexture;
    private final BoundsResolver boundsResolver;
    private long lastFrameTime;

    public ApngTextureElement(String texture, BoundsResolver boundsResolver) {
        this.boundsResolver = boundsResolver;
        try {
            apngTexture = ApngSTBHelper.resolveAndBindApngTexture(texture);
        } catch (FileNotFoundException | PngException e) {
            Log.error("Failed to load texture: ", e.getMessage());
            throw new ConfigurationException(e);
        }
        lastFrameTime = System.currentTimeMillis();
    }

    @Override
    public void render(CSB csb, int frame) {
        final int[] bounds = boundsResolver.resolveBounds(apngTexture.getTotalWidth(), apngTexture.getTotalHeight(), csb.ctx().scaledWidth(), csb.ctx().scaledHeight());
        final float[] uvs = getUV();
        csb.ctx().elementShader().updateTextureUniform(0);
        csb.ctx().elementShader().updateRenderTypeUniform(ElementShader.RenderType.TEXTURE);
        GL32C.glBindTexture(GL_TEXTURE_2D, apngTexture.getCurrentTextureId());
        csb.buffer().begin(SimpleBufferBuilder.Format.POS_TEX_COLOR, SimpleBufferBuilder.Mode.QUADS);
        QuadHelper.loadQuad(csb.buffer(), bounds[0], bounds[2], bounds[1], bounds[3], uvs[0], uvs[1], uvs[2], uvs[3], COLOR);
        csb.buffer().draw();
        GL32C.glBindTexture(GL_TEXTURE_2D, 0);
        nextFrame();
    }

    /**
     * Measures the elapsed time since the last call
     * and if it exceeds the delay of the current frame,
     * it advances to the next frame.
     */
    private void nextFrame() {
        if (System.currentTimeMillis() - lastFrameTime > apngTexture.getCurrentDelay() * 1000) {
            apngTexture.nextFrame();
            lastFrameTime = System.currentTimeMillis();
        }
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
