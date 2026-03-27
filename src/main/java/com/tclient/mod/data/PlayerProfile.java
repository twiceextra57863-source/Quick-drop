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
        BASIC("§c[§4BASIC§c]", "§c", "§4BASIC§r", 0, 0xFF5555, "RED"),
        ADVANCED("§e[§6ADVANCED§e]", "§e", "§6ADVANCED§r", 100, 0xFFFF55, "YELLOW"),
        PRO("§a[§2PRO§a]", "§a", "§2PRO§r", 500, 0x55FF55, "GREEN"),
        EXTREME("§b[§3EXTREME§b]", "§b", "§3EXTREME§r", 1500, 0x55FFFF, "AQUA"),
        LEGENDARY("§6[§eLEGEND§6]", "§6", "§eLEGEND§r", 5000, 0xFFAA00, "GOLD");
        
        public final String prefix;
        public final String color;
        public final String displayName;
        public final int requiredPoints;
        public final int rgbColor;
        public final String colorName;
        
        PlayerTier(String prefix, String color, String displayName, int requiredPoints, int rgbColor, String colorName) {
            this.prefix = prefix;
            this.color = color;
            this.displayName = displayName;
            this.requiredPoints = requiredPoints;
            this.rgbColor = rgbColor;
            this.colorName = colorName;
        }
        
        public static PlayerTier getTierByPoints(int points) {
            if (points >= 5000) return LEGENDARY;
            if (points >= 1500) return EXTREME;
            if (points >= 500) return PRO;
            if (points >= 100) return ADVANCED;
            return BASIC;
        }
        
        public static PlayerTier getTierByName(String name) {
            try {
                return PlayerTier.valueOf(name);
            } catch (IllegalArgumentException e) {
                return BASIC;
            }
        }
        
        public String getFormattedPrefix() {
            return prefix;
        }
        
        public String getColoredName() {
            return color + displayName;
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
        categoryPoints.put("pvp", 0);
        categoryPoints.put("survival", 0);
    }
    
    public void addKill() {
        kills++;
        winStreak++;
        if (winStreak > bestStreak) bestStreak = winStreak;
        addPoints(10);
        addCategoryPoints("combat", 10);
        addCategoryPoints("pvp", 10);
    }
    
    public void addDeath() {
        deaths++;
        winStreak = 0;
        addPoints(2);
        addCategoryPoints("combat", 2);
    }
    
    public void addPoints(int points) {
        totalPoints += points;
        updateTier();
    }
    
    public void addCategoryPoints(String category, int points) {
        categoryPoints.put(category, categoryPoints.getOrDefault(category, 0) + points);
    }
    
    private void updateTier() {
        PlayerTier newTier = PlayerTier.getTierByPoints(totalPoints);
        if (newTier != tier) {
            PlayerTier oldTier = tier;
            tier = newTier;
            System.out.println("§6[TClient] §7" + username + " §eadvanced from " + 
                             oldTier.colorName + " §eto §a" + newTier.colorName + " §7tier! §6[" + totalPoints + " points§6]");
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
    
    public String getSimpleTierDisplay() {
        return tier.color + "[" + tier.name() + "]" + "§r";
    }
    
    public int getTierColor() {
        return tier.rgbColor;
    }
    
    public String getTierColorCode() {
        return tier.color;
    }
    
    public int getNextTierPoints() {
        switch(tier) {
            case BASIC: return PlayerTier.ADVANCED.requiredPoints - totalPoints;
            case ADVANCED: return PlayerTier.PRO.requiredPoints - totalPoints;
            case PRO: return PlayerTier.EXTREME.requiredPoints - totalPoints;
            case EXTREME: return PlayerTier.LEGENDARY.requiredPoints - totalPoints;
            default: return 0;
        }
    }
    
    public double getProgressToNextTier() {
        int nextRequired = getNextTierPoints();
        if (nextRequired <= 0) return 100.0;
        
        int currentTierPoints = 0;
        switch(tier) {
            case BASIC: currentTierPoints = totalPoints; break;
            case ADVANCED: currentTierPoints = totalPoints - PlayerTier.ADVANCED.requiredPoints; break;
            case PRO: currentTierPoints = totalPoints - PlayerTier.PRO.requiredPoints; break;
            case EXTREME: currentTierPoints = totalPoints - PlayerTier.EXTREME.requiredPoints; break;
            default: return 100.0;
        }
        
        int tierRequirement = 0;
        switch(tier) {
            case BASIC: tierRequirement = PlayerTier.ADVANCED.requiredPoints - 0; break;
            case ADVANCED: tierRequirement = PlayerTier.PRO.requiredPoints - PlayerTier.ADVANCED.requiredPoints; break;
            case PRO: tierRequirement = PlayerTier.EXTREME.requiredPoints - PlayerTier.PRO.requiredPoints; break;
            case EXTREME: tierRequirement = PlayerTier.LEGENDARY.requiredPoints - PlayerTier.EXTREME.requiredPoints; break;
            default: return 100.0;
        }
        
        return (currentTierPoints * 100.0) / tierRequirement;
    }
    
    public String getRankTitle() {
        if (totalPoints >= 10000) return "§6[§eGOD§6]";
        if (totalPoints >= 5000) return "§6[§eLEGEND§6]";
        if (totalPoints >= 2500) return "§b[§3MYTHIC§b]";
        if (totalPoints >= 1500) return "§b[§3EXTREME§b]";
        if (totalPoints >= 1000) return "§a[§2ELITE§a]";
        if (totalPoints >= 500) return "§a[§2PRO§a]";
        if (totalPoints >= 250) return "§e[§6SKILLED§e]";
        if (totalPoints >= 100) return "§e[§6ADVANCED§e]";
        if (totalPoints >= 50) return "§7[§fNOVICE§7]";
        return "§c[§4BASIC§c]";
    }
    
    public Map<String, Integer> getCategoryPoints() {
        return categoryPoints;
    }
    
    public int getCategoryPoints(String category) {
        return categoryPoints.getOrDefault(category, 0);
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
    
    // Setters for loading from file
    public void setTotalPoints(int points) {
        this.totalPoints = points;
        updateTier();
    }
    
    public void setKills(int kills) {
        this.kills = kills;
    }
    
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
    
    public void setWinStreak(int streak) {
        this.winStreak = streak;
    }
    
    public void setBestStreak(int streak) {
        this.bestStreak = streak;
    }
    
    public void setTierFromName(String tierName) {
        try {
            this.tier = PlayerTier.valueOf(tierName);
        } catch (IllegalArgumentException e) {
            this.tier = PlayerTier.BASIC;
        }
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    // Stats methods
    public String getStatsString() {
        return String.format("§7=== §6%s §7===§r\n" +
                           "§7Tier: %s %s§r\n" +
                           "§7Points: §e%d§r\n" +
                           "§7Kills: §a%d§r\n" +
                           "§7Deaths: §c%d§r\n" +
                           "§7KDR: §f%.2f§r\n" +
                           "§7Win Streak: §b%d§r\n" +
                           "§7Best Streak: §d%d§r\n" +
                           "§7Next Tier: §f%d points§r",
                           username,
                           tier.color, tier.name(),
                           totalPoints,
                           kills,
                           deaths,
                           getKDR(),
                           winStreak,
                           bestStreak,
                           getNextTierPoints());
    }
    
    @Override
    public String toString() {
        return getFormattedName();
    }
    }
