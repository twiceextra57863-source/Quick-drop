package com.yourname.dupemod.mixin;

import com.yourname.dupemod.gui.DupeScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) { super(title); }

    @Inject(at = @At("TAIL"), method = "initWidgets")
    private void addDupeButton(CallbackInfo ci) {
        this.addDrawableChild(ButtonWidget.builder(Text.of("§b[ DUPE MENU ]"), (button) -> {
            this.client.setScreen(new DupeScreen(this));
        }).dimensions(this.width / 2 - 102, this.height / 4 + 144, 204, 20).build());
    }
}
