package com.tclient.mod.mixin;

import com.tclient.mod.gui.TClientScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    
    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        TitleScreen screen = (TitleScreen)(Object)this;
        
        int btnW = 80;
        int btnH = 20;
        int btnX = screen.width - btnW - 10;
        int btnY = 10;
        
        ButtonWidget button = ButtonWidget.builder(
            Text.literal("§bT Client"),
            buttonWidget -> {
                if (screen.client != null) {
                    screen.client.setScreen(new TClientScreen());
                }
            }
        )
        .position(btnX, btnY)
        .size(btnW, btnH)
        .build();
        
        screen.addDrawableChild(button);
    }
}
