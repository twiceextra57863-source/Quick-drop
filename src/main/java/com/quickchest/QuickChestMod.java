package com.quickchest;

import com.quickchest.config.QuickChestConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
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

    public enum AutoPhase {
        IDLE, STORE, PICK, DROP, PICKUP, DONE
    }

    // Container type
    public enum ContainerType {
        CHEST, HOPPER
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("[QuickChest] Loaded! Chest + Hopper support.");
        QuickChestConfig.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCount++;
            if (client.player == null) return;
            handleManualReturn(client);
            handleAutoMode(client);
        });
    }

    // =============================================
    // UNIFIED CONTAINER HANDLER
    // =============================================

    // Chest click — backward compat
    public static boolean handleChestClick(BlockPos pos) {
        return handleContainerClick(pos, ContainerType.CHEST);
    }

    // Universal handler — chest + hopper dono
    public static boolean handleContainerClick(BlockPos pos, ContainerType type) {
        if (!QuickChestConfig.isEnabled()) return false;

        if (QuickChestConfig.isAutoMode()) {
            return startAutoMode(pos, type);
        }

        long now = System.currentTimeMillis();
        if (now - lastActionTime < COOLDOWN_MS) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return false;

        ClientPlayerEntity player = client.player;
        World world = client.world;

        ItemStack held = player.getMainHandStack();
        if (held.isEmpty()) return false;

        // Container type ke hisaab se block entity lo
        BlockEntity be = world.getBlockEntity(pos);
        LootableContainerBlockEntity container = getContainer(be, type);

        if (container == null) {
            LOGGER.warn("[QuickChest] No {} at {}", type, pos);
            return false;
        }

        int emptySlot = findEmptySlot(container);
        if (emptySlot == -1) {
            player.sendMessage(
                Text.literal("§c[QuickChest] " + type + " full!"), true);
            return false;
        }

        lastActionTime = now;
        ItemStack stored = held.copy();

        // Store
        container.setStack(emptySlot, stored.copy());
        player.getInventory().main.set(
            player.getInventory().selectedSlot, ItemStack.EMPTY
        );

        // Sound — hopper ka alag sound
        if (type == ContainerType.HOPPER) {
            playSound(client, SoundEvents.BLOCK_HOPPER_AMBIENT, 0.8f, 1.2f);
        } else {
            playSound(client, SoundEvents.ENTITY_ITEM_PICKUP, 0.6f, 1.2f);
        }

        pendingReturn = true;
        itemToReturn = stored.copy();
        returnAtTick = tickCount + QuickChestConfig.getReturnDelayTicks();

        player.sendMessage(
            Text.literal("§a[QuickChest] §f"
                + stored.getItem().getName().getString()
                + " §astored in §e" + type.name().toLowerCase() + "§a!"),
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
    private static boolean startAutoMode(BlockPos pos, ContainerType type) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return false;

        ItemStack held = client.player.getMainHandStack();
        if (held.isEmpty()) return false;

        BlockEntity be = client.world.getBlockEntity(pos);
        if (getContainer(be, type) == null) return false;

        autoModeActive = true;
        autoChestPos = pos;
        autoItem = held.copy();
        autoPhase = AutoPhase.STORE;
        autoNextActionTick = tickCount + 1L;
        autoCycleCount = 0;

        client.player.sendMessage(
            Text.literal("§b[QuickChest] §fAuto started on §e"
                + type.name().toLowerCase() + "§f! §e"
                + QuickChestConfig.getAutoCycles() + " cycles"),
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

        // Container type detect karo
        BlockEntity be = world.getBlockEntity(autoChestPos);
        ContainerType type = ContainerType.CHEST;
        if (be instanceof HopperBlockEntity) type = ContainerType.HOPPER;

        switch (autoPhase) {

            case STORE -> {
                LootableContainerBlockEntity container =
                    getContainer(world.getBlockEntity(autoChestPos), type);
                if (container == null) {
                    stopAutoMode(client, "Container not found!");
                    return;
                }
                int slot = findEmptySlot(container);
                if (slot == -1) {
                    stopAutoMode(client, "Container full!");
                    return;
                }
                container.setStack(slot, autoItem.copy());
                player.getInventory().main.set(
                    player.getInventory().selectedSlot, ItemStack.EMPTY
                );
                playSound(client, type == ContainerType.HOPPER
                    ? SoundEvents.BLOCK_HOPPER_AMBIENT
                    : SoundEvents.ENTITY_ITEM_PICKUP, 0.7f, 1.3f);
                autoPhase = AutoPhase.PICK;
                autoNextActionTick = tickCount + speed;
            }

            case PICK -> {
                LootableContainerBlockEntity container =
                    getContainer(world.getBlockEntity(autoChestPos), type);
                if (container == null) {
                    stopAutoMode(client, "Container not found!");
                    return;
                }
                for (int i = 0; i < container.size(); i++) {
                    ItemStack stack = container.getStack(i);
                    if (!stack.isEmpty() &&
                        stack.getItem().equals(autoItem.getItem())) {
                        container.removeStack(i);
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
                if (!player.getMainHandStack().isEmpty()) {
                    player.dropSelectedItem(false);
                    playSound(client,
                        SoundEvents.ENTITY_SNOWBALL_THROW, 0.4f, 1.0f);
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
                    + QuickChestConfig.getAutoCycles() + " cycles ✔"), true);
            playSound(client, SoundEvents.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
        }
    }

    // =============================================
    // HELPERS
    // =============================================

    // Container type ke hisaab se block entity return karo
    private static LootableContainerBlockEntity getContainer(
            BlockEntity be, ContainerType type) {
        return switch (type) {
            case CHEST  -> be instanceof ChestBlockEntity c ? c : null;
            case HOPPER -> be instanceof HopperBlockEntity h ? h : null;
        };
    }

    private static int findEmptySlot(LootableContainerBlockEntity container) {
        for (int i = 0; i < container.size(); i++) {
            if (container.getStack(i).isEmpty()) return i;
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

    public static boolean isEnabled() { return QuickChestConfig.isEnabled(); }
    public static boolean isAutoMode() { return QuickChestConfig.isAutoMode(); }

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
