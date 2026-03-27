package com.pvppractice.mixin.client;

import com.pvppractice.client.gui.PVPDashboardScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    
    protected TitleScreenMixin(Text title) {
        super(title);
    }
    
    @Inject(method = "init", at = @At(value = "RETURN"))
    private void onInit(CallbackInfo ci) {
        TitleScreen titleScreen = (TitleScreen)(Object)this;
        
        // Add custom button to title screen
        // Position: Right side, below the existing buttons
        int buttonWidth = 120;
        int buttonHeight = 20;
        int x = titleScreen.width / 2 + 100;
        int y = titleScreen.height / 4 + 48 + 72;
        
        // Create and add the PVP Practice button
        ButtonWidget pvpButton = ButtonWidget.builder(
            Text.literal("§c❤ PVP Practice"),
            button -> {
                if (this.client != null) {
                    this.client.setScreen(new PVPDashboardScreen());
                }
            }
        ).dimensions(x, y, buttonWidth, buttonHeight).build();
        
        titleScreen.addDrawableChild(pvpButton);
        
        // Optional: Add a second button for quick settings
        ButtonWidget quickSettingsButton = ButtonWidget.builder(
            Text.literal("§6⚙ Quick Settings"),
            button -> {
                if (this.client != null) {
                    // Could open quick settings screen here
                    // For now, just open the dashboard
                    this.client.setScreen(new PVPDashboardScreen());
                }
            }
        ).dimensions(x, y + buttonHeight + 4, buttonWidth, buttonHeight).build();
        
        titleScreen.addDrawableChild(quickSettingsButton);
    }
    
    @Inject(method = "render", at = @At(value = "HEAD"))
    private void onRender(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Optional: Add custom rendering to title screen
        // For example, draw a custom logo or text
        TitleScreen titleScreen = (TitleScreen)(Object)this;
        
        // Draw a small indicator that the mod is active
        context.drawText(
            this.textRenderer, 
            "§aPVP Practice Mod Active", 
            titleScreen.width - 10 - this.textRenderer.getWidth("§aPVP Practice Mod Active"), 
            5, 
            0x55FF55, 
            false
        );
    }
    
    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void onTick(CallbackInfo ci) {
        // Optional: Handle title screen tick logic
        // For example, animate button colors or check for key presses
        TitleScreen titleScreen = (TitleScreen)(Object)this;
        
        // Check for key presses on title screen
        if (this.client != null && this.client.currentScreen instanceof TitleScreen) {
            // You could add keybind handling here if needed
        }
    }
    
    @Inject(method = "removed", at = @At(value = "HEAD"))
    private void onRemoved(CallbackInfo ci) {
        // Clean up when title screen is closed
        // This is called when leaving the title screen
    }
}
