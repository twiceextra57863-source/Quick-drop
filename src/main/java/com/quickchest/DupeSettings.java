package com.quickchest;

import net.minecraft.text.Text;

public class DupeSettings {
    public static int dupeMode = 0; 
    public static boolean autoExit = false;
    public static boolean isExpanded = false; // Menu open/close state

    public static String getModeLabel() {
        return switch (dupeMode) {
            case 1 -> "§6[CTD Mode]";
            case 2 -> "§b[EPC Mode]";
            default -> "§7[OFF]";
        };
    }
}
