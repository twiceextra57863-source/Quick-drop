package com.quickchest.mixin;

import com.quickchest.DupeSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;

@Mixin(ClientPlayNetworkHandler.class)
public class DupeNetworkMixin {

    // "onTick" ko "tick" mein badla gaya hai (Correct Descriptor for 1.21)
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Check if enabled from Menu + Player is in a Container
        if (DupeSettings.isEnabled && client.player != null && client.currentScreen != null) {
            
            // Check if player's current screen is a hopper or chest
            int syncId = client.player.currentScreenHandler.syncId;
            int revision = client.player.currentScreenHandler.getRevision();

            // Packet Spamming Logic (Method 2: Zero-Tick)
            // Hum Slot 0 (Hopper/Chest ka pehla slot) ko target kar rahe hain
            client.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
                syncId,
                revision,
                0, // Slot 0
                0, // Button (Left Click)
                SlotActionType.QUICK_MOVE, // Shift+Click
                ItemStack.EMPTY,
                Collections.emptyMap()
            ));
        }
    }
}
