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
    @Unique private int timer = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || DupeSettings.dupeMode == 0) return;

        // --- MODE 1: CTD (The Chest Sniper) ---
        if (DupeSettings.dupeMode == 1) {
            // Agar koi bhi screen khuli hai (Chest, Barrel, etc.)
            if (client.currentScreen != null && client.player.currentScreenHandler != null) {
                timer++;
                // 5 Ticks par trigger (Fast response for 1.21.4)
                if (timer >= 5) {
                    int sId = client.player.currentScreenHandler.syncId;
                    int rev = client.player.currentScreenHandler.getRevision();

                    // AGGRESSIVE PACKET BURST
                    for (int i = 0; i < 15; i++) {
                        client.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
                            sId, rev, 0, 0, SlotActionType.QUICK_MOVE, ItemStack.EMPTY, Int2ObjectMaps.emptyMap()
                        ));
                    }
                    
                    // Force Sync & Close
                    client.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(sId));
                    client.execute(() -> client.player.closeHandledScreen());
                    timer = 0;
                }
            } else {
                timer = 0;
            }
        }

        // --- MODE 2: EPC (The Off-Hand God) ---
        if (DupeSettings.dupeMode == 2) {
            timer++;
            if (timer % 2 == 0) {
                // Drop and Swap logic
                client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, BlockPos.ORIGIN, Direction.DOWN
                ));
                client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(
                    (client.player.getInventory().selectedSlot + 1) % 9
                ));
            }
        }
    }
}
