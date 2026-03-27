package com.tclient.gui;

import com.tclient.font.FontManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public class TClientScreen extends Screen {
    private final Screen parent;
    private int selectedCategory = 0; // 0 = Fonts, 1 = Visuals, etc.
    private final List<String> categories = new ArrayList<>();

    public TClientScreen(Screen parent) {
        super(Text.literal("T Client Menu"));
        this.parent = parent;
        
        // Add categories here
        categories.add("Fonts");
        categories.add("Visuals");
        categories.add("Combat");
        categories.add("Misc");
    }

    @Override
    protected void init() {
        this.clearChildren();

        // --- LEFT SIDEBAR (Vertical Menu) ---
        int sidebarWidth = 90;
        for (int i = 0; i < categories.size(); i++) {
            int index = i;
            String categoryName = categories.get(i);
            
            this.addDrawableChild(ButtonWidget.builder(Text.literal(categoryName), button -> {
                selectedCategory = index;
                this.init(); // Refresh UI to show selected category buttons
            }).dimensions(10, 40 + (i * 25), 80, 20).build());
        }

        // --- RIGHT DASHBOARD CONTENT ---
        // Only show Font buttons if "Fonts" category (index 0) is selected
        if (selectedCategory == 0) {
            // Modern Font Button
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Modern Font"), button -> {
                FontManager.currentFont = "modern";
            }).dimensions(110, 50, 120, 20).build());

            // Smooth Font Button
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Smooth Font"), button -> {
                FontManager.currentFont = "smooth";
            }).dimensions(110, 80, 120, 20).build());

            // Default Font Button
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Default Font"), button -> {
                FontManager.currentFont = "default";
            }).dimensions(110, 110, 120, 20).build());
        }

        // --- BOTTOM BACK BUTTON ---
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back to Game"), button -> {
            this.client.setScreen(parent);
        }).dimensions(10, this.height - 30, 80, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 1. Black translucent background
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // 2. Sidebar Background Panel
        context.fill(0, 0, 100, this.height, 0xAA111111); // Dark Gray sidebar
        
        // 3. Main Content Panel
        context.fill(105, 10, this.width - 10, this.height - 10, 0x88000000); // Transparent black dashboard

        // 4. Header Title
        context.drawTextWithShadow(this.textRenderer, "§bT-CLIENT §7| " + categories.get(selectedCategory).toUpperCase(), 115, 20, 0xFFFFFF);

        // 5. Help Text for Font Category
        if (selectedCategory == 0) {
            context.drawTextWithShadow(this.textRenderer, "§eCurrent Font: §f" + FontManager.currentFont, 115, 140, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "Note: Make sure to add .ttf in assets.", 115, 160, 0xAAAAAA);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false; // Game pause nahi hoga menu kholne pe (Pro feel)
    }
}
