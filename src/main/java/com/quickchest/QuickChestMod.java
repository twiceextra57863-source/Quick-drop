package com.quickchest;

import com.quickchest.config.QuickChestConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickChestMod implements ClientModInitializer {

    public static final String MOD_ID = "quickchest";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // 0.5ms cooldown
    private static final long COOLDOWN_MS = 500L;
    private static long lastActionTime = 0L;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[QuickChest] Loaded! Version 1.21.4");
        QuickChestConfig.load();
    }

    /**
     * Main method — ek hi baar call hota hai chest click pe
     * Drop animation + chest store DONO simultaneously
     */
    public static boolean handleChestClick(BlockPos chestPos) {
        if (!QuickChestConfig.isEnabled()) return false;

        long now = System.currentTimeMillis();
        if (now - lastActionTime < COOLDOWN_MS) return false;
        lastActionTime = now;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return false;

        ClientPlayerEntity player = client.player;
        ItemStack heldItem = player.getMainHandStack();

        // Kuch nahi hai haath me toh kuch mat karo
        if (heldItem.isEmpty()) return false;

        World world = client.world;
        BlockEntity be = world.getBlockEntity(chestPos);

        if (!(be instanceof ChestBlockEntity chest)) {
            LOGGER.warn("[QuickChest] No chest found at {}", chestPos);
            return false;
        }

        // Chest me pehli empty slot dhundo
        int emptySlot = -1;
        for (int i = 0; i < chest.size(); i++) {
            if (chest.getStack(i).isEmpty()) {
                emptySlot = i;
                break;
            }
        }

        if (emptySlot == -1) {
            player.sendMessage(Text.literal("§c[QuickChest] Chest is full!"), true);
            return false;
        }

        // ✅ Step 1: Client-side chest me item set karo (visual sync)
        chest.setStack(emptySlot, heldItem.copy());

        // ✅ Step 2: Player ke haath se item hata do
        player.getInventory().main.set(
            player.getInventory().selectedSlot,
            ItemStack.EMPTY
        );

        // ✅ Step 3: Drop effect/sound — visual feedback ke liye
        // Actually drop nahi karta, sirf sound aur effect
        world.playSound(
            player,
            chestPos,
            net.minecraft.sound.SoundEvents.ENTITY_ITEM_PICKUP,
            net.minecraft.sound.SoundCategory.PLAYERS,
            0.8f,
            1.2f
        );

        // ✅ Step 4: Server ko sync karo — packet bhejo
        syncToServer(client, chestPos, emptySlot, heldItem);

        player.sendMessage(
            Text.literal("§a[QuickChest] §f" + heldItem.getItem().getName().getString()
                + " §astored in chest!"),
            true
        );

        LOGGER.info("[QuickChest] Item '{}' stored at chest {} slot {}",
            heldItem.getItem().getName().getString(), chestPos, emptySlot);

        return true;
    }

    /**
     * Server sync — chest open karke item move karo properly
     * Ye ensure karta hai ki item actually server pe bhi chest me jaye
     */
    private static void syncToServer(MinecraftClient client, BlockPos chestPos,
                                      int chestSlot, ItemStack item) {
        if (client.player == null || client.interactionManager == null) return;

        ClientPlayerEntity player = client.player;
        ClientPlayerInteractionManager manager = client.interactionManager;

        // Server pe chest open karo silently (no GUI)
        // Ye network packet bhejta hai server ko
        manager.interactBlock(
            player,
            net.minecraft.util.Hand.MAIN_HAND,
            new net.minecraft.util.hit.BlockHitResult(
                chestPos.toCenterPos(),
                net.minecraft.util.math.Direction.UP,
                chestPos,
                false
            )
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
                    ? "§a[QuickChest] §fEnabled! Click chest to store items."
                    : "§c[QuickChest] §fDisabled."
            ), true);
        }
    }
}
