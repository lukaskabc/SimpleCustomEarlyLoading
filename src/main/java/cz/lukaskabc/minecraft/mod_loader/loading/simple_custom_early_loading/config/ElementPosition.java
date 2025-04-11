package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

/**
 * Represents the element position and size on the screen.
 */
public class ElementPosition {
    private Unit positionType = Unit.PIXELS;
    private Unit sizeType = Unit.PIXELS;
    private float x = 0;
    private float y = 0;
    private float width = Float.NaN;
    private float height = Float.NaN;

    public boolean hasWidth() {
        return Float.isFinite(width);
    }

    public boolean hasHeight() {
        return Float.isFinite(height);
    }

    public Unit getPositionType() {
        return positionType;
    }

    public void setPositionType(Unit positionType) {
        this.positionType = positionType;
    }

    public Unit getSizeType() {
        return sizeType;
    }

    public void setSizeType(Unit sizeType) {
        this.sizeType = sizeType;
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

    public enum Unit {
        PIXELS,
        PERCENTAGE,
    }
}
/*
Position
absolute - pixels
percentage on the screen

Size
absolute - pixels
percentage on the screen


width + height
or just one with keeping aspect ratio



 */