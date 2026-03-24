package com.quickchest.gui;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class QuickToggleButton {
    
    public static ButtonWidget create(int x, int y, int width, int height, 
                                       ButtonWidget.PressAction onPress) {
        return ButtonWidget.builder(
            Text.literal("Quick Chest: OFF"),
            onPress
        )
        .dimensions(x, y, width, height)
        .build();
    }
    
    public static void updateButtonText(ButtonWidget button, boolean isEnabled) {
        if (button != null) {
            button.setMessage(Text.literal("Quick Chest: " + (isEnabled ? "ON" : "OFF")));
        }
    }
}
