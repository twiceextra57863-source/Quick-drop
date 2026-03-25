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

    private static final long COOLDOWN_MS = 100L;
    private static long lastActionTime = 0L;
    private static long tickCount = 0L;

    // Manual mode
    private static boolean pendingReturn = false;
    private static ItemStack itemToReturn = null;
    private static long returnAtTick = 0L;

    // Auto mode
    private static boolean autoModeActive = false;
    private static BlockPos autoChestPos = null;
    private static ItemStack autoItem = null;
    private static AutoPhase autoPhase = AutoPhase.IDLE;
    private static long autoNextActionTick = 0L;
    private static int autoCycleCount = 0;

    enum AutoPhase {
        IDLE, STORE, PICK, DROP, PICKUP, DONE
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("[QuickChest] Loaded!");
        QuickChestConfig.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCount++;
            if (client.player == null) return;
            handleManualReturn(client);
            handleAutoMode(client);
        });
    }

    // =============================================
    // MANUAL MODE
    // =============================================
    public static boolean handleChestClick(BlockPos chestPos) {
        if (!QuickChestConfig.isEnabled()) return false;

        if (QuickChestConfig.isAutoMode()) {
            return startAutoMode(chestPos);
        }

        long now = System.currentTimeMillis();
        if (now - lastActionTime < COOLDOWN_MS) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return false;

        ClientPlayerEntity player = client.player;
        World world = client.world;

        ItemStack held = player.getMainHandStack();
        if (held.isEmpty()) return false;

        BlockEntity be = world.getBlockEntity(chestPos);
        if (!(be instanceof ChestBlockEntity chest)) return false;

        int emptySlot = findEmptyChestSlot(chest);
        if (emptySlot == -1) {
            player.sendMessage(Text.literal("§c[QuickChest] Chest full!"), true);
            return false;
        }

        lastActionTime = now;
        ItemStack stored = held.copy();

        chest.setStack(emptySlot, stored.copy());
        player.getInventory().main.set(
            player.getInventory().selectedSlot, ItemStack.EMPTY
        );

        playSound(client, SoundEvents.ENTITY_ITEM_PICKUP, 0.6f, 1.2f);

        pendingReturn = true;
        itemToReturn = stored.copy();
        returnAtTick = tickCount + QuickChestConfig.getReturnDelayTicks();

        player.sendMessage(
            Text.literal("§a[QuickChest] §fStored! Returning in "
                + QuickChestConfig.getReturnDelayTicks() + " ticks..."),
            true
        );

        return true;
    }

    private static void handleManualReturn(MinecraftClient client) {
        if (!pendingReturn) return;
        if (tickCount < returnAtTick) return;
        returnItemToInventory(client, itemToReturn);
        pendingReturn = false;
        itemToReturn = null;
    }

    // =============================================
    // AUTO MODE
    // =============================================
    private static boolean startAutoMode(BlockPos chestPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return false;

        ItemStack held = client.player.getMainHandStack();
        if (held.isEmpty()) return false;

        BlockEntity be = client.world.getBlockEntity(chestPos);
        if (!(be instanceof ChestBlockEntity)) return false;

        autoModeActive = true;
        autoChestPos = chestPos;
        autoItem = held.copy();
        autoPhase = AutoPhase.STORE;
        autoNextActionTick = tickCount + 1L;
        autoCycleCount = 0;

        client.player.sendMessage(
            Text.literal("§b[QuickChest] §fAuto started! §e"
                + QuickChestConfig.getAutoCycles() + " cycles @ "
                + getSpeedLabel(QuickChestConfig.getAutoSpeedTicks())),
            true
        );

        return true;
    }

    private static void handleAutoMode(MinecraftClient client) {
        if (!autoModeActive) return;
        if (tickCount < autoNextActionTick) return;
        if (client.player == null || client.world == null) return;

        ClientPlayerEntity player = client.player;
        World world = client.world;
        long speed = QuickChestConfig.getAutoSpeedTicks();

        switch (autoPhase) {

            case STORE -> {
                BlockEntity be = world.getBlockEntity(autoChestPos);
                if (!(be instanceof ChestBlockEntity chest)) {
                    stopAutoMode(client, "Chest not found!");
                    return;
                }
                int slot = findEmptyChestSlot(chest);
                if (slot == -1) {
                    stopAutoMode(client, "Chest full!");
                    return;
                }
                chest.setStack(slot, autoItem.copy());
                player.getInventory().main.set(
                    player.getInventory().selectedSlot, ItemStack.EMPTY
                );
                playSound(client, SoundEvents.ENTITY_ITEM_PICKUP, 0.7f, 1.3f);
                autoPhase = AutoPhase.PICK;
                autoNextActionTick = tickCount + speed;
            }

            case PICK -> {
                BlockEntity be = world.getBlockEntity(autoChestPos);
                if (!(be instanceof ChestBlockEntity chest)) {
                    stopAutoMode(client, "Chest not found!");
                    return;
                }
                for (int i = 0; i < chest.size(); i++) {
                    ItemStack stack = chest.getStack(i);
                    if (!stack.isEmpty() &&
                        stack.getItem().equals(autoItem.getItem())) {
                        chest.removeStack(i);
                        returnItemToInventory(client, autoItem.copy());
                        playSound(client,
                            SoundEvents.ENTITY_ITEM_PICKUP, 0.5f, 0.9f);
                        break;
                    }
                }
                autoPhase = AutoPhase.DROP;
                autoNextActionTick = tickCount + speed;
            }

            case DROP -> {
    ItemStack currentHeld = player.getMainHandStack();
    if (!currentHeld.isEmpty()) {
        player.dropSelectedItem(false);
        playSound(client,
            SoundEvents.ENTITY_SNOWBALL_THROW, 0.4f, 1.0f); // ✅ fixed
    }
    autoPhase = AutoPhase.PICKUP;
    autoNextActionTick = tickCount + speed;
            }

            case PICKUP -> {
                returnItemToInventory(client, autoItem.copy());
                playSound(client,
                    SoundEvents.ENTITY_ITEM_PICKUP, 0.6f, 1.1f);
                autoCycleCount++;

                player.sendMessage(
                    Text.literal("§b[QuickChest] §fCycle §e"
                        + autoCycleCount + "§f/§e"
                        + QuickChestConfig.getAutoCycles() + " §a✔"),
                    true
                );

                if (autoCycleCount >= QuickChestConfig.getAutoCycles()) {
                    autoPhase = AutoPhase.DONE;
                    autoNextActionTick = tickCount + 1L;
                } else {
                    autoPhase = AutoPhase.STORE;
                    autoNextActionTick = tickCount + speed;
                }
            }

            case DONE -> stopAutoMode(client, null);
        }
    }

    private static void stopAutoMode(MinecraftClient client, String reason) {
        autoModeActive = false;
        autoChestPos = null;
        autoItem = null;
        autoPhase = AutoPhase.IDLE;
        autoCycleCount = 0;

        if (client.player == null) return;

        if (reason != null) {
            client.player.sendMessage(
                Text.literal("§c[QuickChest] §fStopped: " + reason), true);
        } else {
            client.player.sendMessage(
                Text.literal("§a[QuickChest] §fAuto complete! §e"
                    + QuickChestConfig.getAutoCycles() + " cycles ✔"),
                true
            );
            playSound(client, SoundEvents.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
        }
    }

    // =============================================
    // HELPERS
    // =============================================
    private static int findEmptyChestSlot(ChestBlockEntity chest) {
        for (int i = 0; i < chest.size(); i++) {
            if (chest.getStack(i).isEmpty()) return i;
        }
        return -1;
    }

    private static void returnItemToInventory(MinecraftClient client,
                                               ItemStack item) {
        if (client.player == null || item == null) return;
        ClientPlayerEntity player = client.player;
        int sel = player.getInventory().selectedSlot;

        if (player.getInventory().getStack(sel).isEmpty()) {
            player.getInventory().main.set(sel, item.copy());
            return;
        }
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().main.get(i).isEmpty()) {
                player.getInventory().main.set(i, item.copy());
                return;
            }
        }
        for (int i = 9; i < 36; i++) {
            if (player.getInventory().main.get(i).isEmpty()) {
                player.getInventory().main.set(i, item.copy());
                return;
            }
        }
        client.player.dropItem(item, false);
    }

    private static void playSound(MinecraftClient client,
        net.minecraft.sound.SoundEvent sound, float volume, float pitch) {
        if (client.player == null || client.world == null) return;
        client.world.playSound(
            client.player, client.player.getBlockPos(),
            sound, SoundCategory.PLAYERS, volume, pitch
        );
    }

    public static String getSpeedLabel(int ticks) {
        return switch (ticks) {
            case 1  -> "MAX ⚡";
            case 2  -> "Very Fast";
            case 3  -> "Fast";
            case 4  -> "Medium-Fast";
            case 5  -> "Medium";
            case 6  -> "Medium-Slow";
            case 7  -> "Slow";
            case 8  -> "Slow";
            case 9  -> "Very Slow";
            default -> "Slowest";
        };
    }

    // =============================================
    // PUBLIC TOGGLE METHODS
    // =============================================
    public static boolean isEnabled() {
        return QuickChestConfig.isEnabled();
    }

    public static boolean isAutoMode() {
        return QuickChestConfig.isAutoMode();
    }

    public static void toggle() {
        QuickChestConfig.setEnabled(!QuickChestConfig.isEnabled());
        QuickChestConfig.save();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal(
                QuickChestConfig.isEnabled()
                    ? "§a[QuickChest] ON"
                    : "§c[QuickChest] OFF"
            ), true);
        }
    }

    public static void toggleAutoMode() {
        QuickChestConfig.setAutoMode(!QuickChestConfig.isAutoMode());
        QuickChestConfig.save();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal(
                QuickChestConfig.isAutoMode()
                    ? "§b[QuickChest] §fAuto Mode §aON"
                    : "§b[QuickChest] §fAuto Mode §cOFF"
            ), true);
        }
    }
}
