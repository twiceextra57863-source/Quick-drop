package com.quickchest.mixin;

import com.quickchest.DupeSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class DupeNetworkMixin {
    @Unique private int nanoClock = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onUltraTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || DupeSettings.dupeMode == 0) return;

        // --- MODE 2: EPC ULTRA (Main + Off Hand) ---
        if (DupeSettings.dupeMode == 2 && client.currentScreen == null) {
            
            // 0.4 Tick Speed Simulation (Hard-coded high frequency)
            for (int i = 0; i < 2; i++) {
                
                // 1. MAIN-HAND DROP & SWAP
                if (!client.player.getMainHandStack().isEmpty()) {
                    sendDropPackets(client, Hand.MAIN_HAND);
                }

                // 2. OFF-HAND DROP & SWAP (New Feature)
                if (!client.player.getOffHandStack().isEmpty()) {
                    // Packet to swap main and offhand before dropping
                    client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN
                    ));
                    sendDropPackets(client, Hand.OFF_HAND);
                }
            }

            // AUTO-EXIT EXECUTION
            if (DupeSettings.autoExit && (!client.player.getMainHandStack().isEmpty() || !client.player.getOffHandStack().isEmpty())) {
                client.getNetworkHandler().getConnection().disconnect(Text.of("§6[Elite-Dupe] §eTimed Out (Sync-Success)"));
                DupeSettings.dupeMode = 0;
            }
        }
    }

    @Unique
    private void sendDropPackets(MinecraftClient client, Hand hand) {
        // High-Speed Drop Packet
        client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
            PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, BlockPos.ORIGIN, Direction.DOWN
        ));
        
        // Slot Desync (Moving between slots to confuse server tracking)
        int currentSlot = client.player.getInventory().selectedSlot;
        client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket((currentSlot + 1) % 9));
        
        // Hand Swing for Packet Masking (Looks like a normal action to server)
        client.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));
    }
}
