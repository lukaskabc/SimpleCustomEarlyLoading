package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.condition.DisplayCondition;

import java.util.List;

public abstract class DelegatingElement<E extends Element> extends Element {
    private final E delegate;

    public DelegatingElement(Element.Type type, E delegate) {
        super(type);
        this.delegate = delegate;
    }

    @Override
    public List<DisplayCondition> getDisplayConditions() {
        throw new UnsupportedOperationException("DelegatingElement does not support display conditions. Use the delegate element for that.");
    }

    public E getDelegate() {
        return delegate;
    }
}