package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.condition;

import net.neoforged.fml.loading.progress.ProgressMeter;
import net.neoforged.fml.loading.progress.StartupNotificationManager;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @see cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.helper.LoadingProgressHelper#EARLY_LABELS
 * @see cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.helper.LoadingProgressHelper#MINECRAFT_PROGRESS
 */
@SuppressWarnings("unused")
public class LoadingStageCondition extends DisplayCondition {
    private List<String> stages;

    public LoadingStageCondition() {
        super(ConditionType.LOADING_STAGE);
    }

    private static String nameOrEmpty(ProgressMeter meter) {
        if (meter == null || meter.name() == null) {
            return "";
        }
        return meter.name();
    }

    private static String labelOrEmpty(ProgressMeter meter) {
        if (meter == null || meter.label() == null || meter.label().getText() == null) {
            return "";
        }
        return meter.label().getText();
    }

    @Override
    public boolean shouldDisplay() {
        Set<String> labels = StartupNotificationManager.getCurrentProgress().stream()
                .flatMap(meter -> Stream.of(nameOrEmpty(meter), labelOrEmpty(meter)))
                .filter(Objects::nonNull).collect(Collectors.toSet());

        for (String stage : stages) {
            if (labels.contains(stage)) {
                return true;
            }
        }

        return false;
    }

    public List<String> getStages() {
        return stages;
    }
}
