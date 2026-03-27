package com.pvppractice.client.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import java.util.function.Consumer;

public class ToggleWidget extends ClickableWidget {
    protected boolean toggled;
    protected final Consumer<Boolean> onToggle;
    protected final String label;
    protected float animationProgress = 0;
    protected boolean wasHovered = false;
    protected final TextRenderer textRenderer;
    
    public ToggleWidget(int x, int y, int width, int height, String label, boolean initialState, Consumer<Boolean> onToggle) {
        super(x, y, width, height, Text.literal(label));
        this.label = label;
        this.toggled = initialState;
        this.onToggle = onToggle;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
    }
    
    public boolean isToggled() {
        return toggled;
    }
    
    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        if (onToggle != null) {
            onToggle.accept(toggled);
        }
    }
    
    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // Update animation
        float targetAnimation = toggled ? 1.0f : 0.0f;
        if (Math.abs(animationProgress - targetAnimation) > 0.01f) {
            animationProgress += (targetAnimation - animationProgress) * delta * 0.2f;
        } else {
            animationProgress = targetAnimation;
        }
        
        boolean hovered = isHovered();
        if (hovered != wasHovered) {
            wasHovered = hovered;
        }
        
        // Draw label
        int labelColor = hovered ? 0xFFFFFF : 0xDDDDDD;
        if (toggled) {
            labelColor = 0x55FF55;
        }
        context.drawText(textRenderer, label + ":", getX(), getY() + (getHeight() - 8) / 2, labelColor, false);
        
        // Draw toggle switch background
        int toggleX = getX() + getWidth() - 40;
        int toggleY = getY();
        int toggleWidth = 36;
        int toggleHeight = getHeight();
        
        // Background
        int bgColor = toggled ? 0x8855FF55 : 0x88FF5555;
        context.fill(toggleX, toggleY, toggleX + toggleWidth, toggleY + toggleHeight, bgColor);
        
        // Border
        context.drawBorder(toggleX, toggleY, toggleWidth, toggleHeight, hovered ? 0xFFFFFF : 0xAAAAAA);
        
        // Draw slider
        int sliderWidth = toggleWidth / 2 - 2;
        int sliderY = toggleY + 2;
        
        // Animated slider
        float animatedSliderX = toggleX + 2 + (toggleWidth - sliderWidth - 4) * animationProgress;
        int finalSliderX = (int)animatedSliderX;
        
        // Slider background with gradient
        for (int i = 0; i < sliderWidth; i++) {
            int shade = 0xFFAAAAAA;
            if (hovered) {
                shade = 0xFFCCCCCC;
            }
            context.fill(finalSliderX + i, sliderY, finalSliderX + i + 1, sliderY + toggleHeight - 4, shade);
        }
        
        // Draw icon based on state
        if (toggled) {
            // Draw checkmark
            context.drawText(textRenderer, "✓", finalSliderX + sliderWidth / 2 - 3, sliderY + 1, 0x55FF55, false);
        } else {
            // Draw X
            context.drawText(textRenderer, "✗", finalSliderX + sliderWidth / 2 - 3, sliderY + 1, 0xFF5555, false);
        }
        
        // Draw glow effect when hovered
        if (hovered) {
            context.fill(toggleX, toggleY, toggleX + toggleWidth, toggleY + toggleHeight, 0x22FFFFFF);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && isMouseOver(mouseX, mouseY)) {
            this.toggled = !this.toggled;
            if (onToggle != null) {
                onToggle.accept(toggled);
            }
            playDownSound(MinecraftClient.getInstance().getSoundManager());
            return true;
        }
        return false;
    }
    
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }
    
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth() &&
               mouseY >= getY() && mouseY <= getY() + getHeight();
    }
    
    // Switch style toggle
    public static class SwitchToggleWidget extends ToggleWidget {
        public SwitchToggleWidget(int x, int y, int width, int height, String label, boolean initialState, Consumer<Boolean> onToggle) {
            super(x, y, width, height, label, initialState, onToggle);
        }
        
        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            boolean hovered = isHovered();
            context.drawText(textRenderer, label, getX(), getY() + (getHeight() - 8) / 2, hovered ? 0xFFFFFF : 0xDDDDDD, false);
            
            int switchX = getX() + getWidth() - 50;
            int switchY = getY();
            int switchWidth = 48;
            int switchHeight = getHeight();
            
            // Draw switch background
            int bgColor = toggled ? 0xFF55FF55 : 0xFFFF5555;
            context.fill(switchX, switchY, switchX + switchWidth, switchY + switchHeight, bgColor);
            context.drawBorder(switchX, switchY, switchWidth, switchHeight, hovered ? 0xFFFFFF : 0xAAAAAA);
            
            // Draw text on switch
            String switchText = toggled ? "ON" : "OFF";
            int textColor = toggled ? 0x00AA00 : 0xAA0000;
            context.drawCenteredTextWithShadow(textRenderer, switchText, switchX + switchWidth / 2, switchY + (switchHeight - 8) / 2, textColor);
        }
    }
    
    // Button style toggle
    public static class ButtonToggleWidget extends ToggleWidget {
        public ButtonToggleWidget(int x, int y, int width, int height, String label, boolean initialState, Consumer<Boolean> onToggle) {
            super(x, y, width, height, label, initialState, onToggle);
        }
        
        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            boolean hovered = isHovered();
            int bgColor = toggled ? 0xFF55FF55 : 0xFF555555;
            
            if (hovered) {
                bgColor = toggled ? 0xFF66FF66 : 0xFF666666;
            }
            
            context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), bgColor);
            context.drawBorder(getX(), getY(), getWidth(), getHeight(), hovered ? 0xFFFFFF : 0xAAAAAA);
            
            String displayText = (toggled ? "✓ " : "○ ") + label;
            context.drawCenteredTextWithShadow(textRenderer, displayText, getX() + getWidth() / 2, getY() + (getHeight() - 8) / 2, 0xFFFFFF);
        }
    }
    
    // Checkbox style toggle
    public static class CheckboxToggleWidget extends ToggleWidget {
        public CheckboxToggleWidget(int x, int y, int width, int height, String label, boolean initialState, Consumer<Boolean> onToggle) {
            super(x, y, width, height, label, initialState, onToggle);
        }
        
        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            boolean hovered = isHovered();
            int checkBoxSize = getHeight() - 4;
            int checkBoxX = getX();
            int checkBoxY = getY() + 2;
            
            // Draw checkbox
            context.fill(checkBoxX, checkBoxY, checkBoxX + checkBoxSize, checkBoxY + checkBoxSize, 0xFF333333);
            context.drawBorder(checkBoxX, checkBoxY, checkBoxSize, checkBoxSize, hovered ? 0xFFFFFF : 0xAAAAAA);
            
            if (toggled) {
                // Draw checkmark
                context.drawText(textRenderer, "✓", checkBoxX + 2, checkBoxY + 1, 0x55FF55, false);
            }
            
            // Draw label
            int textColor = hovered ? 0xFFFFFF : 0xDDDDDD;
            if (toggled) {
                textColor = 0x55FF55;
            }
            context.drawText(textRenderer, label, checkBoxX + checkBoxSize + 6, getY() + (getHeight() - 8) / 2, textColor, false);
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int checkBoxSize = getHeight() - 4;
            int checkBoxX = getX();
            int checkBoxY = getY() + 2;
            
            if (mouseX >= checkBoxX && mouseX <= checkBoxX + checkBoxSize + textRenderer.getWidth(label) + 10 &&
                mouseY >= checkBoxY && mouseY <= checkBoxY + checkBoxSize) {
                setToggled(!toggled);
                playDownSound(MinecraftClient.getInstance().getSoundManager());
                return true;
            }
            return false;
        }
    }
}
