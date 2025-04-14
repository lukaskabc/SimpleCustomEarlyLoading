package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.element_anchor.ElementAnchor;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements.ProgressBar.BAR_HEIGHT;
import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements.ProgressBar.BAR_WIDTH;

public class StartupProgressBarConfig {
    private int barCount = 3;
    private ElementPosition position;

    public StartupProgressBarConfig() {
        position = new ElementPosition();
        position.setSizeUnit(ElementPosition.Unit.PIXELS);
        position.setWidth(BAR_WIDTH);
        position.setHeight(BAR_HEIGHT);
        position.setPositionAnchor(ElementAnchor.CENTER);
        position.setPositionUnit(ElementPosition.Unit.PERCENTAGE);
        position.setX(50);
        position.setY(50);
    }

    public int getBarCount() {
        return barCount;
    }

    public void setBarCount(int barCount) {
        this.barCount = barCount;
    }

    public ElementPosition getPosition() {
        return position;
    }

    public void setPosition(ElementPosition position) {
        this.position = position;
    }
}
