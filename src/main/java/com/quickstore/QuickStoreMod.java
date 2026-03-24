package com.quickstore;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

public class QuickStoreMod implements ClientModInitializer {
    
    public static final String MOD_ID = "quickstore";
    private static boolean enabled = true;
    private static KeyBinding toggleKey;
    private static boolean isProcessing = false;
    private static long lastActionTime = 0;
    private static final long COOLDOWN_MS = 1000; // 1 second cooldown
    
    @Override
    public void onInitializeClient() {
        System.out.println("========================================");
        System.out.println("QuickStoreMod v1.0.0 Loaded!");
        System.out.println("Right-click on chest with item in hand:");
        System.out.println("  → Item STORES in chest");
        System.out.println("  → Item DROPS on ground");
        System.out.println("  → BOTH actions happen together!");
        System.out.println("========================================");
        
        // Register toggle key (K)
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.quickstore.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.quickstore"
        ));
        
        // Handle key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && toggleKey.wasPressed()) {
                enabled = !enabled;
                client.player.sendMessage(
                    Text.literal((enabled ? "§a✔" : "§c✘") + " QuickStore: " + (enabled ? "ON" : "OFF")),
                    true
                );
            }
        });
        
        // Register right-click handler
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!enabled) return;
            if (client.player == null) return;
            if (client.interactionManager == null) return;
            if (isProcessing) return;
            
            // Check if right-click is pressed
            if (client.options.useKey.isPressed()) {
                handleRightClick(client);
            }
        });
        
        System.out.println("[QuickStore] Controls: Press K to toggle ON/OFF");
        System.out.println("[QuickStore] COOLDOWN: " + COOLDOWN_MS + "ms");
    }
    
    private void handleRightClick(MinecraftClient client) {
        // Prevent multiple triggers
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastActionTime < COOLDOWN_MS) {
            return;
        }
        
        // Check what player is looking at
        HitResult hit = client.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }
        
        BlockHitResult blockHit = (BlockHitResult) hit;
        World world = client.world;
        BlockPos pos = blockHit.getBlockPos();
        
        if (world == null) return;
        
        // Check if looking at chest
        String blockName = world.getBlockState(pos).getBlock().toString().toLowerCase();
        boolean isChest = blockName.contains("chest") || 
                          blockName.contains("barrel") || 
                          blockName.contains("shulker");
        
        if (!isChest) return;
        
        // Get item in hand
        ItemStack heldItem = client.player.getMainHandStack();
        
        if (heldItem.isEmpty()) {
            if (currentTime - lastActionTime > COOLDOWN_MS) {
                client.player.sendMessage(
                    Text.literal("§e⚡ No item in hand!"),
                    true
                );
            }
            return;
        }
        
        // Mark as processing to prevent spam
        isProcessing = true;
        lastActionTime = currentTime;
        
        String itemName = heldItem.getName().getString();
        int itemCount = heldItem.getCount();
        
        client.player.sendMessage(
            Text.literal("§e⚡ QuickStore: Processing " + itemName + " x" + itemCount),
            true
        );
        
        // Schedule both actions with a small delay
        new Thread(() -> {
            try {
                Thread.sleep(100); // Small delay to ensure chest opens
                
                client.execute(() -> {
                    // FIRST: Open chest if not already open
                    if (!(client.player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
                        // Simulate opening chest
                        client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, blockHit);
                    }
                    
                    // Small delay for chest to open
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    client.execute(() -> {
                        // DO BOTH ACTIONS TOGETHER
                        storeItemInChest(client, heldItem, itemName, itemCount);
                        dropItemFromHand(client, heldItem, itemName, itemCount);
                        
                        isProcessing = false;
                    });
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                isProcessing = false;
            }
        }).start();
    }
    
    private void storeItemInChest(MinecraftClient client, ItemStack item, String itemName, int count) {
        if (client.player == null || client.interactionManager == null) return;
        
        // Check if chest container is open
        if (!(client.player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
            client.player.sendMessage(
                Text.literal("§c✗ Could not open chest!"),
                true
            );
            return;
        }
        
        try {
            // Get hotbar slot (0-8) and convert to inventory slot (36 + hotbar slot)
            int hotbarSlot = client.player.getInventory().selectedSlot;
            int sourceSlot = hotbarSlot + 36;
            
            // Shift+click to move to chest
            client.interactionManager.clickSlot(
                client.player.currentScreenHandler.syncId,
                sourceSlot,
                0,  // Button
                SlotActionType.QUICK_MOVE,  // Shift+click
                client.player
            );
            
            client.player.sendMessage(
                Text.literal("§a✔ [STORED] " + itemName + " x" + count + " in chest!"),
                true
            );
            
        } catch (Exception e) {
            client.player.sendMessage(
                Text.literal("§c✗ Failed to store: " + e.getMessage()),
                true
            );
        }
    }
    
    private void dropItemFromHand(MinecraftClient client, ItemStack item, String itemName, int count) {
        if (client.player == null || client.interactionManager == null) return;
        
        try {
            int hotbarSlot = client.player.getInventory().selectedSlot;
            int sourceSlot = hotbarSlot + 36;
            
            int windowId = 0;
            if (client.player.currentScreenHandler != null) {
                windowId = client.player.currentScreenHandler.syncId;
            }
            
            // Drop entire stack on ground
            // Button 1 + THROW = drop entire stack
            client.interactionManager.clickSlot(
                windowId,
                sourceSlot,
                1,  // Button 1 = drop whole stack
                SlotActionType.THROW,
                client.player
            );
            
            client.player.sendMessage(
                Text.literal("§c✘ [DROPPED] " + itemName + " x" + count + " on ground!"),
                true
            );
            
        } catch (Exception e) {
            client.player.sendMessage(
                Text.literal("§c✗ Failed to drop: " + e.getMessage()),
                true
            );
        }
    }
                                                                }
