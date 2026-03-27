package com.tclient.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public class TClientScreen extends Screen {
    private final Screen parent;
    private int selectedCategory = 0;
    private final List<String> categories = new ArrayList<>();

    public TClientScreen(Screen parent) {
        super(Text.literal("T Client Menu"));
        this.parent = parent;
        categories.add("Fonts");
        categories.add("Visuals"); // Future use
        categories.add("Combat");  // Future use
    }

    @Override
    protected void init() {
        // Left Sidebar Buttons
        for (int i = 0; i < categories.size(); i++) {
            int index = i;
            this.addDrawableChild(ButtonWidget.builder(Text.literal(categories.get(i)), button -> {
                selectedCategory = index;
            }).dimensions(10, 40 + (i * 25), 80, 20).build());
        }

        // Back Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> {
            this.client.setScreen(parent);
        }).dimensions(10, this.height - 30, 80, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // Sidebar Background (Translucent Black)
        context.fill(0, 0, 100, this.height, 0x88000000);
        
        // Main Dashboard Background
        context.fill(105, 10, this.width - 10, this.height - 10, 0x44000000);

        context.drawCenteredTextWithShadow(this.textRenderer, "T CLIENT - " + categories.get(selectedCategory), (this.width + 100) / 2, 20, 0xFFFFFF);

        // Logic for showing settings based on selectedCategory
        if (selectedCategory == 0) {
            context.drawTextWithShadow(this.textRenderer, "Select Custom Font:", 120, 50, 0xAAAAAA);
            context.drawTextWithShadow(this.textRenderer, "> Modern Font (Active)", 120, 70, 0x00FF00);
            context.drawTextWithShadow(this.textRenderer, "> Pixel Classic", 120, 85, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "> Smooth Sans", 120, 100, 0xFFFFFF);
        }

        super.render(context, mouseX, mouseY, delta);
    }
}

