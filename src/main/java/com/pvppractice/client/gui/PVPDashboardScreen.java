package com.pvppractice.client.gui;

import com.pvppractice.config.PVPConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PVPDashboardScreen extends Screen {
    private static final Identifier BACKGROUND = Identifier.of("pvppractice", "textures/gui/dashboard.png");
    
    public PVPDashboardScreen() {
        super(Text.literal("PVP Practice Dashboard"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // Heart Indicator Category Button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§c❤ Heart Indicator"),
            button -> {
                if (this.client != null) {
                    this.client.setScreen(new HeartIndicatorSettingsScreen(this));
                }
            }
        ).dimensions(centerX - 100, centerY - 60, 200, 30).build());
        
        // More categories for future expansion
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§6⚔ Combat Tracker"),
            button -> {}
        ).dimensions(centerX - 100, centerY - 20, 200, 30).build());
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§a🎯 Aim Trainer"),
            button -> {}
        ).dimensions(centerX - 100, centerY + 20, 200, 30).build());
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§b📊 Stats Tracker"),
            button -> {}
        ).dimensions(centerX - 100, centerY + 60, 200, 30).build());
        
        // Close button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§cClose"),
            button -> this.close()
        ).dimensions(centerX - 50, centerY + 120, 100, 20).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, "§lPVP Practice Mod", this.width / 2, this.height / 2 - 100, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
