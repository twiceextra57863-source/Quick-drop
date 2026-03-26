package com.quickchest.mixin;

import com.quickchest.DupeSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.screen.GenericContainerScreenHandler; // <--- Sirf Chest/Storage ke liye
import net.minecraft.screen.HopperScreenHandler;           // <--- Hopper ke liye
import net.minecraft.screen.ShulkerBoxScreenHandler;      // <--- Shulker ke liye
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

        // --- MODE 1: STORAGE SNIPER (CHEST/BARREL/SHULKER ONLY) ---
        if (DupeSettings.dupeMode == 1) {
            var handler = client.player.currentScreenHandler;
            
            // ELITE CHECK: Sirf tab chalega jab Storage GUI khula ho (Escape Menu ignore hoga)
            if (handler instanceof GenericContainerScreenHandler || 
                handler instanceof ShulkerBoxScreenHandler || 
                handler instanceof HopperScreenHandler) {
                
                timer++;
                if (timer >= 6) { // 6 Ticks for better stability
                    int sId = handler.syncId;
                    int rev = handler.getRevision();

                    // PACKET BURST (High Command)
                    for (int i = 0; i < 15; i++) {
                        client.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
                            sId, rev, 0, 0, SlotActionType.QUICK_MOVE, ItemStack.EMPTY, Int2ObjectMaps.emptyMap()
                        ));
                    }
                    
                    // Force Sync & Auto-Close
                    client.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(sId));
                    client.execute(() -> client.player.closeHandledScreen());
                    timer = 0;
                }
            } else {
                timer = 0; // Reset if it's Inventory or Escape Menu
            }
        }

        // --- MODE 2: EPC (OFF-HAND/DROP) ---
        if (DupeSettings.dupeMode == 2 && client.currentScreen == null) {
            timer++;
            if (timer % 2 == 0) {
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
