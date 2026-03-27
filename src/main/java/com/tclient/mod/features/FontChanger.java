package com.tclient.mod.features;

import com.tclient.mod.TClientMod;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public class FontChanger {

    public static final Map<String, FontEntry> FONTS = new LinkedHashMap<>();
    public static String currentFontId = "DEFAULT";

    static {
        FONTS.put("DEFAULT",    new FontEntry("Default",       "minecraft:default",  "Standard Minecraft font",       "§rABCDEFG 123",    FontCategory.STANDARD));
        FONTS.put("UNICODE",    new FontEntry("Unicode",       "minecraft:uniform",  "Clean Unicode font",            "§bABCDEFG 123",    FontCategory.CLEAN));
        FONTS.put("ENCHANTING", new FontEntry("Enchanting",    "minecraft:alt",      "Enchantment table language",    "§5ABCDEFG 123",    FontCategory.DECORATIVE));
        FONTS.put("ILLAGER",    new FontEntry("Illager Script","minecraft:illageralt","Ancient Illager runes",        "§6ABCDEFG 123",    FontCategory.DECORATIVE));
        FONTS.put("BOLD_CLEAN", new FontEntry("Bold Clean",    "minecraft:default",  "Bold style",                   "§lABCDEFG 123",    FontCategory.BOLD));
        FONTS.put("MONO",       new FontEntry("Monospace",     "minecraft:uniform",  "Fixed-width monospace",         "§3ABCDEFG 123",    FontCategory.CLEAN));
        FONTS.put("SMALL_CAPS", new FontEntry("Small Caps",    "minecraft:default",  "SMALL CAPS STYLE",              "§eABCDEFG 123",    FontCategory.STYLIZED));
        FONTS.put("RETRO",      new FontEntry("Retro 8-Bit",   "minecraft:default",  "Retro gaming aesthetic",        "§cABCDEFG 123",    FontCategory.STYLIZED));
        FONTS.put("THIN",       new FontEntry("Thin Style",    "minecraft:uniform",  "Thin lightweight appearance",   "§fABCDEFG 123",    FontCategory.CLEAN));
        FONTS.put("NEON",       new FontEntry("Neon Glow",     "minecraft:default",  "Neon glow color theme",         "§a§lABCDEFG 123",  FontCategory.STYLIZED));
        FONTS.put("SHADOW",     new FontEntry("Shadow Drop",   "minecraft:default",  "Deep shadow rendering",         "§7ABCDEFG 123",    FontCategory.STANDARD));
        FONTS.put("MATRIX",     new FontEntry("Matrix Code",   "minecraft:uniform",  "Digital matrix aesthetic",      "§2ABCDEFG 123",    FontCategory.STYLIZED));
    }

    public static void init() {
        TClientMod.LOGGER.info("[T Client] FontChanger initialized with " + FONTS.size() + " fonts.");
        if (TClientMod.CONFIG != null && TClientMod.CONFIG.selectedFont != null) {
            currentFontId = TClientMod.CONFIG.selectedFont;
        }
    }

    public static FontEntry getCurrentFont() {
        return FONTS.getOrDefault(currentFontId, FONTS.get("DEFAULT"));
    }

    public static void setFont(String fontId) {
        if (FONTS.containsKey(fontId)) {
            currentFontId = fontId;
            if (TClientMod.CONFIG != null) {
                TClientMod.CONFIG.selectedFont = fontId;
                TClientMod.CONFIG.fontChangerEnabled = !fontId.equals("DEFAULT");
                TClientMod.CONFIG.save();
            }
        }
    }

    public static Identifier getCurrentFontIdentifier() {
        FontEntry entry = getCurrentFont();
        try {
            String[] parts = entry.minecraftFontId.split(":");
            if (parts.length == 2) return Identifier.of(parts[0], parts[1]);
        } catch (Exception ignored) {}
        return Identifier.of("minecraft", "default");
    }

    public static boolean isFontChangerActive() {
        return TClientMod.CONFIG != null && TClientMod.CONFIG.fontChangerEnabled
                && !currentFontId.equals("DEFAULT");
    }

    public enum FontCategory { STANDARD, CLEAN, BOLD, DECORATIVE, STYLIZED }

    public static class FontEntry {
        public final String displayName, minecraftFontId, description, preview;
        public final FontCategory category;

        public FontEntry(String displayName, String minecraftFontId,
                         String description, String preview, FontCategory category) {
            this.displayName = displayName;
            this.minecraftFontId = minecraftFontId;
            this.description = description;
            this.preview = preview;
            this.category = category;
        }
    }
}
