package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

/**
 * Represents the element position and size on the screen.
 */
public class ElementPosition {
    /**
     * Denotes the alignment of the element to the position defined by {@link #x} and {@link #y}.
     */
    private Anchor positionAnchor = Anchor.CENTER;
    /**
     * Unit for {@link #x} and {@link #y} values.
     */
    private Unit positionUnit = Unit.PIXELS;
    /**
     * Unit for {@link #width} and {@link #height} values.
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

    public Anchor getPositionAnchor() {
        return positionAnchor;
    }

    public void setPositionAnchor(Anchor positionAnchor) {
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

    public enum Anchor {
        /**
         * Anchor is in the top left corner of the screen.
         * <pre><code>
         * +-------------+
         * |#            |
         * |             |
         * |             |
         * +-------------+
         * </code></pre>
         */
        TOP_LEFT,
        /**
         * Anchor is in the top right corner of the screen.
         * <pre><code>
         * +-------------+
         * |            #|
         * |             |
         * |             |
         * +-------------+
         * </code></pre>
         */
        TOP_RIGHT,
        /**
         * Anchor is in the bottom left corner of the screen.
         * <pre><code>
         * +-------------+
         * |             |
         * |             |
         * |#            |
         * +-------------+
         * </code></pre>
         */
        BOTTOM_LEFT,
        /**
         * Anchor is in the bottom right corner of the screen.
         * <pre><code>
         * +-------------+
         * |             |
         * |             |
         * |            #|
         * +-------------+
         * </code></pre>
         */
        BOTTOM_RIGHT,
        /**
         * Anchor is in the center of the screen.
         * <pre><code>
         * +-------------+
         * |             |
         * |      #      |
         * |             |
         * +-------------+
         * </code></pre>
         */
        CENTER,
        /**
         * Anchor is in the top center of the screen.
         * <pre><code>
         * +-------------+
         * |      #      |
         * |             |
         * |             |
         * +-------------+
         * </code></pre>
         */
        TOP_CENTER,
        /**
         * Anchor is in the bottom center of the screen.
         * <pre><code>
         * +-------------+
         * |             |
         * |             |
         * |      #      |
         * +-------------+
         * </code></pre>
         */
        BOTTOM_CENTER,
        /**
         * Anchor is in the left center of the screen.
         * <pre><code>
         * +-------------+
         * |             |
         * |#            |
         * |             |
         * +-------------+
         * </code></pre>
         */
        LEFT_CENTER,
        /**
         * Anchor is in the right center of the screen.
         * <pre><code>
         * +-------------+
         * |             |
         * |            #|
         * |             |
         * +-------------+
         * </code></pre>
         */
        RIGHT_CENTER,
    }
}
