package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

public abstract class Element {
    private Type type;

    public Type getType() {
        return type;
    }

    public enum Type {
        IMAGE
    }
}
