package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection;

import net.minecraftforge.fml.earlydisplay.SimpleFont;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.ReflectionAccessor.findField;
import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.ReflectionAccessor.privateLookup;

public class RefSimpleFont {
    private static final MethodHandles.Lookup lookup = privateLookup(SimpleFont.class);
    private static final VarHandle lineSpacing = findField(lookup, "lineSpacing", int.class);
    private static final VarHandle textureNumber = findField(lookup, "textureNumber", int.class);
    private static final VarHandle descent = findField(lookup, "descent", int.class);
    private final SimpleFont target;

    public RefSimpleFont(SimpleFont target) {
        this.target = target;
    }

    public int lineSpacing() {
        return (int) lineSpacing.get(target);
    }

    public int textureNumber() {
        return (int) textureNumber.get(target);
    }

    public int descent() {
        return (int) descent.get(target);
    }
}
