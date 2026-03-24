package com.quickchest.config;

import com.quickchest.QuickChestMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class QuickChestConfig {

    private static final Path CONFIG_PATH =
        FabricLoader.getInstance().getConfigDir().resolve("quickchest.properties");

    private static boolean enabled = true;

    public static boolean isEnabled() { return enabled; }
    public static void setEnabled(boolean value) { enabled = value; }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) { save(); return; }
        try (InputStream in = Files.newInputStream(CONFIG_PATH)) {
            Properties props = new Properties();
            props.load(in);
            enabled = Boolean.parseBoolean(props.getProperty("enabled", "true"));
        } catch (IOException e) {
            QuickChestMod.LOGGER.error("[QuickChest] Config load failed", e);
        }
    }

    public static void save() {
        try {
            Properties props = new Properties();
            props.setProperty("enabled", String.valueOf(enabled));
            try (OutputStream out = Files.newOutputStream(CONFIG_PATH,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                props.store(out, "QuickChest Config");
            }
        } catch (IOException e) {
            QuickChestMod.LOGGER.error("[QuickChest] Config save failed", e);
        }
    }
}
