package com.yourname.speedchestmod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class SpeedChestScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget countField;
    private TextFieldWidget speedField;
    private ButtonWidget toggleButton;

    protected SpeedChestScreen(Screen parent) {
        super(Text.literal("Speed Chest Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        ModConfig config = ModConfig.getInstance();

        // Toggle Button
        toggleButton = ButtonWidget.builder(
            Text.literal(config.enabled ? "Status: ON" : "Status: OFF"),
            button -> {
                config.enabled = !config.enabled;
                button.setMessage(Text.literal(config.enabled ? "Status: ON" : "Status: OFF"));
                config.save();
            }
        ).dimensions(width / 2 - 100, height / 4, 200, 20).build();
        addDrawableChild(toggleButton);

        // Repeat Count Input
        countField = new TextFieldWidget(textRenderer, width / 2 - 100, height / 2 - 20, 200, 20, Text.literal("Count"));
        countField.setText(String.valueOf(config.repeatCount));
        countField.setChangedListener(s -> {
            try { config.repeatCount = Integer.parseInt(s); config.save(); } catch(Exception e){}
        });
        addDrawableChild(countField);

        // Speed Input
        speedField = new TextFieldWidget(textRenderer, width / 2 - 100, height / 2 + 20, 200, 20, Text.literal("Speed"));
        speedField.setText(String.valueOf(config.speedTicks));
        speedField.setChangedListener(s -> {
            try { config.speedTicks = Double.parseDouble(s); config.save(); } catch(Exception e){}
        });
        addDrawableChild(speedField);
        
        // Back Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> close())
            .dimensions(width / 2 - 100, height / 2 + 60, 200, 20).build());
            
        // Set focus initially to none so keyboard doesn't type immediately
        setInitialFocus(null);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        
        // Draw Labels manually here instead of adding them as children
        context.drawText(textRenderer, "Repeat Count:", width / 2 - 95, height / 2 - 30, 0xFFFFFF, true);
        context.drawText(textRenderer, "Speed (0.1 = Max):", width / 2 - 95, height / 2 + 10, 0xFFFFFF, true);
        
        // Draw Title
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 4 - 10, 0xFFFFFF);
    }

    @Override
    public void close() {
        assert client != null;
        client.setScreen(parent);
    }
}
