package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.TotalLoadingElement;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.helper.LoadingProgressHelper;
import net.neoforged.fml.loading.progress.ProgressMeter;
import net.neoforged.fml.loading.progress.StartupNotificationManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TotalLoadingApngTextureElement extends ApngTextureElement {
    private static final float GAME_LOADING_RATIO = 0.35f;
    protected final Set<String> earlyLabels = new HashSet<>();
    protected int currentFrame = 0;

    public TotalLoadingApngTextureElement(TotalLoadingElement element) {
        super(element.getDelegate());
    }

    @Override
    protected boolean nextFrame() {
        final List<ProgressMeter> progressMeters = StartupNotificationManager.getCurrentProgress();
        final int currentFrameTarget = (int) (LoadingProgressHelper.getTotalProgress(progressMeters, earlyLabels, GAME_LOADING_RATIO) * apngTexture.getFrameCount());
        while (currentFrameTarget > currentFrame) {
            apngTexture.nextFrame();
            currentFrame++;
        }
        return false;
    }
}
