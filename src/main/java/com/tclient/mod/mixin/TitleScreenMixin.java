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
    
    // Minecraft 1.21 ka sahi method
    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        try {
            TitleScreen screen = (TitleScreen)(Object)this;
            
            int btnW = 80;
            int btnH = 20;
            int btnX = screen.width - btnW - 5;
            int btnY = 5;
            
            ButtonWidget button = new ButtonWidget.Builder(
                Text.literal("§bT Client"),
                (buttonWidget) -> {
                    if (screen.client != null) {
                        screen.client.setScreen(new TClientScreen());
                    }
                }
            )
            .position(btnX, btnY)
            .size(btnW, btnH)
            .build();
            
            screen.addDrawableChild(button);
            
        } catch (Exception e) {
            System.out.println("TClient: Failed to add button - " + e.getMessage());
        }
    }
}
