package com.quickchest.mixin;

import com.quickchest.DupeSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;

@Mixin(ClientPlayNetworkHandler.class)
public class DupeNetworkMixin {
    @Unique private int tickClock = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onEngineTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || DupeSettings.dupeMode == 0) return;

        tickClock++;

        // --- MODE 1: CTD (Container Transaction Desync) ---
        if (DupeSettings.dupeMode == 1 && client.currentScreen != null) {
            if (tickClock % 8 == 0) { // Snipe on Hopper Transfer Tick
                int sId = client.player.currentScreenHandler.syncId;
                int rev = client.player.currentScreenHandler.getRevision();

                // Advanced Packet Burst: High-frequency interaction
                for (int i = 0; i < 12; i++) {
                    client.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(sId, rev, 0, 0, SlotActionType.QUICK_MOVE, ItemStack.EMPTY, Int2ObjectMaps.emptyMap()));
                }
                
                // Force Close to prevent Server-Side Inventory Verification
                client.getNetworkHandler().sendPacket(new CloseScreenC2SPacket(sId));
                client.execute(() -> client.player.closeHandledScreen());
            }
        }

        // --- MODE 2: EPC (Entity Packet Cramming) ---
        if (DupeSettings.dupeMode == 2 && client.currentScreen == null) {
            // Logic: Drop item but block the "Inventory Update" from server
            // Best used while standing in Portals or high lag areas
            if (tickClock % 2 == 0) {
                client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, client.player.getBlockPos(), net.minecraft.util.math.Direction.DOWN));
                // Immediately swap to confuse Entity tracker
                client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket((client.player.getInventory().selectedSlot + 1) % 9));
            }
        }
    }
}
