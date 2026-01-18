package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

import com.google.gson.annotations.SerializedName;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.element_anchor.ElementAnchor;
import org.jspecify.annotations.Nullable;

/**
 * Represents the element position and size on the screen.
 */
public class ElementPosition implements BoundsResolver {
    /**
     * Denotes the alignment of the element to the position defined by {@link #x} and {@link #y}.
     */
    private ElementAnchor positionAnchor = ElementAnchor.CENTER;
    /**
     * Unit for {@link #x} and {@link #y} values.
     * <p>
     * Percentage are relative to the window size.
     */
    private Unit positionUnit = Unit.PIXELS;
    @Nullable
    private Unit positionUnitX = null;
    @Nullable
    private Unit positionUnitY = null;
    /**
     * Unit for {@link #width} and {@link #height} values.
     * <p>
     * Percentage are relative to the window size.
     */
    private Unit sizeUnit = Unit.PIXELS;
    /**
     * X position of the element anchor on the screen.
     */
    private float x = 0;
    /**
     * Y position of the element anchor on the screen.
     */
    private float y = 0;
    /**
     * Width of the element on the screen.
     * If not set by configuration, {@link Float#NaN} is used.
     * In that case {@link #height} should be set while width is calculated to keep the aspect ratio.
     */
    private float width = Float.NaN;
    /**
     * Height of the element on the screen.
     * If not set by configuration, {@link Float#NaN} is used.
     * In that case {@link #width} should be set while height is calculated to keep the aspect ratio.
     */
    private float height = Float.NaN;

    private boolean keepAspectRatio = true;

    /**
     * Checks whether the element has a width set.
     *
     * @return true when the width is finite (not NaN or infinite), false otherwise
     */
    public boolean hasWidth() {
        return Float.isFinite(width);
    }

    public void validate() {
        if (!hasHeight() && !hasWidth()) {
            throw new ConfigurationException("Element position must have either width or height set");
        }
    }

    /**
     * Returns the width of the element on the screen.
     * If the width is not set, it is calculated based on the height and the aspect ratio of the element.
     *
     * @param elementTextureWidth  the width of the element texture
     * @param elementTextureHeight the height of the element texture
     * @return the width of the element on the screen
     */
    public int getSafeWidth(int elementTextureWidth, int elementTextureHeight) {
        if (hasWidth()) {
            return (int) width;
        } else {
            return (int) (height * elementTextureWidth / elementTextureHeight);
        }
    }

    public int getSafeHeight(int elementTextureWidth, int elementTextureHeight) {
        if (hasHeight()) {
            return (int) height;
        } else {
            return (int) (width * elementTextureHeight / elementTextureWidth);
        }
    }

    public int getRelativeWidth(int elementTextureWidth, int elementTextureHeight, int screenWidth, int screenHeight) {
        if (hasWidth()) {
            // The 'width' field is interpreted as a percentage relative to the screen width.
            return (int) (width * screenWidth / 100f);
        } else {
            return Math.round((height * screenHeight / 100f) * (elementTextureWidth / (float) elementTextureHeight));
        }
    }

    public int getRelativeHeight(int elementTextureWidth, int elementTextureHeight, int screenWidth, int screenHeight) {
        if (hasHeight()) {
            return (int) (height * screenHeight / 100f);
        } else {
            return Math.round((width * screenWidth / 100f) * (elementTextureHeight / (float) elementTextureWidth));
        }
    }

    /**
     * Applies "cover" scaling when keepAspectRatio is enabled and both width and height are specified.
     * The content is scaled to cover the entire bounding box while maintaining aspect ratio.
     * This may result in some parts of the content being cropped.
     *
     * @param elementWidth  the original width of the element texture
     * @param elementHeight the original height of the element texture
     * @param boxWidth      the width of the bounding box
     * @param boxHeight     the height of the bounding box
     * @param output        array where output[2] = width, output[3] = height
     */
    private void applyCoverScaling(int elementWidth, int elementHeight, int boxWidth, int boxHeight, int[] output) {
        float elementAspect = (float) elementWidth / elementHeight;
        float boxAspect = (float) boxWidth / boxHeight;

        if (elementAspect > boxAspect) {
            // Element is wider than box (relative to their heights)
            // Scale by height to cover, width will exceed box width
            output[3] = boxHeight;
            output[2] = Math.round(boxHeight * elementAspect);
        } else {
            // Element is taller than box (relative to their widths)
            // Scale by width to cover, height will exceed box height
            output[2] = boxWidth;
            output[3] = Math.round(boxWidth / elementAspect);
        }
    }

    private void resolveSize(int elementWidth, int elementHeight, int screenWidth, int screenHeight, int[] output) {
        assert output.length >= 4;

        int resolvedWidth;
        int resolvedHeight;

        if (sizeUnit == Unit.PERCENTAGE) {
            resolvedWidth = getRelativeWidth(elementWidth, elementHeight, screenWidth, screenHeight);
            resolvedHeight = getRelativeHeight(elementWidth, elementHeight, screenWidth, screenHeight);
        } else if (sizeUnit == Unit.PIXELS) {
            resolvedWidth = getSafeWidth(elementWidth, elementHeight);
            resolvedHeight = getSafeHeight(elementWidth, elementHeight);
        } else {
            throw new ConfigurationException("Invalid size unit: " + sizeUnit);
        }

        output[2] = resolvedWidth;
        output[3] = resolvedHeight;

        // Apply cover scaling when keepAspectRatio is enabled and both dimensions are explicitly set
        if (keepAspectRatio && hasWidth() && hasHeight()) {
            applyCoverScaling(elementWidth, elementHeight, resolvedWidth, resolvedHeight, output);
        }
    }

    @Override
    public int[] resolveBounds(int elementWidth, int elementHeight, int screenWidth, int screenHeight) {
        validate();
        int[] position = new int[4];

        // the size of the element on the screen
        resolveSize(elementWidth, elementHeight, screenWidth, screenHeight, position);

        if (getPositionUnitX() == Unit.PERCENTAGE) {
            position[0] = (int) (x * screenWidth / 100f);
        } else {
            position[0] = (int) x;
        }
        if (getPositionUnitY() == Unit.PERCENTAGE) {
            position[1] = (int) (y * screenHeight / 100f);
        } else {
            position[1] = (int) y;
        }

        return positionAnchor.apply(position[0], position[1], position[2], position[3]);
    }

    /**
     * Checks whether the element has a height set.
     *
     * @return true when the height is finite (not NaN or infinite), false otherwise
     */
    public boolean hasHeight() {
        return Float.isFinite(height);
    }

    public Unit getPositionUnitX() {
        if (positionUnitX == null)
            return positionUnit;
        return positionUnitX;
    }

    public void setPositionUnitX(@Nullable Unit positionUnitX) {
        this.positionUnitX = positionUnitX;
    }

    public Unit getPositionUnitY() {
        if (positionUnitY == null)
            return positionUnit;
        return positionUnitY;
    }

    public void setPositionUnitY(@Nullable Unit positionUnitY) {
        this.positionUnitY = positionUnitY;
    }

    public Unit getPositionUnit() {
        return positionUnit;
    }

    public void setPositionUnit(Unit positionUnit) {
        this.positionUnit = positionUnit;
    }

    public Unit getSizeUnit() {
        return sizeUnit;
    }

    public void setSizeUnit(Unit sizeUnit) {
        this.sizeUnit = sizeUnit;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public ElementAnchor getPositionAnchor() {
        return positionAnchor;
    }

    public void setPositionAnchor(ElementAnchor positionAnchor) {
        this.positionAnchor = positionAnchor;
    }

    public boolean isKeepAspectRatio() {
        return keepAspectRatio;
    }

    public void setKeepAspectRatio(boolean keepAspectRatio) {
        this.keepAspectRatio = keepAspectRatio;
    }

    public enum Unit {
        /**
         * Absolute size in pixels on the screen.
         */
        @SerializedName(value = "PIXELS", alternate = {"pixels", "PIXEL", "pixel", "PX", "px"})
        PIXELS,
        /**
         * Percentage of the window size.
         * <p>
         * Allowed values: {@code 0 - 100}
         */
        @SerializedName(value = "PERCENTAGE", alternate = {"percentage", "PERCENT", "percent", "%"})
        PERCENTAGE,
    }
}