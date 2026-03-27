package com.yourmod.client.gui;

import com.yourmod.client.gui.categories.FontSettingsCategory;
import com.yourmod.client.gui.components.CategoryButton;
import com.yourmod.client.gui.components.SettingsPanel;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TClientScreen extends Screen {
    private final Screen parent;
    private List<CategoryButton> categoryButtons;
    private SettingsPanel currentSettingsPanel;
    private int selectedCategory = 0;
    
    private static final int BACKGROUND_COLOR = 0xFF1A1A1A;
    private static final int SIDEBAR_COLOR = 0xFF2D2D2D;
    private static final int ACCENT_COLOR = 0xFF00A8FF;
    private static final int SIDEBAR_WIDTH = 180;
    private static final int HEADER_HEIGHT = 60;
    
    public TClientScreen(Screen parent) {
        super(Text.literal("T Client Menu"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        categoryButtons = new ArrayList<>();
        
        addCategory("Font Settings", 0);
        
        updateSettingsPanel();
    }
    
    private void addCategory(String name, int id) {
        int y = HEADER_HEIGHT + 10 + (categoryButtons.size() * 45);
        CategoryButton button = new CategoryButton(10, y, SIDEBAR_WIDTH - 20, 35, 
            Text.literal(name), id, this::onCategorySelected);
        categoryButtons.add(button);
        addDrawableChild(button);
        
        if (id == 0) {
            button.setSelected(true);
        }
    }
    
    private void onCategorySelected(int categoryId) {
        this.selectedCategory = categoryId;
        
        for (int i = 0; i < categoryButtons.size(); i++) {
            categoryButtons.get(i).setSelected(i == categoryId);
        }
        
        updateSettingsPanel();
    }
    
    private void updateSettingsPanel() {
        if (currentSettingsPanel != null) {
            for (var widget : currentSettingsPanel.getWidgets()) {
                remove(widget);
            }
        }
        
        if (selectedCategory == 0) {
            currentSettingsPanel = new FontSettingsCategory(this, SIDEBAR_WIDTH + 20, 80, 
                width - SIDEBAR_WIDTH - 40, height - 100);
        } else {
            currentSettingsPanel = new SettingsPanel(this, SIDEBAR_WIDTH + 20, 80,
                width - SIDEBAR_WIDTH - 40, height - 100);
        }
        
        for (var widget : currentSettingsPanel.getWidgets()) {
            addDrawableChild(widget);
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, BACKGROUND_COLOR);
        context.fill(0, 0, SIDEBAR_WIDTH, height, SIDEBAR_COLOR);
        context.fill(0, 0, width, HEADER_HEIGHT, 0xFF000000);
        
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("§lT CLIENT MENU"), 
            width / 2, 20, ACCENT_COLOR);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Advanced Client Mod"), 
            width / 2, 38, 0xFF888888);
        context.drawTextWithShadow(textRenderer, Text.literal("§lCATEGORIES"), 
            15, HEADER_HEIGHT - 20, ACCENT_COLOR);
        
        context.fill(0, HEADER_HEIGHT, SIDEBAR_WIDTH, HEADER_HEIGHT + 1, ACCENT_COLOR);
        context.fill(0, HEADER_HEIGHT + 45, SIDEBAR_WIDTH, HEADER_HEIGHT + 46, 0xFF444444);
        
        if (currentSettingsPanel != null) {
            currentSettingsPanel.render(context, mouseX, mouseY, delta);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (currentSettingsPanel != null && currentSettingsPanel.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (currentSettingsPanel != null && currentSettingsPanel.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (currentSettingsPanel != null && currentSettingsPanel.charTyped(chr, modifiers)) {
            return true;
        }
        return super.charTyped(chr, modifiers);
    }
    
    @Override
    public void close() {
        client.setScreen(parent);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
