package com.sikandar.tpvpmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class TPVPMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen titleScreen) {
                addTPVPButton(titleScreen);
            }
        });
    }

    private void addTPVPButton(TitleScreen screen) {
        // Button ko title screen ke children me add karne ke liye reflection ya cast ki zarurat nahi
        // Hum ScreenEvents ke baad init ho chuka hota hai, lekin better way ke liye mixin use karenge agar issue aaye
        // Abhi try with this safe position (Singleplayer ke neeche adjust kiya)
        ButtonWidget tpvpButton = ButtonWidget.builder(
                Text.literal("§cT PVP"),
                button -> MinecraftClient.getInstance().setScreen(new TPVPDashboardScreen())
        ).dimensions(screen.width / 2 - 100, screen.height / 4 + 120, 200, 20).build();

        // Protected method ko bypass karne ke liye (1.21 me yeh common issue hai)
        screen.children().add(tpvpButton);           // selectable
        screen.addDrawable(tpvpButton);              // drawable
        // Agar upar wala nahi chalta to neeche wala mixin use karna padega
    }
}
