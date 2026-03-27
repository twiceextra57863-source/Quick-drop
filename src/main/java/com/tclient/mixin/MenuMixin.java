package com.tclient.mixin;

import com.tclient.gui.TClientScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TitleScreen.class, GameMenuScreen.class})
public abstract class MenuMixin extends Screen {
    protected MenuMixin(Text title) { super(title); }

    @Inject(at = @At("TAIL"), method = "init")
    private void addTClientButton(CallbackInfo info) {
        int yPos = (this instanceof TitleScreen) ? this.height / 4 + 48 + 72 + 12 : this.height / 4 + 120;
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("T Client"), button -> {
            this.client.setScreen(new TClientScreen(this));
        }).dimensions(this.width / 2 - 100, yPos, 200, 20).build());
    }
}
