package com.yourname.speedchestmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
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

        // 1. Pause Menu Button Injection
        // Hum ScreenEvents.AFTER_INIT ka sahi syntax use kar rahe hain
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            // Check if it is PauseScreen by class name to avoid import issues
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
                screen.addDrawableChild(myButton);
            }
        });

        // 2. Chest Open Logic
        // Jab bhi koi HandledScreen (Chest) khulegi, hum check karenge
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
