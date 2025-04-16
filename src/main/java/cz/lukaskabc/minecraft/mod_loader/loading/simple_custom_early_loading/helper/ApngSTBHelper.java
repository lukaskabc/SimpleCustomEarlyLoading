package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.helper;

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

    public static ApngTexture resolveAndBindApngTexture(String file) throws FileNotFoundException, PngException {
        final InputStream inputStream = ConfigLoader.resolveFile(Path.of(file), null);

        final Argb8888BitmapSequence argbSequence = Png.readArgb8888BitmapSequence(inputStream);

        if (!argbSequence.isAnimated()) {
            throw new ConfigurationException("APNG file is not animated: " + file);
        }

        final ApngTexture apngTexture = new ApngTexture(argbSequence);

        if (apngTexture.getTotalHeight() >= MAX_TEXTURE_SIZE || apngTexture.getTotalWidth() >= MAX_TEXTURE_SIZE) {
            LOG.error("APNG file {} reaches the maximum hardware texture size of {}x{}", file, MAX_TEXTURE_SIZE, MAX_TEXTURE_SIZE);
            throw new ConfigurationException("APNG file is too big: " + file);
        }

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
        if (buffer == null || buffer.capacity() < requiredSize) {
            buffer = BufferUtils.createByteBuffer(requiredSize);
        }
        buffer.clear();
        return buffer;
    }

    /**
     * @see <a href="https://stackoverflow.com/a/61645451/12690791">StackOverflow</a>
     */
    public static int argbToRgba(int argb) {
        // Source is in format: 0xAARRGGBB
        return ((argb & 0x00FF0000) >> 16) | //______RR
                ((argb & 0x0000FF00)) | //____GG__
                ((argb & 0x000000FF) << 16) | //___BB____
                ((argb & 0xFF000000));         //AA______
        // Return value is in format:  0xAABBGGRR
    }
}
