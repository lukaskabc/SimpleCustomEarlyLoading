package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading;

import net.neoforged.fml.earlydisplay.DisplayWindow;
import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.neoforgespi.earlywindow.ImmediateWindowProvider;

import javax.swing.*;

public class SimpleCustomEarlyLoadingWindow extends DisplayWindow implements ImmediateWindowProvider {
    public static final String WINDOW_PROVIDER = "SimpleCustomEarlyLoadingWindow";

    /**
     * Checks whether the FML configuration has the {@link #WINDOW_PROVIDER} set as the {@link FMLConfig.ConfigValue#EARLY_WINDOW_PROVIDER EARLY_WINDOW_PROVIDER}.
     * <p>
     * If the value does not match,
     * an error message dialog is displayed to instruct the user to update the config.
     */
    private static void checkFMLConfig() {
        final String windowProvider = FMLConfig.getConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_PROVIDER);
        if (!WINDOW_PROVIDER.equals(windowProvider)) {
            JOptionPane.showMessageDialog(null, """
                    You have installed the Simple Custom Early Loading mod,
                    but the early window provider is not set to WINDOW_PROVIDER in the fml.toml config!
                    Please update the config and restart the game.
                    See mod description for instructions.
                    """.replace("WINDOW_PROVIDER", WINDOW_PROVIDER));
            // TODO: add link
        }
    }
}
