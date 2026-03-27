package com.sikandar.tpvpmod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class TPVPDashboardScreen extends Screen {
    public TPVPDashboardScreen() {
        super(Text.literal("§cT PVP Dashboard"));
    }

    @Override
    protected void init() {
        int y = this.height / 4;

        // Player Health Indicator button
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Player Health Indicator: " + (TPVPConfig.healthIndicatorEnabled ? "§aON" : "§cOFF")),
                button -> {
                    TPVPConfig.healthIndicatorEnabled = !TPVPConfig.healthIndicatorEnabled;
                    if (TPVPConfig.healthIndicatorEnabled) {
                        this.client.setScreen(new HealthIndicatorSettingsScreen());
                    } else {
                        this.client.setScreen(this); // refresh
                    }
                }
        ).dimensions(this.width / 2 - 100, y, 200, 20).build());

        // Baad me aur options yaha add kar sakta hai (TBD)
        // Example: addDrawableChild(ButtonWidget.builder(Text.literal("More Options Coming..."), b -> {}).dimensions(...).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 30, 0xFFFFFF);
    }
}
