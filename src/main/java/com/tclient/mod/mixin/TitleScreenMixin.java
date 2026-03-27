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

    protected TitleScreenMixin() { super(Text.literal("Title Screen")); }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        int btnW = 72, btnH = 16;
        this.addDrawableChild(
            ButtonWidget.builder(
                Text.literal("■ T Client"),
                button -> this.client.setScreen(new TClientScreen())
            )
            .dimensions(this.width - btnW - 4, 4, btnW, btnH)
            .build()
        );
    }
}
