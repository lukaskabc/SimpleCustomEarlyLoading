package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

public class GameLoadingElement extends DelegatingElement<ImageElement> {
    public GameLoadingElement(ImageElement delegate) {
        super(Type.GAME_LOADING_IMAGE, delegate);
    }
}
