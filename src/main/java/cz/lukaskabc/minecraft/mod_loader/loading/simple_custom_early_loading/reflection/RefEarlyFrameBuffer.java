package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection;

import net.neoforged.fml.earlydisplay.RenderElement;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.ReflectionAccessor.*;

public class RefEarlyFrameBuffer {
    private static final Class<?> FRAME_BUFFER_CLASS = findClass("net.neoforged.fml.earlydisplay.EarlyFramebuffer");
    private static final MethodHandles.Lookup lookup = privateLookup(FRAME_BUFFER_CLASS);
    private static final MethodHandle constructor = findConstructor(lookup, RenderElement.DisplayContext.class);
    private static final MethodHandle close = findVirtual(lookup, "close", void.class);

    private RefEarlyFrameBuffer() {
        throw new AssertionError();
    }

    /**
     * @return {@link net.neoforged.fml.earlydisplay.EarlyFramebuffer EarlyFramebuffer}
     */
    public static Object constructor(RenderElement.DisplayContext context) {
        try {
            return constructor.invoke(context);
        } catch (Throwable e) {
            throw new ReflectionException(e);
        }
    }

    public static void close(Object earlyFramebuffer) {
        try {
            close.invoke(earlyFramebuffer);
        } catch (Throwable e) {
            throw new ReflectionException(e);
        }
    }
}
