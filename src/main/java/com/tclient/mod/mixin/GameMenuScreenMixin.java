package com.tclient.mod.mixin;

import com.tclient.mod.gui.TClientScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends net.minecraft.client.gui.screen.Screen {

    protected GameMenuScreenMixin() { super(Text.literal("Game Menu")); }

    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void onInitWidgets(CallbackInfo ci) {
        int btnW = 100, btnH = 18;
        this.addDrawableChild(
            ButtonWidget.builder(
                Text.literal("■ T CLIENT"),
                button -> this.client.setScreen(new TClientScreen())
            )
            .dimensions((this.width - btnW) / 2, 10, btnW, btnH)
            .build()
        );
    }
}
