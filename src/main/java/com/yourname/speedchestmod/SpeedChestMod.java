package com.yourname.speedchestmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.screen.ScreenHandler;

public class SpeedChestMod implements ModInitializer {

    @Override
    public void onInitialize() {
        // Config Load
        ModConfig.getInstance(); 

        // 1. Pause Menu Button Injection
        // Hum specific class import karne ke bajaye screen ke type se check karenge
        ScreenEvents.AFTER_INIT.register((client, screen, access) -> {
            // Check if the current screen is the Pause Menu by checking its class name
            // This avoids import issues with PauseScreen specifically
            if (screen.getClass().getSimpleName().equals("PauseScreen")) {
                
                ButtonWidget myButton = ButtonWidget.builder(
                    Text.literal("⚡ Speed Chest Mod"),
                    btn -> client.setScreen(new SpeedChestScreen(screen))
                ).dimensions(
                    screen.width / 2 - 100, 
                    screen.height / 4 + 72, // Position below existing buttons
                    200, 
                    20
                ).build();
                
                screen.addDrawableChild(myButton);
            }
        });

        // 2. Chest Open Event Listener (Trigger)
        ScreenEvents.AFTER_INIT.register((client, screen, access) -> {
            if (screen instanceof net.minecraft.client.gui.screen.ingame.HandledScreen<?> handledScreen) {
                ScreenHandler handler = handledScreen.getScreenHandler();
                
                // Check if it's a chest/container
                if (handler instanceof net.minecraft.screen.GenericContainerScreenHandler) {
                    if (ModConfig.getInstance().enabled && client.player != null) {
                        AutomationLogic.startAutomation(client.player, handler);
                    }
                }
            }
        });

        // 3. Tick Event (For High Speed Loop)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.currentScreen instanceof net.minecraft.client.gui.screen.ingame.HandledScreen<?> handledScreen) {
                ScreenHandler handler = handledScreen.getScreenHandler();
                if (handler instanceof net.minecraft.screen.GenericContainerScreenHandler) {
                    AutomationLogic.tick(client.player, handler);
                }
            }
        });
    }
}
