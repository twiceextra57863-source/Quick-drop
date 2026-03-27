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
        
        // Categories list
        categories.add("Fonts");
        categories.add("Visuals");
        categories.add("Combat");
        categories.add("Misc");
    }

    @Override
    protected void init() {
        this.clearChildren();

        // --- LEFT SIDEBAR (The Navigation) ---
        int sidebarWidth = 100;
        for (int i = 0; i < categories.size(); i++) {
            int index = i;
            String categoryName = categories.get(i);
            
            // Design: Buttons are slim and placed vertically
            this.addDrawableChild(ButtonWidget.builder(Text.literal(categoryName), button -> {
                selectedCategory = index;
                this.init(); // UI refresh
            }).dimensions(10, 45 + (i * 25), 80, 20).build());
        }

        // --- RIGHT DASHBOARD CONTENT (Settings Area) ---
        if (selectedCategory == 0) { // Fonts Category Logic
            List<String> fonts = FontManager.availableFonts;
            int startX = 115;
            int startY = 55;
            
            for (int i = 0; i < fonts.size(); i++) {
                String fontName = fonts.get(i);
                int col = i % 2; // 2 Columns layout
                int row = i / 2;
                
                int buttonX = startX + (col * 115);
                int buttonY = startY + (row * 25);

                // Prevent overflow if fonts list is too long
                if (buttonY < this.height - 40) {
                    this.addDrawableChild(ButtonWidget.builder(Text.literal(fontName), button -> {
                        FontManager.currentFont = fontName;
                    }).dimensions(buttonX, buttonY, 110, 20).build());
                }
            }
        }

        // --- UTILITY BUTTONS ---
        // Refresh button for external fonts
        this.addDrawableChild(ButtonWidget.builder(Text.literal("§aScan Folder"), button -> {
            FontManager.loadExternalFonts();
            this.init();
        }).dimensions(this.width - 95, this.height - 30, 85, 20).build());

        // Back Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> {
            this.client.setScreen(parent);
        }).dimensions(10, this.height - 30, 80, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 1. Background Blur/Darken
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // 2. Sidebar Panel Design (Dark translucent)
        context.fill(0, 0, 100, this.height, 0xAA000000); 
        context.fill(100, 0, 101, this.height, 0xFF333333); // Vertical separator line
        
        // 3. Main Dashboard Glass Panel
        context.fill(105, 10, this.width - 10, this.height - 10, 0x77000000);

        // 4. Header Text
        context.drawCenteredTextWithShadow(this.textRenderer, "§b§lT-CLIENT §r§7| Dashboard", (this.width + 100) / 2, 15, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "§eCategory: §f" + categories.get(selectedCategory), 115, 35, 0xFFFFFF);

        // 5. Specific Category UI Details
        if (selectedCategory == 0) {
            context.drawTextWithShadow(this.textRenderer, "§7Current: §a" + FontManager.currentFont, this.width - 150, 35, 0xFFFFFF);
            
            // Helpful tip at bottom
            context.drawTextWithShadow(this.textRenderer, "§8Drop .ttf files in config/T-Client/fonts", 115, this.height - 25, 0xFFFFFF);
        } else {
            // Placeholder text for empty categories
            context.drawCenteredTextWithShadow(this.textRenderer, "§7More features coming soon in " + categories.get(selectedCategory) + "...", (this.width + 100) / 2, this.height / 2, 0x555555);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false; // Game continues in background
    }
}
