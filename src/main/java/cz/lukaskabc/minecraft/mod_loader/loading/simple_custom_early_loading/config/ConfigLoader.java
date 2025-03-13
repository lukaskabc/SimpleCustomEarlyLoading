package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoader {
    private static final String STARGATE_VARIANT_CONFIG_DIRECTORY = "simple-custom-early-loading";
    private static final String DEFAULT_CONFIG_FILE = "/default_config.json";
    private static final String CONFIG_FILE_NAME = "simple-custom-early-loading.json";
    private static final Logger LOG = LogManager.getLogger();

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    private ConfigLoader() {
        throw new AssertionError();
    }

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get().resolve(STARGATE_VARIANT_CONFIG_DIRECTORY);
    }

    private static Path getConfigFilePath() {
        return FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE_NAME).toAbsolutePath();
    }

    /**
     * If the configuration file does not exist, it's created by copying the default config.
     */
    public static void copyDefaultConfig() {
        if (!Files.exists(getConfigFilePath())) {
            LOG.atDebug().log("Creating default config file {}", getConfigFilePath());
            try (final InputStream defaultConfig = ConfigLoader.class.getResourceAsStream(DEFAULT_CONFIG_FILE)) {
                if (defaultConfig == null) {
                    throw new ConfigurationException("Could not find default config file: " + DEFAULT_CONFIG_FILE);
                }
                Files.copy(defaultConfig, getConfigFilePath());
                LOG.atDebug().log("Config file successfully created");
            } catch (IOException e) {
                LOG.atError().log("Failed to create default config file: {}", e.getMessage());
                throw new ConfigurationException(e);
            }
        }
    }

    /**
     * Loads the config file
     *
     * @return loaded config
     */
    public static Config loadConfiguration() {
        LOG.atDebug().log("Loading configuration from {}", getConfigFilePath());
        try {
            return GSON.fromJson(new FileReader(getConfigFilePath().toFile()), Config.class);
        } catch (JsonParseException | FileNotFoundException e) {
            LOG.atError().log("Failed to load configuration from {}\nError:{}", getConfigFilePath(), e.getMessage());
            throw new ConfigurationException(e);
        }
    }

    /**
     * Tries to resolve file inside {@link #getConfigDirectory()} otherwise resolves file from resources on classpath
     */
    public static InputStream resolveFile(Path path) throws FileNotFoundException {
        if (path.startsWith("/") || path.startsWith("\\")) {
            path = Path.of(path.toString().substring(1));
        }
        final Path configDir = getConfigDirectory().resolve(path);
        if (Files.exists(configDir)) {
            return new FileInputStream(configDir.toFile());
        }

        String classPath = "/" + path.toString().replace('\\', '/');
        final InputStream result = ConfigLoader.class.getResourceAsStream(classPath);
        if (result == null) {
            throw new FileNotFoundException(configDir.toString());
        }
        return result;
    }
}
