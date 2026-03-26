package com.quickchest.mixin;

import com.quickchest.DupeSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;

@Mixin(ClientPlayNetworkHandler.class)
public class DupeNetworkMixin {
    @Unique private int autoTickCounter = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // MODE 1: AUTO-HOPPER (Puran wala optimized)
        if (DupeSettings.dupeMode == 1 && client.currentScreen != null) {
            autoTickCounter++;
            if (autoTickCounter >= 7) {
                int syncId = client.player.currentScreenHandler.syncId;
                int rev = client.player.currentScreenHandler.getRevision();
                
                for (int i = 0; i < 5; i++) {
                    client.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(syncId, rev, 0, 0, SlotActionType.QUICK_MOVE, ItemStack.EMPTY, Int2ObjectMaps.emptyMap()));
                }
                client.getNetworkHandler().sendPacket(new CloseScreenC2SPacket(syncId));
                client.execute(() -> client.player.closeHandledScreen());
                autoTickCounter = 0;
            }
        }

        // MODE 2: PACKET-DROP (Naya Feature - 100% Desync)
        // Isme item ko hath me pakad kar button dabana hota hai
        if (DupeSettings.dupeMode == 2 && client.currentScreen == null) {
            // Ye mode tab trigger hota hai jab aap bina inventory khole item hath me lekar khade ho
            // Iska trigger hum input handler me dalenge ya tick spam me
        }
    }
}
