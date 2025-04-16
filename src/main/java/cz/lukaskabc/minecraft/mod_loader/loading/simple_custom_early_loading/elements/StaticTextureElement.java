package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.BoundsResolver;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.ConfigurationException;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.helper.StaticSTBHelper;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.CSB;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefRenderElement;
import net.minecraftforge.fml.earlydisplay.ElementShader;
import net.minecraftforge.fml.earlydisplay.QuadHelper;
import net.minecraftforge.fml.earlydisplay.RenderElement;
import net.minecraftforge.fml.earlydisplay.SimpleBufferBuilder;
import org.jline.utils.Log;
import org.lwjgl.opengl.GL32C;

import java.io.FileNotFoundException;
import java.util.Set;

import static org.lwjgl.opengl.GL32C.GL_TEXTURE_2D;

/**
 * Renders a single static texture.
 */
public class StaticTextureElement implements ElementSupplier {
    public static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
            "jpg", "jpeg",
            "png",
            "tga",
            "bmp",
            "psd",
            "gif",
            "hdr",
            "pic",
            "pnm"
    );

    public static final int COLOR = (255 << 24) | 0xFFFFFF;

    private final int[] textureSize;
    private final int textureId;
    private final BoundsResolver boundsResolver;

    public StaticTextureElement(String texture, BoundsResolver boundsResolver) {
        final int[] textureWidth = new int[1];
        final int[] textureHeight = new int[1];
        this.boundsResolver = boundsResolver;
        try {
            textureId = StaticSTBHelper.resolveAndBindTexture(texture, textureWidth, textureHeight);
        } catch (FileNotFoundException e) {
            Log.error("Failed to load texture: ", e.getMessage());
            throw new ConfigurationException(e);
        }
        this.textureSize = new int[]{textureWidth[0], textureHeight[0]};
    }

    @Override
    public void render(CSB csb, int frame) {
        final int[] bounds = boundsResolver.resolveBounds(textureSize[0], textureSize[1], csb.ctx().scaledWidth(), csb.ctx().scaledHeight());
        csb.ctx().elementShader().updateTextureUniform(0);
        csb.ctx().elementShader().updateRenderTypeUniform(ElementShader.RenderType.TEXTURE);
        GL32C.glBindTexture(GL_TEXTURE_2D, textureId);
        csb.buffer().begin(SimpleBufferBuilder.Format.POS_TEX_COLOR, SimpleBufferBuilder.Mode.QUADS);
        QuadHelper.loadQuad(csb.buffer(), bounds[0], bounds[2], bounds[1], bounds[3], 0f, 1f, 0f, 1f, COLOR);
        csb.buffer().draw();
        GL32C.glBindTexture(GL_TEXTURE_2D, 0);
    }

}
