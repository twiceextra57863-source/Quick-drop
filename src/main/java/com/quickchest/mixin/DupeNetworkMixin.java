package com.quickchest.mixin;

import com.quickchest.DupeSettings;
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
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;

@Mixin(ClientPlayNetworkHandler.class)
public class DupeNetworkMixin {

    @Unique
    private int autoTickCounter = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Check: Mod ON hai aur koi Container (Chest/Hopper) khula hai
        if (DupeSettings.isEnabled && client.player != null && client.currentScreen != null) {
            
            autoTickCounter++;

            // Hopper har 8 ticks mein item leta hai. Hum har 8th tick par snipe karenge.
            if (autoTickCounter >= 8) {
                int syncId = client.player.currentScreenHandler.syncId;
                int revision = client.player.currentScreenHandler.getRevision();

                // AUTOMATIC PACKET FLOOD:
                // Ek saath 15 packets bhej rahe hain taaki server-side 'Race Condition' paida ho.
                for (int i = 0; i < 15; i++) {
                    client.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
                        syncId,
                        revision,
                        0, // Slot 0 (Pehla box)
                        0, // Button (Left Click)
                        SlotActionType.QUICK_MOVE, // Shift-Click automatic
                        ItemStack.EMPTY,
                        Int2ObjectMaps.emptyMap()
                    ));
                }
                
                autoTickCounter = 0; // Reset for next transfer
            }
        } else {
            autoTickCounter = 0; // Reset agar screen band ho jaye
        }
    }
}
