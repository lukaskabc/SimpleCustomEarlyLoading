package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading;

import cpw.mods.modlauncher.ArgumentHandler;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.ObjectFieldCopier;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefDisplayWindow;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefImmediateWindowHandler;
import net.minecraftforge.fml.earlydisplay.DisplayWindow;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import static cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.SimpleCustomEarlyLoadingWindow.EXPECTED_WINDOW_PROVIDER;

/**
 * Dummy transformation service that attempts to inject
 * {@link SimpleCustomEarlyLoadingWindow} to {@link net.minecraftforge.fml.loading.ImmediateWindowHandler#provider}
 * during construction.
 */
public class DummyTransformationService implements ITransformationService {
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Loaded and constructed by {@link cpw.mods.modlauncher.TransformationServicesHandler#discoverServices(ArgumentHandler.DiscoveryData) TransformationServicesHandler#discoverServices(ArgumentHandler.DiscoveryData)}
     * At time of construction, the {@link DisplayWindow} was already constructed and initialization scheduled.
     * Since the initialization is happening asynchronously we can't assume any specific state.
     */
    public DummyTransformationService() {
        LOG.debug("Injecting Simple Custom Early Loading");
        injectAndReplaceEarlyWindow();
    }

    /**
     * Constructs {@link SimpleCustomEarlyLoadingWindow}
     * and verifies that the current {@link net.minecraftforge.fml.loading.ImmediateWindowHandler#provider ImmediateWindowHandler#provider} is an instance of {@link DisplayWindow}
     * and so it is possible to replace it with the new instance.
     */
    private static void injectAndReplaceEarlyWindow() {
        final SimpleCustomEarlyLoadingWindow newProvider = new SimpleCustomEarlyLoadingWindow();
        if (EXPECTED_WINDOW_PROVIDER.equals(RefImmediateWindowHandler.getProvider().name()) &&
                RefImmediateWindowHandler.getProvider().getClass().equals(DisplayWindow.class)) {
            // not using instance of since we don't want any child classes, we want specifically DisplayWindow
            replaceImmediateWindowHandler(newProvider, (DisplayWindow) RefImmediateWindowHandler.getProvider());
            LOG.info("Injected Simple Custom Early Loading");
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
     * {@link DisplayWindow#initialize(String[])} and {@link DisplayWindow#start(String, String)} are called synchronously before initialization of {@link DummyTransformationService}.
     * We are sure that {@link DisplayWindow#initializationFuture} is set - not sure about its state.
     *
     * @param newProvider new provider to be set
     * @param oldProvider old provider to be replaced
     */
    private static void replaceImmediateWindowHandler(SimpleCustomEarlyLoadingWindow newProvider, DisplayWindow oldProvider) {
        final RefDisplayWindow displayWindow = new RefDisplayWindow(oldProvider);
        final ScheduledFuture<?> initializationFuture = displayWindow.getInitializationFuture();
        // await the window initialization
        try {
            initializationFuture.get();
        } catch (ExecutionException e) {
            LOG.atError().withThrowable(e).log("Early loading initialization failed");
            return;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        ObjectFieldCopier.copyAllFields(RefImmediateWindowHandler.getProvider(), newProvider, DisplayWindow.class);
        RefImmediateWindowHandler.setProvider(newProvider);
        FMLLoader.progressWindowTick = newProvider.reinitializeAfterStateCopy();
    }

    @Override
    public String name() {
        return "SimpleCustomEarlyLoading_DummyTransformationService";
    }

    @Override
    public void initialize(IEnvironment environment) {

    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) {

    }

    @Override
    public @NonNull List<ITransformer> transformers() {
        return List.of();
    }
}
