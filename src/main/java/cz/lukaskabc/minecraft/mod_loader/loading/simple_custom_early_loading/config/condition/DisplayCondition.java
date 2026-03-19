package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.condition;


public abstract class DisplayCondition {
    private final ConditionType conditionType;
    private boolean negate = false;
    private boolean holdAfterTrigger = false;
    private boolean active = false;

    public DisplayCondition(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    protected abstract boolean shouldDisplayImpl();

    public boolean shouldDisplay() {
        boolean result = negate != shouldDisplayImpl();
        if (result && holdAfterTrigger) {
            active = true;
        }
        return result || active;
    }


    public ConditionType getConditionType() {
        return conditionType;
    }
}
