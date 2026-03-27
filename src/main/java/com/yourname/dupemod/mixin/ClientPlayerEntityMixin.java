package com.yourname.dupemod.mixin;

import com.yourname.dupemod.feature.ChestDupeEngine;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    private boolean wasInChest = false;

    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen instanceof GenericContainerScreen) {
            if (!wasInChest) {
                ChestDupeEngine.onChestOpen(); // Chest khulte hi dupe start!
                wasInChest = true;
            }
        } else {
            wasInChest = false;
        }
    }
}

