package com.quickchest;

import com.quickchest.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class QuickChestMod implements ClientModInitializer {
    public static final String MOD_ID = "quickchest";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static ModConfig config;
    private static boolean isEnabled = true;
    private static KeyBinding toggleKeyBinding;
    
    // Cooldown tracking
    private static long lastClickTime = 0;
    private static final long COOLDOWN_MS = 300; // 0.3 seconds
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("Quick Chest Mod Initialized!");
        
        // Load config
        File configFile = new File(MinecraftClient.getInstance().runDirectory, "config/quickchest.json");
        config = ModConfig.load(configFile);
        isEnabled = config.enabled;
        
        // Register toggle keybinding (default: K)
        toggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.quickchest.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.quickchest"
        ));
        
        // Tick event to handle keybinding
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleKeyBinding.wasPressed()) {
                toggleMod();
            }
        });
        
        LOGGER.info("Quick Chest Mod - Press K to toggle ON/OFF");
    }
    
    public static boolean isEnabled() {
        return isEnabled;
    }
    
    public static void toggleMod() {
        isEnabled = !isEnabled;
        config.enabled = isEnabled;
        config.save();
        LOGGER.info("Quick Chest Mod toggled: " + (isEnabled ? "ON" : "OFF"));
    }
    
    public static boolean canPerformAction() {
        if (!isEnabled) return false;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= COOLDOWN_MS) {
            lastClickTime = currentTime;
            return true;
        }
        return false;
    }
    
    public static void performDropAndStore(MinecraftClient client) {
        if (!canPerformAction()) return;
        
        if (client.player != null && client.interactionManager != null) {
            // Get currently held item
            var stack = client.player.getMainHandStack();
            
            if (!stack.isEmpty()) {
                // Step 1: Drop the item (simulate drop key)
                // In modern MC, we need to use the drop key binding
                client.player.dropSelectedItem(false); // false = not full stack? Actually false drops 1 item
                LOGGER.debug("Dropped item: {}", stack.getItem().getName().getString());
            }
            
            // Step 2: Store items from inventory to chest
            // This will be handled in the mixin when chest is clicked
        }
    }
}
