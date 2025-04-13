package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

public class Element {
    private String image = null;
    private ElementPosition position;

    public String getExtension() {
        if (image == null) {
            throw new ConfigurationException("No image specified");
        }
        int index = image.lastIndexOf('.');
        if (index == -1) {
            throw new ConfigurationException("No extension specified for file " + image);
        }
        return image.substring(index + 1);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ElementPosition getPosition() {
        return position;
    }

    public void setPosition(ElementPosition position) {
        this.position = position;
    }
}
