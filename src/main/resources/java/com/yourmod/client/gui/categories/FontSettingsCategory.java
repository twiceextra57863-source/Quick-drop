package com.yourmod.client.gui.categories;

import com.yourmod.client.gui.components.SettingsPanel;
import com.yourmod.client.gui.TClientScreen;
import com.yourmod.config.ModConfig;
import com.yourmod.utils.FontManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
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
    
    // Available fonts
    private final String[] availableFonts = {
        "Default", "Minecraft", "Arial", "Times New Roman", "Courier New", 
        "Verdana", "Comic Sans MS", "Impact", "Georgia", "Custom"
    };
    
    public FontSettingsCategory(TClientScreen parent, int x, int y) {
        super(parent, x, y);
        initializeFontButtons();
        initializeCustomFontInput();
        initializeActionButtons();
    }
    
    private void initializeFontButtons() {
        for (int i = 0; i < availableFonts.length; i++) {
            final int index = i;
            ButtonWidget fontButton = ButtonWidget.builder(
                Text.literal(availableFonts[i]),
                button -> onFontSelected(index)
            ).dimensions(10, 50 + (i * 30), 150, 20).build();
            
            fontButtons.add(fontButton);
            widgets.add(fontButton);
        }
    }
    
    private void initializeCustomFontInput() {
        customFontField = new TextFieldWidget(parent.textRenderer, 10, 
            fontButtons.size() * 30 + 60, 200, 20, Text.literal("Custom Font Name"));
        customFontField.setPlaceholder(Text.literal("Enter custom font name"));
        customFontField.setText(ModConfig.getCustomFontName());
        widgets.add(customFontField);
    }
    
    private void initializeActionButtons() {
        applyButton = ButtonWidget.builder(
            Text.literal("Apply Font"),
            button -> applyFont()
        ).dimensions(10, fontButtons.size() * 30 + 100, 90, 20).build();
        
        resetButton = ButtonWidget.builder(
            Text.literal("Reset to Default"),
            button -> resetToDefault()
        ).dimensions(110, fontButtons.size() * 30 + 100, 90, 20).build();
        
        widgets.add(applyButton);
        widgets.add(resetButton);
    }
    
    private void onFontSelected(int index) {
        selectedFontIndex = index;
        // Update visual feedback for selected font
        for (int i = 0; i < fontButtons.size(); i++) {
            ButtonWidget button = fontButtons.get(i);
            if (i == index) {
                button.setMessage(Text.literal("> " + availableFonts[i]));
            } else {
                button.setMessage(Text.literal(availableFonts[i]));
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
        
        // Show success message
        parent.client.inGameHud.getChatHud().addMessage(
            Text.literal("§aFont changed to: §f" + fontName)
        );
    }
    
    private void resetToDefault() {
        selectedFontIndex = 0;
        onFontSelected(0);
        customFontField.setText("");
        FontManager.resetFont();
        ModConfig.resetFont();
        
        parent.client.inGameHud.getChatHud().addMessage(
            Text.literal("§aFont reset to default")
        );
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        // Draw section title
        context.drawTextWithShadow(parent.textRenderer, 
            Text.literal("Font Settings"), 
            getLeft() + 10, getTop() + 20, 0xFFFFFFFF);
        
        context.drawTextWithShadow(parent.textRenderer, 
            Text.literal("Select a font style:"), 
            getLeft() + 10, getTop() + 40, 0xFFAAAAAA);
            
        context.drawTextWithShadow(parent.textRenderer, 
            Text.literal("Current Font: §e" + ModConfig.getCurrentFont()), 
            getLeft() + 10, getTop() + fontButtons.size() * 30 + 130, 0xFFAAAAAA);
            
        // Show preview if custom font
        if (selectedFontIndex == availableFonts.length - 1 && !customFontField.getText().isEmpty()) {
            context.drawTextWithShadow(parent.textRenderer, 
                Text.literal("Preview: " + customFontField.getText()), 
                getLeft() + 10, getTop() + fontButtons.size() * 30 + 150, 0xFF00FF00);
        }
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
                                   }
