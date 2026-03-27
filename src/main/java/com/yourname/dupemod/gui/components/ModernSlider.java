package com.yourname.dupemod.gui.components;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import java.util.function.Consumer;

public class ModernSlider extends SliderWidget {
    private final String prefix;
    private final double min;
    private final double max;
    private final Consumer<Double> onValueChange;

    public ModernSlider(int x, int y, int width, int height, String prefix, double min, double max, double defaultValue, Consumer<Double> onValueChange) {
        super(x, y, width, height, Text.of(""), (defaultValue - min) / (max - min));
        this.prefix = prefix;
        this.min = min;
        this.max = max;
        this.onValueChange = onValueChange;
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        double currentValue = min + (value * (max - min));
        setMessage(Text.of(prefix + ": " + (int)currentValue + "ms"));
    }

    @Override
    protected void applyValue() {
        double currentValue = min + (value * (max - min));
        onValueChange.accept(currentValue);
    }

    // YAHAN FIX HAI: 'protected' ko 'public' kar diya gaya hai
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // Background Track (Sleek Grey)
        context.fill(this.getX(), this.getY() + (this.height / 2) - 2, this.getX() + this.width, this.getY() + (this.height / 2) + 2, 0xFF333333);

        // Filled Part (Cyan/Aqua Accent)
        int thumbX = (int) (this.getX() + (this.value * (this.width - 8)));
        context.fill(this.getX(), this.getY() + (this.height / 2) - 2, thumbX + 4, this.getY() + (this.height / 2) + 2, 0xFF00FBFF);

        // Thumb Handle (White)
        context.fill(thumbX, this.getY(), thumbX + 8, this.getY() + this.height, 0xFFFFFFFF);

        // Text Label
        context.drawCenteredTextWithShadow(
            net.minecraft.client.MinecraftClient.getInstance().textRenderer, 
            this.getMessage(), 
            this.getX() + this.width / 2, 
            this.getY() - 12, 
            0xFFFFFFFF
        );
    }
}
