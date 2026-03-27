package com.tclient.mod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

public class TClientConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("tclient.json");

    public boolean fontChangerEnabled = false;
    public String selectedFont = "DEFAULT";
    public float fontScale = 1.0f;
    public boolean boldEnabled = false;
    public boolean shadowEnabled = true;
    public boolean italicEnabled = false;
    public int fontColor = 0xFFFFFF;
    public boolean customColorEnabled = false;

    public static TClientConfig load() {
        if (CONFIG_PATH.toFile().exists()) {
            try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
                TClientConfig cfg = GSON.fromJson(reader, TClientConfig.class);
                if (cfg != null) return cfg;
            } catch (IOException e) {
                System.err.println("[T Client] Failed to load config: " + e.getMessage());
            }
        }
        TClientConfig def = new TClientConfig();
        def.save();
        return def;
    }

    public void save() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            System.err.println("[T Client] Failed to save config: " + e.getMessage());
        }
    }
}
