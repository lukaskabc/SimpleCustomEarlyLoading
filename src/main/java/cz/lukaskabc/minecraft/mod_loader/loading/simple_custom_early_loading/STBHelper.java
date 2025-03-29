/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 * Moved for execution on different class loader.
 * All rights belong to the original authors.
 * Changes:
 * - Magnification filter to nearest
 */
package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.ConfigLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.file.Path;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefRenderElement.INDEX_TEXTURE_OFFSET;
import static org.lwjgl.opengl.GL32C.*;

public class STBHelper {
    public static final int TEXTURE_UNIT = GL_TEXTURE0 + INDEX_TEXTURE_OFFSET;

    private STBHelper() {
        throw new AssertionError();
    }

    public static ByteBuffer readToBuffer(final InputStream inputStream, int initialCapacity) {
        ByteBuffer buf;
        try (var channel = Channels.newChannel(inputStream)) {
            buf = BufferUtils.createByteBuffer(initialCapacity);
            while (true) {
                var readbytes = channel.read(buf);
                if (readbytes == -1) break;
                if (buf.remaining() == 0) { // extend the buffer by 50%
                    var newBuf = BufferUtils.createByteBuffer(buf.capacity() * 3 / 2);
                    buf.flip();
                    newBuf.put(buf);
                    buf = newBuf;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        buf.flip();
        return MemoryUtil.memSlice(buf); // we trim the final buffer to the size of the content
    }

    /**
     * @return textureId
     */
    public static int resolveAndBindTexture(String file, int size) throws FileNotFoundException {
        int[] lw = new int[1];
        int[] lh = new int[1];
        int[] lc = new int[1];
        final ByteBuffer img = loadImageFromClasspath(file, size, lw, lh, lc);
        int texid = glGenTextures();
        glActiveTexture(TEXTURE_UNIT);
        glBindTexture(GL_TEXTURE_2D, texid);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, lw[0], lh[0], 0, GL_RGBA, GL_UNSIGNED_BYTE, img);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);
        MemoryUtil.memFree(img);
        return texid;
    }

    public static ByteBuffer loadImageFromClasspath(String file, int size, int[] width, int[] height, int[] channels) throws FileNotFoundException {
        final InputStream inputStream = ConfigLoader.resolveFile(Path.of(file));
        ByteBuffer buf = readToBuffer(inputStream, size);
        return STBImage.stbi_load_from_memory(buf, width, height, channels, 4);
    }
}
