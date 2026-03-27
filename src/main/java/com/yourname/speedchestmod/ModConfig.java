package com.yourname.speedchestmod;

import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.Path;

public class ModConfig {
    public boolean enabled = false;
    public double speedTicks = 0.1; // User input (logic max speed pe chalega)
    public int repeatCount = 10;
    
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("speedchestmod.json");

    public void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_PATH.toFile()))) {
                // Simple manual parsing for brevity, ideally use Gson
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("\"enabled\"")) enabled = line.contains("true");
                    if (line.contains("\"speedTicks\"")) speedTicks = Double.parseDouble(line.split(":")[1].replace(",", "").trim());
                    if (line.contains("\"repeatCount\"")) repeatCount = Integer.parseInt(line.split(":")[1].replace(",", "").trim());
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            e.printStackTrace();
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
import java.nio.file.Files;
