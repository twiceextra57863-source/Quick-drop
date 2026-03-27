package com.yourmod.client.gui.categories;

import com.yourmod.client.gui.components.SettingsPanel;
import com.yourmod.config.ModConfig;
import com.yourmod.utils.FontManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class FontSettingsCategory extends SettingsPanel {
    private final List<ButtonWidget> fontButtons = new ArrayList<>();
    private TextFieldWidget customFontField;
    private ButtonWidget applyButton;
    private ButtonWidget resetButton;
    private int selectedFontIndex = 0;
    private int currentY = 20;
    
    // Available fonts
    private final String[] availableFonts = {
        "Default", "Minecraft", "Arial", "Times New Roman", "Courier New", 
        "Verdana", "Comic Sans MS", "Impact", "Georgia", "Custom"
    };
    
    public FontSettingsCategory(Screen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        initializeComponents();
    }
    
    private void initializeComponents() {
        currentY = 20;
        
        // Create font buttons
        for (int i = 0; i < availableFonts.length; i++) {
            final int index = i;
            ButtonWidget fontButton = ButtonWidget.builder(
                Text.literal(availableFonts[i]),
                button -> onFontSelected(index)
            ).dimensions(x + 10, y + currentY, 150, 20).build();
            
            fontButtons.add(fontButton);
            addWidget(fontButton);
            currentY += 25;
        }
        
        currentY += 10;
        
        // Custom font input
        customFontField = new TextFieldWidget(parent.getTextRenderer(), x + 10, y + currentY, 200, 20, Text.literal("Custom Font Name"));
        customFontField.setPlaceholder(Text.literal("Enter custom font name"));
        customFontField.setText(ModConfig.getCustomFontName());
        addWidget(customFontField);
        
        currentY += 30;
        
        // Action buttons
        applyButton = ButtonWidget.builder(
            Text.literal("Apply Font"),
            button -> applyFont()
        ).dimensions(x + 10, y + currentY, 90, 20).build();
        
        resetButton = ButtonWidget.builder(
            Text.literal("Reset to Default"),
            button -> resetToDefault()
        ).dimensions(x + 110, y + currentY, 90, 20).build();
        
        addWidget(applyButton);
        addWidget(resetButton);
        
        currentY += 40;
        
        // Set initial selected font
        String currentFont = ModConfig.getCurrentFont();
        for (int i = 0; i < availableFonts.length; i++) {
            if (availableFonts[i].equalsIgnoreCase(currentFont) || 
                (i == availableFonts.length - 1 && currentFont.equals(ModConfig.getCustomFontName()))) {
                selectedFontIndex = i;
                break;
            }
        }
        updateButtonVisuals();
    }
    
    private void onFontSelected(int index) {
        selectedFontIndex = index;
        updateButtonVisuals();
        
        // If custom font is selected, focus on the text field
        if (selectedFontIndex == availableFonts.length - 1) {
            customFontField.setFocused(true);
        }
    }
    
    private void updateButtonVisuals() {
        for (int i = 0; i < fontButtons.size(); i++) {
            ButtonWidget button = fontButtons.get(i);
            if (i == selectedFontIndex) {
                button.setMessage(Text.literal("→ " + availableFonts[i]));
            } else {
                button.setMessage(Text.literal("  " + availableFonts[i]));
            }
        }
    }
    
    private void applyFont() {
        String fontName;
        
        if (selectedFontIndex == availableFonts.length - 1) {
            // Custom font selected
            fontName = customFontField.getText();
            if (fontName.isEmpty()) {
                fontName = "Minecraft";
            }
            ModConfig.setCustomFontName(fontName);
        } else {
            fontName = availableFonts[selectedFontIndex];
        }
        
        // Apply the font
        FontManager.applyFont(fontName);
        ModConfig.setSelectedFont(fontName);
        
        // Show success message (you'll need to access MinecraftClient for this)
        // This part would need MinecraftClient.getInstance()
    }
    
    private void resetToDefault() {
        selectedFontIndex = 0;
        updateButtonVisuals();
        customFontField.setText("");
        FontManager.resetFont();
        ModConfig.resetFont();
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        int panelX = x + 10;
        int panelY = y + 10;
        
        // Draw section title
        context.drawTextWithShadow(parent.getTextRenderer(), 
            Text.literal("§lFont Settings"), 
            panelX, panelY, 0xFFFFFFFF);
        
        context.drawTextWithShadow(parent.getTextRenderer(), 
            Text.literal("Select a font style:"), 
            panelX, panelY + 15, 0xFFAAAAAA);
        
        // Draw current font info
        context.drawTextWithShadow(parent.getTextRenderer(), 
            Text.literal("Current Font: §e" + ModConfig.getCurrentFont()), 
            panelX, y + currentY + 10, 0xFFAAAAAA);
        
        // Show preview if custom font
        if (selectedFontIndex == availableFonts.length - 1 && !customFontField.getText().isEmpty()) {
            context.drawTextWithShadow(parent.getTextRenderer(), 
                Text.literal("Preview: §a" + customFontField.getText()), 
                panelX, y + currentY + 25, 0xFFAAAAAA);
        }
        
        // Draw separator line
        context.fill(x, y + 55, x + width, y + 56, 0xFF444444);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle custom font field click
        if (customFontField.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        
        // Handle button clicks
        for (ButtonWidget fontButton : fontButtons) {
            if (fontButton.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        
        if (applyButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        
        if (resetButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (customFontField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (customFontField.charTyped(chr, modifiers)) {
            return true;
        }
        return super.charTyped(chr, modifiers);
    }
}
