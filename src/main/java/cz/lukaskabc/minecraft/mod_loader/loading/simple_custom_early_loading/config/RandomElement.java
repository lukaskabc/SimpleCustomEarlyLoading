package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RandomElement extends ImageElement {
    private static final Random RANDOM = new Random();
    /**
     * the name of the field must match {@link SpecificElement#image}
     */
    private List<String> image = List.of();

    @Override
    public String getImage() {
        if (image.isEmpty()) {
            throw new ConfigurationException("No image specified");
        }
        if (image.size() == 1) {
            return image.get(0);
        }
        synchronized (this) {
            if (image.size() > 1) {
                final String image = this.image.get(RANDOM.nextInt(this.image.size()));
                this.image = List.of(image);
                return image;
            }
        }
        return image.get(0);
    }

    public void setImage(List<String> image) {
        this.image = Objects.requireNonNull(image);
    }
}
