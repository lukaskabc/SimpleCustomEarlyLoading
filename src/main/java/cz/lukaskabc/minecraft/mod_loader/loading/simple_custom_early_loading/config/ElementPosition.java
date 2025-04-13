package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.element_anchor.ElementAnchor;

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

    public int getRelativeWidth(int elementTextureWidth, int elementTextureHeight, int screenWidth) {
        if (hasWidth()) {
            return (int) (width * screenWidth / 100f);
        } else {
            return Math.round(height * elementTextureWidth / elementTextureHeight * screenWidth / 100f);
        }
    }

    public int getRelativeHeight(int elementTextureWidth, int elementTextureHeight, int screenHeight) {
        if (hasHeight()) {
            return (int) (height * screenHeight / 100f);
        } else {
            return Math.round(width * elementTextureHeight / elementTextureWidth * screenHeight / 100f);
        }
    }

    private void resolveSize(int elementWidth, int elementHeight, int screenWidth, int screenHeight, int[] output) {
        assert output.length >= 4;

        output[2] = getSafeWidth(elementWidth, elementHeight);
        output[3] = getSafeHeight(elementWidth, elementHeight);
        if (sizeUnit == Unit.PERCENTAGE) {
            output[2] = getRelativeWidth(elementWidth, elementHeight, screenWidth);
            output[3] = getRelativeHeight(elementWidth, elementHeight, screenHeight);
        } else if (sizeUnit != Unit.PIXELS) {
            throw new ConfigurationException("Invalid size unit: " + sizeUnit);
        }
    }

    @Override
    public int[] resolveBounds(int elementWidth, int elementHeight, int screenWidth, int screenHeight) {
        validate();
        int[] position = new int[4];

        // the size of the element on the screen
        resolveSize(elementWidth, elementHeight, screenWidth, screenHeight, position);

        if (positionUnit == Unit.PERCENTAGE) {
            position[0] = (int) (x * screenWidth / 100f);
            position[1] = (int) (y * screenHeight / 100f);
        } else {
            position[0] = (int) x;
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

    public enum Unit {
        /**
         * Absolute size in pixels on the screen.
         */
        PIXELS,
        /**
         * Percentage of the window size.
         * <p>
         * Allowed values: {@code 0 - 100}
         */
        PERCENTAGE,
    }
}
