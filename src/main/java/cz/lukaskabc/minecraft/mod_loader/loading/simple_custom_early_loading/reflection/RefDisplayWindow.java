package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection;

import net.neoforged.fml.earlydisplay.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.ReflectionAccessor.*;

/**
 * {@link ReflectionAccessor} for {@link DisplayWindow}.
 */
public class RefDisplayWindow {
    private static final MethodHandles.Lookup lookup = privateLookup(DisplayWindow.class);
    private static final MethodHandle initRender = findVirtual(lookup, "initRender", void.class, String.class, String.class);
    private static final MethodHandle fbResize = findVirtual(lookup, "fbResize", void.class, long.class, int.class, int.class);
    private static final MethodHandle renderThreadFunc = findVirtual(lookup, "renderThreadFunc", void.class);
    private static final VarHandle loadingOverlay = findField(lookup, "loadingOverlay", Method.class);
    private static final VarHandle renderScheduler = findField(lookup, "renderScheduler", ScheduledExecutorService.class);
    private static final VarHandle initializationFuture = findField(lookup, "initializationFuture", ScheduledFuture.class);
    // concrete type is ArrayList<RenderElement>
    private static final VarHandle elements = findField(lookup, "elements", List.class);
    private static final VarHandle window = findField(lookup, "window", long.class);
    private static final VarHandle colourScheme = findField(lookup, "colourScheme", ColourScheme.class);
    private static final VarHandle maximized = findField(lookup, "maximized", boolean.class);
    private static final VarHandle fbWidth = findField(lookup, "fbWidth", int.class);
    private static final VarHandle fbHeight = findField(lookup, "fbHeight", int.class);
    private static final VarHandle context = findField(lookup, "context", RenderElement.DisplayContext.class);
    private static final VarHandle framebuffer = findField(lookup, "framebuffer", EarlyFramebuffer.class);
    private static final VarHandle framecount = findField(lookup, "framecount", int.class);
    private static final VarHandle font = findField(lookup, "font", SimpleFont.class);
    private static final VarHandle renderLock = findField(lookup, "renderLock", Semaphore.class);
    private static final VarHandle windowTick = findField(lookup, "windowTick", ScheduledFuture.class);
    private static final VarHandle winWidth = findField(lookup, "winWidth", int.class);
    private static final VarHandle winHeight = findField(lookup, "winHeight", int.class);
    private final DisplayWindow target;

    public RefDisplayWindow(DisplayWindow displayWindow) {
        this.target = displayWindow;
    }

    /**
     * Sets the {@link DisplayWindow#loadingOverlay} field.
     *
     * @param loadingOverlay the new value
     */
    public void setLoadingOverlay(Method overlay) {
        loadingOverlay.set(target, overlay);
    }

    /**
     * @see DisplayWindow#initRender(String, String)
     */
    public void initRender(String mcVersion, String forgeVersion) {
        try {
            initRender.invoke(target, mcVersion, forgeVersion);
        } catch (Throwable e) {
            throw new ReflectionException(e);
        }
    }

    public ScheduledExecutorService getRenderScheduler() {
        return (ScheduledExecutorService) renderScheduler.get(target);
    }

    /**
     * Sets the {@link DisplayWindow#renderScheduler} field.
     *
     * @param renderScheduler the new value
     */
    public void setRenderScheduler(ScheduledExecutorService service) {
        renderScheduler.set(target, service);
    }

    /**
     * Sets the {@link DisplayWindow#initializationFuture} field.
     *
     * @param initializationFuture the new value
     */
    public void setInitializationFuture(ScheduledFuture<?> future) {
        initializationFuture.set(target, future);
    }

    /**
     * @return the value of the {@link DisplayWindow#elements} field
     */
    @SuppressWarnings("unchecked")
    public List<RenderElement> getElements() {
        return (List<RenderElement>) elements.get(target);
    }

    public long getGlWindow() {
        return (long) window.get(target);
    }

    public void setColourScheme(ColourScheme scheme) {
        colourScheme.set(target, scheme);
    }

    public boolean isMaximized() {
        return (boolean) maximized.get(target);
    }

    public void setFBSize(int width, int height) {
        fbWidth.set(target, width);
        fbHeight.set(target, height);
    }

    public RenderElement.DisplayContext getContext() {
        return (RenderElement.DisplayContext) context.get(target);
    }

    public void setContext(RenderElement.DisplayContext ctx) {
        context.set(target, ctx);
    }

    public void setFrameBuffer(Object fb) {
        framebuffer.set(target, fb);
    }

    public EarlyFramebuffer getFramebuffer() {
        return (EarlyFramebuffer) framebuffer.get(target);
    }

    public int getFrameCount() {
        return (int) framecount.get(target);
    }

    public SimpleFont getFont() {
        return (SimpleFont) font.get(target);
    }

    public int getFbWidth() {
        return (int) fbWidth.get(target);
    }

    public int getFbHeight() {
        return (int) fbHeight.get(target);
    }

    public Semaphore getRenderLock() {
        return (Semaphore) renderLock.get(target);
    }

    public void fbResize(final long window, final int width, final int height) {
        try {
            fbResize.invoke(target, window, width, height);
        } catch (Throwable e) {
            throw new ReflectionException(e);
        }
    }

    public ScheduledFuture<?> getWindowTick() {
        return (ScheduledFuture<?>) windowTick.get(target);
    }

    public void setWindowTick(ScheduledFuture<?> future) {
        windowTick.set(target, future);
    }

    public void renderThreadFunc() {
        try {
            renderThreadFunc.invoke(target);
        } catch (Throwable e) {
            throw new ReflectionException(e);
        }
    }

    public int getWinWidth() {
        return (int) winWidth.get(target);
    }

    public int getWinHeight() {
        return (int) winHeight.get(target);
    }
}
