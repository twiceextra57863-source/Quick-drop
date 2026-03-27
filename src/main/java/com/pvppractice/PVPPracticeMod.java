package com.pvppractice;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PVPPracticeMod implements ModInitializer {
    public static final String MOD_ID = "pvppractice";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static KeyBinding openDashboardKey;
    
    @Override
    public void onInitialize() {
        LOGGER.info("PVP Practice Mod Initialized!");
        
        // Register keybinding for opening dashboard
        openDashboardKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.pvppractice.open_dashboard",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_T,
            "category.pvppractice.main"
        ));
        
        // Client-side registration
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openDashboardKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new com.pvppractice.client.gui.PVPDashboardScreen());
                }
            }
        });
    }
}
