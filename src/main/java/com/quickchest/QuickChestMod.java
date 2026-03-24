package com.quickchest;

import com.quickchest.config.QuickChestConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickChestMod implements ClientModInitializer {

    public static final String MOD_ID = "quickchest";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final long INTERACT_COOLDOWN_MS = 300;

    private static long lastInteractTime = 0;
    private static boolean pendingStore = false;
    private static BlockPos pendingChestPos = null;
    private static long pendingStoreTime = 0;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[QuickChest] Initialized for Minecraft 1.21.4!");
        QuickChestConfig.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!QuickChestConfig.isEnabled()) return;
            if (!pendingStore) return;
            if (client.player == null || client.world == null) return;

            long now = System.currentTimeMillis();
            if (now - pendingStoreTime >= INTERACT_COOLDOWN_MS) {
                performStoreAction(client, pendingChestPos);
                pendingStore = false;
                pendingChestPos = null;
            }
        });
    }

    public static boolean handleChestClick(BlockPos chestPos) {
        if (!QuickChestConfig.isEnabled()) return false;

        long now = System.currentTimeMillis();
        if (now - lastInteractTime < INTERACT_COOLDOWN_MS) return false;
        lastInteractTime = now;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return false;

        // Step 1: Drop held item immediately
        ItemStack heldItem = client.player.getMainHandStack();
        if (!heldItem.isEmpty()) {
            performDropAction(client);
            LOGGER.info("[QuickChest] Dropped: {}", heldItem.getItem().getName().getString());
        }

        // Step 2: Schedule store after 0.3 sec
        pendingStore = true;
        pendingChestPos = chestPos;
        pendingStoreTime = now;

        return true;
    }

    private static void performDropAction(MinecraftClient client) {
        if (client.player == null) return;
        // 1.21.4 - dropSelectedItem still works same
        client.player.dropSelectedItem(true);
    }

    private static void performStoreAction(MinecraftClient client, BlockPos chestPos) {
        if (client.player == null || client.world == null || chestPos == null) return;

        World world = client.world;
        BlockEntity be = world.getBlockEntity(chestPos);

        if (!(be instanceof ChestBlockEntity chest)) {
            LOGGER.warn("[QuickChest] No chest at {}", chestPos);
            return;
        }

        ItemStack heldItem = client.player.getMainHandStack();
        if (heldItem.isEmpty()) {
            LOGGER.info("[QuickChest] Hand empty, nothing to store.");
            return;
        }

        // Find empty slot in chest
        for (int i = 0; i < chest.size(); i++) {
            if (chest.getStack(i).isEmpty()) {
                chest.setStack(i, heldItem.copy());
                // 1.21.4 compatible way to clear hand
                client.player.getInventory().main.set(
                    client.player.getInventory().selectedSlot,
                    ItemStack.EMPTY
                );
                LOGGER.info("[QuickChest] Stored in slot {}", i);

                // Send success message to player
                client.player.sendMessage(
                    Text.literal("§a[QuickChest] Item stored in chest!"), true
                );
                return;
            }
        }

        // No space in chest
        client.player.sendMessage(
            Text.literal("§c[QuickChest] Chest is full!"), true
        );
    }

    public static boolean isEnabled() {
        return QuickChestConfig.isEnabled();
    }

    public static void toggle() {
        QuickChestConfig.setEnabled(!QuickChestConfig.isEnabled());
        QuickChestConfig.save();

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            String msg = QuickChestConfig.isEnabled()
                ? "§a[QuickChest] Enabled!"
                : "§c[QuickChest] Disabled!";
            client.player.sendMessage(Text.literal(msg), true);
        }
    }
}
