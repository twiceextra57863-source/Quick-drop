package com.quickchest;

import com.quickchest.config.QuickChestConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickChestMod implements ClientModInitializer {

    public static final String MOD_ID = "quickchest";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final long COOLDOWN_MS = 100L; // 0.1 second

    private static long lastActionTime = 0L;

    // Return item pending state
    private static boolean pendingReturn = false;
    private static ItemStack itemToReturn = null;
    private static long returnTime = 0L;
    private static final long RETURN_DELAY_TICKS = 2L; // ~100ms baad wapas

    // Tick counter
    private static long tickCount = 0L;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[QuickChest] Loaded!");
        QuickChestConfig.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCount++;

            // Pending return check
            if (!pendingReturn) return;
            if (client.player == null) return;
            if (tickCount < returnTime) return;

            // Item wapas inventory me do
            returnItemToInventory(client, itemToReturn);
            pendingReturn = false;
            itemToReturn = null;
        });
    }

    public static boolean handleChestClick(BlockPos chestPos) {
        if (!QuickChestConfig.isEnabled()) return false;

        long now = System.currentTimeMillis();
        if (now - lastActionTime < COOLDOWN_MS) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return false;

        ClientPlayerEntity player = client.player;
        World world = client.world;

        ItemStack held = player.getMainHandStack();
        if (held.isEmpty()) return false;

        // Chest access karo
        BlockEntity be = world.getBlockEntity(chestPos);
        if (!(be instanceof ChestBlockEntity chest)) return false;

        // Empty slot dhundo
        int emptySlot = -1;
        for (int i = 0; i < chest.size(); i++) {
            if (chest.getStack(i).isEmpty()) {
                emptySlot = i;
                break;
            }
        }

        if (emptySlot == -1) {
            player.sendMessage(Text.literal("§c[QuickChest] Chest full!"), true);
            return false;
        }

        lastActionTime = now;

        ItemStack storedItem = held.copy();
        String itemName = held.getItem().getName().getString();

        // ✅ STEP 1: Item chest me store karo
        chest.setStack(emptySlot, storedItem.copy());

        // ✅ STEP 2: Player haath khali karo
        player.getInventory().main.set(
            player.getInventory().selectedSlot,
            ItemStack.EMPTY
        );

        // ✅ STEP 3: Store sound
        world.playSound(
            player,
            player.getBlockPos(),
            SoundEvents.ENTITY_ITEM_PICKUP,
            SoundCategory.PLAYERS,
            0.6f, 1.2f
        );

        // ✅ STEP 4: 2 ticks (~100ms) baad item wapas inventory me
        pendingReturn = true;
        itemToReturn = storedItem.copy();
        returnTime = tickCount + RETURN_DELAY_TICKS;

        player.sendMessage(
            Text.literal("§a[QuickChest] §f" + itemName + " §astored & returning..."),
            true
        );

        LOGGER.info("[QuickChest] Stored '{}' → returning in 2 ticks", itemName);

        return true; // GUI cancel
    }

    private static void returnItemToInventory(MinecraftClient client, ItemStack item) {
        if (client.player == null || item == null) return;

        ClientPlayerEntity player = client.player;
        int selectedSlot = player.getInventory().selectedSlot;

        // Pehle selected slot pe try karo
        if (player.getInventory().getStack(selectedSlot).isEmpty()) {
            player.getInventory().main.set(selectedSlot, item.copy());
        } else {
            // Koi bhi empty slot dhundo hotbar me
            boolean placed = false;
            for (int i = 0; i < 9; i++) {
                if (player.getInventory().main.get(i).isEmpty()) {
                    player.getInventory().main.set(i, item.copy());
                    placed = true;
                    break;
                }
            }

            // Hotbar full hai toh main inventory me
            if (!placed) {
                for (int i = 9; i < 36; i++) {
                    if (player.getInventory().main.get(i).isEmpty()) {
                        player.getInventory().main.set(i, item.copy());
                        placed = true;
                        break;
                    }
                }
            }

            if (!placed) {
                // Inventory full — ground pe drop karo
                client.player.dropItem(item, false);
                client.player.sendMessage(
                    Text.literal("§e[QuickChest] §fInventory full — item dropped!"),
                    true
                );
                return;
            }
        }

        // Return sound
        if (client.world != null) {
            client.world.playSound(
                player,
                player.getBlockPos(),
                SoundEvents.ENTITY_ITEM_PICKUP,
                SoundCategory.PLAYERS,
                0.4f, 0.8f
            );
        }

        client.player.sendMessage(
            Text.literal("§b[QuickChest] §fItem returned to inventory!"),
            true
        );

        LOGGER.info("[QuickChest] Item '{}' returned to inventory",
            item.getItem().getName().getString());
    }

    public static boolean isEnabled() {
        return QuickChestConfig.isEnabled();
    }

    public static void toggleAutoMode() {
        QuickChestConfig.setAutoMode(!QuickChestConfig.isAutoMode());
        QuickChestConfig.save();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal(
                QuickChestConfig.isAutoMode()
                    ? "§b[QuickChest] §fAuto Mode §aON §7(beginner friendly)"
                    : "§b[QuickChest] §fAuto Mode §cOFF §7(manual)"
            ), true);
        }
    }
}  // class closing brace
