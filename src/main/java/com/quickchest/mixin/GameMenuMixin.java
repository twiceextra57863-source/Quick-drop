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
    protected GameMenuMixin(Text title) {
        super(title);
    }

    @Inject(method = "initWidgets", at = @At("HEAD"))
    private void addDupeButton(CallbackInfo ci) {
        // Button text dynamic rakhenge (ON/OFF dikhane ke liye)
        Text buttonText = Text.of("Dupe Mod: " + (DupeSettings.isEnabled ? "§aON" : "§cOFF"));

        // Button ko menu mein add karna (X, Y, Width, Height)
        this.addDrawableChild(ButtonWidget.builder(buttonText, button -> {
            DupeSettings.isEnabled = !DupeSettings.isEnabled; // Toggle logic
            button.setMessage(Text.of("Dupe Mod: " + (DupeSettings.isEnabled ? "§aON" : "§cOFF")));
        }).dimensions(this.width / 2 - 102, 10, 204, 20).build()); 
        // Ye button bilkul top par aayega
    }
}

