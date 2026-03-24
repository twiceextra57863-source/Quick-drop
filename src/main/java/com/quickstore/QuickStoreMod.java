package com.quickstore;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class QuickStoreMod implements ClientModInitializer {
    
    public static final String MOD_ID = "quickstore";
    private static boolean enabled = true;
    private static KeyBinding toggleKey;
    private static boolean isProcessing = false;
    private static long lastActionTime = 0;
    private static final long COOLDOWN_MS = 1000;
    
    @Override
    public void onInitializeClient() {
        System.out.println("[QuickStoreMod] Loaded for Minecraft 1.21.11!");
        System.out.println("[QuickStoreMod] Features: Nautilus, Spear, Locator Bar supported!");
        
        // Toggle key (K)
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.quickstore.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.quickstore"
        ));
        
        // Key handler
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && toggleKey.wasPressed()) {
                enabled = !enabled;
                client.player.sendMessage(
                    Text.literal((enabled ? "§a✔" : "§c✘") + " QuickStore: " + (enabled ? "ON" : "OFF")),
                    true
                );
            }
        });
        
        // Right-click handler
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!enabled || isProcessing) return;
            if (client.player == null || client.interactionManager == null) return;
            
            if (client.options.useKey.isPressed()) {
                handleRightClick(client);
            }
        });
    }
    
    private void handleRightClick(MinecraftClient client) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastActionTime < COOLDOWN_MS) return;
        
        HitResult hit = client.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return;
        
        BlockHitResult blockHit = (BlockHitResult) hit;
        var world = client.world;
        var pos = blockHit.getBlockPos();
        
        if (world == null) return;
        
        // Check for chest (includes new 1.21.11 blocks)
        String blockName = world.getBlockState(pos).getBlock().toString().toLowerCase();
        boolean isChest = blockName.contains("chest") || 
                          blockName.contains("barrel") || 
                          blockName.contains("shulker") ||
                          blockName.contains("copper_chest"); // 1.21.11 copper chest
        
        if (!isChest) return;
        
        ItemStack heldItem = client.player.getMainHandStack();
        if (heldItem.isEmpty()) return;
        
        isProcessing = true;
        lastActionTime = currentTime;
        
        String itemName = heldItem.getName().getString();
        int itemCount = heldItem.getCount();
        
        client.player.sendMessage(
            Text.literal("§e⚡ QuickStore: " + itemName + " x" + itemCount),
            true
        );
        
        new Thread(() -> {
            try {
                Thread.sleep(150);
                client.execute(() -> {
                    // Open chest if not open
                    if (!(client.player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
                        client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, blockHit);
                    }
                    
                    try { Thread.sleep(100); } catch (InterruptedException e) {}
                    
                    client.execute(() -> {
                        storeItemInChest(client, heldItem, itemName, itemCount);
                        dropItemFromHand(client, heldItem, itemName, itemCount);
                        isProcessing = false;
                    });
                });
            } catch (Exception e) {
                isProcessing = false;
            }
        }).start();
    }
    
    private void storeItemInChest(MinecraftClient client, ItemStack item, String name, int count) {
        if (client.player == null || !(client.player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
            return;
        }
        
        try {
            int hotbarSlot = client.player.getInventory().selectedSlot;
            int sourceSlot = hotbarSlot + 36;
            
            client.interactionManager.clickSlot(
                client.player.currentScreenHandler.syncId,
                sourceSlot,
                0,
                SlotActionType.QUICK_MOVE,
                client.player
            );
            
            client.player.sendMessage(Text.literal("§a✔ [STORED] " + name + " x" + count), true);
        } catch (Exception e) {
            client.player.sendMessage(Text.literal("§c✗ Store failed"), true);
        }
    }
    
    private void dropItemFromHand(MinecraftClient client, ItemStack item, String name, int count) {
        if (client.player == null) return;
        
        try {
            int hotbarSlot = client.player.getInventory().selectedSlot;
            int sourceSlot = hotbarSlot + 36;
            
            int windowId = client.player.currentScreenHandler != null ? 
                          client.player.currentScreenHandler.syncId : 0;
            
            client.interactionManager.clickSlot(
                windowId,
                sourceSlot,
                1,
                SlotActionType.THROW,
                client.player
            );
            
            client.player.sendMessage(Text.literal("§c✘ [DROPPED] " + name + " x" + count), true);
        } catch (Exception e) {
            client.player.sendMessage(Text.literal("§c✗ Drop failed"), true);
        }
    }
}
