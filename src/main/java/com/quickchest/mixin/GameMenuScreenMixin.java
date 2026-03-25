package com.quickchest.mixin;

import com.quickchest.QuickChestMod;
import com.quickchest.gui.QuickChestSettingsScreen;
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

    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void addQuickChestButton(CallbackInfo ci) {
        // Ek hi button — Settings screen kholega
        ButtonWidget btn = ButtonWidget.builder(
            Text.literal(QuickChestMod.isEnabled()
                ? "§6⚡ QuickChest Settings"
                : "§8⚡ QuickChest (OFF)"),
            b -> {
                assert this.client != null;
                this.client.setScreen(
                    new QuickChestSettingsScreen(this));
            }
        )
        .dimensions(
            this.width / 2 - 80,
            this.height / 4 - 24,
            160, 20
        )
        .build();

        this.addDrawableChild(btn);
    }
}
