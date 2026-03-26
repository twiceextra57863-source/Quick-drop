package com.quickchest.mixin;

import com.quickchest.DupeSettings;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuMixin extends Screen {
    protected GameMenuMixin(Text title) { super(title); }

    @Inject(method = "initWidgets", at = @At("HEAD"))
    private void injectElitePanel(CallbackInfo ci) {
        // Mode Selector Button
        this.addDrawableChild(ButtonWidget.builder(Text.of("Mode: " + DupeSettings.getStatusText().getString().split(" ")[0]), button -> {
            DupeSettings.dupeMode = (DupeSettings.dupeMode + 1) % 3;
            button.setMessage(Text.of("Mode: " + DupeSettings.getStatusText().getString().split(" ")[0]));
        }).dimensions(this.width / 2 - 102, 10, 100, 20).build());

        // Auto-Exit Toggle Button
        this.addDrawableChild(ButtonWidget.builder(Text.of("Auto-Exit: " + (DupeSettings.autoExit ? "§aON" : "§cOFF")), button -> {
            DupeSettings.autoExit = !DupeSettings.autoExit;
            button.setMessage(Text.of("Auto-Exit: " + (DupeSettings.autoExit ? "§aON" : "§cOFF")));
        }).dimensions(this.width / 2 + 2, 10, 100, 20).build());
    }
}
