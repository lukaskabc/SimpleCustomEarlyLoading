package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.helper;

import net.ellerton.japng.argb8888.Argb8888BitmapSequence;
import net.ellerton.japng.chunks.PngHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.helper.ApngSTBHelper.MAX_TEXTURE_SIZE;
import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.helper.ApngSTBHelper.argbToRgba;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;

public class ApngTexture {
    private static final Logger LOG = LogManager.getLogger();
    private final FrameControl[] frameControls;
    private final PngHeader header;
    /**
     * When 0, the texture is played infinitely
     */
    private final int numberOfPlays;
    /**
     * Ids of combined textures
     */
    private final List<Integer> textureIds = new ArrayList<>(1);
    private final List<int[]> textureSizes = new ArrayList<>(1);

    /**
     * Index of texture ID in {@link #textureIds}
     */
    private int currentTexture;
    /**
     * X offset in the current texture of the current frame
     */
    private int currentXOffset;
    /**
     * Y offset in the current texture of the current frame
     */
    private int currentYOffset;

    private int currentPlay = 0;
    private int currentFrame = 0;

    /**
     * Allocates texture IDs for the given argbSequence
     * The frames are combined into a grid in several textures (Texture Atlases)
     * while respecting the runtime max texture size
     *
     * @param argbSequence the sequence to allocate the texture for
     */
    public ApngTexture(Argb8888BitmapSequence argbSequence) {
        this.header = argbSequence.header;
        this.frameControls = new FrameControl[argbSequence.getAnimationFrames().size()];
        numberOfPlays = argbSequence.getAnimationControl().numPlays;
        assert frameControls.length == argbSequence.getAnimationControl().numFrames;
    }

    public boolean loopForever() {
        return numberOfPlays == 0;
    }

    public int getFrameCount() {
        return frameControls.length;
    }

    public int getTotalWidth() {
        return header.width;
    }

    public int getTotalHeight() {
        return header.height;
    }

    public int getCurrentTextureId() {
        return textureIds.get(currentTexture);
    }

    public int[] getCurrentTextureSize() {
        return textureSizes.get(currentTexture);
    }

    public int getCurrentFrameHeight() {
        return frameControls[currentFrame].height();
    }

    public int getCurrentFrameWidth() {
        return frameControls[currentFrame].width();
    }

    public int getCurrentFrameXOffset() {
        return frameControls[currentFrame].xOffset();
    }

    public int getCurrentFrameYOffset() {
        return frameControls[currentFrame].yOffset();
    }

    public int getCurrentTextureXOffset() {
        return currentXOffset;
    }

    public int getCurrentTextureYOffset() {
        return currentYOffset;
    }

    /**
     * Transition to the next play if it is not the last one.
     * Resets the current frame to 0 if infinity looping is enabled
     * or if the play was moved to the next one
     *
     * @implNote intended only as internal method called from {@link #nextFrame()}
     */
    private void nextPlay() {
        if (numberOfPlays == 0) {
            currentFrame = 0;
            currentTexture = 0;
            currentXOffset = 0;
            currentYOffset = 0;
            return; // infinite looping
        }
        if (currentPlay + 1 < numberOfPlays) {
            currentPlay++;
            currentFrame = 0;
            currentTexture = 0;
            currentXOffset = 0;
            currentYOffset = 0;
        }
    }

    private void moveTextureOffsets() {
        currentXOffset += getCurrentFrameWidth();
        final int[] currentTextureSize = getCurrentTextureSize();

        if (currentXOffset >= currentTextureSize[0]) {
            currentXOffset = 0;
            currentYOffset += getTotalHeight();
        }
        if (currentYOffset >= currentTextureSize[1]) {
            currentYOffset = 0;
            currentTexture++;
        }
    }

    /**
     * Moves the texture to the next frame.
     * If the current frame is the last one, it will move to the next play and reset the current frame to 0
     * <p>
     * If this play is the last one, the frame remains unchanged
     */
    public void nextFrame() {
        if (currentFrame + 1 < getFrameCount()) {
            moveTextureOffsets();
            ++currentFrame;
        } else {
            nextPlay();
        }
    }

    private void fillBuffer(ByteBuffer byteBuffer, int startingFrame, int nextStartingFrame, List<Argb8888BitmapSequence.Frame> frames, int textureWidth) {
        int xOffset = 0;
        int yOffset = 0;
        for (int frameIndex = startingFrame; frameIndex < nextStartingFrame; frameIndex++) {
            final int[] data = frames.get(frameIndex).bitmap.getPixelArray();
            final var control = frameControls[frameIndex];
            int x = 0;

            // skip to position for the frame
            byteBuffer.position(yOffset * textureWidth * 4 + xOffset * 4);

            for (int i : data) {
                if (x == control.width()) {
                    x = 0;
                    byteBuffer.position(byteBuffer.position() + (textureWidth - control.width() - xOffset) * 4 + xOffset * 4);
                }

                byteBuffer.putInt(argbToRgba(i));
                x++;
            }
            xOffset += control.width();
            if (xOffset >= textureWidth) {
                xOffset = 0;
                yOffset += header.height;
            }
        }
    }

    /**
     * Iterates the frames and uploads them to textures
     */
    public void uploadTextures(List<Argb8888BitmapSequence.Frame> frames) {
        glActiveTexture(GL_TEXTURE0);

        ByteBuffer byteBuffer = null;

        assert frames.size() == getFrameCount();

        int startingFrame = 0;

        // for each frame
        while (startingFrame < getFrameCount()) {
            if (startingFrame > 0) {
                LOG.warn("Creating new texture! APNG frames exceeds hardware maximum texture size of {}x{}. Consider using smaller size or less frames.", MAX_TEXTURE_SIZE, MAX_TEXTURE_SIZE);
            }

            int lineWidth = 0;
            int totalWidth = 0;
            int totalHeight = 0;
            int nextStartingFrame = getFrameCount();

            for (int frameIndex = startingFrame; frameIndex < getFrameCount(); frameIndex++) {
                this.frameControls[frameIndex] = new FrameControl(frames.get(frameIndex).control);
                final int newWidth = lineWidth + frameControls[frameIndex].width();

                if (newWidth >= MAX_TEXTURE_SIZE) {
                    // if we reached the max line width, check if we can make a new line
                    int newTotalHeight = totalHeight + header.height;
                    if (newTotalHeight + header.height >= MAX_TEXTURE_SIZE) {
                        nextStartingFrame = frameIndex; // this frame was not included
                        break; // we can't fit the next frame
                    }
                    totalHeight = newTotalHeight;
                    totalWidth = Math.max(totalWidth, lineWidth);
                    lineWidth = 0;
                } else {
                    lineWidth = newWidth;
                }
            }
            totalWidth = Math.max(totalWidth, lineWidth);

            totalHeight += header.height; // add the last line height
            byteBuffer = ApngSTBHelper.reallocateWhenRequired(byteBuffer, totalHeight * totalWidth * 4); // times 4 as the int size a+r+g+b
            fillBuffer(byteBuffer, startingFrame, nextStartingFrame, frames, totalWidth);

            // prepare the buffer for reading
            byteBuffer.rewind();

            final int textureId = glGenTextures();
            // create the texture
            glBindTexture(GL_TEXTURE_2D, textureId);
            checkGlError();
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, totalWidth, totalHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
            checkGlError();

            textureIds.add(textureId);
            textureSizes.add(new int[]{totalWidth, totalHeight});
            startingFrame = nextStartingFrame;
        }
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private void checkGlError() {
        final int error = glGetError();
        if (error != GL_NO_ERROR) {
            throw new RuntimeException("OpenGL error: " + error);
        }
    }

}
