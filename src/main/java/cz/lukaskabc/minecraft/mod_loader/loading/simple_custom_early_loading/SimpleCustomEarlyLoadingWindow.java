package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.Config;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.ConfigLoader;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.ConfigurationException;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.Element;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements.ApngTextureElement;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements.StartupProgressBar;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements.StaticTextureElement;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefDisplayWindow;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefEarlyFrameBuffer;
import net.minecraftforge.fml.earlydisplay.ColourScheme;
import net.minecraftforge.fml.earlydisplay.DisplayWindow;
import net.minecraftforge.fml.earlydisplay.RenderElement;
import net.minecraftforge.fml.earlydisplay.SimpleFont;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.ImmediateWindowProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.glClearColor;

public class SimpleCustomEarlyLoadingWindow extends DisplayWindow implements ImmediateWindowProvider {
    public static final String EXPECTED_WINDOW_PROVIDER = "fmlearlywindow";
    private static final Logger LOG = LogManager.getLogger();
    private final RefDisplayWindow accessor;
    private final Config configuration;

    public SimpleCustomEarlyLoadingWindow() {
        this.accessor = new RefDisplayWindow(this);
        checkFMLConfig();
        ConfigLoader.copyDefaultConfig();
        configuration = ConfigLoader.loadConfiguration();
    }

    /**
     * Checks whether the FML configuration has the {@link #EXPECTED_WINDOW_PROVIDER} set as the {@link FMLConfig.ConfigValue#EARLY_WINDOW_PROVIDER EARLY_WINDOW_PROVIDER}.
     * <p>
     * If the value does not match,
     * an error message dialog is displayed to instruct the user to update the config.
     */
    private static void checkFMLConfig() {
        final String windowProvider = FMLConfig.getConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_PROVIDER);
        if (!EXPECTED_WINDOW_PROVIDER.equals(windowProvider)) {
            // Create a parent frame that will appear in the taskbar
            final JFrame frame = new JFrame("Missing Forge configuration");
            frame.setAlwaysOnTop(true);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);

            // question is how much reliable this is
            final int answer = JOptionPane.showConfirmDialog(frame, """
                            You have installed the Simple Custom Early Loading mod,
                            but the early window provider is not set to WINDOW_PROVIDER in the fml.toml config!
                            Please update the config and restart the game.
                            See mod description for instructions.
                            https://github.com/lukaskabc/SimpleCustomEarlyLoading
                            
                            Do you wish to update the config?
                            Answering yes will update the config and exit the game.
                            """.replace("WINDOW_PROVIDER", EXPECTED_WINDOW_PROVIDER),
                    "Missing NeoForge configuration",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            frame.dispose();

            if (answer == JOptionPane.YES_OPTION) {
                FMLConfig.updateConfig(FMLConfig.ConfigValue.EARLY_WINDOW_PROVIDER, EXPECTED_WINDOW_PROVIDER);
                System.exit(0);
            }
        }
    }

    private Supplier<RenderElement> constructElement(Element element) {
        // yes, sure, I could use abstract factories, but lets keep it simple
        if (ApngTextureElement.SUPPORTED_EXTENSIONS.contains(element.getExtension())) {
            return new ApngTextureElement(element.getImage(), element.getPosition());
        }
        if (StaticTextureElement.SUPPORTED_EXTENSIONS.contains(element.getExtension())) {
            return new StaticTextureElement(element.getImage(), element.getPosition());
        }
        throw new ConfigurationException("Unsupported image extension: " + element.getExtension());
    }

    /**
     * Constructs the elements to be rendered in the loading window.
     *
     * @param elements The list where render elements will be added.
     */
    private void constructElements(final List<RenderElement> elements) {
        final SimpleFont font = accessor.getFont();

        Optional.ofNullable(configuration.getElements()).ifPresent(list -> {
            list.forEach(el ->
                    elements.add(constructElement(el).get())
            );
        });

        Optional.ofNullable(configuration.getProgressBar()).ifPresent(barConfig -> {
            elements.add(new StartupProgressBar(font, barConfig).get());
        });

        // from forge early loading:
        if (configuration.isPerformanceBar()) {
            // top middle memory info
            elements.add(RenderElement.performanceBar(font));
        }

        if (configuration.isAnvil()) {
            elements.add(RenderElement.anvil(font));
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
        return EXPECTED_WINDOW_PROVIDER;
    }

    /**
     * In Forge context should never be called.
     *
     * @throws AssertionError always
     * @see #reinitializeAfterStateCopy()
     */
    @Override
    public Runnable initialize(String[] arguments) {
        throw new AssertionError("The Simple Custom Early Loading Window should not be initialized in Forge context!");
    }

    /**
     * In Forge context should never be called.
     *
     * @throws AssertionError always
     * @see #reinitializeAfterStateCopy()
     */
    @Override
    public Runnable start(String mcVersion, String forgeVersion) {
        throw new AssertionError("The Simple Custom Early Loading Window should not be initialized in Forge context!");
    }

    public Runnable reinitializeAfterStateCopy() {
        // from initialize method
        accessor.setColourScheme(ColourScheme.BLACK);
        // from start method
        final var future = accessor.getRenderScheduler().schedule(() -> {
            try {
                accessor.getRenderLock().acquire(); // block rendering
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            // from initWindow
            // rebind callbacks to the new instance
            glfwSetFramebufferSizeCallback(accessor.getGlWindow(), accessor::fbResize);
            glfwSetWindowPosCallback(accessor.getGlWindow(), accessor::winMove);
            glfwSetWindowSizeCallback(accessor.getGlWindow(), accessor::winResize);

            // cancel the old window tick
            if (accessor.getWindowTick() != null) {
                while (!accessor.getWindowTick().isDone()) {
                    accessor.getWindowTick().cancel(false);
                }
            } else {
                // TODO: this is potentially a problem, if the window tick was null,
                //  then the window was already handed over to the game
                LOG.error("Early window was already handed over to the game - that was fast! Aborting Simple Custom Early Loading initialization.");
                return;
            }
            // from initRender
            accessor.setWindowTick(accessor.getRenderScheduler().scheduleAtFixedRate(accessor::renderThreadFunc, 50, 50, TimeUnit.MILLISECONDS));
            accessor.getRenderScheduler().scheduleAtFixedRate(() -> accessor.getAnimationTimerTrigger().set(true), 1, 50, TimeUnit.MILLISECONDS);
            afterInitRender();
            accessor.getRenderLock().release();
        }, 1, TimeUnit.MILLISECONDS);
        accessor.setInitializationFuture(future);
        return this::periodicTick;
    }

    /**
     * Performs post-render initialization.
     * <p>
     * Establishes the OpenGL context, recreates the render context, and constructs render elements.
     *
     * @implNote The method is called after init render is called inside the same schedule.
     * Since the scheduler is single threaded there is no possibility for race condition
     * with other scheduled tasks.
     */
    public void afterInitRender() {
        glfwMakeContextCurrent(accessor.getGlWindow());
        // Set the clear color based on the colour scheme
        final ColourScheme colourScheme = accessor.getColourScheme();
        glClearColor(colourScheme.background().redf(), colourScheme.background().greenf(), colourScheme.background().bluef(), 1f);
        recreateContext();
        final List<RenderElement> elements = accessor.getElements();
        constructElements(elements);
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
        var fm = layer.findModule("forge").orElseThrow();
        getClass().getModule().addReads(fm);
        var clz = FMLLoader.getGameLayer().findModule("forge").map(l -> Class.forName(l, "net.minecraftforge.client.loading.ForgeLoadingOverlay")).orElseThrow();
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
        accessor.getElements().add(RenderElement.mojang(textureId, accessor.getFrameCount()));
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
        if (configuration.getResolutionWidth() > 0 && configuration.getResolutionHeight() > 0) {
            width[0] = configuration.getResolutionWidth();
            height[0] = configuration.getResolutionHeight();
        } else {
            glfwGetFramebufferSize(accessor.getGlWindow(), width, height);
        }

        accessor.setFBSize(width[0], height[0]);
        final RenderElement.DisplayContext context = new RenderElement.DisplayContext(
                width[0],
                height[0],
                oldContext.scale(),
                oldContext.elementShader(),
                accessor.getColourScheme(),
                oldContext.performance()
        );
        accessor.setContext(context);
        accessor.setFrameBuffer(RefEarlyFrameBuffer.constructor(context));
        RefEarlyFrameBuffer.close(oldFrameBuffer);
    }
}
