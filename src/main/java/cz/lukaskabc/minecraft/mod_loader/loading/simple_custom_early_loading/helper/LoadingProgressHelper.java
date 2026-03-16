package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.helper;

import net.neoforged.fml.loading.progress.ProgressMeter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoadingProgressHelper {
    public static final String MINECRAFT_PROGRESS = "Minecraft Progress";
    /**
     * Known names and labels of progress meters in neoforge early loading.
     * Note that some of them occur concurrently and their order should not be considered as reliable.
     */
    public static final Set<String> EARLY_LABELS = Stream.of(
            "Scanning mod candidates",
            "Launching minecraft",
            "Loading bootstrap resources",
            "Loading mods",
            "Mod Construction",
            "Mod Construction: Deferred Queue",
            "Registry initialization",
            "Config loading",
            MINECRAFT_PROGRESS,
            "Sided setup"
    ).map(String::toLowerCase).collect(Collectors.toSet());
    public static final ProgressMeter NULL_PROGRESS_METER = new ProgressMeter(null, 1, 0, null);
    private static final Logger LOG = LogManager.getLogger();

    private LoadingProgressHelper() {
        throw new AssertionError();
    }

    /**
     * Returns the percentage progress of the minecraft loading.
     *
     * @param progressMeters current progress meters
     * @return percentage between 0 and 1, or 0 if the early loading is still in progress and Minecraft did not start loading yet.
     */
    public static float getMinecraftProgress(final List<ProgressMeter> progressMeters) {
        return progressMeters.stream().filter(meter -> MINECRAFT_PROGRESS.equals(meter.name())).findAny()
                .orElse(NULL_PROGRESS_METER)
                .progress();
    }

    public static boolean anyEarlyLabelStartsWith(String label) {
        if (label == null) {
            return false;
        }
        LOG.info("Found early label: {}", label);
        final String lowerLabel = label.toLowerCase();
        for (String earlyLabel : EARLY_LABELS) {
            if (lowerLabel.startsWith(earlyLabel)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a guessed progress of the early progress based on passed known early labels
     *
     * @return value from 0 to 1
     */
    public static float getEarlyProgress(final List<ProgressMeter> progressMeters, final Set<String> passedEarlyLabels) {
        if (passedEarlyLabels.contains(MINECRAFT_PROGRESS)) {
            return 1;
        }
        for (ProgressMeter meter : progressMeters) {
            if (!passedEarlyLabels.contains(meter.name()) && anyEarlyLabelStartsWith(meter.name())) {
                passedEarlyLabels.add(meter.name());
            } else if (!passedEarlyLabels.contains(meter.label().getText()) && anyEarlyLabelStartsWith(meter.label().getText())) {
                passedEarlyLabels.add(meter.label().getText());
            }
        }
        return (float) passedEarlyLabels.size() / EARLY_LABELS.size();
    }

    public static float getTotalProgress(final List<ProgressMeter> progressMeters, final Set<String> passedEarlyLabels, float gameLoadingRatio) {
        float earlyLoadingRatio = 1 - gameLoadingRatio;
        return getMinecraftProgress(progressMeters) * gameLoadingRatio + getEarlyProgress(progressMeters, passedEarlyLabels) * earlyLoadingRatio;
    }
}
