package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

import com.google.gson.*;

import java.lang.reflect.Type;

class ElementDeserializer implements JsonDeserializer<Element> {
    private final String TYPE = "type";
    private final String IMAGE = "image";

    @Override
    public Element deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        final Element.Type type = resolveType(jsonObject);
        switch (type) {
            case IMAGE:
                return deserializeImage(jsonObject, typeOfT, context);
            case GAME_LOADING_IMAGE:
                return new GameLoadingElement(deserializeImage(jsonObject, typeOfT, context));
            case TOTAL_LOADING_IMAGE:
                return new TotalLoadingElement(deserializeImage(jsonObject, typeOfT, context));
            default:
                throw new ConfigurationException("Unknown element type: " + type);
        }
    }

    public Element.Type resolveType(JsonObject jsonObject) {
        if (jsonObject.has(TYPE)) {
            return Element.Type.valueOf(jsonObject.get(TYPE).getAsString());
        }
        if (jsonObject.has(IMAGE)) {
            jsonObject.addProperty(TYPE, Element.Type.IMAGE.name());
            return Element.Type.IMAGE;
        }
        throw new ConfigurationException("Unable to resolve type for element: " + jsonObject);
    }

    public ImageElement deserializeImage(JsonObject jsonObject, Type typeOfT, JsonDeserializationContext context) {
        JsonElement image = jsonObject.get(IMAGE);

        if (image != null && image.isJsonArray()) {
            return context.deserialize(jsonObject, RandomElement.class);
        }

        return context.deserialize(jsonObject, SpecificElement.class);
    }
}
