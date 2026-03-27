package com.sikandar.tpvpmod.mixin;

import com.sikandar.tpvpmod.TPVPDashboardScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "initWidgetsNormal", at = @At("RETURN"))
    private void addTPVPButton(int y, int spacingY, CallbackInfo ci) {
        TitleScreen screen = (TitleScreen) (Object) this;

        ButtonWidget tpvpButton = ButtonWidget.builder(
                Text.literal("§cT PVP"),
                button -> screen.client.setScreen(new TPVPDashboardScreen())
        ).dimensions(screen.width / 2 - 100, y + spacingY * 3, 200, 20).build();  // position adjust kar sakta hai

        screen.addDrawableChild(tpvpButton);
    }
}
