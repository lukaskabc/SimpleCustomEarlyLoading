package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection;


import net.minecraftforge.fml.earlydisplay.RenderElement;
import net.minecraftforge.fml.earlydisplay.SimpleBufferBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RendererProxy implements InvocationHandler {
    private static final Logger LOG = LogManager.getLogger();
    private final TextureRenderer textureRenderer;

    public RendererProxy(TextureRenderer textureRenderer) {
        this.textureRenderer = textureRenderer;
    }

    @Override
    public Object invoke(Object renderProxy, Method renderMethod, Object[] renderArgs) throws Throwable {
        if (renderMethod.getName().equals("accept")) {
            final SimpleBufferBuilder bb = (SimpleBufferBuilder) renderArgs[0];
            final RenderElement.DisplayContext ctx = (RenderElement.DisplayContext) renderArgs[1];
            final int frame = (int) renderArgs[2];
            try {
                textureRenderer.accept(new CSB(ctx, bb), frame);
            } catch (Throwable t) {
                /**
                 * The problem is that DisplayWindow for whatever reason catch any throwable from rendering
                 * and just silently logs it, which is very very bad
                 * This will log it and then exit the application immediately
                 */
                LOG.error("Early loading rendering exception", t);
                System.exit(1);
                LOG.error("An exception occurred during early loading rendering, the game will exit immediately. No crash report will be generated!");
                throw new RuntimeException(t);
            }
            return null; // void returning method
        }
        if (!renderMethod.canAccess(renderProxy)) {
            renderMethod.setAccessible(true);
        }
        return renderMethod.invoke(renderProxy, renderArgs);
    }
}
