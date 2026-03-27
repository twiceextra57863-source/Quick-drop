package com.yourmod.utils;

import com.yourmod.TClientMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.Text;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FontManager {
    private static TextRenderer customTextRenderer;
    
    public static void applyFont(String fontName) {
        try {
            // This is a simplified version - actual font implementation
            // would require creating a custom TextRenderer
            MinecraftClient client = MinecraftClient.getInstance();
            
            // Here you would normally load and apply the font
            TClientMod.LOGGER.info("Applying font: " + fontName);
            
            // For demonstration, we'll just log the change
            // In a real implementation, you would need to override the font rendering
        } catch (Exception e) {
            TClientMod.LOGGER.error("Failed to apply font", e);
        }
    }
    
    public static void resetFont() {
        // Reset to default Minecraft font
        TClientMod.LOGGER.info("Resetting to default font");
    }
    
    private static TextRenderer loadCustomFont(String fontPath) {
        try {
            Path path = Paths.get(fontPath);
            if (Files.exists(path)) {
                InputStream is = Files.newInputStream(path);
                Font font = Font.createFont(Font.TRUETYPE_FONT, is);
                // Create custom text renderer with the loaded font
                return new TextRenderer(
                    font -> font,
                    (text, shadow) -> {},
                    (text, underline, strikethrough) -> {}
                );
            }
        } catch (IOException | FontFormatException e) {
            TClientMod.LOGGER.error("Failed to load custom font", e);
        }
        return null;
    }
}
