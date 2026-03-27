package com.yourmod.client.gui.components;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.function.Consumer;

public class CategoryButton extends ButtonWidget {
    private final int id;
    private final Consumer<Integer> onSelect;
    private boolean selected = false;
    
    public CategoryButton(int x, int y, int width, int height, Text message, 
                          int id, Consumer<Integer> onSelect) {
        super(x, y, width, height, message, button -> {});
        this.id = id;
        this.onSelect = onSelect;
    }
    
    @Override
    public void onClick(double mouseX, double mouseY) {
        onSelect.accept(id);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int bgColor = selected ? 0xFF00A8FF : (isHovered() ? 0xFF444444 : 0xFF333333);
        int textColor = selected ? 0xFFFFFFFF : (isHovered() ? 0xFFFFFFFF : 0xFFAAAAAA);
        
        context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), bgColor);
        
        if (selected) {
            context.fill(getX(), getY(), getX() + 3, getY() + getHeight(), 0xFFFFFFFF);
        }
        
        context.drawTextWithShadow(textRenderer, getMessage(), 
            getX() + 10, getY() + (getHeight() - 8) / 2, textColor);
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
