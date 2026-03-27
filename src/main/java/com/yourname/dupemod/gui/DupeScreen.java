package com.yourname.dupemod.gui;

import com.yourname.dupemod.feature.ChestDupeEngine;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class DupeScreen extends Screen {
    private final Screen parent;

    public DupeScreen(Screen parent) {
        super(Text.of("Dupe Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // Toggle Button
        this.addDrawableChild(ButtonWidget.builder(Text.of("Status: " + (ChestDupeEngine.enabled ? "§aON" : "§cOFF")), (btn) -> {
            ChestDupeEngine.enabled = !ChestDupeEngine.enabled;
            btn.setMessage(Text.of("Status: " + (ChestDupeEngine.enabled ? "§aON" : "§cOFF")));
        }).dimensions(this.width / 2 - 100, 80, 200, 20).build());

        // Back Button
        this.addDrawableChild(ButtonWidget.builder(Text.of("Back"), (btn) -> this.client.setScreen(parent))
            .dimensions(this.width / 2 - 100, 180, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Feather-style background (Dark Translucent)
        context.fill(this.width / 2 - 120, 50, this.width / 2 + 120, 220, 0xAA000000);
        context.drawCenteredTextWithShadow(this.textRenderer, "DUPE MOD SETTINGS", this.width / 2, 60, 0x00FFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}
