package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection;

import net.minecraftforge.fml.earlydisplay.RenderElement;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.ReflectionAccessor.*;

public class RefRenderElement {
    public static final Class<?> RENDERER_CLASS = findClass("net.minecraftforge.fml.earlydisplay.RenderElement$Renderer");
    public static final Class<?> INITIALIZER_CLASS = findClass("net.minecraftforge.fml.earlydisplay.RenderElement$Initializer");
    private static final MethodHandles.Lookup lookup = privateLookup(RenderElement.class);
    private static final MethodHandle constructor = findConstructor(lookup, INITIALIZER_CLASS);
    private static final VarHandle globalAlpha = findStaticField(lookup, "globalAlpha", int.class);

    private RefRenderElement() {
        throw new AssertionError();
    }

    private static RenderElement constructor(final Supplier<?> rendererInitializer) {
        try {
            return (RenderElement) constructor.invoke(rendererInitializer);
        } catch (Throwable e) {
            throw new ReflectionException(e);
        }
    }

    public static int getGlobalAlpha() {
        return (int) globalAlpha.get();
    }

    public static void setGlobalAlpha(int alpha) {
        globalAlpha.set(alpha);
    }

    /**
     * @return creates a new {@link RenderElement RenderElement$Initializer}
     * @implNote You must initialize the texture
     * <p>
     * Example renderer implementation:
     * <code><pre>
     * ctx.elementShader().updateTextureUniform(textureNumber + INDEX_TEXTURE_OFFSET);
     * ctx.elementShader().updateRenderTypeUniform(ElementShader.RenderType.TEXTURE);
     * bb.begin(SimpleBufferBuilder.Format.POS_TEX_COLOR, bufferMode);
     * // rendering code
     * bb.draw();
     * </pre></code>
     */
    public static RenderElement constructor(final TextureRenderer renderer) {
        return constructor(proxyInitializer(renderer));
    }

    /**
     * @return {@link Supplier}&lt;{@link net.minecraftforge.fml.earlydisplay.RenderElement.Initializer}&gt;
     */
    public static Supplier<?> proxyInitializer(TextureRenderer textureRenderer) {
        return (Supplier<?>) Proxy.newProxyInstance(INITIALIZER_CLASS.getClassLoader(),
                new Class[]{INITIALIZER_CLASS},
                (proxy, method, args) -> {
                    if (method.getName().equals("get")) {
                        return proxyRenderer(textureRenderer);
                    }
                    if (!method.canAccess(proxy)) {
                        method.setAccessible(true);
                    }
                    return method.invoke(proxy, args);
                });
    }

    /**
     * @return proxied {@link net.minecraftforge.fml.earlydisplay.RenderElement.Renderer}
     * @see RendererProxy
     */
    private static Object proxyRenderer(TextureRenderer textureRenderer) {
        return Proxy.newProxyInstance(RENDERER_CLASS.getClassLoader(),
                new Class[]{RENDERER_CLASS},
                new RendererProxy(textureRenderer));
    }

}
