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
            TitleScreen screen = (TitleScreen)(Object)this;
            
            Field clientField = Screen.class.getDeclaredField("client");
            clientField.setAccessible(true);
            MinecraftClient client = (MinecraftClient) clientField.get(screen);
            
            if (client == null) return;
            
            // Button position - Single Player ke right side
            // Single Player button is usually at: (width/2 - 100, height/2 - 48)
            int buttonX = (screen.width / 2) + 5;      // Single Player ke right side
            int buttonY = (screen.height / 2) - 48;    // Same Y as Single Player
            
            ButtonWidget tclientButton = ButtonWidget.builder(
                Text.literal("§bT Client"),
                buttonWidget -> {
                    try {
                        client.setScreen(new TClientScreen());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            )
            .position(buttonX, buttonY)
            .size(90, 20)
            .build();
            
            Method addMethod = Screen.class.getDeclaredMethod("addDrawableChild", net.minecraft.client.gui.Element.class);
            addMethod.setAccessible(true);
            addMethod.invoke(screen, tclientButton);
            
            System.out.println("TClient: Button added!");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
