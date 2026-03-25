package com.quickchest.config;

import com.quickchest.QuickChestMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class QuickChestConfig {

    private static final Path CONFIG_PATH =
        FabricLoader.getInstance().getConfigDir().resolve("quickchest.properties");

    // Defaults
    private static boolean enabled = true;
    private static boolean autoMode = false;
    private static int autoCycles = 8;        // 1–20
    private static int autoSpeedTicks = 3;    // 1–10
    private static int returnDelayTicks = 10; // 1–20

    public static boolean isEnabled() { return enabled; }
    public static void setEnabled(boolean v) { enabled = v; }

    public static boolean isAutoMode() { return autoMode; }
    public static void setAutoMode(boolean v) { autoMode = v; }

    public static int getAutoCycles() { return autoCycles; }
    public static void setAutoCycles(int v) { autoCycles = Math.max(1, Math.min(20, v)); }

    public static int getAutoSpeedTicks() { return autoSpeedTicks; }
    public static void setAutoSpeedTicks(int v) { autoSpeedTicks = Math.max(1, Math.min(10, v)); }

    public static int getReturnDelayTicks() { return returnDelayTicks; }
    public static void setReturnDelayTicks(int v) { returnDelayTicks = Math.max(1, Math.min(20, v)); }

    public static void resetToDefault() {
        enabled = true;
        autoMode = false;
        autoCycles = 8;
        autoSpeedTicks = 3;
        returnDelayTicks = 10;
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) { save(); return; }
        try (InputStream in = Files.newInputStream(CONFIG_PATH)) {
            Properties p = new Properties();
            p.load(in);
            enabled = Boolean.parseBoolean(p.getProperty("enabled", "true"));
            autoMode = Boolean.parseBoolean(p.getProperty("autoMode", "false"));
            autoCycles = Integer.parseInt(p.getProperty("autoCycles", "8"));
            autoSpeedTicks = Integer.parseInt(p.getProperty("autoSpeedTicks", "3"));
            returnDelayTicks = Integer.parseInt(p.getProperty("returnDelayTicks", "10"));
            QuickChestMod.LOGGER.info("[QuickChest] Config loaded.");
        } catch (IOException | NumberFormatException e) {
            QuickChestMod.LOGGER.error("[QuickChest] Config load failed", e);
        }
    }

    public static void save() {
        try {
            Properties p = new Properties();
            p.setProperty("enabled", String.valueOf(enabled));
            p.setProperty("autoMode", String.valueOf(autoMode));
            p.setProperty("autoCycles", String.valueOf(autoCycles));
            p.setProperty("autoSpeedTicks", String.valueOf(autoSpeedTicks));
            p.setProperty("returnDelayTicks", String.valueOf(returnDelayTicks));
            try (OutputStream out = Files.newOutputStream(CONFIG_PATH,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                p.store(out, "QuickChest Config");
            }
        } catch (IOException e) {
            QuickChestMod.LOGGER.error("[QuickChest] Config save failed", e);
        }
    }
}
