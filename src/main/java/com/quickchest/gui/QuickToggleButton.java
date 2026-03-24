package com.quickchest.gui;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class QuickToggleButton {

    public static ButtonWidget create(
        int x, int y, int width, int height,
        Text message,
        ButtonWidget.PressAction onPress
    ) {
        return ButtonWidget.builder(message, onPress)
            .dimensions(x, y, width, height)
            .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(
                Text.literal("Toggle QuickChest: drop + store on chest click")
            ))
            .build();
    }
}
