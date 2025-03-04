package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private List<Element> elements = new ArrayList<>(0);

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }
}
