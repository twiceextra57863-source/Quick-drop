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
            String[] premade = {"Default", "Modern", "Smooth", "Minecraftia", "Roboto", "Arial", "Impact", "PixelPro"};
            for (String f : premade) availableFonts.add(f);
            if (!FONT_DIR.exists()) FONT_DIR.mkdirs();
        }
    }

    public static Identifier getFontIdentifier() {
        if (currentFont == null || currentFont.equals("Default")) {
            return Identifier.of("minecraft", "default");
        }
        // Identifier hamesha lowercase hona chahiye
        return Identifier.of("tclient", currentFont.toLowerCase().replace(" ", "_"));
    }
}
