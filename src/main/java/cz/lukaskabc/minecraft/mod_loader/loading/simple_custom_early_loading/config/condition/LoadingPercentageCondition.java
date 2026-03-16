package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.condition;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.helper.LoadingProgressHelper;
import net.neoforged.fml.loading.progress.ProgressMeter;
import net.neoforged.fml.loading.progress.StartupNotificationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@SuppressWarnings("unused")
public class LoadingPercentageCondition extends DisplayCondition {
    private static final Logger LOG = LogManager.getLogger();
    private boolean minimumInclusive = true;
    private int minimum;

    private boolean maximumInclusive = true;
    private int maximum;

    public LoadingPercentageCondition() {
        super(ConditionType.LOADING_PERCENTAGE);
    }

    private boolean minHolds(double progress) {
        if (minimumInclusive) {
            return progress >= minimum;
        }
        return progress > minimum;
    }

    private boolean maxHolds(double progress) {
        if (maximumInclusive) {
            return progress <= maximum;
        }
        return progress < maximum;
    }

    @Override
    public boolean shouldDisplay() {
        List<ProgressMeter> currentProgress = StartupNotificationManager.getCurrentProgress();
        currentProgress.stream().map(ProgressMeter::name).forEach(label -> LOG.info("Current loading label: {}", label));
        LOG.info("=============");
        final double mcProgress = (int) (LoadingProgressHelper.getMinecraftProgress(currentProgress) * 100f);
        return minHolds(mcProgress) && maxHolds(mcProgress);
    }
}
