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
        // High Command Toggle Button
        this.addDrawableChild(ButtonWidget.builder(DupeSettings.getStatusText(), button -> {
            DupeSettings.dupeMode = (DupeSettings.dupeMode + 1) % 3;
            button.setMessage(DupeSettings.getStatusText());
        }).dimensions(this.width / 2 - 102, 10, 204, 20).build());
    }
}
