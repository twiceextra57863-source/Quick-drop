package com.tclient.mixin;

import com.tclient.gui.TClientScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MenuMixin extends Screen {
    protected MenuMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init")
    private void addTClientButton(CallbackInfo info) {
        // Casting 'this' to Object first to bypass Mixin's strict type checking
        Object currentScreen = (Object) this;

        // Check if we are on TitleScreen or GameMenuScreen
        if (currentScreen instanceof TitleScreen || currentScreen instanceof GameMenuScreen) {
            
            // Positioning logic
            int x = this.width / 2 - 100;
            int y = (currentScreen instanceof TitleScreen) ? (this.height / 4 + 48 + 72 + 12) : (this.height / 4 + 120);

            // Add T-Client Button
            this.addDrawableChild(ButtonWidget.builder(Text.literal("§bT-Client Settings"), button -> {
                if (this.client != null) {
                    this.client.setScreen(new TClientScreen(this));
                }
            }).dimensions(x, y, 200, 20).build());
        }
    }
}
