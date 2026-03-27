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
public abstract class TitleScreenMixin extends net.minecraft.client.gui.screen.Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "addNormalWidgets", at = @At("RETURN"))
    private void addTPVPButton(int y, int spacingY, CallbackInfo ci) {
        ButtonWidget tpvpButton = ButtonWidget.builder(
                Text.literal("§cT PVP"),
                button -> this.client.setScreen(new TPVPDashboardScreen())
        ).dimensions(this.width / 2 - 100, y + spacingY * 3, 200, 20).build();   // position adjust kar sakta hai (*3 ya *4 try kar)

        this.addDrawableChild(tpvpButton);
    }
}
