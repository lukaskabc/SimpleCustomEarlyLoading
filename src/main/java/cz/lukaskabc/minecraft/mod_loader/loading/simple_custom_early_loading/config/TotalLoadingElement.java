package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

public class TotalLoadingElement extends DelegatingElement<ImageElement> {
    public TotalLoadingElement(ImageElement delegate) {
        super(Type.TOTAL_LOADING_IMAGE, delegate);
    }
}
