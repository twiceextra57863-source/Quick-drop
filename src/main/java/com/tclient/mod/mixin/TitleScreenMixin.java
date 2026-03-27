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
            Object screenObj = this;
            Screen screen = (Screen) screenObj;
            
            // Get client using reflection
            Field clientField = Screen.class.getDeclaredField("client");
            clientField.setAccessible(true);
            MinecraftClient client = (MinecraftClient) clientField.get(screen);
            
            if (client == null) {
                System.out.println("TClient: Client is null, can't add button");
                return;
            }
            
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
            .position(screen.width - 85, 5)
            .size(80, 20)
            .build();
            
            // Add button using reflection
            Method addMethod = Screen.class.getDeclaredMethod("addDrawableChild", net.minecraft.client.gui.Element.class);
            addMethod.setAccessible(true);
            addMethod.invoke(screen, button);
            
            System.out.println("TClient: Button added successfully!");
            
        } catch (NoSuchFieldException e) {
            System.err.println("TClient: Field not found - " + e.getMessage());
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            System.err.println("TClient: Method not found - " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("TClient: Unexpected error - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
