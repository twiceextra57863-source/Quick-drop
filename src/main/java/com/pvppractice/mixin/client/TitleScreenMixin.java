package com.pvppractice.mixin.client;

import com.pvppractice.client.gui.PVPDashboardScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    
    @Inject(method = "init", at = @At(value = "RETURN"))
    private void onInit(CallbackInfo ci) {
        TitleScreen titleScreen = (TitleScreen)(Object)this;
        
        // Add PVP Practice button to title screen
        ButtonWidget pvpButton = ButtonWidget.builder(
            Text.literal("§c❤ PVP Practice"),
            button -> {
                if (titleScreen.client != null) {
                    titleScreen.client.setScreen(new PVPDashboardScreen());
                }
            }
        ).dimensions(titleScreen.width / 2 + 100, titleScreen.height / 4 + 48 + 72, 120, 20).build();
        
        // Use the addDrawableChild method via the screen instance
        titleScreen.addDrawable(pvpButton);
    }
}
