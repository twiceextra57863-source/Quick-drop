package com.yourname.dupemod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DupeMod implements ModInitializer {
    // Mod ID - Isse fabric.mod.json me match hona chahiye
    public static final String MOD_ID = "dupemod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // Jab game load hoga, ye message console me dikhega
        LOGGER.info("Feather-Style Dupe Mod Initialized! Press ESC to see Menu.");
        
        // Yahan aap configurations ya keybindings register kar sakte hain
    }
}
