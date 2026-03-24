package com.quickchest.config;

import com.quickchest.QuickChestMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class QuickChestConfig {

    private static final Path CONFIG_PATH =
        FabricLoader.getInstance().getConfigDir().resolve("quickchest.properties");

    private static boolean enabled = true;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }
        try (InputStream in = Files.newInputStream(CONFIG_PATH)) {
            Properties props = new Properties();
            props.load(in);
            enabled = Boolean.parseBoolean(props.getProperty("enabled", "true"));
            QuickChestMod.LOGGER.info("[QuickChest] Config loaded. enabled={}", enabled);
        } catch (IOException e) {
            QuickChestMod.LOGGER.error("[QuickChest] Config load failed", e);
        }
    }

    public static void save() {
        try {
            Properties props = new Properties();
            props.setProperty("enabled", String.valueOf(enabled));
            try (OutputStream out = Files.newOutputStream(CONFIG_PATH,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
                props.store(out, "QuickChest Mod Config");
            }
            QuickChestMod.LOGGER.info("[QuickChest] Config saved.");
        } catch (IOException e) {
            QuickChestMod.LOGGER.error("[QuickChest] Config save failed", e);
        }
    }
}
