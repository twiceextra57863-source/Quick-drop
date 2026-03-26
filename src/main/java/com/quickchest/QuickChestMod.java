package com.quickchest;

import com.quickchest.config.QuickChestConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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

    public enum AutoPhase { IDLE, STORE, PICK, DROP, PICKUP, DONE }
    public enum ContainerType { CHEST, HOPPER }

    // ── MIXIN FIX: Ye method Mixins dhoond rahe hain ──
    public static boolean isEnabled() {
        return QuickChestConfig.isEnabled();
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("[QuickChest] 1.21 Final Fix Loaded!");
        QuickChestConfig.load();

        // ══════════════════════════════════════════════════════════
        // LAG SPAMMER ENGINE (FIXED FOR 1.21)
        // ══════════════════════════════════════════════════════════
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient && isEnabled() && hand == Hand.MAIN_HAND) {
                int count = QuickChestConfig.getActionsPerClick();
                int speed = QuickChestConfig.getSpammerSpeed();

                if (count > 1) {
                    new Thread(() -> {
                        try {
                            // 1.21 Fixed Network Handler access
                            var handler = MinecraftClient.getInstance().getNetworkHandler();
                            if (handler == null) return;

                            for (int i = 0; i < count; i++) {
                                if (MinecraftClient.getInstance().player == null) break;
                                
                                // Packet sending (Sequence 0)
                                handler.sendPacket(new PlayerInteractBlockC2SPacket(hand, hitResult, 0));
                                
                                if (speed > 0) Thread.sleep(speed);
                            }
                        } catch (Exception e) {
                            LOGGER.error("Spammer Error", e);
                        }
                    }).start();
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCount++;
            if (client.player == null) return;
            handleManualReturn(client);
            handleAutoMode(client);
        });
    }

    // =============================================
    // CONTAINER HANDLERS (OLD CODE)
    // =============================================

    public static boolean handleChestClick(BlockPos pos) {
        return handleContainerClick(pos, ContainerType.CHEST);
    }

    public static boolean handleContainerClick(BlockPos pos, ContainerType type) {
        if (!isEnabled()) return false;
        if (QuickChestConfig.isAutoMode()) return startAutoMode(pos, type);

        long now = System.currentTimeMillis();
        if (now - lastActionTime < COOLDOWN_MS) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) return false;

        BlockEntity be = client.world.getBlockEntity(pos);
        LootableContainerBlockEntity container = getContainer(be, type);
        if (container == null) return false;

        int slot = findEmptySlot(container);
        if (slot == -1) return false;

        lastActionTime = now;
        ItemStack held = player.getMainHandStack();
        ItemStack stored = held.copy();
        container.setStack(slot, stored.copy());
        player.getInventory().main.set(player.getInventory().selectedSlot, ItemStack.EMPTY);

        pendingReturn = true;
        itemToReturn = stored.copy();
        returnAtTick = tickCount + QuickChestConfig.getReturnDelayTicks();
        return true;
    }

    private static void handleManualReturn(MinecraftClient client) {
        if (pendingReturn && tickCount >= returnAtTick) {
            if (client.player != null) client.player.getInventory().insertStack(itemToReturn);
            pendingReturn = false;
            itemToReturn = null;
        }
    }

    private static boolean startAutoMode(BlockPos pos, ContainerType type) {
        autoModeActive = true;
        autoChestPos = pos;
        autoPhase = AutoPhase.STORE;
        autoNextActionTick = tickCount + 1L;
        return true;
    }

    private static void handleAutoMode(MinecraftClient client) {
        // Purana auto mode logic...
    }

    private static LootableContainerBlockEntity getContainer(BlockEntity be, ContainerType type) {
        if (type == ContainerType.CHEST && be instanceof ChestBlockEntity) return (ChestBlockEntity)be;
        if (type == ContainerType.HOPPER && be instanceof HopperBlockEntity) return (HopperBlockEntity)be;
        return (be instanceof LootableContainerBlockEntity) ? (LootableContainerBlockEntity)be : null;
    }

    private static int findEmptySlot(LootableContainerBlockEntity c) {
        for (int i = 0; i < c.size(); i++) if (c.getStack(i).isEmpty()) return i;
        return -1;
    }

    public static void toggle() { QuickChestConfig.setEnabled(!isEnabled()); QuickChestConfig.save(); }
}
