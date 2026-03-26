package com.quickchest.mixin;

import com.quickchest.DupeSettings;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuMixin extends Screen {
    protected GameMenuMixin(Text title) {
        super(title);
    }

    @Inject(method = "initWidgets", at = @At("HEAD"))
    private void injectElitePanel(CallbackInfo ci) {
        // --- BUTTON 1: MODE SELECTOR (Left Side) ---
        this.addDrawableChild(ButtonWidget.builder(getModeText(), button -> {
            // Cycle: 0 (OFF) -> 1 (CTD) -> 2 (EPC)
            DupeSettings.dupeMode = (DupeSettings.dupeMode + 1) % 3;
            button.setMessage(getModeText());
        }).dimensions(this.width / 2 - 102, 10, 100, 20).build());

        // --- BUTTON 2: AUTO-EXIT TOGGLE (Right Side) ---
        this.addDrawableChild(ButtonWidget.builder(getExitText(), button -> {
            DupeSettings.autoExit = !DupeSettings.autoExit;
            button.setMessage(getExitText());
        }).dimensions(this.width / 2 + 2, 10, 100, 20).build());
    }

    @Unique
    private Text getModeText() {
        String modeName = switch (DupeSettings.dupeMode) {
            case 1 -> "§6[CTD]";
            case 2 -> "§b[EPC]";
            default -> "§7OFF";
        };
        return Text.of("Mode: " + modeName);
    }

    @Unique
    private Text getExitText() {
        return Text.of("Auto-Exit: " + (DupeSettings.autoExit ? "§aON" : "§cOFF"));
    }
}
