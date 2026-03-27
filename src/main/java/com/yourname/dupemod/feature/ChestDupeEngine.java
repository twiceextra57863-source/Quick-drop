package com.yourname.dupemod.feature;

import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ChestDupeEngine {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    // Default Settings
    public static boolean enabled = false;
    public static int delay = 50; 
    public static int iterations = 1; // Kitni baar repeat karna hai
    public static boolean autoPick = true;

    public static void onChestOpen() {
        if (!enabled || mc.player == null || mc.interactionManager == null) return;

        new Thread(() -> {
            try {
                for (int i = 0; i < iterations; i++) {
                    // 1. AUTO-STORE (First item in Hotbar to Chest)
                    // Slot 0 is usually the first slot in a chest
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 0, 0, SlotActionType.QUICK_MOVE, mc.player);
                    Thread.sleep(delay);

                    // 2. AUTO-SWAP
                    mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
                    
                    // 3. AUTO-DROP
                    mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, BlockPos.ORIGIN, Direction.DOWN));

                    // 4. AUTO-PICK (Instant)
                    if (autoPick) {
                        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                            PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, BlockPos.ORIGIN, Direction.DOWN));
                    }
                    Thread.sleep(delay);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
