package com.quickchest.mixin;

import com.quickchest.DupeSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class DupeNetworkMixin {
    @Unique private int clock = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onEliteTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || DupeSettings.dupeMode == 0) return;

        // --- MODE 2: EPC + AUTO-EXIT (THE GOD-RUN) ---
        if (DupeSettings.dupeMode == 2 && client.currentScreen == null) {
            // Check if player is holding something (Hath mein item hona chahiye)
            if (!client.player.getMainHandStack().isEmpty()) {
                
                // 1. Drop Packet
                client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, BlockPos.ORIGIN, Direction.DOWN
                ));
                
                // 2. Slot Switch Desync
                client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket((client.player.getInventory().selectedSlot + 1) % 9));

                // 3. AUTO-EXIT TRIGGER
                if (DupeSettings.autoExit) {
                    // Instant Disconnect with Fake Timeout Message
                    client.getNetworkHandler().getConnection().disconnect(Text.of("§cConnection Lost: Timed Out (Dupe Success)"));
                    DupeSettings.dupeMode = 0; // Turn off to prevent loop
                }
            }
        }
    }
}
