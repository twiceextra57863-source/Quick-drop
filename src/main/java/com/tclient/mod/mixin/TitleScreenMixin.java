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

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    
    // 1.21.4 mein method ka naam "init" hi hai
    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        try {
            TitleScreen screen = (TitleScreen)(Object)this;
            
            // Button create karo
            ButtonWidget button = ButtonWidget.builder(
                Text.literal("§bT Client"),
                buttonWidget -> {
                    if (screen.client != null) {
                        screen.client.setScreen(new TClientScreen());
                    }
                }
            )
            .position(screen.width - 85, 5)
            .size(80, 20)
            .build();
            
            // Screen mein button add karo
            screen.addDrawableChild(button);
            
            System.out.println("TClient: Button added successfully in 1.21.4!");
            
        } catch (Exception e) {
            System.err.println("TClient Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
