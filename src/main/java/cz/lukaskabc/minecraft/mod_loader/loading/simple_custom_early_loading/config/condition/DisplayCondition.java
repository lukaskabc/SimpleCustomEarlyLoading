package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.condition;


public abstract class DisplayCondition {
    private final ConditionType conditionType;

    public DisplayCondition(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public abstract boolean shouldDisplay();

    public ConditionType getConditionType() {
        return conditionType;
    }
}
