package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection;

import net.neoforged.fml.earlydisplay.PerformanceInfo;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.ReflectionAccessor.*;

public class RefPerformanceInfo {
    private static final MethodHandles.Lookup lookup = privateLookup(PerformanceInfo.class);
    private static final VarHandle text = findField(lookup, "text", String.class);
    private static final VarHandle memory = findField(lookup, "memory", float.class);

    private RefPerformanceInfo() {
        throw new AssertionError();
    }

    public static PerformanceInfo create() {
        try {
            return (PerformanceInfo) findConstructor(lookup).invoke();
        } catch (Throwable e) {
            throw new ReflectionException(e);
        }
    }

    public String text(PerformanceInfo target) {
        return (String) text.get(target);
    }

    public float memory(PerformanceInfo target) {
        return (float) memory.get(target);
    }
}
