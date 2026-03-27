package com.tclient.mod.data;

import net.minecraft.client.MinecraftClient;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager {
    private static ProfileManager instance;
    private Map<UUID, PlayerProfile> profiles;
    private PlayerProfile currentProfile;
    private Path savePath;
    
    private ProfileManager() {
        profiles = new HashMap<>();
        
        // Set save path in game directory
        if (MinecraftClient.getInstance().runDirectory != null) {
            savePath = Paths.get(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), 
                                 "tclient", "profiles.dat");
        } else {
            savePath = Paths.get("profiles.dat");
        }
        
        // Create directory if it doesn't exist
        try {
            Files.createDirectories(savePath.getParent());
        } catch (IOException e) {
            System.err.println("Failed to create profile directory: " + e.getMessage());
        }
        
        // Load existing profiles
        loadProfiles();
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
        return profiles.computeIfAbsent(uuid, k -> {
            PlayerProfile newProfile = new PlayerProfile(uuid, username);
            saveProfiles(); // Save after creating new profile
            return newProfile;
        });
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
    
    public void updateCurrentProfile() {
        if (MinecraftClient.getInstance().player != null) {
            currentProfile = getOrCreateProfile(
                MinecraftClient.getInstance().player.getUuid(),
                MinecraftClient.getInstance().player.getName().getString()
            );
        }
    }
    
    public Collection<PlayerProfile> getAllProfiles() {
        return profiles.values();
    }
    
    public void saveProfiles() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savePath.toFile()))) {
            // Convert to serializable format
            Map<String, PlayerProfileData> saveData = new HashMap<>();
            for (Map.Entry<UUID, PlayerProfile> entry : profiles.entrySet()) {
                PlayerProfile profile = entry.getValue();
                PlayerProfileData data = new PlayerProfileData(
                    profile.getUuid().toString(),
                    profile.getUsername(),
                    profile.getTotalPoints(),
                    profile.getKills(),
                    profile.getDeaths(),
                    profile.getWinStreak(),
                    profile.getBestStreak(),
                    profile.getTier().name()
                );
                saveData.put(entry.getKey().toString(), data);
            }
            oos.writeObject(saveData);
            System.out.println("TClient: Profiles saved to " + savePath);
        } catch (IOException e) {
            System.err.println("TClient: Failed to save profiles - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    public void loadProfiles() {
        if (!Files.exists(savePath)) {
            System.out.println("TClient: No existing profiles found, starting fresh");
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savePath.toFile()))) {
            Map<String, PlayerProfileData> saveData = (Map<String, PlayerProfileData>) ois.readObject();
            profiles.clear();
            
            for (Map.Entry<String, PlayerProfileData> entry : saveData.entrySet()) {
                PlayerProfileData data = entry.getValue();
                UUID uuid = UUID.fromString(data.uuid);
                PlayerProfile profile = new PlayerProfile(uuid, data.username);
                profile.setTotalPoints(data.totalPoints);
                profile.setKills(data.kills);
                profile.setDeaths(data.deaths);
                profile.setWinStreak(data.winStreak);
                profile.setBestStreak(data.bestStreak);
                profile.setTierFromName(data.tierName);
                profiles.put(uuid, profile);
            }
            System.out.println("TClient: Loaded " + profiles.size() + " profiles from " + savePath);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("TClient: Failed to load profiles - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void addPointsToPlayer(UUID uuid, int points) {
        PlayerProfile profile = profiles.get(uuid);
        if (profile != null) {
            profile.addPoints(points);
            saveProfiles();
        }
    }
    
    public void addKillToPlayer(UUID uuid) {
        PlayerProfile profile = profiles.get(uuid);
        if (profile != null) {
            profile.addKill();
            saveProfiles();
        }
    }
    
    public void addDeathToPlayer(UUID uuid) {
        PlayerProfile profile = profiles.get(uuid);
        if (profile != null) {
            profile.addDeath();
            saveProfiles();
        }
    }
    
    public void resetProfile(UUID uuid) {
        if (MinecraftClient.getInstance().player != null && 
            uuid.equals(MinecraftClient.getInstance().player.getUuid())) {
            // Reset current profile
            currentProfile = new PlayerProfile(uuid, MinecraftClient.getInstance().player.getName().getString());
            profiles.put(uuid, currentProfile);
        } else {
            PlayerProfile profile = profiles.get(uuid);
            if (profile != null) {
                PlayerProfile newProfile = new PlayerProfile(uuid, profile.getUsername());
                profiles.put(uuid, newProfile);
            }
        }
        saveProfiles();
    }
    
    public void resetAllProfiles() {
        profiles.clear();
        updateCurrentProfile();
        saveProfiles();
        System.out.println("TClient: All profiles have been reset");
    }
    
    public String getTopPlayers(int limit) {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== TOP ").append(limit).append(" PLAYERS ===\n");
        
        profiles.values().stream()
            .sorted((p1, p2) -> Integer.compare(p2.getTotalPoints(), p1.getTotalPoints()))
            .limit(limit)
            .forEach(profile -> {
                sb.append(profile.getTier().color)
                  .append(profile.getUsername())
                  .append("§7 - ")
                  .append(profile.getTotalPoints())
                  .append(" points §7(KDR: ")
                  .append(String.format("%.2f", profile.getKDR()))
                  .append(")\n");
            });
        
        return sb.toString();
    }
    
    // Helper class for serialization
    private static class PlayerProfileData implements Serializable {
        private static final long serialVersionUID = 1L;
        String uuid;
        String username;
        int totalPoints;
        int kills;
        int deaths;
        int winStreak;
        int bestStreak;
        String tierName;
        
        PlayerProfileData(String uuid, String username, int totalPoints, int kills, 
                         int deaths, int winStreak, int bestStreak, String tierName) {
            this.uuid = uuid;
            this.username = username;
            this.totalPoints = totalPoints;
            this.kills = kills;
            this.deaths = deaths;
            this.winStreak = winStreak;
            this.bestStreak = bestStreak;
            this.tierName = tierName;
        }
    }
    }
