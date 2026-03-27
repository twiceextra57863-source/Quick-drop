package com.yourname.dupemod.feature;

import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ChestDupeEngine {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public static int delay = 50; // ms
    public static boolean enabled = false;

    public static void executeDupeSequence(int syncSlot) {
        if (mc.player == null || mc.interactionManager == null) return;

        new Thread(() -> {
            try {
                // 1. AUTO-STORE (Shift-Click into Chest)
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, syncSlot, 0, SlotActionType.QUICK_MOVE, mc.player);
                Thread.sleep(delay);

                // 2. AUTO-SWAP (Main to Offhand)
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
                
                // 3. AUTO-DROP
                mc.player.dropSelectedItem(true);
                
                // 4. AUTO-PICK (Optional logic for high speed pick can go here)
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }
}
