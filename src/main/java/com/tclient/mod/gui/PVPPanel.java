package com.tclient.mod.gui;

import com.tclient.mod.features.PVPModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class PVPPanel implements TClientPanel {
    
    private final TClientScreen parent;
    private final PVPModule pvp = PVPModule.getInstance();
    private final List<ButtonWidget> buttons = new ArrayList<>();
    private final List<SliderWidget> sliders = new ArrayList<>();
    
    private int scrollOffset = 0;
    private int contentHeight = 0;
    
    public PVPPanel(TClientScreen parent) {
        this.parent = parent;
    }
    
    @Override
    public void init(net.minecraft.client.MinecraftClient client, int width, int height, 
                     int sidebarWidth, int headerHeight, int footerHeight) {
        int startX = sidebarWidth + 20;
        int startY = headerHeight + 20;
        int buttonWidth = 120;
        int buttonHeight = 20;
        
        // Toggle Buttons
        buttons.add(createToggleButton("Reach", pvp.isReachEnabled(), 
            startX, startY, buttonWidth, buttonHeight, () -> pvp.toggleReach()));
            
        buttons.add(createToggleButton("Velocity", pvp.isVelocityEnabled(), 
            startX, startY + 35, buttonWidth, buttonHeight, () -> pvp.toggleVelocity()));
            
        buttons.add(createToggleButton("Aim Assist", pvp.isAimAssistEnabled(), 
            startX, startY + 70, buttonWidth, buttonHeight, () -> pvp.toggleAimAssist()));
            
        buttons.add(createToggleButton("Auto Clicker", pvp.isAutoClickerEnabled(), 
            startX, startY + 105, buttonWidth, buttonHeight, () -> pvp.toggleAutoClicker()));
            
        buttons.add(createToggleButton("Hitbox", pvp.isHitboxEnabled(), 
            startX, startY + 140, buttonWidth, buttonHeight, () -> pvp.toggleHitbox()));
            
        buttons.add(createToggleButton("W-Tap", pvp.isWTapEnabled(), 
            startX, startY + 175, buttonWidth, buttonHeight, () -> pvp.toggleWTap()));
        
        // Sliders for settings
        sliders.add(new SliderWidget(startX + 150, startY, 150, 20, 
            Text.literal("Reach: " + pvp.getReachDistance() + " blocks"), 3.0, 6.0) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("Reach: " + String.format("%.1f", value) + " blocks"));
            }
            
            @Override
            protected void applyValue() {
                pvp.setReachDistance(value);
            }
        }.withValue(pvp.getReachDistance()));
        
        sliders.add(new SliderWidget(startX + 150, startY + 35, 150, 20,
            Text.literal("Velocity H: " + pvp.getVelocityHorizontal() + "%"), 0, 100) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("Velocity H: " + String.format("%.0f", value) + "%"));
            }
            
            @Override
            protected void applyValue() {
                pvp.setVelocityHorizontal(value);
            }
        }.withValue(pvp.getVelocityHorizontal()));
        
        sliders.add(new SliderWidget(startX + 150, startY + 70, 150, 20,
            Text.literal("Velocity V: " + pvp.getVelocityVertical() + "%"), 0, 100) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("Velocity V: " + String.format("%.0f", value) + "%"));
            }
            
            @Override
            protected void applyValue() {
                pvp.setVelocityVertical(value);
            }
        }.withValue(pvp.getVelocityVertical()));
        
        sliders.add(new SliderWidget(startX + 150, startY + 105, 150, 20,
            Text.literal("CPS: " + pvp.getAutoClickerCPS()), 1, 20) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("CPS: " + String.format("%.0f", value)));
            }
            
            @Override
            protected void applyValue() {
                pvp.setAutoClickerCPS((int) value);
            }
        }.withValue(pvp.getAutoClickerCPS()));
        
        sliders.add(new SliderWidget(startX + 150, startY + 140, 150, 20,
            Text.literal("Aim Speed: " + pvp.getAimAssistSpeed()), 1, 10) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("Aim Speed: " + String.format("%.1f", value)));
            }
            
            @Override
            protected void applyValue() {
                pvp.setAimAssistSpeed(value);
            }
        }.withValue(pvp.getAimAssistSpeed()));
        
        sliders.add(new SliderWidget(startX + 150, startY + 175, 150, 20,
            Text.literal("Hitbox: " + pvp.getHitboxSize()), 0, 0.5f) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("Hitbox: " + String.format("%.2f", value)));
            }
            
            @Override
            protected void applyValue() {
                pvp.setHitboxSize((float) value);
            }
        }.withValue(pvp.getHitboxSize()));
        
        contentHeight = startY + 250;
    }
    
    private ButtonWidget createToggleButton(String name, boolean enabled, 
                                            int x, int y, int width, int height, Runnable action) {
        String buttonText = enabled ? "§a" + name + " ✓" : "§c" + name + " ✗";
        
        return ButtonWidget.builder(
            Text.literal(buttonText),
            button -> {
                action.run();
                // Update button text
                boolean newState = false;
                switch(name) {
                    case "Reach": newState = pvp.isReachEnabled(); break;
                    case "Velocity": newState = pvp.isVelocityEnabled(); break;
                    case "Aim Assist": newState = pvp.isAimAssistEnabled(); break;
                    case "Auto Clicker": newState = pvp.isAutoClickerEnabled(); break;
                    case "Hitbox": newState = pvp.isHitboxEnabled(); break;
                    case "W-Tap": newState = pvp.isWTapEnabled(); break;
                }
                String newText = newState ? "§a" + name + " ✓" : "§c" + name + " ✗";
                button.setMessage(Text.literal(newText));
            }
        )
        .dimensions(x, y, width, height)
        .build();
    }
    
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta,
                       int x, int y, int width, int height) {
        // Draw title
        ctx.drawText(parent.getTextRenderer(), "§lPVP MODULES", x + 20, y + 10, 0x00E5FF, false);
        ctx.drawText(parent.getTextRenderer(), "§7Combat enhancements for PVP", x + 20, y + 22, 0x7A7A9A, false);
        
        // Draw separator
        ctx.fill(x + 10, y + 35, x + width - 10, y + 36, 0x4400E5FF);
        
        // Render buttons
        for (ButtonWidget button : buttons) {
            button.render(ctx, mouseX, mouseY, delta);
        }
        
        // Render sliders
        for (SliderWidget slider : sliders) {
            slider.render(ctx, mouseX, mouseY, delta);
        }
        
        // Draw info box
        ctx.fill(x + width - 220, y + height - 100, x + width - 20, y + height - 20, 0x1A00E5FF);
        ctx.drawText(parent.getTextRenderer(), "§7PVP Info:", x + width - 210, y + height - 85, 0xECECEC, false);
        ctx.drawText(parent.getTextRenderer(), "§7Reach: §f" + pvp.getReachDistance() + " blocks", 
            x + width - 210, y + height - 70, 0x7A7A9A, false);
        ctx.drawText(parent.getTextRenderer(), "§7CPS: §f" + pvp.getAutoClickerCPS(), 
            x + width - 210, y + height - 55, 0x7A7A9A, false);
        
        String status = pvp.isAimAssistEnabled() ? "§aON" : "§cOFF";
        ctx.drawText(parent.getTextRenderer(), "§7Aim Assist: " + status, 
            x + width - 210, y + height - 40, 0x7A7A9A, false);
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
        // Handle scrolling if content exceeds height
        scrollOffset = MathHelper.clamp(scrollOffset + (int) amount * 10, 
            -Math.max(0, contentHeight - 400), 0);
    }
                          }
