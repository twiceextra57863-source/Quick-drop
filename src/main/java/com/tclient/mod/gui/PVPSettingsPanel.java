package com.tclient.mod.gui;

import com.tclient.mod.data.PlayerProfile;
import com.tclient.mod.data.ProfileManager;
import com.tclient.mod.features.PVPModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class PVPSettingsPanel implements TClientPanel {
    
    private final TClientScreen parent;
    private final PVPModule pvp = PVPModule.getInstance();
    private final ProfileManager profileManager = ProfileManager.getInstance();
    private PlayerProfile currentProfile;
    
    private enum SettingCategory {
        BASIC("§aBASIC", 0),
        ADVANCED("§bADVANCED", 1),
        PRO("§dPRO", 2),
        EXTREME("§cEXTREME", 3);
        
        public final String name;
        public final int index;
        
        SettingCategory(String name, int index) {
            this.name = name;
            this.index = index;
        }
    }
    
    private SettingCategory currentCategory = SettingCategory.BASIC;
    private final List<ButtonWidget> buttons = new ArrayList<>();
    private final List<SliderWidget> sliders = new ArrayList<>();
    
    public PVPSettingsPanel(TClientScreen parent) {
        this.parent = parent;
        this.currentProfile = profileManager.getCurrentProfile();
    }
    
    @Override
    public void init(net.minecraft.client.MinecraftClient client, int width, int height,
                     int sidebarWidth, int headerHeight, int footerHeight) {
        int startX = sidebarWidth + 20;
        int startY = headerHeight + 60;
        int buttonWidth = 100;
        int buttonHeight = 20;
        
        // Category buttons
        for (int i = 0; i < SettingCategory.values().length; i++) {
            SettingCategory cat = SettingCategory.values()[i];
            int finalI = i;
            ButtonWidget catButton = ButtonWidget.builder(
                Text.literal(cat.name),
                button -> {
                    currentCategory = SettingCategory.values()[finalI];
                    updateUI();
                }
            )
            .dimensions(startX + (i * 85), headerHeight + 25, 80, 20)
            .build();
            buttons.add(catButton);
        }
        
        updateUI();
    }
    
    private void updateUI() {
        buttons.removeIf(btn -> !(btn.getMessage().getString().contains("BASIC") || 
                                   btn.getMessage().getString().contains("ADVANCED") ||
                                   btn.getMessage().getString().contains("PRO") ||
                                   btn.getMessage().getString().contains("EXTREME")));
        sliders.clear();
        
        int startX = parent.width - 400;
        int startY = parent.height / 2 - 100;
        
        switch(currentCategory) {
            case BASIC:
                addBasicFeatures(startX, startY);
                break;
            case ADVANCED:
                addAdvancedFeatures(startX, startY);
                break;
            case PRO:
                addProFeatures(startX, startY);
                break;
            case EXTREME:
                addExtremeFeatures(startX, startY);
                break;
        }
    }
    
    private void addBasicFeatures(int x, int y) {
        // Hitbox toggle (Basic feature)
        ButtonWidget hitboxBtn = createToggleButton("Hitbox", pvp.isHitboxEnabled(),
            x, y, 120, 20, () -> pvp.toggleHitbox());
        buttons.add(hitboxBtn);
        
        // Reach slider (Basic)
        SliderWidget reachSlider = new SliderWidget(x, y + 30, 150, 20,
            Text.literal("Reach: " + pvp.getReachDistance()), 3.0, 4.0) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("Reach: " + String.format("%.1f", value)));
            }
            @Override
            protected void applyValue() {
                pvp.setReachDistance(value);
            }
        }.withValue(pvp.getReachDistance());
        sliders.add(reachSlider);
    }
    
    private void addAdvancedFeatures(int x, int y) {
        // Advanced features
        ButtonWidget velocityBtn = createToggleButton("Velocity", pvp.isVelocityEnabled(),
            x, y, 120, 20, () -> pvp.toggleVelocity());
        buttons.add(velocityBtn);
        
        ButtonWidget aimAssistBtn = createToggleButton("Aim Assist", pvp.isAimAssistEnabled(),
            x, y + 30, 120, 20, () -> pvp.toggleAimAssist());
        buttons.add(aimAssistBtn);
        
        SliderWidget velocityHSlider = new SliderWidget(x + 130, y, 150, 20,
            Text.literal("Velocity H: " + pvp.getVelocityHorizontal() + "%"), 50, 100) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("Velocity H: " + String.format("%.0f", value) + "%"));
            }
            @Override
            protected void applyValue() {
                pvp.setVelocityHorizontal(value);
            }
        }.withValue(pvp.getVelocityHorizontal());
        sliders.add(velocityHSlider);
    }
    
    private void addProFeatures(int x, int y) {
        // Pro features
        ButtonWidget autoClickerBtn = createToggleButton("Auto Clicker", pvp.isAutoClickerEnabled(),
            x, y, 120, 20, () -> pvp.toggleAutoClicker());
        buttons.add(autoClickerBtn);
        
        ButtonWidget wtapBtn = createToggleButton("W-Tap", pvp.isWTapEnabled(),
            x, y + 30, 120, 20, () -> pvp.toggleWTap());
        buttons.add(wtapBtn);
        
        SliderWidget cpsSlider = new SliderWidget(x + 130, y, 150, 20,
            Text.literal("CPS: " + pvp.getAutoClickerCPS()), 8, 20) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("CPS: " + String.format("%.0f", value)));
            }
            @Override
            protected void applyValue() {
                pvp.setAutoClickerCPS((int) value);
            }
        }.withValue(pvp.getAutoClickerCPS());
        sliders.add(cpsSlider);
    }
    
    private void addExtremeFeatures(int x, int y) {
        // Extreme features
        ButtonWidget reachBtn = createToggleButton("Extreme Reach", pvp.isReachEnabled(),
            x, y, 120, 20, () -> pvp.toggleReach());
        buttons.add(reachBtn);
        
        SliderWidget extremeReach = new SliderWidget(x + 130, y, 150, 20,
            Text.literal("Reach: " + pvp.getReachDistance()), 4.0, 6.0) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("Reach: " + String.format("%.1f", value)));
            }
            @Override
            protected void applyValue() {
                pvp.setReachDistance(value);
            }
        }.withValue(pvp.getReachDistance());
        sliders.add(extremeReach);
    }
    
    private ButtonWidget createToggleButton(String name, boolean enabled,
                                            int x, int y, int width, int height, Runnable action) {
        String buttonText = enabled ? "§a" + name + " ✓" : "§c" + name + " ✗";
        return ButtonWidget.builder(
            Text.literal(buttonText),
            button -> {
                action.run();
                boolean newState = false;
                switch(name) {
                    case "Hitbox": newState = pvp.isHitboxEnabled(); break;
                    case "Velocity": newState = pvp.isVelocityEnabled(); break;
                    case "Aim Assist": newState = pvp.isAimAssistEnabled(); break;
                    case "Auto Clicker": newState = pvp.isAutoClickerEnabled(); break;
                    case "W-Tap": newState = pvp.isWTapEnabled(); break;
                    case "Extreme Reach": newState = pvp.isReachEnabled(); break;
                }
                button.setMessage(Text.literal(newState ? "§a" + name + " ✓" : "§c" + name + " ✗"));
            }
        )
        .dimensions(x, y, width, height)
        .build();
    }
    
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta,
                       int x, int y, int width, int height) {
        // Draw profile info at top
        drawProfileInfo(ctx, x, y, width);
        
        // Draw category indicator
        ctx.drawText(parent.getTextRenderer(), "§l" + currentCategory.name + " SETTINGS", 
            x + 20, y + 10, 0x00E5FF, false);
        ctx.drawText(parent.getTextRenderer(), "§7Unlock higher tiers by earning points!", 
            x + 20, y + 22, 0x7A7A9A, false);
        
        // Draw separator
        ctx.fill(x + 10, y + 45, x + width - 10, y + 46, 0x4400E5FF);
        
        // Render UI elements
        for (ButtonWidget button : buttons) {
            button.render(ctx, mouseX, mouseY, delta);
        }
        for (SliderWidget slider : sliders) {
            slider.render(ctx, mouseX, mouseY, delta);
        }
        
        // Draw tier info
        drawTierInfo(ctx, x + width - 200, y + 20);
    }
    
    private void drawProfileInfo(DrawContext ctx, int x, int y, int width) {
        if (currentProfile == null) currentProfile = profileManager.getCurrentProfile();
        
        String tierColor = currentProfile.getTier().color;
        String tierName = currentProfile.getTier().name();
        
        ctx.drawText(parent.getTextRenderer(), "§7Player: §f" + currentProfile.getUsername(),
            x + width - 200, y + 5, 0xECECEC, false);
        ctx.drawText(parent.getTextRenderer(), tierColor + tierName + " §7Tier",
            x + width - 200, y + 17, 0xECECEC, false);
        ctx.drawText(parent.getTextRenderer(), "§7Points: §e" + currentProfile.getTotalPoints(),
            x + width - 200, y + 29, 0xECECEC, false);
        
        int nextPoints = currentProfile.getNextTierPoints();
        if (nextPoints > 0) {
            ctx.drawText(parent.getTextRenderer(), "§7Next Tier: §f" + nextPoints + " points",
                x + width - 200, y + 41, 0x7A7A9A, false);
        }
    }
    
    private void drawTierInfo(DrawContext ctx, int x, int y) {
        ctx.fill(x - 5, y - 5, x + 190, y + 130, 0x1A00E5FF);
        
        ctx.drawText(parent.getTextRenderer(), "§lTIER REQUIREMENTS", x, y, 0x00E5FF, false);
        ctx.drawText(parent.getTextRenderer(), "§7BASIC: §f0 points", x, y + 15, 0x7A7A9A, false);
        ctx.drawText(parent.getTextRenderer(), "§bADVANCED: §f100 points", x, y + 30, 0x7A7A9A, false);
        ctx.drawText(parent.getTextRenderer(), "§dPRO: §f500 points", x, y + 45, 0x7A7A9A, false);
        ctx.drawText(parent.getTextRenderer(), "§cEXTREME: §f1500 points", x, y + 60, 0x7A7A9A, false);
        ctx.drawText(parent.getTextRenderer(), "§6LEGENDARY: §f5000 points", x, y + 75, 0x7A7A9A, false);
        
        ctx.drawText(parent.getTextRenderer(), "§7Kills: §f" + currentProfile.getKills(), 
            x, y + 100, 0x7A7A9A, false);
        ctx.drawText(parent.getTextRenderer(), "§7KDR: §f" + String.format("%.2f", currentProfile.getKDR()), 
            x + 80, y + 100, 0x7A7A9A, false);
        ctx.drawText(parent.getTextRenderer(), "§7Streak: §f" + currentProfile.getWinStreak(), 
            x, y + 115, 0x7A7A9A, false);
    }
    
    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        for (ButtonWidget btn : buttons) {
            btn.mouseClicked(mouseX, mouseY, button);
        }
        for (SliderWidget slider : sliders) {
            slider.mouseClicked(mouseX, mouseY, button);
        }
    }
    
    @Override
    public void mouseScrolled(double mouseX, double mouseY, double amount) {
        // Handle scrolling if needed
    }
                       }
