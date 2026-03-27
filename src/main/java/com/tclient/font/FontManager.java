package com.tclient.font;

import net.minecraft.util.Identifier;

public class FontManager {
    public static String currentFont = "default"; // Options: default, modern, smooth, pixel

    public static Identifier getFontIdentifier() {
        if (currentFont.equals("modern")) {
            return Identifier.of("tclient", "modern_font");
        } else if (currentFont.equals("smooth")) {
            return Identifier.of("tclient", "smooth_font");
        }
        return Identifier.of("minecraft", "default");
    }
}
