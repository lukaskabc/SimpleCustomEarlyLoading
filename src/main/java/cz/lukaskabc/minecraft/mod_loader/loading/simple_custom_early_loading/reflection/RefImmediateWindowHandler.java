package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection;

import net.minecraftforge.fml.loading.ImmediateWindowHandler;
import net.minecraftforge.fml.loading.ImmediateWindowProvider;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.ReflectionAccessor.findStaticField;
import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.ReflectionAccessor.privateLookup;

public class RefImmediateWindowHandler {
    private static final MethodHandles.Lookup lookup = privateLookup(ImmediateWindowHandler.class);
    private static final VarHandle provider = findStaticField(lookup, "provider", ImmediateWindowProvider.class);

    private RefImmediateWindowHandler() {
        throw new AssertionError();
    }

    public static ImmediateWindowProvider getProvider() {
        return (ImmediateWindowProvider) provider.get();
    }

    public static void setProvider(ImmediateWindowProvider provider) {
        RefImmediateWindowHandler.provider.set(provider);
    }
}
