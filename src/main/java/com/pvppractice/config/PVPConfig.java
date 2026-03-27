package com.pvppractice.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pvppractice.client.render.HeartIndicatorType;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.Path;

public class PVPConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("pvppractice.json");
    
    private static PVPConfig instance;
    
    // Heart Indicator Settings
    public boolean heartIndicatorEnabled = true;
    public HeartIndicatorType heartStyle = HeartIndicatorType.MINECRAFT_HEARTS;
    public boolean showHealthNumbers = true;
    public HealthBarColor healthBarColor = HealthBarColor.RED;
    public int indicatorSize = 2; // 1=Small, 2=Medium, 3=Large
    public int indicatorXOffset = 0;
    public int indicatorYOffset = 0;
    
    public enum HealthBarColor {
        RED("Red", 0xFF5555),
        GREEN("Green", 0x55FF55),
        BLUE("Blue", 0x5555FF),
        YELLOW("Yellow", 0xFFFF55),
        PURPLE("Purple", 0xFF55FF),
        CYAN("Cyan", 0x55FFFF);
        
        private final String name;
        private final int color;
        
        HealthBarColor(String name, int color) {
            this.name = name;
            this.color = color;
        }
        
        public String getName() {
            return name;
        }
        
        public int getColor() {
            return color;
        }
    }
    
    public static PVPConfig getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }
    
    private static PVPConfig load() {
        if (FabricLoader.getInstance().getConfigDir().resolve("pvppractice.json").toFile().exists()) {
            try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
                return GSON.fromJson(reader, PVPConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new PVPConfig();
    }
    
    public void save() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
