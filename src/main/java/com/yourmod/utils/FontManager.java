package com.yourmod.utils;

import com.yourmod.TClientMod;
import net.minecraft.client.MinecraftClient;

public class FontManager {
    public static void applyFont(String fontName) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            TClientMod.LOGGER.info("Applying font: {}", fontName);
            // Font implementation would go here
        } catch (Exception e) {
            TClientMod.LOGGER.error("Failed to apply font", e);
        }
    }
    
    public static void resetFont() {
        TClientMod.LOGGER.info("Resetting to default font");
    }
}
