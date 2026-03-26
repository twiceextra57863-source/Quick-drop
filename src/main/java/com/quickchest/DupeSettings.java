package com.quickchest;

public class DupeSettings {
    public static int dupeMode = 0; // 0 = OFF, 1 = Auto-Hopper, 2 = Packet-Drop (New)
    
    public static String getModeName() {
        return switch (dupeMode) {
            case 1 -> "§aAuto-Hopper";
            case 2 -> "§bPacket-Drop";
            default -> "§cOFF";
        };
    }
}
