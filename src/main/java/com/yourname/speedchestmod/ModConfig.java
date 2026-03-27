package com.yourname.speedchestmod;

// Sabse pehle imports yahan hone chahiye
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Files; // Ye line ab sahi jagah hai

public class ModConfig {
    public boolean enabled = false;
    public double speedTicks = 0.1; 
    public int repeatCount = 10;
    
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("speedchestmod.json");

    public void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_PATH.toFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("\"enabled\"")) enabled = line.contains("true");
                    if (line.contains("\"speedTicks\"")) {
                        try {
                            speedTicks = Double.parseDouble(line.split(":")[1].replace(",", "").trim());
                        } catch (NumberFormatException e) {
                            speedTicks = 0.1;
                        }
                    }
                    if (line.contains("\"repeatCount\"")) {
                        try {
                            repeatCount = Integer.parseInt(line.split(":")[1].replace(",", "").trim());
                        } catch (NumberFormatException e) {
                            repeatCount = 10;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("[SpeedChestMod] Config load error: " + e.getMessage());
            }
        }
    }

    public void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CONFIG_PATH.toFile()))) {
            writer.println("{");
            writer.println("  \"enabled\": " + enabled + ",");
            writer.println("  \"speedTicks\": " + speedTicks + ",");
            writer.println("  \"repeatCount\": " + repeatCount);
            writer.println("}");
        } catch (Exception e) {
            System.out.println("[SpeedChestMod] Config save error: " + e.getMessage());
        }
    }
    
    // Singleton instance
    private static ModConfig instance;
    public static ModConfig getInstance() {
        if (instance == null) {
            instance = new ModConfig();
            instance.load();
        }
        return instance;
    }
}
