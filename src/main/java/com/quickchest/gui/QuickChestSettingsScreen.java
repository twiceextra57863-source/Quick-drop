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

    // Text field for manual input
    private TextFieldWidget actionsInput;
    private TextFieldWidget maxClicksInput;

    public QuickChestSettingsScreen(Screen parent) {
        super(Text.literal("§6QuickChest Settings"));
        this.parent = parent;
        this.cycles       = QuickChestConfig.getAutoCycles();
        this.speedTicks   = QuickChestConfig.getAutoSpeedTicks();
        this.maxClicks    = QuickChestConfig.getMaxClicksPerSession();
        this.actionsPerClick = QuickChestConfig.getActionsPerClick();
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y  = this.height / 2 - 130;

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
                        : "§cQuick Chest: OFF"));
            }
        ).dimensions(cx - 100, y, 200, 20)
         .tooltip(Tooltip.of(Text.literal("Quick Chest ON/OFF")))
         .build());

        // ── Auto Mode ──
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(QuickChestConfig.isAutoMode()
                ? "§bAuto Mode: ON §7(Beginner)"
                : "§7Auto Mode: OFF §8(Manual)"),
            btn -> {
                QuickChestMod.toggleAutoMode();
                btn.setMessage(Text.literal(
                    QuickChestConfig.isAutoMode()
                        ? "§bAuto Mode: ON §7(Beginner)"
                        : "§7Auto Mode: OFF §8(Manual)"));
            }
        ).dimensions(cx - 100, y + 25, 200, 20)
         .tooltip(Tooltip.of(Text.literal("Beginner ke liye auto mode")))
         .build());

        // ═══════════════════════════════
        // AUTO MODE SECTION
        // ═══════════════════════════════

        // Cycles Slider
        this.addDrawableChild(new SliderWidget(
            cx - 100, y + 60, 200, 20,
            Text.literal("§eCycles: §f" + cycles),
            (cycles - 1) / 19.0
        ) {
            @Override protected void updateMessage() {
                int v = 1 + (int) Math.round(this.value * 19);
                QuickChestSettingsScreen.this.cycles = v;
                this.setMessage(Text.literal("§eCycles: §f" + v));
            }
            @Override protected void applyValue() {
                int v = 1 + (int) Math.round(this.value * 19);
                QuickChestConfig.setAutoCycles(v);
                QuickChestConfig.save();
            }
        });

        // Speed Slider
        this.addDrawableChild(new SliderWidget(
            cx - 100, y + 85, 200, 20,
            Text.literal("§dSpeed: §f" + getSpeedLabel(speedTicks)
                + " §8(" + speedTicks + "t)"),
            1.0 - (speedTicks - 1) / 9.0
        ) {
            @Override protected void updateMessage() {
                int v = 10 - (int) Math.round(this.value * 9);
                QuickChestSettingsScreen.this.speedTicks = v;
                this.setMessage(Text.literal(
                    "§dSpeed: §f" + getSpeedLabel(v) + " §8(" + v + "t)"));
            }
            @Override protected void applyValue() {
                int v = 10 - (int) Math.round(this.value * 9);
                QuickChestConfig.setAutoSpeedTicks(v);
                QuickChestConfig.save();
            }
        });

        // ═══════════════════════════════
        // MANUAL MODE SECTION
        // ═══════════════════════════════

        // Return Delay Slider
        this.addDrawableChild(new SliderWidget(
            cx - 100, y + 125, 200, 20,
            Text.literal("§aReturn Delay: §f"
                + QuickChestConfig.getReturnDelayTicks() + " ticks"),
            (QuickChestConfig.getReturnDelayTicks() - 1) / 19.0
        ) {
            @Override protected void updateMessage() {
                int v = 1 + (int) Math.round(this.value * 19);
                this.setMessage(Text.literal(
                    "§aReturn Delay: §f" + v + " ticks"));
            }
            @Override protected void applyValue() {
                int v = 1 + (int) Math.round(this.value * 19);
                QuickChestConfig.setReturnDelayTicks(v);
                QuickChestConfig.save();
            }
        });

        // ═══════════════════════════════
        // CLICK PRACTICE SECTION
        // ═══════════════════════════════

        // Max Clicks Per Session — TextField (apne man se type karo)
        maxClicksInput = new TextFieldWidget(
            this.textRenderer,
            cx - 100, y + 165, 95, 20,
            Text.literal("Max Clicks")
        );
        maxClicksInput.setMaxLength(3);
        maxClicksInput.setText(String.valueOf(maxClicks));
        maxClicksInput.setPlaceholder(Text.literal("§81-999"));
        maxClicksInput.setChangedListener(text -> {
            try {
                int v = Integer.parseInt(text);
                if (v >= 1 && v <= 999) {
                    QuickChestSettingsScreen.this.maxClicks = v;
                    QuickChestConfig.setMaxClicksPerSession(v);
                    QuickChestConfig.save();
                }
            } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(maxClicksInput);

        // Actions Per Click — TextField (kitni baar action ho ek click me)
        actionsInput = new TextFieldWidget(
            this.textRenderer,
            cx + 5, y + 165, 95, 20,
            Text.literal("Actions/Click")
        );
        actionsInput.setMaxLength(3);
        actionsInput.setText(String.valueOf(actionsPerClick));
        actionsInput.setPlaceholder(Text.literal("§81-50"));
        actionsInput.setChangedListener(text -> {
            try {
                int v = Integer.parseInt(text);
                if (v >= 1 && v <= 50) {
                    QuickChestSettingsScreen.this.actionsPerClick = v;
                    QuickChestConfig.setActionsPerClick(v);
                    QuickChestConfig.save();
                }
            } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(actionsInput);

        // Quick preset buttons — Beginner/Normal/Expert
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§a🐣 Beginner"),
            btn -> applyPreset(1, 1, 3, 5)
        ).dimensions(cx - 100, y + 190, 60, 18)
         .tooltip(Tooltip.of(Text.literal(
             "1 click = 1 action\nMax 3 clicks\nSpeed: Slow")))
         .build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§e👍 Normal"),
            btn -> applyPreset(5, 3, 5, 10)
        ).dimensions(cx - 38, y + 190, 60, 18)
         .tooltip(Tooltip.of(Text.literal(
             "1 click = 5 actions\nMax 5 clicks\nSpeed: Medium")))
         .build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§c🔥 Expert"),
            btn -> applyPreset(20, 10, 2, 20)
        ).dimensions(cx + 24, y + 190, 60, 18)
         .tooltip(Tooltip.of(Text.literal(
             "1 click = 20 actions\nMax 10 clicks\nSpeed: Fast")))
         .build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§5👑 Legend"),
            btn -> applyPreset(50, 20, 1, 30)
        ).dimensions(cx + 86, y + 190, 60, 18)
         .tooltip(Tooltip.of(Text.literal(
             "1 click = 50 actions\nMax 20 clicks\nSpeed: MAX")))
         .build());

        // ── Reset + Done ──
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§cReset"),
            btn -> {
                QuickChestConfig.resetToDefault();
                QuickChestConfig.save();
                assert this.client != null;
                this.client.setScreen(new QuickChestSettingsScreen(parent));
            }
        ).dimensions(cx - 100, y + 215, 95, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§aDone"),
            btn -> {
                assert this.client != null;
                this.client.setScreen(parent);
            }
        ).dimensions(cx + 5, y + 215, 95, 20).build());
    }

    // Preset apply karo
    private void applyPreset(int actPerClick, int maxCl,
                              int speedT, int cyc) {
        this.actionsPerClick = actPerClick;
        this.maxClicks = maxCl;
        this.speedTicks = speedT;
        this.cycles = cyc;

        QuickChestConfig.setActionsPerClick(actPerClick);
        QuickChestConfig.setMaxClicksPerSession(maxCl);
        QuickChestConfig.setAutoSpeedTicks(speedT);
        QuickChestConfig.setAutoCycles(cyc);
        QuickChestConfig.save();

        // Refresh screen
        assert this.client != null;
        this.client.setScreen(new QuickChestSettingsScreen(parent));
    }

    @Override
    public void render(DrawContext context, int mouseX,
                       int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int cx = this.width / 2;
        int y  = this.height / 2 - 130;

        // Title
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("§6§lQuickChest Settings"),
            cx, y - 14, 0xFFFFFF);

        // Section headers
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("§e§l── Auto Mode ──"),
            cx - 100, y + 52, 0xFFAA00);

        context.drawTextWithShadow(this.textRenderer,
            Text.literal("§a§l── Manual Mode ──"),
            cx - 100, y + 117, 0x55FF55);

        context.drawTextWithShadow(this.textRenderer,
            Text.literal("§6§l── Click Practice ──"),
            cx - 100, y + 157, 0xFFAA00);

        // Labels for text fields
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("§7Max Clicks:"),
            cx - 100, y + 155, 0xAAAAAA);

        context.drawTextWithShadow(this.textRenderer,
            Text.literal("§7Actions/Click:"),
            cx + 5, y + 155, 0xAAAAAA);

        // Tick connection info — education
        renderTickInfo(context, cx, y);

        // Live preview
        renderPreviewBox(context, cx, y + 242);

        super.render(context, mouseX, mouseY, delta);
    }

    // Tick se connection explain karo
    private void renderTickInfo(DrawContext context, int cx, int y) {
        int bx = cx - 100;
        int by = y + 108;

        // Tick math box
        context.fill(bx, by, bx + 200, by + 8, 0x00000000);

        // Speed ticks to ms conversion
        double msPerAction = (speedTicks / 20.0) * 1000;
        double totalActions = (long) actionsPerClick * maxClicks;
        double totalTime = (totalActions * speedTicks) / 20.0;

        context.drawTextWithShadow(this.textRenderer,
            Text.literal("§8" + speedTicks + "t = §7"
                + String.format("%.0f", msPerAction) + "ms/action"),
            cx + 5, y + 108, 0x888888);
    }

    private void renderPreviewBox(DrawContext context, int cx, int y) {
        context.fill(cx - 100, y, cx + 100, y + 68, 0x99000000);
        context.drawBorder(cx - 100, y, 200, 68, 0xFFFFAA00);

        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("§6§l⚡ Live Preview"), cx, y + 4, 0xFFAA00);

        // Row 1: Cycles + Speed
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("§7Cycles: §e" + cycles
                + "  §7Speed: §d" + getSpeedLabel(speedTicks)
                + " §8(" + speedTicks + "t)"),
            cx, y + 16, 0xFFFFFF);

        // Row 2: Click settings
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("§7Max Clicks: §6" + maxClicks
                + "  §7Per Click: §a" + actionsPerClick + " actions"),
            cx, y + 28, 0xFFFFFF);

        // Row 3: Tick math — education
        double msPerTick = (speedTicks / 20.0) * 1000;
        double totalPerSession = (double) maxClicks * actionsPerClick;
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("§81 tick = 50ms  |  "
                + speedTicks + "t = §7"
                + String.format("%.0f", msPerTick) + "ms"),
            cx, y + 40, 0xAAAAAA);

        // Row 4: Total estimate
        double estSecs = (totalPerSession * speedTicks) / 20.0;
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("§7Total: §e"
                + (int) totalPerSession + " actions"
                + "  §7Est: §f~"
                + String.format("%.1f", estSecs) + "s"),
            cx, y + 52, 0xFFFFFF);
    }

    public static String getSpeedLabel(int ticks) {
        return switch (ticks) {
            case 1  -> "MAX ⚡";
            case 2  -> "Very Fast";
            case 3  -> "Fast";
            case 4  -> "Med-Fast";
            case 5  -> "Medium";
            case 6  -> "Med-Slow";
            case 7  -> "Slow";
            case 8  -> "Slow";
            case 9  -> "Very Slow";
            default -> "Slowest";
        };
    }

    @Override
    public boolean shouldPause() { return false; }
    }
