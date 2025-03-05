package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

import org.jspecify.annotations.NullUnmarked;

import java.util.ArrayList;
import java.util.List;

@NullUnmarked
public class Config {
    private List<Element> elements = new ArrayList<>(0);
    private ProgressBarConfig progressBar = null;
    private boolean performanceBar = true;
    private boolean fox = true;
    private boolean logMessages = true;
    private boolean forgeVersion = true;

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public ProgressBarConfig getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBarConfig progressBar) {
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
}
