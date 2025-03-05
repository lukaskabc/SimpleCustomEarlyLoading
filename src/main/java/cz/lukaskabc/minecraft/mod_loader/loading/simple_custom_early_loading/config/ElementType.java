package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

import com.google.gson.annotations.SerializedName;

public enum ElementType {
    @SerializedName(value = "absolute", alternate = {"ABSOLUTE", ""})
    ABSOLUTE,
    @SerializedName(value = "percentage", alternate = {"PERCENTAGE", "%"})
    PERCENTAGE,
}
