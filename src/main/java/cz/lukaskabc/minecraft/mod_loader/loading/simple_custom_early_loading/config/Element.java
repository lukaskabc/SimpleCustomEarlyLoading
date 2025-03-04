package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

public class Element {
    private String image = null;
    private ElementType type = ElementType.PIXELS;
    private double x = 0;
    private double y = 0;

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

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
