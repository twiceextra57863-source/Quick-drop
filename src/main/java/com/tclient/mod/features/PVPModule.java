package com.tclient.mod.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class PVPModule {
    private static PVPModule instance;
    private MinecraftClient client;
    
    // PVP Features
    private boolean reachEnabled = false;
    private boolean velocityEnabled = false;
    private boolean aimAssistEnabled = false;
    private boolean autoClickerEnabled = false;
    private boolean hitboxEnabled = false;
    private boolean wtapEnabled = false;
    
    // Settings
    private double reachDistance = 3.5;
    private double velocityHorizontal = 90;
    private double velocityVertical = 100;
    private int autoClickerCPS = 12;
    private double aimAssistSpeed = 5.0;
    private float hitboxSize = 0.1f;
    
    private PVPModule() {
        this.client = MinecraftClient.getInstance();
    }
    
    public static PVPModule getInstance() {
        if (instance == null) {
            instance = new PVPModule();
        }
        return instance;
    }
    
    // Getters and Setters
    public boolean isReachEnabled() { return reachEnabled; }
    public void setReachEnabled(boolean enabled) { this.reachEnabled = enabled; }
    
    public boolean isVelocityEnabled() { return velocityEnabled; }
    public void setVelocityEnabled(boolean enabled) { this.velocityEnabled = enabled; }
    
    public boolean isAimAssistEnabled() { return aimAssistEnabled; }
    public void setAimAssistEnabled(boolean enabled) { this.aimAssistEnabled = enabled; }
    
    public boolean isAutoClickerEnabled() { return autoClickerEnabled; }
    public void setAutoClickerEnabled(boolean enabled) { this.autoClickerEnabled = enabled; }
    
    public boolean isHitboxEnabled() { return hitboxEnabled; }
    public void setHitboxEnabled(boolean enabled) { this.hitboxEnabled = enabled; }
    
    public boolean isWTapEnabled() { return wtapEnabled; }
    public void setWTapEnabled(boolean enabled) { this.wtapEnabled = enabled; }
    
    public double getReachDistance() { return reachDistance; }
    public void setReachDistance(double distance) { this.reachDistance = distance; }
    
    public double getVelocityHorizontal() { return velocityHorizontal; }
    public void setVelocityHorizontal(double horizontal) { this.velocityHorizontal = horizontal; }
    
    public double getVelocityVertical() { return velocityVertical; }
    public void setVelocityVertical(double vertical) { this.velocityVertical = vertical; }
    
    public int getAutoClickerCPS() { return autoClickerCPS; }
    public void setAutoClickerCPS(int cps) { this.autoClickerCPS = cps; }
    
    public double getAimAssistSpeed() { return aimAssistSpeed; }
    public void setAimAssistSpeed(double speed) { this.aimAssistSpeed = speed; }
    
    public float getHitboxSize() { return hitboxSize; }
    public void setHitboxSize(float size) { this.hitboxSize = size; }
    
    public void toggleReach() { reachEnabled = !reachEnabled; }
    public void toggleVelocity() { velocityEnabled = !velocityEnabled; }
    public void toggleAimAssist() { aimAssistEnabled = !aimAssistEnabled; }
    public void toggleAutoClicker() { autoClickerEnabled = !autoClickerEnabled; }
    public void toggleHitbox() { hitboxEnabled = !hitboxEnabled; }
    public void toggleWTap() { wtapEnabled = !wtapEnabled; }
}
