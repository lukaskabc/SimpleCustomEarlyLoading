package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.condition.DisplayCondition;

import java.util.List;

public abstract class Element {
    private Type type;
    private List<DisplayCondition> displayConditions = List.of();

    public Element() {
    }

    public Element(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public List<DisplayCondition> getDisplayConditions() {
        return displayConditions;
    }

    public enum Type {
        IMAGE,
        GAME_LOADING_IMAGE,
        TOTAL_LOADING_IMAGE,
    }
}
