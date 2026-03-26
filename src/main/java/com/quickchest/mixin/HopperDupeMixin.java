package com.quickchest.mixin;

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

    @Unique
    private int hopperTickCounter = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        // 1. Check karo agar player game mein hai aur container (Hopper/Chest) khula hai
        if (client.player != null && client.currentScreen != null) {
            
            // 2. Hopper 8-tick cooldown par chalta hai, hum 8th tick ko "Snipe" karenge
            hopperTickCounter++;
            
            if (hopperTickCounter >= 8) {
                int syncId = client.player.currentScreenHandler.syncId;

                // 3. Double Action: Ek hi tick mein packet spam
                // Isse server confuse hota hai ki item Hopper ne liya ya Player ne (Shift-Click)
                for (int i = 0; i < 3; i++) { // 3 baar spam for better success
                    client.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
                        syncId,
                        client.player.currentScreenHandler.getRevision(),
                        0, // Slot 0 (Hopper ka pehla slot)
                        0, // Button (Left Click)
                        SlotActionType.QUICK_MOVE, // Shift+Click action
                        ItemStack.EMPTY,
                        Collections.emptyMap()
                    ));
                }
                
                hopperTickCounter = 0; // Reset counter
            }
        } else {
            hopperTickCounter = 0; // Agar screen band hai toh reset
        }
    }
}

