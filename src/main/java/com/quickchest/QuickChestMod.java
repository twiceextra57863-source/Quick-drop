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
    private static long lastClickTime = 0;
    private static final long COOLDOWN_MS = 300;
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("Quick Chest Mod Initialized!");
        
        File configFile = new File(MinecraftClient.getInstance().runDirectory, "config/quickchest.json");
        config = ModConfig.load(configFile);
        isEnabled = config.enabled;
        
        // Register K key to toggle mod
        toggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.quickchest.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.quickchest"
        ));
        
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
}
