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
            Screen screen = (Screen)(Object)this;
            
            int btnW = 80;
            int btnH = 20;
            int btnX = screen.width - btnW - 5;
            int btnY = 5;
            
            // Create button
            ButtonWidget button = ButtonWidget.builder(
                Text.literal("§bT Client"),
                buttonWidget -> {
                    try {
                        // Get client field via reflection
                        Field clientField = Screen.class.getDeclaredField("client");
                        clientField.setAccessible(true);
                        MinecraftClient client = (MinecraftClient) clientField.get(screen);
                        if (client != null) {
                            client.setScreen(new TClientScreen());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            )
            .dimensions(btnX, btnY, btnW, btnH)
            .build();
            
            // Add button via reflection
            Method addDrawableChild = Screen.class.getDeclaredMethod("addDrawableChild", net.minecraft.client.gui.Element.class);
            addDrawableChild.setAccessible(true);
            addDrawableChild.invoke(screen, button);
            
        } catch (Exception e) {
            System.err.println("TClient: Failed to add button - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
