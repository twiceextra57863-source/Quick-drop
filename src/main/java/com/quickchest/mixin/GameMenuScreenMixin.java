package com.quickchest.mixin;

import com.quickchest.QuickChestMod;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void addQuickChestButton(CallbackInfo ci) {
        int btnW = 160;
        int btnH = 20;
        int x = this.width / 2 - btnW / 2;
        int y = this.height / 4 - 24;

        String label = QuickChestMod.isEnabled()
            ? "§aQuick Chest: ON"
            : "§cQuick Chest: OFF";

        ButtonWidget btn = ButtonWidget.builder(
            Text.literal(label),
            button -> {
                QuickChestMod.toggle();
                button.setMessage(Text.literal(
                    QuickChestMod.isEnabled()
                        ? "§aQuick Chest: ON"
                        : "§cQuick Chest: OFF"
                ));
            }
        )
        .dimensions(x, y, btnW, btnH)
        .tooltip(Tooltip.of(Text.literal("Toggle Quick Chest drop+store")))
        .build();

        this.addDrawableChild(btn);
    }
}
