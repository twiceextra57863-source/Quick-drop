package com.sikandar.tpvpmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class TPVPMod implements ClientModInitializer {
    public static final String MOD_ID = "tpvpmod";

    @Override
    public void onInitializeClient() {
        // Title screen pe T PVP button add kar rahe hai (no mixin needed, Fabric API ka ScreenEvents use kiya)
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen titleScreen) {
                addTPVPButton(titleScreen, client);
            }
        });
    }

    private void addTPVPButton(TitleScreen screen, MinecraftClient client) {
        // Button position adjust kar sakta hai (abhi Singleplayer button ke neeche)
        ButtonWidget tpvpButton = ButtonWidget.builder(
                Text.literal("§cT PVP"),  // red colour for cool look
                button -> client.setScreen(new TPVPDashboardScreen())
        ).dimensions(screen.width / 2 - 100, screen.height / 4 + 120, 200, 20).build();

        screen.addDrawableChild(tpvpButton);
    }
}
