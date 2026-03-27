package com.tclient.mod.data;

import net.minecraft.client.MinecraftClient;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager {
    private static ProfileManager instance;
    private Map<UUID, PlayerProfile> profiles;
    private PlayerProfile currentProfile;
    
    private ProfileManager() {
        profiles = new HashMap<>();
    }
    
    public static ProfileManager getInstance() {
        if (instance == null) {
            instance = new ProfileManager();
        }
        return instance;
    }
    
    public PlayerProfile getProfile(UUID uuid) {
        return profiles.get(uuid);
    }
    
    public PlayerProfile getOrCreateProfile(UUID uuid, String username) {
        return profiles.computeIfAbsent(uuid, k -> new PlayerProfile(uuid, username));
    }
    
    public PlayerProfile getCurrentProfile() {
        if (currentProfile == null && MinecraftClient.getInstance().player != null) {
            currentProfile = getOrCreateProfile(
                MinecraftClient.getInstance().player.getUuid(),
                MinecraftClient.getInstance().player.getName().getString()
            );
        }
        return currentProfile;
    }
    
    public void saveProfiles() {
        // Save to file (implement later)
    }
    
    public void loadProfiles() {
        // Load from file (implement later)
    }
}
