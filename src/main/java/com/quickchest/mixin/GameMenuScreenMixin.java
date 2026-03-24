package com.quickchest.mixin;

import com.quickchest.QuickChestMod;
import com.quickchest.gui.QuickToggleButton;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }
    
    @Inject(method = "init", at = @At("RETURN"))
    private void addQuickChestButton(CallbackInfo ci) {
        // Calculate button position
        int buttonWidth = 150;
        int buttonHeight = 20;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 4 - 24; // Position above main menu buttons
        
        // Create toggle button
        ButtonWidget toggleButton = QuickToggleButton.create(
            x, y, buttonWidth, buttonHeight,
            button -> {
                QuickChestMod.toggleMod();
                // Update button text
                button.setMessage(Text.literal("Quick Chest: " + 
                    (QuickChestMod.isEnabled() ? "ON" : "OFF")));
            }
        );
        
        // Set initial text
        toggleButton.setMessage(Text.literal("Quick Chest: " + 
            (QuickChestMod.isEnabled() ? "ON" : "OFF")));
        
        this.addDrawableChild(toggleButton);
    }
}
