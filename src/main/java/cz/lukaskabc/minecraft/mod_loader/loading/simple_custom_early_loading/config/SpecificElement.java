package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

import java.util.Objects;

public class SpecificElement extends ImageElement {
    private String image = null;

    @Override
    public String getImage() {
        return Objects.requireNonNull(image);
    }

    public void setImage(String image) {
        this.image = image;
    }
}
