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
        BASIC("§7[§fBASIC§7]", 0, "§7"),
        ADVANCED("§b[§3ADVANCED§b]", 100, "§b"),
        PRO("§a[§2PRO§a]", 500, "§a"),
        EXTREME("§c[§4EXTREME§c]", 1500, "§c"),
        LEGENDARY("§6[§eLEGEND§6]", 5000, "§6");
        
        public final String prefix;
        public final int requiredPoints;
        public final String color;
        
        PlayerTier(String prefix, int requiredPoints, String color) {
            this.prefix = prefix;
            this.requiredPoints = requiredPoints;
            this.color = color;
        }
        
        public static PlayerTier getTierByPoints(int points) {
            if (points >= 5000) return LEGENDARY;
            if (points >= 1500) return EXTREME;
            if (points >= 500) return PRO;
            if (points >= 100) return ADVANCED;
            return BASIC;
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
        
        // Initialize category points
        categoryPoints.put("combat", 0);
        categoryPoints.put("movement", 0);
        categoryPoints.put("visual", 0);
    }
    
    public void addKill() {
        kills++;
        winStreak++;
        if (winStreak > bestStreak) bestStreak = winStreak;
        addPoints(10); // 10 points per kill
    }
    
    public void addDeath() {
        deaths++;
        winStreak = 0;
        addPoints(2); // 2 points for participation
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
