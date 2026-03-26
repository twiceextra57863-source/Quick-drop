package com.quickchest.mixin;

import com.quickchest.DupeSettings; // <--- Import Settings
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Collections;

@Mixin(ClientPlayNetworkHandler.class)
public class HopperDupeMixin {
    @Unique private int hopperTickCounter = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Check if enabled from Menu + Container is open
        if (DupeSettings.isEnabled && client.player != null && client.currentScreen != null) {
            hopperTickCounter++;
            if (hopperTickCounter >= 8) {
                int syncId = client.player.currentScreenHandler.syncId;
                
                // Packet sending logic...
                client.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
                    syncId, client.player.currentScreenHandler.getRevision(),
                    0, 0, SlotActionType.QUICK_MOVE, ItemStack.EMPTY, Collections.emptyMap()
                ));
                
                hopperTickCounter = 0;
            }
        }
    }
}
