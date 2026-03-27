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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("TClient");
    
    // Try init method
    @Inject(method = "init", at = @At("TAIL"))
    private void onInitTail(CallbackInfo ci) {
        addCustomButton();
    }
    
    // Try widgets method
    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void onInitWidgets(CallbackInfo ci) {
        addCustomButton();
    }
    
    private void addCustomButton() {
        try {
            Screen screen = (Screen)(Object)this;
            LOGGER.info("TClient: Adding button - Screen: {}", screen.getClass().getSimpleName());
            
            int btnW = 80;
            int btnH = 20;
            int btnX = screen.width - btnW - 5;
            int btnY = 5;
            
            ButtonWidget button = ButtonWidget.builder(
                Text.literal("§bTC"),
                buttonWidget -> {
                    try {
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
            
            Method addDrawableChild = Screen.class.getDeclaredMethod("addDrawableChild", net.minecraft.client.gui.Element.class);
            addDrawableChild.setAccessible(true);
            addDrawableChild.invoke(screen, button);
            
            LOGGER.info("TClient: Button added at position {},{}", btnX, btnY);
            
        } catch (Exception e) {
            LOGGER.error("TClient: Failed to add button", e);
        }
    }
}
