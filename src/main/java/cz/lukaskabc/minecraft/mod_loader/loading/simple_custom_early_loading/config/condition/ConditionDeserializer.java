package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.condition;

import com.google.gson.*;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.ConfigurationException;

import java.lang.reflect.Type;

public class ConditionDeserializer implements JsonDeserializer<DisplayCondition> {
    public static ConditionType resolveType(JsonObject jsonObject) {
        if (jsonObject.has("type")) {
            return ConditionType.valueOf(jsonObject.get("type").getAsString());
        }
        throw new ConfigurationException("Unable to resolve type for condition: " + jsonObject);
    }

    @Override
    public DisplayCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        final ConditionType conditionType = resolveType(jsonObject);
        switch (conditionType) {
            case LOADING_STAGE:
                return context.deserialize(jsonObject, LoadingStageCondition.class);
            case LOADING_PERCENTAGE:
                return context.deserialize(jsonObject, LoadingPercentageCondition.class);
            default:
                throw new ConfigurationException("Unknown ConditionType " + conditionType);
        }
    }
}
