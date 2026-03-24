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
public class GameMenuScreenMixin extends Screen {
    
    private ButtonWidget quickChestButton;
    
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }
    
    @Inject(method = "init", at = @At("RETURN"))
    private void addQuickChestButton(CallbackInfo ci) {
        int buttonWidth = 150;
        int buttonHeight = 20;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 4 - 24;
        
        quickChestButton = QuickToggleButton.create(
            x, y, buttonWidth, buttonHeight,
            button -> {
                QuickChestMod.toggleMod();
                QuickToggleButton.updateButtonText(button, QuickChestMod.isEnabled());
            }
        );
        
        QuickToggleButton.updateButtonText(quickChestButton, QuickChestMod.isEnabled());
        this.addDrawableChild(quickChestButton);
    }
}
