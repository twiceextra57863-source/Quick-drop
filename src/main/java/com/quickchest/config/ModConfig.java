package com.quickchest.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quickchest.QuickChestMod;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    public boolean enabled = true;
    public int cooldownMs = 300;
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    public static ModConfig load(File file) {
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                return GSON.fromJson(reader, ModConfig.class);
            } catch (IOException e) {
                QuickChestMod.LOGGER.error("Failed to load config", e);
            }
        }
        ModConfig config = new ModConfig();
        config.save(file);
        return config;
    }
    
    public void save() {
        try {
            File file = new File(MinecraftClient.getInstance().runDirectory, "config/quickchest.json");
            save(file);
        } catch (Exception e) {
            QuickChestMod.LOGGER.error("Failed to save config", e);
        }
    }
    
    public void save(File file) {
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            QuickChestMod.LOGGER.error("Failed to save config", e);
        }
    }
}
