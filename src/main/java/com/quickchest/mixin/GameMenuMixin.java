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
    private void injectWebPanel(CallbackInfo ci) {
        int centerX = this.width / 2;

        // --- MAIN CONTROL BUTTON (The "Web" Toggle) ---
        this.addDrawableChild(ButtonWidget.builder(
            Text.of(DupeSettings.isExpanded ? "§c✖ Close Elite Panel" : "§b⚡ Open Elite Panel"), 
            button -> {
                DupeSettings.isExpanded = !DupeSettings.isExpanded;
                this.clearAndInit(); // Refresh UI to show/hide sub-buttons
            }).dimensions(centerX - 102, 10, 204, 20).build());

        // --- SUB-MENU (Only shows when Expanded) ---
        if (DupeSettings.isExpanded) {
            // MODE SELECTOR (CTD / EPC)
            this.addDrawableChild(ButtonWidget.builder(
                Text.of("Mode: " + DupeSettings.getModeLabel()), 
                button -> {
                    DupeSettings.dupeMode = (DupeSettings.dupeMode + 1) % 3;
                    button.setMessage(Text.of("Mode: " + DupeSettings.getModeLabel()));
                }).dimensions(centerX - 102, 35, 100, 20).build());

            // AUTO-EXIT TOGGLE
            this.addDrawableChild(ButtonWidget.builder(
                Text.of("Auto-Exit: " + (DupeSettings.autoExit ? "§aON" : "§cOFF")), 
                button -> {
                    DupeSettings.autoExit = !DupeSettings.autoExit;
                    button.setMessage(Text.of("Auto-Exit: " + (DupeSettings.autoExit ? "§aON" : "§cOFF")));
                }).dimensions(centerX + 2, 35, 100, 20).build());
                
            // SEPARATOR LINE (Status)
            this.addDrawableChild(ButtonWidget.builder(
                Text.of("§eStatus: Ready to Dupe"), b -> {}).dimensions(centerX - 102, 60, 204, 20).build());
        }
    }
}
