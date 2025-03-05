package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.Config;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.ConfigLoader;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefDisplayWindow;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefEarlyFrameBuffer;
import net.neoforged.fml.earlydisplay.ColourScheme;
import net.neoforged.fml.earlydisplay.DisplayWindow;
import net.neoforged.fml.earlydisplay.RenderElement;
import net.neoforged.fml.earlydisplay.SimpleFont;
import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.neoforgespi.earlywindow.ImmediateWindowProvider;
import org.joml.Vector2f;

import javax.swing.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class SimpleCustomEarlyLoadingWindow extends DisplayWindow implements ImmediateWindowProvider {
    public static final String WINDOW_PROVIDER = "SimpleCustomEarlyLoadingWindow";
    private static Vector2f center = new Vector2f(1, 1);
    private final RefDisplayWindow accessor;
    private final Config configuration;

    public SimpleCustomEarlyLoadingWindow() {
        this.accessor = new RefDisplayWindow(this);
        configuration = ConfigLoader.loadConfiguration();
        checkFMLConfig();
    }

    /**
     * Checks whether the FML configuration has the {@link #WINDOW_PROVIDER} set as the {@link FMLConfig.ConfigValue#EARLY_WINDOW_PROVIDER EARLY_WINDOW_PROVIDER}.
     * <p>
     * If the value does not match,
     * an error message dialog is displayed to instruct the user to update the config.
     */
    private static void checkFMLConfig() {
        final String windowProvider = FMLConfig.getConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_PROVIDER);
        if (!WINDOW_PROVIDER.equals(windowProvider)) {
            // TODO maybe update the config?
            JOptionPane.showMessageDialog(null, """
                    You have installed the Simple Custom Early Loading mod,
                    but the early window provider is not set to WINDOW_PROVIDER in the fml.toml config!
                    Please update the config and restart the game.
                    See mod description for instructions.
                    https://github.com/lukaskabc/SimpleCustomEarlyLoading
                    """.replace("WINDOW_PROVIDER", WINDOW_PROVIDER));
        }
    }

    /**
     * Sets the center position of the frame buffer based on provided coordinates.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    public static void setCenter(final int x, final int y) {
        center.set(x, y);
    }

    /**
     * Constructs the elements to be rendered in the loading window.
     *
     * @param mcVersion    The Minecraft version.
     * @param forgeVersion The Forge version.
     * @param elements     The list where render elements will be added.
     */
    private void constructElements(String mcVersion, String forgeVersion, final List<RenderElement> elements) {
        final SimpleFont font = accessor.getFont();

        Optional.ofNullable(configuration.getElements()).ifPresent(list -> {
            list.forEach(element -> {
                // TODO: create render element
            });
        });

        Optional.ofNullable(configuration.getProgressBar()).ifPresent(bar -> {
            // TODO
        });

        // from forge early loading:
        if (configuration.isPerformanceBar()) {
            // top middle memory info
            elements.add(RenderElement.performanceBar(font));
        }

        if (configuration.isFox()) {
            elements.add(RenderElement.fox(font));
        }

        if (configuration.isLogMessages()) {
            // bottom left log messages
            elements.add(RenderElement.logMessageOverlay(font));
        }

        if (configuration.isForgeVersion()) {
            // bottom right game version
            elements.add(RenderElement.forgeVersionOverlay(font, mcVersion + "-" + forgeVersion.split("-")[0]));
        }
    }

    /**
     * @return The window provider name.
     */
    @Override
    public String name() {
        return WINDOW_PROVIDER;
    }

    /**
     * Calls the super method and sets the colour scheme to black.
     *
     * @param arguments The arguments provided to the Java process.
     *                  This is the entire command line, so you can process stuff from it.
     * @return result of the super method
     * @see DisplayWindow#initialize(String[])
     */
    @Override
    public Runnable initialize(String[] arguments) {
        final Runnable result = super.initialize(arguments);
        // force black colour scheme
        accessor.setColourScheme(ColourScheme.BLACK);
        return result;
    }

    /**
     * Reimplements the super method {@link DisplayWindow#start(String, String)}<br>
     * and injects {@link #afterInitRender(String, String)}<br>
     * that is called after {@link DisplayWindow#initRender(String, String)}
     * <p>
     * Starts the loading window rendering process.
     * <p>
     * Schedules the window's rendering initialization and sets up a periodic tick.
     *
     * @param mcVersion    The Minecraft version.
     * @param forgeVersion The Forge version.
     * @return A Runnable responsible for the periodic tick.
     * @see DisplayWindow#start(String, String)
     */
    @Override
    public Runnable start(String mcVersion, String forgeVersion) {
        final ScheduledExecutorService renderScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            final Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        });
        accessor.setRenderScheduler(renderScheduler);
        initWindow(mcVersion);
        final var initializationFuture = renderScheduler.schedule(() -> {
            accessor.initRender(mcVersion, forgeVersion);
            afterInitRender(mcVersion, forgeVersion);
        }, 1, TimeUnit.MILLISECONDS);
        accessor.setInitializationFuture(initializationFuture);
        return this::periodicTick;
    }

    /**
     * Performs post-render initialization.
     * <p>
     * Establishes the OpenGL context, recreates the render context, and constructs render elements.
     *
     * @param mcVersion    The Minecraft version.
     * @param forgeVersion The Forge version.
     * @implNote The method is called after init render is called inside the same schedule.
     * Since the scheduler is single threaded there is no possibility for race condition
     * with other scheduled tasks.
     */
    private void afterInitRender(String mcVersion, String forgeVersion) {
        glfwMakeContextCurrent(accessor.getGlWindow());
        recreateContext();
        final List<RenderElement> elements = accessor.getElements();
        elements.clear();
        constructElements(mcVersion, forgeVersion, elements);
        glfwMakeContextCurrent(0);
    }

    /**
     * Reimplementation of the superclass method using the module of this class.
     * <p>
     * Updates the module reads, adding a read edge to the 'neoforge' module.
     * <p>
     * Uses reflection to obtain the loading overlay instance method.
     *
     * @param layer The ModuleLayer from which modules are read.
     * @see DisplayWindow#updateModuleReads(ModuleLayer)
     */
    @Override
    public void updateModuleReads(final ModuleLayer layer) {
        var fm = layer.findModule("neoforge").orElseThrow();
        getClass().getModule().addReads(fm);
        var clz = Class.forName(fm, "net.neoforged.neoforge.client.loading.NeoForgeLoadingOverlay");
        var methods = Arrays.stream(clz.getMethods()).filter(m -> Modifier.isStatic(m.getModifiers())).collect(Collectors.toMap(Method::getName, Function.identity()));
        accessor.setLoadingOverlay(methods.get("newInstance"));
    }

    /**
     * Adds the Mojang logo to the loading screen.
     *
     * @param textureId The texture id of the Mojang logo.
     */
    @Override
    public void addMojangTexture(int textureId) {
        accessor.getElements().addLast(RenderElement.mojang(textureId, accessor.getFrameCount()));
    }

    /**
     * Recreates the rendering context overriding the frame buffer resolution set by the original DisplayWindow implementation.
     * <p>
     * Updates the frame buffer size according to OpenGL window frame buffer size,
     * creates a new display context, sets up a new frame buffer,
     * and updates the center position based on the scaled context dimensions.
     * <p>
     * The old frame buffer is closed to release the resources.
     */
    private void recreateContext() {
        final RenderElement.DisplayContext oldContext = accessor.getContext();
        final Object oldFrameBuffer = accessor.getFramebuffer();
        final int[] width = new int[1];
        final int[] height = new int[1];
        glfwGetFramebufferSize(accessor.getGlWindow(), width, height);
        accessor.setFBSize(width[0], height[0]);
        final RenderElement.DisplayContext context = new RenderElement.DisplayContext(
                width[0],
                height[0],
                oldContext.scale(),
                oldContext.elementShader(),
                oldContext.colourScheme(),
                oldContext.performance()
        );
        accessor.setContext(context);
        accessor.setFrameBuffer(RefEarlyFrameBuffer.constructor(context));
        setCenter(context.scaledWidth() / 2, context.scaledHeight() / 2);
        RefEarlyFrameBuffer.close(oldFrameBuffer);
    }
}
