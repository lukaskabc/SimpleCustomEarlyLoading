package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.stb;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.ConfigLoader;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.ConfigurationException;
import net.ellerton.japng.Png;
import net.ellerton.japng.argb8888.Argb8888BitmapSequence;
import net.ellerton.japng.error.PngException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL32C;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL32C.GL_MAX_TEXTURE_SIZE;

public class ApngSTBHelper {
    public static final int MAX_TEXTURE_SIZE = GL32C.glGetInteger(GL_MAX_TEXTURE_SIZE);
    private static final Logger LOG = LogManager.getLogger();

    private ApngSTBHelper() {
        throw new AssertionError();
    }

    public static ApngTexture resolveAndBindApngTexture(String file, int size) throws FileNotFoundException, PngException {
        int[] lw = new int[1];
        int[] lh = new int[1];
        int[] lc = new int[1];

        final InputStream inputStream = ConfigLoader.resolveFile(Path.of(file));

        final Argb8888BitmapSequence argbSequence = Png.readArgb8888BitmapSequence(inputStream);

        if (!argbSequence.isAnimated()) {
            throw new ConfigurationException("APNG file is not animated: " + file);
        }

        final int frameCount = argbSequence.getAnimationControl().numFrames;

        final ApngTexture apngTexture = new ApngTexture(argbSequence);

        apngTexture.uploadTextures(argbSequence.getAnimationFrames());

        return apngTexture;
    }

    /**
     * Clears the buffer contents {@link ByteBuffer#clear()} and reallocates it if the remaining size is less than the required size.
     *
     * @param buffer       the buffer to reallocate
     * @param requiredSize the required size
     * @return the reallocated buffer
     */
    public static ByteBuffer reallocateWhenRequired(@Nullable ByteBuffer buffer, int requiredSize) {
        if (buffer != null) {
            buffer.clear();
        }
        if (buffer == null || buffer.remaining() < requiredSize) {
            buffer = BufferUtils.createByteBuffer(requiredSize);
        }
        return buffer;
    }
}
