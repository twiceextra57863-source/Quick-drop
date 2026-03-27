package com.yourname.speedchestmod.mixin;

import com.yourname.speedchestmod.SpeedChestScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class PauseScreenMixin {

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;
        
        // Only add button to PauseScreen (check by class name)
        if (!screen.getClass().getName().contains("PauseScreen")) {
            return;
        }
        
        ButtonWidget myButton = ButtonWidget.builder(
            Text.literal("⚡ Speed Chest"),
            btn -> {
                assert screen.client != null;
                screen.client.setScreen(new SpeedChestScreen(screen));
            }
        ).dimensions(
            screen.width / 2 - 100, 
            screen.height / 4 + 72, 
            200, 
            20
        ).build();
        
        screen.addDrawableChild(myButton);
    }
}
