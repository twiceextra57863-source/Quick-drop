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
    private int selectedCategory = 0;
    private final List<String> categories = new ArrayList<>();
    private int scrollOffset = 0;

    public TClientScreen(Screen parent) {
        super(Text.literal("T Client Menu"));
        this.parent = parent;
        categories.add("Fonts");
        categories.add("Visuals");
        categories.add("Combat");
    }

    @Override
    protected void init() {
        this.clearChildren();

        // --- SIDEBAR ---
        for (int i = 0; i < categories.size(); i++) {
            int index = i;
            this.addDrawableChild(ButtonWidget.builder(Text.literal(categories.get(i)), button -> {
                selectedCategory = index;
                this.init();
            }).dimensions(10, 40 + (i * 25), 80, 20).build());
        }

        // --- FONT LIST WITH PAGINATION/SCROLL ---
        if (selectedCategory == 0) {
            List<String> fonts = FontManager.availableFonts;
            int startX = 115;
            int startY = 50;
            
            for (int i = 0; i < fonts.size(); i++) {
                String fontName = fonts.get(i);
                // Grid layout (2 columns)
                int col = i % 2;
                int row = i / 2;
                
                this.addDrawableChild(ButtonWidget.builder(Text.literal(fontName), button -> {
                    FontManager.currentFont = fontName;
                }).dimensions(startX + (col * 110), startY + (row * 25), 100, 20).build());
            }
            
            // Refresh Button to scan new fonts in folder
            this.addDrawableChild(ButtonWidget.builder(Text.literal("§aScan Folder"), button -> {
                FontManager.loadExternalFonts();
                this.init();
            }).dimensions(this.width - 90, this.height - 30, 80, 20).build());
        }

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> {
            this.client.setScreen(parent);
        }).dimensions(10, this.height - 30, 80, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, 100, this.height, 0xAA111111);
        context.fill(105, 10, this.width - 10, this.height - 10, 0x88000000);

        context.drawTextWithShadow(this.textRenderer, "§bT-CLIENT §7> " + categories.get(selectedCategory), 115, 20, 0xFFFFFF);
        
        if (selectedCategory == 0) {
            context.drawTextWithShadow(this.textRenderer, "§eActive: §f" + FontManager.currentFont, 115, 35, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "Put .ttf files in: .minecraft/config/T-Client/fonts", 115, this.height - 25, 0xAAAAAA);
        }

        super.render(context, mouseX, mouseY, delta);
    }
}
