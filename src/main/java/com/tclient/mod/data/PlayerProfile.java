package com.tclient.mod.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerProfile {
    private UUID uuid;
    private String username;
    private int totalPoints;
    private int kills;
    private int deaths;
    private int winStreak;
    private int bestStreak;
    private PlayerTier tier;
    private Map<String, Integer> categoryPoints;
    
    public enum PlayerTier {
        BASIC("§c[§4BASIC§c]", 0, "§c", "§4BASIC§r", 0xFF5555),
        ADVANCED("§e[§6ADVANCED§e]", 100, "§e", "§6ADVANCED§r", 0xFFFF55),
        PRO("§a[§2PRO§a]", 500, "§a", "§2PRO§r", 0x55FF55),
        EXTREME("§b[§3EXTREME§b]", 1500, "§b", "§3EXTREME§r", 0x55FFFF),
        LEGENDARY("§6[§eLEGEND§6]", 5000, "§6", "§eLEGEND§r", 0xFFAA00);
        
        public final String prefix;
        public final int requiredPoints;
        public final String color;
        public final String displayName;
        public final int rgbColor;
        
        PlayerTier(String prefix, int requiredPoints, String color, String displayName, int rgbColor) {
            this.prefix = prefix;
            this.requiredPoints = requiredPoints;
            this.color = color;
            this.displayName = displayName;
            this.rgbColor = rgbColor;
        }
        
        public static PlayerTier getTierByPoints(int points) {
            if (points >= 5000) return LEGENDARY;
            if (points >= 1500) return EXTREME;
            if (points >= 500) return PRO;
            if (points >= 100) return ADVANCED;
            return BASIC;
        }
        
        public static PlayerTier getTierByPointsWithColor(int points) {
            PlayerTier tier = getTierByPoints(points);
            return tier;
        }
    }
    
    public PlayerProfile(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.totalPoints = 0;
        this.kills = 0;
        this.deaths = 0;
        this.winStreak = 0;
        this.bestStreak = 0;
        this.tier = PlayerTier.BASIC;
        this.categoryPoints = new HashMap<>();
        
        categoryPoints.put("combat", 0);
        categoryPoints.put("movement", 0);
        categoryPoints.put("visual", 0);
    }
    
    public void addKill() {
        kills++;
        winStreak++;
        if (winStreak > bestStreak) bestStreak = winStreak;
        addPoints(10);
    }
    
    public void addDeath() {
        deaths++;
        winStreak = 0;
        addPoints(2);
    }
    
    public void addPoints(int points) {
        totalPoints += points;
        updateTier();
    }
    
    public void addCategoryPoints(String category, int points) {
        categoryPoints.put(category, categoryPoints.getOrDefault(category, 0) + points);
        addPoints(points);
    }
    
    private void updateTier() {
        PlayerTier newTier = PlayerTier.getTierByPoints(totalPoints);
        if (newTier != tier) {
            tier = newTier;
            System.out.println(username + " reached " + tier.name() + " tier!");
        }
    }
    
    public double getKDR() {
        return deaths == 0 ? kills : (double) kills / deaths;
    }
    
    public String getFormattedName() {
        return tier.color + tier.displayName + " §r" + username + " §7[" + totalPoints + " pts§7]";
    }
    
    public String getTierWithColor() {
        return tier.prefix;
    }
    
    public int getTierColor() {
        return tier.rgbColor;
    }
    
    // Getters
    public UUID getUuid() { return uuid; }
    public String getUsername() { return username; }
    public int getTotalPoints() { return totalPoints; }
    public int getKills() { return kills; }
    public int getDeaths() { return deaths; }
    public int getWinStreak() { return winStreak; }
    public int getBestStreak() { return bestStreak; }
    public PlayerTier getTier() { return tier; }
    public Map<String, Integer> getCategoryPoints() { return categoryPoints; }
    
    public int getNextTierPoints() {
        switch(tier) {
            case BASIC: return PlayerTier.ADVANCED.requiredPoints - totalPoints;
            case ADVANCED: return PlayerTier.PRO.requiredPoints - totalPoints;
            case PRO: return PlayerTier.EXTREME.requiredPoints - totalPoints;
            case EXTREME: return PlayerTier.LEGENDARY.requiredPoints - totalPoints;
            default: return 0;
        }
    }
}
