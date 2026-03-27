package com.yourmod.client.gui.components;

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
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
