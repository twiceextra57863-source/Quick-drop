package com.pvppractice.client.gui;

import com.pvppractice.config.PVPConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;

public class HeartIndicatorSettingsScreen extends Screen {
    private final Screen parent;
    private PVPConfig config;
    
    public HeartIndicatorSettingsScreen(Screen parent) {
        super(Text.literal("Heart Indicator Settings"));
        this.parent = parent;
        this.config = PVPConfig.getInstance();
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = this.height / 2 - 80;
        
        // Enable/Disable toggle
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal((config.heartIndicatorEnabled ? "§a[✓] Enabled" : "§c[✗] Disabled")),
            button -> {
                config.heartIndicatorEnabled = !config.heartIndicatorEnabled;
                button.setMessage(Text.literal((config.heartIndicatorEnabled ? "§a[✓] Enabled" : "§c[✗] Disabled")));
                config.save();
            }
        ).dimensions(centerX - 100, startY, 200, 20).build());
        
        // Heart Style selector
        this.addDrawableChild(CyclingButtonWidget.<PVPConfig.HeartStyle>builder(
            style -> Text.literal("Style: " + style.getDisplayName())
        ).values(PVPConfig.HeartStyle.values())
        .initially(config.heartStyle)
        .build(centerX - 100, startY + 30, 200, 20, Text.literal("Heart Style"), 
            (button, style) -> {
                config.heartStyle = style;
                config.save();
            }));
        
        // Show health numbers toggle
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal((config.showHealthNumbers ? "§a[✓] Show Numbers" : "§c[✗] Show Numbers")),
            button -> {
                config.showHealthNumbers = !config.showHealthNumbers;
                button.setMessage(Text.literal((config.showHealthNumbers ? "§a[✓] Show Numbers" : "§c[✗] Show Numbers")));
                config.save();
            }
        ).dimensions(centerX - 100, startY + 60, 200, 20).build());
        
        // Color selector
        this.addDrawableChild(CyclingButtonWidget.<PVPConfig.HealthBarColor>builder(
            color -> Text.literal("Color: " + color.getName())
        ).values(PVPConfig.HealthBarColor.values())
        .initially(config.healthBarColor)
        .build(centerX - 100, startY + 90, 200, 20, Text.literal("Bar Color"),
            (button, color) -> {
                config.healthBarColor = color;
                config.save();
            }));
        
        // Size selector
        this.addDrawableChild(CyclingButtonWidget.<Integer>builder(
            size -> {
                String sizeText;
                switch(size) {
                    case 1: sizeText = "Small"; break;
                    case 2: sizeText = "Medium"; break;
                    case 3: sizeText = "Large"; break;
                    default: sizeText = "Medium";
                }
                return Text.literal("Size: " + sizeText);
            }
        ).values(1, 2, 3)
        .initially(config.indicatorSize)
        .build(centerX - 100, startY + 120, 200, 20, Text.literal("Indicator Size"),
            (button, size) -> {
                config.indicatorSize = size;
                config.save();
            }));
        
        // Back button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§7← Back"),
            button -> {
                if (this.client != null) {
                    this.client.setScreen(parent);
                }
            }
        ).dimensions(centerX - 100, startY + 160, 200, 20).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, "§lHeart Indicator Settings", this.width / 2, this.height / 2 - 110, 0xFF5555);
        
        // Preview section
        context.drawCenteredTextWithShadow(this.textRenderer, "§7Preview:", this.width / 2, this.height / 2 - 40, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
