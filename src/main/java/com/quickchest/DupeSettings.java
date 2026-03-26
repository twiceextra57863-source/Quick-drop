package com.quickchest;

import net.minecraft.text.Text;

public class DupeSettings {
    public static int dupeMode = 0; 
    public static boolean autoExit = false; // <--- New Toggle

    public static Text getStatusText() {
        String mode = switch (dupeMode) {
            case 1 -> "§6[CTD]";
            case 2 -> "§b[EPC]";
            default -> "§7OFF";
        };
        String exit = autoExit ? " §a[Auto-Exit: ON]" : " §c[Auto-Exit: OFF]";
        return Text.of(mode + exit);
    }
}
