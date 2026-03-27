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
    
    // Method 2: Agar init nahi chalta to widgets use karo
    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void onInitWidgets(CallbackInfo ci) {
        addTClientButton((TitleScreen)(Object)this);
    }
    
    private void addTClientButton(TitleScreen screen) {
        int btnW = 80;
        int btnH = 18;
        int btnX = screen.width - btnW - 6;
        int btnY = 6;

        screen.addDrawableChild(
            ButtonWidget.builder(
                Text.literal("§b■ T Client"),
                button -> {
                    if (screen.client != null) {
                        screen.client.setScreen(new TClientScreen());
                    }
                }
            )
            .dimensions(btnX, btnY, btnW, btnH)
            .build()
        );
    }
}
