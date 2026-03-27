package com.yourname.speedchestmod;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class AutomationLogic {
    
    // State variables
    public static int currentRepetitions = 0;
    public static boolean isRunning = false;
    public static ItemStack storedItem = ItemStack.EMPTY;

    public static void startAutomation(PlayerEntity player, ScreenHandler handler) {
        if (!ModConfig.getInstance().enabled) return;

        // Reset state
        currentRepetitions = 0;
        isRunning = true;
        
        // Capture item in hand immediately
        storedItem = player.getStackInHand(Hand.MAIN_HAND).copy();
        
        // Pehla action turant karo
        executeStep(player, handler);
    }

    public static void tick(PlayerEntity player, ScreenHandler handler) {
        if (!isRunning || !ModConfig.getInstance().enabled) {
            isRunning = false;
            return;
        }

        // Check if chest is still open
        if (player.currentScreenHandler != handler) {
            isRunning = false;
            return;
        }

        // High speed loop: Ek tick mein multiple actions agar speed 0.1 hai
        // Kyunki game 1 tick mein rukta hai, hum yahan loop chala ke "fast forward" karte hain
        int actionsPerTick = (int) (1.0 / ModConfig.getInstance().speedTicks); 
        if (actionsPerTick < 1) actionsPerTick = 1; // Safety

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

        // ACTION 1: STORE (Haath ka item chest mein daalo)
        if (!storedItem.isEmpty()) {
            insertItemIntoInventory(chestInv, storedItem);
            // Haath se remove kiya (simulate store)
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        }

        // ACTION 2: DROP & SWAP (Chest se nikaalo aur drop karo ya swap karo)
        // Hum chest se wapas item leke ground pe drop karenge (Challenge mechanic)
        ItemStack picked = takeOneItemFromInventory(chestInv);
        
        if (!picked.isEmpty()) {
            // Drop Item Entity
            World world = player.getWorld();
            ItemEntity entity = new ItemEntity(world, player.getX(), player.getY() + 0.5, player.getZ(), picked);
            world.spawnEntity(entity);
            
            // Optional: Swap logic (agar offhand mein kuch hai to swap)
            // Abhi simple drop rakha hai challenge ke liye
        }

        // ACTION 3: AUTO PICK (Wapas chest se utha ke haath mein lao - Next round ke liye)
        // Agla item uthao taaki agle loop mein store ho sake
        storedItem = takeOneItemFromInventory(chestInv);
        
        currentRepetitions++;
    }

    // Helper: Inventory extract karna
    private static ItemStack takeOneItemFromInventory(Inventory inv) {
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty()) {
                ItemStack split = stack.split(1); // 1 item nikalo
                inv.markDirty();
                return split;
            }
        }
        return ItemStack.EMPTY;
    }

    // Helper: Inventory insert karna
    private static void insertItemIntoInventory(Inventory inv, ItemStack stack) {
        // Simple merge logic
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getStack(i).isEmpty()) {
                inv.setStack(i, stack.copy());
                stack.setCount(0);
                inv.markDirty();
                return;
            }
        }
    }
    
    private static Inventory getInventoryFromHandler(ScreenHandler handler) {
        if (handler instanceof net.minecraft.screen.GenericContainerScreenHandler) {
            return ((net.minecraft.screen.GenericContainerScreenHandler) handler).inventory;
        }
        // Add other chest types here if needed
        return null;
    }
}
