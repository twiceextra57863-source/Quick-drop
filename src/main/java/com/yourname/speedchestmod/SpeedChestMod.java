package com.yourname.speedchestmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;

public class SpeedChestMod implements ClientModInitializer {

    // Flag to track if we already added the button (prevent duplicates)
    private static boolean pauseButtonAdded = false;

    @Override
    public void onInitializeClient() {
        ModConfig.getInstance(); 

        // 1. Pause Menu Button - JUGAR: Use addDrawable() which is PUBLIC!
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            // Detect PauseScreen by checking if it has no title and is a base Screen
            // PauseScreen typically has empty/null title and no handler
            if (isPauseScreen(screen)) {
                
                // Prevent adding multiple buttons
                if (pauseButtonAdded) return;
                
                ButtonWidget myButton = ButtonWidget.builder(
                    Text.literal("⚡ Speed Chest"),
                    btn -> {
                        pauseButtonAdded = false; // Reset for next time
                        client.setScreen(new SpeedChestScreen(screen));
                    }
                ).dimensions(
                    width / 2 - 100, 
                    height / 4 + 72, 
                    200, 
                    20
                ).build();
                
                // JUGAR: addDrawable() is PUBLIC, addDrawableChild() is protected!
                screen.addDrawable(myButton);
                pauseButtonAdded = true;
            } else {
                // Reset flag when leaving pause screen
                pauseButtonAdded = false;
            }
        });

        // 2. Chest Open Logic
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

        // 3. Tick Event for Automation Loop
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.currentScreen instanceof HandledScreen<?> handledScreen) {
                ScreenHandler handler = handledScreen.getScreenHandler();
                if (handler instanceof GenericContainerScreenHandler) {
                    AutomationLogic.tick(client.player, handler);
                }
            }
        });
    }

    // JUGAR: Detect PauseScreen without importing the class
    private boolean isPauseScreen(Screen screen) {
        // PauseScreen has these characteristics:
        // 1. It's a base Screen (not HandledScreen)
        // 2. Class name contains "Pause"
        // 3. No screen handler
        String className = screen.getClass().getName();
        return className.contains("Pause") && !(screen instanceof HandledScreen<?>);
    }
}
