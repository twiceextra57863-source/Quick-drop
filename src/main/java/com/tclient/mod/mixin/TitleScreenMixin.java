package com.tclient.mod.mixin;

import com.tclient.mod.gui.TClientScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends net.minecraft.client.gui.screen.Screen {

    protected TitleScreenMixin() {
        super(Text.literal("Title Screen"));
    }

    // "init" nahi — "initWidgetsNormal" use karo 1.21 mein
    @Inject(method = "initWidgetsNormal", at = @At("TAIL"))
    private void onInitWidgetsNormal(CallbackInfo ci) {
        int btnW = 80;
        int btnH = 18;
        int btnX = this.width - btnW - 6;
        int btnY = 6;

        this.addDrawableChild(
            ButtonWidget.builder(
                Text.literal("§b■ T Client"),
                button -> this.client.setScreen(new TClientScreen())
            )
            .dimensions(btnX, btnY, btnW, btnH)
            .build()
        );
    }
}
