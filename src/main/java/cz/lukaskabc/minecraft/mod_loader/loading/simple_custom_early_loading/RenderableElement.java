package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.ConfigurationException;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.CSB;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefRenderElement;
import net.neoforged.fml.earlydisplay.ElementShader;
import net.neoforged.fml.earlydisplay.QuadHelper;
import net.neoforged.fml.earlydisplay.RenderElement;
import net.neoforged.fml.earlydisplay.SimpleBufferBuilder;
import org.jline.utils.Log;

import java.io.FileNotFoundException;
import java.util.function.Supplier;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefRenderElement.INDEX_TEXTURE_OFFSET;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;

public class RenderableElement implements Supplier<RenderElement> {
    public static final int COLOR = (255 << 24) | 0xFFFFFF;
    private static final int DEFAULT_TEXTURE_SIZE = 34881;
    private static int TEXTURE_ID = 1;

    private final String texture;
    private final int textureId;

    public RenderableElement(String texture) {
        this.texture = texture;
        this.textureId = TEXTURE_ID++;
    }

    /**
     * Returns a RenderElement rendering the background texture.
     *
     * @return a RenderElement for rendering the background.
     * @throws ConfigurationException if texture loading fails.
     */
    @Override
    public RenderElement get() {
        try {
            STBHelper.resolveAndBindTexture(texture, DEFAULT_TEXTURE_SIZE, GL_TEXTURE0 + TEXTURE_ID + INDEX_TEXTURE_OFFSET);
        } catch (FileNotFoundException e) {
            Log.error("Failed to load texture: ", e.getMessage());
            throw new ConfigurationException(e);
        }
        return RefRenderElement.constructor(this::render);
    }

    /**
     * Renders the background texture using the provided buffer context.
     *
     * @param csb   the buffer context for rendering.
     * @param frame the current frame number.
     */
    private void render(CSB csb, int frame) {
        csb.ctx().elementShader().updateTextureUniform(textureId + INDEX_TEXTURE_OFFSET);
        csb.ctx().elementShader().updateRenderTypeUniform(ElementShader.RenderType.TEXTURE);
        csb.buffer().begin(SimpleBufferBuilder.Format.POS_TEX_COLOR, SimpleBufferBuilder.Mode.QUADS);
        QuadHelper.loadQuad(csb.buffer(), 0f, csb.ctx().scaledWidth(), 0f, csb.ctx().scaledHeight(), 0f, 1f, 0f, 1f, COLOR);
        csb.buffer().draw();
    }

}
