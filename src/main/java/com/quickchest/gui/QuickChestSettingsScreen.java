package com.quickchest.gui;

import com.quickchest.QuickChestMod;
import com.quickchest.config.QuickChestConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class QuickChestSettingsScreen extends Screen {

    private final Screen parent;

    private int cycles;
    private int speedTicks;
    private int maxClicks;
    private int actionsPerClick;
    private int spammerSpeed; // Naya variable speed ke liye

    private TextFieldWidget actionsInput;
    private TextFieldWidget speedInput; // Naya text field
    private TextFieldWidget maxClicksInput;

    public QuickChestSettingsScreen(Screen parent) {
        super(Text.literal("§6QuickChest Settings"));
        this.parent = parent;
        this.cycles          = QuickChestConfig.getAutoCycles();
        this.speedTicks      = QuickChestConfig.getAutoSpeedTicks();
        this.maxClicks       = QuickChestConfig.getMaxClicksPerSession();
        this.actionsPerClick = QuickChestConfig.getActionsPerClick();
        this.spammerSpeed    = QuickChestConfig.getSpammerSpeed();
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y  = this.height / 2 - 130;

        // ── 1. MAIN TOGGLE (OLD) ──
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(QuickChestConfig.isEnabled() ? "§aQuick Chest: ON" : "§cQuick Chest: OFF"),
            btn -> {
                QuickChestConfig.setEnabled(!QuickChestConfig.isEnabled());
                QuickChestConfig.save();
                btn.setMessage(Text.literal(QuickChestConfig.isEnabled() ? "§aQuick Chest: ON" : "§cQuick Chest: OFF"));
            }
        ).dimensions(cx - 100, y, 200, 20).build());

        // ── 2. AUTO MODE TOGGLE (OLD) ──
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(QuickChestConfig.isAutoMode() ? "§bAuto Mode: ON" : "§7Auto Mode: OFF"),
            btn -> {
                QuickChestConfig.setAutoMode(!QuickChestConfig.isAutoMode());
                QuickChestConfig.save();
                btn.setMessage(Text.literal(QuickChestConfig.isAutoMode() ? "§bAuto Mode: ON" : "§7Auto Mode: OFF"));
            }
        ).dimensions(cx - 100, y + 25, 200, 20).build());

        // ── 3. AUTO MODE SLIDERS (OLD) ──
        this.addDrawableChild(new SliderWidget(cx - 100, y + 55, 200, 20, Text.literal("§eCycles: " + cycles), (cycles - 1) / 19.0) {
            @Override protected void updateMessage() { this.setMessage(Text.literal("§eCycles: " + (1 + (int)Math.round(this.value * 19)))); }
            @Override protected void applyValue() { QuickChestConfig.setAutoCycles(1 + (int)Math.round(this.value * 19)); QuickChestConfig.save(); }
        });

        // ── 4. MANUAL RETURN DELAY (OLD) ──
        this.addDrawableChild(new SliderWidget(cx - 100, y + 85, 200, 20, Text.literal("§aReturn Delay: " + QuickChestConfig.getReturnDelayTicks() + "t"), (QuickChestConfig.getReturnDelayTicks() - 1) / 19.0) {
            @Override protected void updateMessage() { this.setMessage(Text.literal("§aReturn Delay: " + (1 + (int)Math.round(this.value * 19)) + "t")); }
            @Override protected void applyValue() { QuickChestConfig.setReturnDelayTicks(1 + (int)Math.round(this.value * 19)); QuickChestConfig.save(); }
        });

        // ═══════════════════════════════
        // 5. SPAMMER INPUT SECTION (NEW - MANUALLY TYPE)
        // ═══════════════════════════════

        // CLICKS INPUT (Actions/Click)
        actionsInput = new TextFieldWidget(this.textRenderer, cx - 100, y + 125, 95, 20, Text.literal("Clicks"));
        actionsInput.setMaxLength(4); // Up to 1000
        actionsInput.setText(String.valueOf(actionsPerClick));
        actionsInput.setPlaceholder(Text.literal("Clicks (1-1000)"));
        actionsInput.setChangedListener(text -> {
            try {
                int v = Integer.parseInt(text);
                QuickChestConfig.setActionsPerClick(v);
                QuickChestConfig.save();
            } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(actionsInput);

        // SPEED/DELAY INPUT (Spammer Speed)
        speedInput = new TextFieldWidget(this.textRenderer, cx + 5, y + 125, 95, 20, Text.literal("Speed"));
        speedInput.setMaxLength(3);
        speedInput.setText(String.valueOf(spammerSpeed));
        speedInput.setPlaceholder(Text.literal("Delay (0-100)"));
        speedInput.setChangedListener(text -> {
            try {
                int v = Integer.parseInt(text);
                QuickChestConfig.setSpammerSpeed(v);
                QuickChestConfig.save();
            } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(speedInput);

        // MAX CLICKS SESSION INPUT (OLD)
        maxClicksInput = new TextFieldWidget(this.textRenderer, cx - 100, y + 155, 200, 20, Text.literal("Max Clicks"));
        maxClicksInput.setText(String.valueOf(maxClicks));
        maxClicksInput.setChangedListener(text -> {
            try {
                int v = Integer.parseInt(text);
                QuickChestConfig.setMaxClicksPerSession(v);
                QuickChestConfig.save();
            } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(maxClicksInput);

        // ── 6. PRESET BUTTONS (OLD) ──
        this.addDrawableChild(ButtonWidget.builder(Text.literal("§aBeginner"), btn -> applyPreset(1, 0)).dimensions(cx - 100, y + 185, 60, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("§eNormal"),   btn -> applyPreset(10, 2)).dimensions(cx - 30, y + 185, 60, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("§cLegend"),   btn -> applyPreset(500, 0)).dimensions(cx + 40, y + 185, 60, 20).build());

        // ── 7. CLOSE BUTTON (OLD) ──
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), btn -> this.client.setScreen(parent)).dimensions(cx - 100, y + 215, 200, 20).build());
    }

    private void applyPreset(int clicks, int speed) {
        actionsInput.setText(String.valueOf(clicks));
        speedInput.setText(String.valueOf(speed));
        QuickChestConfig.setActionsPerClick(clicks);
        QuickChestConfig.setSpammerSpeed(speed);
        QuickChestConfig.save();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "§7Manual Spammer (Clicks | Speed)", this.width / 2 - 100, this.height / 2 - 20, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }
}
