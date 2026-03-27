package com.yourname.speedchestmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.GenericContainerScreenHandler;

public class SpeedChestMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConfig.getInstance(); 

        // 1. Pause Menu Button Injection - Using correct event registration
        // We register a callback that gets called when the screen is initialized
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen.getClass().getSimpleName().equals("PauseScreen")) {
                // Create the button
                ButtonWidget myButton = ButtonWidget.builder(
                    Text.literal("⚡ Speed Chest Mod"),
                    btn -> client.setScreen(new SpeedChestScreen(screen))
                ).dimensions(
                    width / 2 - 100, 
                    height / 4 + 72, 
                    200, 
                    20
                ).build();
                
                // Add button using the screen's public method via cast or direct access if allowed
                // Since direct addDrawableChild is protected, we rely on the fact that 
                // inside the AFTER_INIT lambda, we are effectively part of the screen's initialization context.
                // If this still fails, we might need a Mixin for PauseScreen specifically.
                // Let's try casting to Screen and calling the method which should work in this context.
                screen.addDrawable(myButton); // Try addDrawable instead of addDrawableChild
            }
        });

        // Alternative approach if above fails: Register specifically for adding buttons
        // This is the more robust way for Fabric API
        ScreenEvents.register((client, screen, width, height) -> {
             if (screen.getClass().getSimpleName().equals("PauseScreen")) {
                 ButtonWidget myButton = ButtonWidget.builder(
                    Text.literal("⚡ Speed Chest Mod"),
                    btn -> client.setScreen(new SpeedChestScreen(screen))
                ).dimensions(
                    width / 2 - 100, 
                    height / 4 + 72, 
                    200, 
                    20
                ).build();
                 screen.addDrawable(myButton);
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
}
