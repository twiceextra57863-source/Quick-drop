package com.tclient.mod.mixin;

import com.tclient.mod.gui.TClientScreen;
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

    // Mixin classes abstract honi chahiye aur constructor ki jarurat nahi hoti
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    // 1.21.4 mein "init" method use karna sabse safe hai
    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        int btnW = 80;
        int btnH = 20;
        int btnX = this.width - btnW - 10;
        int btnY = 10;

        // Button build karke add karna
        this.addDrawableChild(
            ButtonWidget.builder(
                Text.literal("§b■ T Client"), 
                button -> {
                    if (this.client != null) {
                        this.client.setScreen(new TClientScreen());
                    }
                }
            )
            .dimensions(btnX, btnY, btnW, btnH)
            .build()
        );
    }
}
