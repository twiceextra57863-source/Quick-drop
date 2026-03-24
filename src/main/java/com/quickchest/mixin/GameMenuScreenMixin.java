package com.quickchest.mixin;

import com.quickchest.QuickChestMod;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }
    
    @Inject(method = "init", at = @At("RETURN"))
    private void addQuickChestButton(CallbackInfo ci) {
        int buttonWidth = 150;
        int buttonHeight = 20;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 4 - 24;
        
        ButtonWidget toggleButton = ButtonWidget.builder(
            Text.literal("Quick Chest: " + (QuickChestMod.isEnabled() ? "ON" : "OFF")),
            button -> {
                QuickChestMod.toggleMod();
                button.setMessage(Text.literal("Quick Chest: " + 
                    (QuickChestMod.isEnabled() ? "ON" : "OFF")));
            }
        )
        .dimensions(x, y, buttonWidth, buttonHeight)
        .build();
        
        this.addDrawableChild(toggleButton);
    }
}
