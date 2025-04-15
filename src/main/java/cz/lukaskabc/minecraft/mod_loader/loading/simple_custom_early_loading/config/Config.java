package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

import org.jspecify.annotations.NullUnmarked;

import java.util.ArrayList;
import java.util.List;

@NullUnmarked
public class Config {
    private List<Element> elements = new ArrayList<>(0);
    private StartupProgressBarConfig progressBar = null;
    private boolean performanceBar = true;
    private boolean fox = true;
    private boolean logMessages = true;
    private boolean forgeVersion = true;

    private int resolutionWidth = 0;
    private int resolutionHeight = 0;

    private float originalElementScale = 1f;

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public StartupProgressBarConfig getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(StartupProgressBarConfig progressBar) {
        this.progressBar = progressBar;
    }

    public boolean isPerformanceBar() {
        return performanceBar;
    }

    public void setPerformanceBar(boolean performanceBar) {
        this.performanceBar = performanceBar;
    }

    public boolean isFox() {
        return fox;
    }

    public void setFox(boolean fox) {
        this.fox = fox;
    }

    public boolean isLogMessages() {
        return logMessages;
    }

    public void setLogMessages(boolean logMessages) {
        this.logMessages = logMessages;
    }

    public boolean isForgeVersion() {
        return forgeVersion;
    }

    public void setForgeVersion(boolean forgeVersion) {
        this.forgeVersion = forgeVersion;
    }

    public int getResolutionWidth() {
        return resolutionWidth;
    }

    public void setResolutionWidth(int resolutionWidth) {
        this.resolutionWidth = resolutionWidth;
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }

    public void setResolutionHeight(int resolutionHeight) {
        this.resolutionHeight = resolutionHeight;
    }

    public float getOriginalElementScale() {
        return originalElementScale;
    }

    public void setOriginalElementScale(float originalElementScale) {
        this.originalElementScale = originalElementScale;
    }
}
