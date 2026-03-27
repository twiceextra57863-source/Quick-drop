package com.yourname.speedchestmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;

public class SpeedChestMod implements ClientModInitializer {

    // Button position and state
    public static boolean showPauseButton = false;
    public static int buttonX = 0;
    public static int buttonY = 0;
    public static int buttonWidth = 200;
    public static int buttonHeight = 20;

    @Override
    public void onInitializeClient() {
        ModConfig.getInstance(); 

        // 1. Detect when Pause Screen opens
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            String className = screen.getClass().getName();
            if (className.contains("Pause") && !(screen instanceof HandledScreen<?>)) {
                showPauseButton = true;
                buttonX = width / 2 - 100;
                buttonY = height / 4 + 72;
            } else {
                showPauseButton = false;
            }
        });

        // 2. Mouse Click Detection for our custom button
        ScreenMouseEvents.afterMouseClick((client, screen, mouseX, mouseY, button) -> {
            if (showPauseButton && button == 0) { // Left click
                if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                    mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
                    
                    showPauseButton = false;
                    client.setScreen(new SpeedChestScreen(screen));
                }
            }
        });

        // 3. Register Render Callback for drawing our button
        WorldRenderEvents.END.register((context) -> {
            // This won't work for screens, we need ScreenEvents for rendering
        });
        
        // Better: Use ScreenEvents for rendering on PauseScreen
        ScreenEvents.AFTER_RENDER.register((client, screen, context, mouseX, mouseY) -> {
            if (showPauseButton && screen != null) {
                // Draw button background
                context.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, 0xFF404040);
                context.fill(buttonX + 1, buttonY + 1, buttonX + buttonWidth - 1, buttonY + buttonHeight - 1, 0xFF606060);
                
                // Draw button text
                String text = "⚡ Speed Chest";
                int textWidth = client.textRenderer.getWidth(text);
                context.drawText(client.textRenderer, text, 
                    buttonX + (buttonWidth - textWidth) / 2, 
                    buttonY + 6, 0xFFFFFF, true);
                
                // Draw hover effect
                if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                    mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
                    context.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, 0x40FFFFFF);
                }
            }
        });

        // 4. Chest Open Logic
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

        // 5. Tick Event for Automation Loop
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
