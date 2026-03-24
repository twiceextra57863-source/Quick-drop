package com.quickchest.mixin;

import com.quickchest.QuickChestMod;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends net.minecraft.client.gui.screen.Screen {

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    // 1.21.4: initWidgets() is the correct method name
    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void addQuickChestToggleButton(CallbackInfo ci) {
        int btnW = 160;
        int btnH = 20;

        // Place button at top-center of pause menu, above other buttons
        int x = this.width / 2 - btnW / 2;
        int y = this.height / 4 - 24; // Above main menu buttons

        String label = QuickChestMod.isEnabled()
            ? "§a▶ Quick Chest: ON"
            : "§c■ Quick Chest: OFF";

        ButtonWidget toggleBtn = ButtonWidget.builder(
            Text.literal(label),
            btn -> {
                QuickChestMod.toggle();
                String newLabel = QuickChestMod.isEnabled()
                    ? "§a▶ Quick Chest: ON"
                    : "§c■ Quick Chest: OFF";
                btn.setMessage(Text.literal(newLabel));
            }
        )
        .dimensions(x, y, btnW, btnH)
        .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(
            Text.literal("Click chest = Drop + Auto-Store in 0.3s")
        ))
        .build();

        this.addDrawableChild(toggleBtn);
    }
}
