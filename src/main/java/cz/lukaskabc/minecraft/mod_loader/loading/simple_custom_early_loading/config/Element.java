package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

public class Element {
    private String image = null;
    private ElementType type = ElementType.ABSOLUTE;
    private float x1;
    private float x2;
    private float y1;
    private float y2;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ElementType getType() {
        return type;
    }

    public void setType(ElementType type) {
        this.type = type;
    }

    public float getX1() {
        return x1;
    }

    public void setX1(float x1) {
        this.x1 = x1;
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public float getY1() {
        return y1;
    }

    public void setY1(float y1) {
        this.y1 = y1;
    }

    public float getY2() {
        return y2;
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }

    public float[] getCoords() {
        return new float[]{x1, x2, y1, y2};
    }
}
