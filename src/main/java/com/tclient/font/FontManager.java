package com.tclient.font;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FontManager {
    public static String currentFont = "Default";
    public static final List<String> availableFonts = new ArrayList<>();
    private static final File FONT_DIR = new File(FabricLoader.getInstance().getConfigDir().toFile(), "T-Client/fonts");

    public static void init() {
        if (availableFonts.isEmpty()) {
            availableFonts.add("Default");
            availableFonts.add("Modern");
            availableFonts.add("Smooth");
            availableFonts.add("Minecraftia");
            availableFonts.add("Roboto");
            availableFonts.add("Arial");
            availableFonts.add("Impact");
            availableFonts.add("Verdana");
            availableFonts.add("ComicSans");
            availableFonts.add("Pixel-Pro");
            
            if (!FONT_DIR.exists()) FONT_DIR.mkdirs();
            loadExternalFonts();
        }
    }

    public static void loadExternalFonts() {
        if (!FONT_DIR.exists()) return;
        File[] files = FONT_DIR.listFiles((dir, name) -> name.toLowerCase().endsWith(".ttf"));
        if (files != null) {
            for (File file : files) {
                String name = file.getName().replace(".ttf", "");
                if (!availableFonts.contains(name)) {
                    availableFonts.add(name);
                }
            }
        }
    }

    public static Identifier getFontIdentifier() {
        if (currentFont == null || currentFont.equals("Default")) {
            return Identifier.of("minecraft", "default");
        }
        // Lowercase and underscore are mandatory for Minecraft 1.21 identifiers
        String path = currentFont.toLowerCase().replace(" ", "_");
        return Identifier.of("tclient", path);
    }
}
