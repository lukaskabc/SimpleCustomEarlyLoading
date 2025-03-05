package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

public class ProgressBarConfig {
    private ElementType type;
    private float x;
    private float y;

    public ElementType getType() {
        return type;
    }

    public void setType(ElementType type) {
        this.type = type;
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

    public float[] getCoords() {
        return new float[]{x, y};
    }
}
