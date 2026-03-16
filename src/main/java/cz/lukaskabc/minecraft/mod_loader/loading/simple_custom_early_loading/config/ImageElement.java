package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

public abstract class ImageElement extends Element {
    private ElementPosition position;

    public ImageElement() {
    }

    public ImageElement(Type type) {
        super(type);
    }

    public ElementPosition getPosition() {
        return position;
    }

    public void setPosition(ElementPosition position) {
        this.position = position;
    }

    public String getExtension() {
        final String image = getImage();
        if (image == null) {
            throw new ConfigurationException("No image specified");
        }
        int index = image.lastIndexOf('.');
        if (index == -1) {
            throw new ConfigurationException("No extension specified for file " + image);
        }
        return image.substring(index + 1);
    }

    public abstract String getImage();
}
