package com.yourname.dupemod.gui;

import com.yourname.dupemod.feature.ChestDupeEngine;
import com.yourname.dupemod.gui.components.ModernSlider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class DupeScreen extends Screen {
    private final Screen parent;

    public DupeScreen(Screen parent) {
        super(Text.of("Feather Dupe Menu"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 80;
        int y = this.height / 2 - 60;

        // 1. MASTER TOGGLE
        this.addDrawableChild(ButtonWidget.builder(
            Text.of("Master: " + (ChestDupeEngine.enabled ? "§aON" : "§cOFF")), (btn) -> {
                ChestDupeEngine.enabled = !ChestDupeEngine.enabled;
                btn.setMessage(Text.of("Master: " + (ChestDupeEngine.enabled ? "§aON" : "§cOFF")));
            }).dimensions(x, y, 160, 20).build());

        // 2. DELAY SETTING (10ms - 500ms)
        this.addDrawableChild(new ModernSlider(x, y + 30, 160, 16, "Speed Delay", 10, 500, ChestDupeEngine.delay, (val) -> {
            ChestDupeEngine.delay = val.intValue();
        }));

        // 3. REPEAT COUNT (1 - 10 times)
        this.addDrawableChild(new ModernSlider(x, y + 60, 160, 16, "Repeats", 1, 10, ChestDupeEngine.iterations, (val) -> {
            ChestDupeEngine.iterations = val.intValue();
        }));

        // 4. AUTO PICK TOGGLE
        this.addDrawableChild(ButtonWidget.builder(
            Text.of("Auto-Pick: " + (ChestDupeEngine.autoPick ? "§bON" : "§7OFF")), (btn) -> {
                ChestDupeEngine.autoPick = !ChestDupeEngine.autoPick;
                btn.setMessage(Text.of("Auto-Pick: " + (ChestDupeEngine.autoPick ? "§bON" : "§7OFF")));
            }).dimensions(x, y + 85, 160, 20).build());

        // 5. BACK
        this.addDrawableChild(ButtonWidget.builder(Text.of("Save & Exit"), (btn) -> this.client.setScreen(parent))
            .dimensions(x + 30, y + 120, 100, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x99000000); // Black Overlay
        context.fill(this.width / 2 - 100, this.height / 2 - 80, this.width / 2 + 100, this.height / 2 + 80, 0xFF111111); // Box
        context.fill(this.width / 2 - 100, this.height / 2 - 81, this.width / 2 + 100, this.height / 2 - 80, 0xFF00FBFF); // Cyan Top Line
        
        context.drawCenteredTextWithShadow(this.textRenderer, "§b§lFEATHER DUPE SETTINGS", this.width / 2, this.height / 2 - 75, 0xFFFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}
