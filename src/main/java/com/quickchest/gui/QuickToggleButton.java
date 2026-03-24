package com.quickchest.gui;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class QuickToggleButton {

    public static ButtonWidget create(
        int x, int y,
        int width, int height,
        Text message,
        ButtonWidget.PressAction onPress
    ) {
        return ButtonWidget.builder(message, onPress)
            .dimensions(x, y, width, height)
            .tooltip(Tooltip.of(Text.literal("Toggle Quick Chest drop+store")))
            .build();
    }

    public static void updateButtonText(ButtonWidget button, boolean isEnabled) {
        button.setMessage(Text.literal(
            isEnabled ? "§aQuick Chest: ON" : "§cQuick Chest: OFF"
        ));
    }
}
