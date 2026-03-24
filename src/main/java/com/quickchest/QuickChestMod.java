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

    private static final long DELAY_MS = 300L;

    private static long lastInteractTime = 0L;
    private static boolean pendingStore = false;
    private static BlockPos pendingChestPos = null;
    private static long pendingStoreTime = 0L;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[QuickChest] Loaded for 1.21.4!");
        QuickChestConfig.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!QuickChestConfig.isEnabled()) return;
            if (!pendingStore) return;
            if (client.player == null || client.world == null) return;

            if (System.currentTimeMillis() - pendingStoreTime >= DELAY_MS) {
                performStoreAction(client, pendingChestPos);
                pendingStore = false;
                pendingChestPos = null;
            }
        });
    }

    public static boolean handleChestClick(BlockPos pos) {
        if (!QuickChestConfig.isEnabled()) return false;

        long now = System.currentTimeMillis();
        if (now - lastInteractTime < DELAY_MS) return false;
        lastInteractTime = now;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return false;

        ItemStack held = client.player.getMainHandStack();
        if (!held.isEmpty()) {
            client.player.dropSelectedItem(true);
            LOGGER.info("[QuickChest] Dropped: {}", held.getItem().getName().getString());
        }

        pendingStore = true;
        pendingChestPos = pos;
        pendingStoreTime = now;
        return true;
    }

    private static void performStoreAction(MinecraftClient client, BlockPos pos) {
        if (client.player == null || client.world == null || pos == null) return;

        World world = client.world;
        BlockEntity be = world.getBlockEntity(pos);

        if (!(be instanceof ChestBlockEntity chest)) {
            LOGGER.warn("[QuickChest] No chest at {}", pos);
            return;
        }

        ItemStack held = client.player.getMainHandStack();
        if (held.isEmpty()) return;

        for (int i = 0; i < chest.size(); i++) {
            if (chest.getStack(i).isEmpty()) {
                chest.setStack(i, held.copy());
                client.player.getInventory().main.set(
                    client.player.getInventory().selectedSlot,
                    ItemStack.EMPTY
                );
                client.player.sendMessage(
                    Text.literal("§a[QuickChest] Stored!"), true
                );
                LOGGER.info("[QuickChest] Stored in slot {}", i);
                return;
            }
        }

        client.player.sendMessage(
            Text.literal("§c[QuickChest] Chest full!"), true
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
            client.player.sendMessage(Text.literal(
                QuickChestConfig.isEnabled()
                    ? "§a[QuickChest] Enabled!"
                    : "§c[QuickChest] Disabled!"
            ), true);
        }
    }
}
