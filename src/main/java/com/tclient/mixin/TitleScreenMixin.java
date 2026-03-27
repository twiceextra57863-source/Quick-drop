package com.tclient.mixin;

import com.tclient.gui.TClientScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) { super(title); }

    @Inject(at = @At("TAIL"), method = "init")
    private void addTClientButton(CallbackInfo info) {
        int x = this.width / 2 - 100;
        int y = this.height / 4 + 48 + 72 + 12; // Vanilla buttons ke niche

        this.addDrawableChild(ButtonWidget.builder(Text.literal("§bT-Client Settings"), button -> {
            if (this.client != null) {
                this.client.setScreen(new TClientScreen(this));
            }
        }).dimensions(x, y, 200, 20).build());
    }
}
