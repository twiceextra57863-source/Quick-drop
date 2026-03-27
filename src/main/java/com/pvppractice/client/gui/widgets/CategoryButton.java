package com.pvppractice.client.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CategoryButton extends ButtonWidget {
    private final CategoryType categoryType;
    private boolean selected;
    private float hoverAnimation = 0;
    private final TextRenderer textRenderer;
    
    public enum CategoryType {
        HEART_INDICATOR("❤ Heart Indicator", 0xFF5555),
        COMBAT_TRACKER("⚔ Combat Tracker", 0xFFAA00),
        AIM_TRAINER("🎯 Aim Trainer", 0x55FF55),
        STATS_TRACKER("📊 Stats Tracker", 0x55FFFF);
        
        private final String displayName;
        private final int color;
        
        CategoryType(String displayName, int color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public int getColor() {
            return color;
        }
    }
    
    public CategoryButton(int x, int y, int width, int height, CategoryType categoryType, PressAction onPress) {
        super(x, y, width, height, Text.literal(categoryType.getDisplayName()), onPress, DEFAULT_NARRATION_SUPPLIER);
        this.categoryType = categoryType;
        this.selected = false;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // Update hover animation
        if (isHovered()) {
            hoverAnimation = Math.min(1.0f, hoverAnimation + delta * 0.1f);
        } else {
            hoverAnimation = Math.max(0.0f, hoverAnimation - delta * 0.1f);
        }
        
        // Background with gradient
        int bgColor = 0xFF2D2D2D;
        int borderColor = selected ? categoryType.getColor() : 0xFFAAAAAA;
        
        if (isHovered()) {
            bgColor = 0xFF3D3D3D;
        }
        
        // Draw background
        context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), bgColor);
        
        // Draw border with animation
        int borderWidth = selected ? 3 : (int)(1 + hoverAnimation * 1);
        for (int i = 0; i < borderWidth; i++) {
            if (selected) {
                // Glowing effect for selected button
                int glowColor = categoryType.getColor();
                context.fill(getX() + i, getY() + i, getX() + getWidth() - i, getY() + i + 1, glowColor);
                context.fill(getX() + i, getY() + getHeight() - i - 1, getX() + getWidth() - i, getY() + getHeight() - i, glowColor);
                context.fill(getX() + i, getY() + i, getX() + i + 1, getY() + getHeight() - i, glowColor);
                context.fill(getX() + getWidth() - i - 1, getY() + i, getX() + getWidth() - i, getY() + getHeight() - i, glowColor);
            } else {
                context.drawBorder(getX() + i, getY() + i, getWidth() - i * 2, getHeight() - i * 2, borderColor);
            }
        }
        
        // Draw icon and text with color
        int textColor = selected ? categoryType.getColor() : (isHovered() ? 0xFFFFFF : 0xDDDDDD);
        
        // Draw icon (using Unicode characters with color)
        String icon = categoryType.getDisplayName().substring(0, 2);
        context.drawText(textRenderer, icon, getX() + 8, getY() + (getHeight() - 8) / 2, textColor, false);
        
        // Draw text with shadow
        String displayText = categoryType.getDisplayName().substring(2);
        context.drawText(textRenderer, displayText, getX() + 24, getY() + (getHeight() - 8) / 2, textColor, false);
        
        // Draw selection indicator if selected
        if (selected) {
            int indicatorX = getX() + getWidth() - 12;
            int indicatorY = getY() + getHeight() / 2 - 4;
            context.fill(indicatorX, indicatorY, indicatorX + 8, indicatorY + 8, categoryType.getColor());
            context.fill(indicatorX + 2, indicatorY + 2, indicatorX + 6, indicatorY + 6, 0xFFFFFFFF);
        }
        
        // Draw hover effect
        if (hoverAnimation > 0) {
            int hoverColor = (int)(0x22FFFFFF * hoverAnimation);
            context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), hoverColor);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && this.isMouseOver(mouseX, mouseY)) {
            this.onPress();
            return true;
        }
        return false;
    }
}
