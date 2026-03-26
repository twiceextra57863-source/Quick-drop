package com.quickchest;

import net.minecraft.text.Text;

public class DupeSettings {
    public static int dupeMode = 0; // 0: OFF, 1: CTD (Hopper), 2: EPC (Drop)

    public static Text getStatusText() {
        return switch (dupeMode) {
            case 1 -> Text.of("§6[CTD Mode] §aACTIVE");
            case 2 -> Text.of("§b[EPC Mode] §aACTIVE");
            default -> Text.of("§7Dupe: §cOFF");
        };
    }
}
