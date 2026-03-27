package com.tclient.mod.mixin;

import com.tclient.mod.gui.TClientScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    
    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        try {
            // Get screen object
            Object screenObject = this;
            Class<?> screenClass = Class.forName("net.minecraft.client.gui.screen.Screen");
            
            // Get width field
            Field widthField = screenClass.getDeclaredField("width");
            widthField.setAccessible(true);
            int width = (int) widthField.get(screenObject);
            
            // Get client field
            Field clientField = screenClass.getDeclaredField("client");
            clientField.setAccessible(true);
            MinecraftClient client = (MinecraftClient) clientField.get(screenObject);
            
            if (client == null) {
                return;
            }
            
            // Button position
            int btnW = 80;
            int btnH = 20;
            int btnX = width - btnW - 10;
            int btnY = 10;
            
            // Create button
            ButtonWidget button = ButtonWidget.builder(
                Text.literal("§bT Client"),
                buttonWidget -> {
                    try {
                        client.setScreen(new TClientScreen());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            )
            .position(btnX, btnY)
            .size(btnW, btnH)
            .build();
            
            // Add button to screen using reflection
            Method addMethod = screenClass.getDeclaredMethod("addDrawableChild", net.minecraft.client.gui.Element.class);
            addMethod.setAccessible(true);
            addMethod.invoke(screenObject, button);
            
            System.out.println("TClient: Button added successfully!");
            
        } catch (Exception e) {
            System.err.println("TClient: Failed to add button - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
