package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.ObjectFieldCopier;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefDisplayWindow;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefImmediateWindowHandler;
import net.minecraftforge.fml.earlydisplay.DisplayWindow;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.SimpleCustomEarlyLoadingWindow.EXPECTED_WINDOW_PROVIDER;

public class DummyModLocator implements IModLocator {
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Constructed by {@link net.minecraftforge.fml.loading.moddiscovery.ModDiscoverer ModDiscoverer}
     * <pre><code>
     *     modLocatorList = ServiceLoaderUtils.streamServiceLoader(()-> modLocators, sce->LOGGER.error("Failed to load mod locator list", sce)).collect(Collectors.toList());
     * </code></pre>
     * At time of construction, the {@link DisplayWindow} was already constructed and initialization scheduled.
     * Since the initialization is happening asynchronously we can't assume any specific state.
     * <p>
     */
    public DummyModLocator() {
        LOG.debug("Injecting Simple Custom Early Loading");
        injectAndReplaceEarlyWindow();
        LOG.info("Injected Simple Custom Early Loading");
    }

    /**
     * Constructs {@link SimpleCustomEarlyLoadingWindow}
     * and verifies that the current {@link net.minecraftforge.fml.loading.ImmediateWindowHandler#provider ImmediateWindowHandler#provider} is an instance of {@link DisplayWindow}
     * and so it is possible to replace it with the new instance.
     */
    private static void injectAndReplaceEarlyWindow() {
        final SimpleCustomEarlyLoadingWindow newProvider = new SimpleCustomEarlyLoadingWindow();
        if (EXPECTED_WINDOW_PROVIDER.equals(RefImmediateWindowHandler.getProvider().name()) &&
                RefImmediateWindowHandler.getProvider() instanceof DisplayWindow oldProvider) {
            replaceImmediateWindowHandler(newProvider, oldProvider);
        } else {
            LOG.error("""
                            Something went really wrong!
                            Immediate window provider mismatch!
                            Expected: {}
                            Actual: {}
                            The Simple Custom Early loading can't be injected.
                            """,
                    DisplayWindow.class,
                    RefImmediateWindowHandler.getProvider().getClass());
        }
    }

    /**
     * Since we can't be sure about the initialization state of the {@link DisplayWindow} we need to synchronize with it as much as possible.
     * {@link DisplayWindow#initialize(String[])} and {@link DisplayWindow#start(String, String)} are called synchronously before initialization of {@link DummyModLocator}.
     * We are sure that {@link DisplayWindow#initializationFuture} is set - not sure about its state.
     *
     * @param newProvider
     * @param oldProvider
     */
    private static void replaceImmediateWindowHandler(SimpleCustomEarlyLoadingWindow newProvider, DisplayWindow oldProvider) {
        final RefDisplayWindow displayWindow = new RefDisplayWindow(oldProvider);
        try {
            displayWindow.getInitializationFuture().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ObjectFieldCopier.copyAllFields(RefImmediateWindowHandler.getProvider(), newProvider, DisplayWindow.class);
        RefImmediateWindowHandler.setProvider(newProvider);
        newProvider.scheduleInit();
        FMLLoader.progressWindowTick = newProvider::periodicTick;
    }

    @Override
    public List<ModFileOrException> scanMods() {
        // nothing to do in dummy implementation
        // returning immutable empty list
        return List.of();
    }

    @Override
    public String name() {
        return "SimpleCustomEarlyLoading_DummyModLocator";
    }

    @Override
    public void scanFile(IModFile modFile, Consumer<Path> pathConsumer) {
        // nothing to do in dummy implementation
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {
        // nothing to do in dummy implementation
    }

    @Override
    public boolean isValid(IModFile modFile) {
        // nothing to do in dummy implementation
        return false;
    }
}
