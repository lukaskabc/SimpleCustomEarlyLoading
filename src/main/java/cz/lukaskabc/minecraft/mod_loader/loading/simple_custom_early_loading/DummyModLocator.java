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

public class DummyModLocator implements IModLocator {
    private static final Logger LOG = LogManager.getLogger();

    public DummyModLocator() {
        // TODO: assert dummyprovider, otherwise show error pop up
        final SimpleCustomEarlyLoadingWindow provider = new SimpleCustomEarlyLoadingWindow();
        final RefDisplayWindow oldProvider = new RefDisplayWindow((DisplayWindow) RefImmediateWindowHandler.getProvider());
        try {
            oldProvider.getInitializationFuture().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ObjectFieldCopier.copyAllFields(RefImmediateWindowHandler.getProvider(), provider, DisplayWindow.class);
        RefImmediateWindowHandler.setProvider(provider);
        provider.scheduleInit();
        FMLLoader.progressWindowTick = provider::periodicTick;
        LOG.info("Injected Simple Custom Early Loading");
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
