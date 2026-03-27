package com.yourname.speedchestmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;

public class SpeedChestMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModConfig.getInstance(); 

        // Pause Menu Button is handled by PauseScreenMixin

        // Chest Open Logic
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof HandledScreen<?> handledScreen) {
                ScreenHandler handler = handledScreen.getScreenHandler();
                
                if (handler instanceof GenericContainerScreenHandler) {
                    if (ModConfig.getInstance().enabled && client.player != null) {
                        AutomationLogic.startAutomation(client.player, handler);
                    }
                }
            }
        });

        // Tick Event for Automation Loop
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.currentScreen instanceof HandledScreen<?> handledScreen) {
                ScreenHandler handler = handledScreen.getScreenHandler();
                if (handler instanceof GenericContainerScreenHandler) {
                    AutomationLogic.tick(client.player, handler);
                }
            }
        });
    }
}
