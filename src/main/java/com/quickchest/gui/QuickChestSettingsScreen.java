package com.quickchest.gui;

import com.quickchest.QuickChestMod;
import com.quickchest.config.QuickChestConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class QuickChestSettingsScreen extends Screen {

    private final Screen parent;

    // Slider values
    private int cycles;     // 1–20
    private int speedTicks; // 1–10 ticks per phase

    public QuickChestSettingsScreen(Screen parent) {
        super(Text.literal("§6QuickChest Settings"));
        this.parent = parent;
        this.cycles = QuickChestConfig.getAutoCycles();
        this.speedTicks = QuickChestConfig.getAutoSpeedTicks();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 2 - 80;

        // ── Quick Chest ON/OFF ──
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(QuickChestConfig.isEnabled()
                ? "§aQuick Chest: ON"
                : "§cQuick Chest: OFF"),
            btn -> {
                QuickChestMod.toggle();
                btn.setMessage(Text.literal(
                    QuickChestConfig.isEnabled()
                        ? "§aQuick Chest: ON"
                        : "§cQuick Chest: OFF"
                ));
            }
        ).dimensions(centerX - 100, startY, 200, 20)
         .tooltip(Tooltip.of(Text.literal("Quick Chest mod enable/disable")))
         .build());

        // ── Auto Mode ON/OFF ──
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(QuickChestConfig.isAutoMode()
                ? "§bAuto Mode: ON"
                : "§7Auto Mode: OFF"),
            btn -> {
                QuickChestMod.toggleAutoMode();
                btn.setMessage(Text.literal(
                    QuickChestConfig.isAutoMode()
                        ? "§bAuto Mode: ON"
                        : "§7Auto Mode: OFF"
                ));
            }
        ).dimensions(centerX - 100, startY + 25, 200, 20)
         .tooltip(Tooltip.of(Text.literal(
             "Auto: beginner ke liye automatic drop+store cycles")))
         .build());

        // ── Cycles Slider ──
        this.addDrawableChild(new SliderWidget(
            centerX - 100, startY + 55, 200, 20,
            Text.literal("§eCycles: §f" + cycles),
            (cycles - 1) / 19.0
        ) {
            @Override
            protected void updateMessage() {
                int val = 1 + (int) Math.round(this.value * 19);
                QuickChestSettingsScreen.this.cycles = val;
                this.setMessage(Text.literal("§eCycles: §f" + val));
            }

            @Override
            protected void applyValue() {
                int val = 1 + (int) Math.round(this.value * 19);
                QuickChestSettingsScreen.this.cycles = val;
                QuickChestConfig.setAutoCycles(val);
                QuickChestConfig.save();
            }
        });

        // ── Speed Slider ──
        this.addDrawableChild(new SliderWidget(
            centerX - 100, startY + 85, 200, 20,
            Text.literal("§dSpeed: §f" + getSpeedLabel(speedTicks)),
            1.0 - (speedTicks - 1) / 9.0  // reverse — right = faster
        ) {
            @Override
            protected void updateMessage() {
                // Reverse: slider right = fast (1 tick), left = slow (10 ticks)
                int val = 10 - (int) Math.round(this.value * 9);
                QuickChestSettingsScreen.this.speedTicks = val;
                this.setMessage(Text.literal(
                    "§dSpeed: §f" + getSpeedLabel(val)));
            }

            @Override
            protected void applyValue() {
                int val = 10 - (int) Math.round(this.value * 9);
                QuickChestSettingsScreen.this.speedTicks = val;
                QuickChestConfig.setAutoSpeedTicks(val);
                QuickChestConfig.save();
            }
        });

        // ── Return Delay Slider (manual mode) ──
        this.addDrawableChild(new SliderWidget(
            centerX - 100, startY + 115, 200, 20,
            Text.literal("§aReturn Delay: §f"
                + QuickChestConfig.getReturnDelayTicks() + " ticks"),
            (QuickChestConfig.getReturnDelayTicks() - 1) / 19.0
        ) {
            @Override
            protected void updateMessage() {
                int val = 1 + (int) Math.round(this.value * 19);
                this.setMessage(Text.literal(
                    "§aReturn Delay: §f" + val + " ticks"));
            }

            @Override
            protected void applyValue() {
                int val = 1 + (int) Math.round(this.value * 19);
                QuickChestConfig.setReturnDelayTicks(val);
                QuickChestConfig.save();
            }
        });

        // ── Reset to Default ──
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§cReset to Default"),
            btn -> {
                QuickChestConfig.resetToDefault();
                QuickChestConfig.save();
                // Reload screen
                assert this.client != null;
                this.client.setScreen(
                    new QuickChestSettingsScreen(this.parent));
            }
        ).dimensions(centerX - 100, startY + 145, 95, 20)
         .tooltip(Tooltip.of(Text.literal("Sab settings default pe reset karo")))
         .build());

        // ── Done ──
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§aDone"),
            btn -> {
                assert this.client != null;
                this.client.setScreen(this.parent);
            }
        ).dimensions(centerX + 5, startY + 145, 95, 20)
         .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Background
        this.renderBackground(context, mouseX, mouseY, delta);

        // Title
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal("§6§lQuickChest Settings"),
            this.width / 2,
            this.height / 2 - 100,
            0xFFFFFF
        );

        int centerX = this.width / 2;
        int startY = this.height / 2 - 80;

        // Labels
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("§7— Manual Mode —"),
            centerX - 100, startY + 108, 0xAAAAAA);

        context.drawTextWithShadow(this.textRenderer,
            Text.literal("§7— Auto Mode —"),
            centerX - 100, startY + 48, 0xAAAAAA);

        // Live preview box
        renderPreviewBox(context, centerX, startY + 170);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderPreviewBox(DrawContext context, int cx, int y) {
        // Box background
        context.fill(cx - 100, y, cx + 100, y + 45, 0x88000000);
        context.drawBorder(cx - 100, y, 200, 45, 0xFF666666);

        // Preview text
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("§e§lLive Preview"),
            cx, y + 4, 0xFFFF55);

        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("§fCycles: §e" + cycles
                + "  §fSpeed: §d" + getSpeedLabel(speedTicks)
                + "  §fReturn: §a" + QuickChestConfig.getReturnDelayTicks() + "t"),
            cx, y + 18, 0xFFFFFF);

        // Total time estimate
        double totalSecs = (cycles * 4 * speedTicks) / 20.0;
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("§7Est. auto duration: §f~"
                + String.format("%.1f", totalSecs) + "s"),
            cx, y + 30, 0xAAAAAA);
    }

    private static String getSpeedLabel(int ticks) {
        return switch (ticks) {
            case 1 -> "§cMAX ⚡";
            case 2 -> "§cVery Fast";
            case 3 -> "§6Fast";
            case 4 -> "§6Medium-Fast";
            case 5 -> "§eMedium";
            case 6 -> "§eMedium-Slow";
            case 7 -> "§aSlow";
            case 8 -> "§aSlow";
            case 9 -> "§2Very Slow";
            default -> "§2Slowest";
        };
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
