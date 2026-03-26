package com.quickchest.config;

import com.quickchest.QuickChestMod;
import net.fabricmc.loader.api.FabricLoader;

package com.quickchest.config;

import com.quickchest.QuickChestMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class QuickChestConfig {

    private static final Path CONFIG_PATH =
        FabricLoader.getInstance().getConfigDir()
            .resolve("quickchest.properties");

    // --- OLD FEATURES VARIABLES ---
    private static boolean enabled            = true;
    private static boolean autoMode           = false;
    private static int autoCycles             = 8;
    private static int autoSpeedTicks         = 3;
    private static int returnDelayTicks       = 10;
    private static int maxClicksPerSession    = 5;

    // --- NEW SPAMMER FEATURES (MANUAL INPUT) ---
    private static int actionsPerClick        = 1;   // Kitne clicks honge (1-1000)
    private static int spammerSpeed           = 0;   // Packets ke beech delay (0-100)

    // =============================================
    // GETTERS & SETTERS (OLD + NEW)
    // =============================================

    public static boolean isEnabled()          { return enabled; }
    public static void setEnabled(boolean v)   { enabled = v; }

    public static boolean isAutoMode()         { return autoMode; }
    public static void setAutoMode(boolean v)  { autoMode = v; }

    public static int getAutoCycles()          { return autoCycles; }
    public static void setAutoCycles(int v)    {
        autoCycles = Math.max(1, Math.min(20, v)); }

    public static int getAutoSpeedTicks()      { return autoSpeedTicks; }
    public static void setAutoSpeedTicks(int v) {
        autoSpeedTicks = Math.max(1, Math.min(10, v)); }

    public static int getReturnDelayTicks()    { return returnDelayTicks; }
    public static void setReturnDelayTicks(int v) {
        returnDelayTicks = Math.max(1, Math.min(20, v)); }

    public static int getMaxClicksPerSession() { return maxClicksPerSession; }
    public static void setMaxClicksPerSession(int v) {
        maxClicksPerSession = Math.max(1, Math.min(999, v)); }

    // Spammer: Actions/Click (Limit badha kar 1000 kar di hai)
    public static int getActionsPerClick()     { return actionsPerClick; }
    public static void setActionsPerClick(int v) {
        actionsPerClick = Math.max(1, Math.min(1000, v)); }

    // Spammer: Speed/Delay (Naya control)
    public static int getSpammerSpeed()        { return spammerSpeed; }
    public static void setSpammerSpeed(int v)  {
        spammerSpeed = Math.max(0, Math.min(100, v)); }

    // =============================================
    // RESET, LOAD & SAVE
    // =============================================

    public static void resetToDefault() {
        enabled            = true;
        autoMode           = false;
        autoCycles         = 8;
        autoSpeedTicks     = 3;
        returnDelayTicks   = 10;
        maxClicksPerSession = 5;
        actionsPerClick    = 1;
        spammerSpeed       = 0;
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) { save(); return; }
        try (InputStream in = Files.newInputStream(CONFIG_PATH)) {
            Properties p = new Properties();
            p.load(in);
            
            // Loading Old Features
            enabled             = Boolean.parseBoolean(p.getProperty("enabled", "true"));
            autoMode            = Boolean.parseBoolean(p.getProperty("autoMode", "false"));
            autoCycles          = Integer.parseInt(p.getProperty("autoCycles", "8"));
            autoSpeedTicks      = Integer.parseInt(p.getProperty("autoSpeedTicks", "3"));
            returnDelayTicks    = Integer.parseInt(p.getProperty("returnDelayTicks", "10"));
            maxClicksPerSession = Integer.parseInt(p.getProperty("maxClicksPerSession", "5"));
            
            // Loading New Spammer Features
            actionsPerClick     = Integer.parseInt(p.getProperty("actionsPerClick", "1"));
            spammerSpeed        = Integer.parseInt(p.getProperty("spammerSpeed", "0"));

            QuickChestMod.LOGGER.info("[QuickChest] Config loaded successfully.");
        } catch (IOException | NumberFormatException e) {
            QuickChestMod.LOGGER.error("[QuickChest] Config load failed", e);
        }
    }

    public static void save() {
        try {
            Properties p = new Properties();
            // Saving Old Features
            p.setProperty("enabled", String.valueOf(enabled));
            p.setProperty("autoMode", String.valueOf(autoMode));
            p.setProperty("autoCycles", String.valueOf(autoCycles));
            p.setProperty("autoSpeedTicks", String.valueOf(autoSpeedTicks));
            p.setProperty("returnDelayTicks", String.valueOf(returnDelayTicks));
            p.setProperty("maxClicksPerSession", String.valueOf(maxClicksPerSession));
            
            // Saving New Spammer Features
            p.setProperty("actionsPerClick", String.valueOf(actionsPerClick));
            p.setProperty("spammerSpeed", String.valueOf(spammerSpeed));

            try (OutputStream out = Files.newOutputStream(CONFIG_PATH,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
                p.store(out, "QuickChest Config");
            }
        } catch (IOException e) {
            QuickChestMod.LOGGER.error("[QuickChest] Config save failed", e);
        }
    }
}
