package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.GameLoadingElement;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.helper.LoadingProgressHelper;
import net.neoforged.fml.loading.progress.ProgressMeter;
import net.neoforged.fml.loading.progress.StartupNotificationManager;

import java.util.List;

public class MinecraftLoadingApngTextureElement extends ApngTextureElement {
    protected int currentFrame = 0;

    public MinecraftLoadingApngTextureElement(GameLoadingElement element) {
        super(element.getDelegate());
    }

    @Override
    protected boolean nextFrame() {
        final List<ProgressMeter> progressMeters = StartupNotificationManager.getCurrentProgress();
        final int currentFrameTarget = (int) (LoadingProgressHelper.getMinecraftProgress(progressMeters) * apngTexture.getFrameCount());
        while (currentFrameTarget > currentFrame) {
            apngTexture.nextFrame();
            currentFrame++;
        }
        return false;
    }
}
