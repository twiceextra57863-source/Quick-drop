package com.yourmod.client.gui.components;

import com.yourmod.client.gui.TClientScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public class SettingsPanel extends ElementListWidget<SettingsPanel.Entry> {
    protected final TClientScreen parent;
    protected final List<ClickableWidget> widgets = new ArrayList<>();
    
    public SettingsPanel(TClientScreen parent, int x, int y) {
        super(parent.client, parent.width - x - 20, parent.height - y - 20, y, 25);
        this.parent = parent;
        this.setLeftPos(x);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        for (ClickableWidget widget : widgets) {
            widget.render(context, mouseX, mouseY, delta);
        }
    }
    
    protected static class Entry extends ElementListWidget.Entry<Entry> {
        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {}
        
        @Override
        public List<ClickableWidget> selectableChildren() {
            return List.of();
        }
        
        @Override
        public List<ClickableWidget> children() {
            return List.of();
        }
    }
}
