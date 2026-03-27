package com.yourmod.config;

import com.yourmod.TClientMod;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

public class ModConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("tclient.properties");
    private static Properties properties = new Properties();
    private static String currentFont = "Minecraft";
    
    static {
        loadConfig();
    }
    
    public static void loadConfig() {
        if (CONFIG_PATH.toFile().exists()) {
            try (FileInputStream fis = new FileInputStream(CONFIG_PATH.toFile())) {
                properties.load(fis);
                currentFont = properties.getProperty("selected_font", "Minecraft");
            } catch (IOException e) {
                TClientMod.LOGGER.error("Failed to load config", e);
            }
        }
    }
    
    public static void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_PATH.toFile())) {
            properties.store(fos, "T Client Configuration");
        } catch (IOException e) {
            TClientMod.LOGGER.error("Failed to save config", e);
        }
    }
    
    public static String getCurrentFont() {
        return currentFont;
    }
    
    public static void setSelectedFont(String font) {
        currentFont = font;
        properties.setProperty("selected_font", font);
        saveConfig();
    }
    
    public static String getCustomFontName() {
        return properties.getProperty("custom_font", "");
    }
    
    public static void setCustomFontName(String fontName) {
        properties.setProperty("custom_font", fontName);
        saveConfig();
    }
    
    public static void resetFont() {
        setSelectedFont("Minecraft");
        properties.remove("custom_font");
        saveConfig();
    }
}
