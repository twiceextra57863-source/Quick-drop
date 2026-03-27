package com.yourname.speedchestmod;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.screen.GenericContainerScreenHandler;

public class AutomationLogic {
    
    public static int currentRepetitions = 0;
    public static boolean isRunning = false;
    public static ItemStack storedItem = ItemStack.EMPTY;

    public static void startAutomation(PlayerEntity player, ScreenHandler handler) {
        if (!ModConfig.getInstance().enabled) return;

        currentRepetitions = 0;
        isRunning = true;
        storedItem = player.getStackInHand(Hand.MAIN_HAND).copy();
        
        executeStep(player, handler);
    }

    public static void tick(PlayerEntity player, ScreenHandler handler) {
        if (!isRunning || !ModConfig.getInstance().enabled) {
            isRunning = false;
            return;
        }

        if (player.currentScreenHandler != handler) {
            isRunning = false;
            return;
        }

        // Calculate actions per tick based on speed setting
        double speed = ModConfig.getInstance().speedTicks;
        int actionsPerTick = (int) Math.ceil(1.0 / speed); 
        if (actionsPerTick < 1) actionsPerTick = 1;

        for (int i = 0; i < actionsPerTick; i++) {
            if (currentRepetitions >= ModConfig.getInstance().repeatCount) {
                isRunning = false;
                break;
            }
            executeStep(player, handler);
        }
    }

    private static void executeStep(PlayerEntity player, ScreenHandler handler) {
        Inventory chestInv = getInventoryFromHandler(handler);
        if (chestInv == null) return;

        // ACTION 1: STORE
        if (!storedItem.isEmpty()) {
            insertItemIntoInventory(chestInv, storedItem);
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        }

        // ACTION 2: DROP & SWAP (Take from chest and drop)
        ItemStack picked = takeOneItemFromInventory(chestInv);
        
        if (!picked.isEmpty()) {
            World world = player.getWorld();
            ItemEntity entity = new ItemEntity(world, player.getX(), player.getY() + 0.5, player.getZ(), picked);
            world.spawnEntity(entity);
        }

        // ACTION 3: AUTO PICK (Prepare for next loop)
        storedItem = takeOneItemFromInventory(chestInv);
        
        currentRepetitions++;
    }

    private static ItemStack takeOneItemFromInventory(Inventory inv) {
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty()) {
                ItemStack split = stack.split(1);
                inv.markDirty();
                return split;
            }
        }
        return ItemStack.EMPTY;
    }

    private static void insertItemIntoInventory(Inventory inv, ItemStack stack) {
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getStack(i).isEmpty()) {
                inv.setStack(i, stack.copy());
                stack.setCount(0);
                inv.markDirty();
                return;
            }
        }
    }
    
    // Fixed: Using public method getInventory() instead of private field
    private static Inventory getInventoryFromHandler(ScreenHandler handler) {
        if (handler instanceof GenericContainerScreenHandler) {
            return ((GenericContainerScreenHandler) handler).getInventory();
        }
        return null;
    }
}
