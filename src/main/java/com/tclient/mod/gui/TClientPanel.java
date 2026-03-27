package com.tclient.mod.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public abstract class TClientPanel {

    protected MinecraftClient client;
    protected Screen parentScreen;
    protected int panelX, panelY, panelW, panelH, fullWidth, fullHeight;

    // Color palette
    protected static final int BG_PANEL       = 0xFF0D0D1A;
    protected static final int ACCENT         = 0xFF00E5FF;
    protected static final int ACCENT_DIM     = 0x3300E5FF;
    protected static final int ACCENT_MED     = 0x6600E5FF;
    protected static final int TEXT_PRIMARY   = 0xFFECECEC;
    protected static final int TEXT_SECONDARY = 0xFF7A7A9A;
    protected static final int TEXT_ACCENT    = 0xFF00E5FF;
    protected static final int TEXT_MUTED     = 0xFF3A3A5A;
    protected static final int CARD_BG        = 0xFF111120;
    protected static final int CARD_BORDER    = 0xFF1E1E35;
    protected static final int SUCCESS        = 0xFF00FF88;
    protected static final int WARNING        = 0xFFFFAA00;
    protected static final int DANGER         = 0xFFFF4455;

    public TClientPanel(Screen parent) {
        this.parentScreen = parent;
        this.client = MinecraftClient.getInstance();
    }

    public void init(MinecraftClient client, int screenWidth, int screenHeight,
                     int sidebarWidth, int headerHeight, int footerHeight) {
        this.client    = client;
        this.fullWidth = screenWidth;
        this.fullHeight = screenHeight;
        this.panelX    = sidebarWidth + 1;
        this.panelY    = headerHeight;
        this.panelW    = screenWidth - sidebarWidth - 1;
        this.panelH    = screenHeight - headerHeight - footerHeight;
    }

    public abstract void render(DrawContext ctx, int mouseX, int mouseY, float delta,
                                int x1, int y1, int x2, int y2);

    public boolean mouseClicked(double mouseX, double mouseY, int button,
                                int x1, int y1, int x2, int y2) { return false; }
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) { return false; }
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)       { return false; }
    public boolean charTyped(char chr, int modifiers)                         { return false; }

    protected void drawSectionTitle(DrawContext ctx, String title, int x, int y) {
        ctx.drawText(client.textRenderer, title, x, y, TEXT_ACCENT, false);
        int tw = client.textRenderer.getWidth(title);
        ctx.fill(x, y + 10, x + tw, y + 11, ACCENT_DIM);
    }

    protected void drawCard(DrawContext ctx, int x, int y, int w, int h) {
        ctx.fill(x, y, x + w, y + h, CARD_BG);
        ctx.fill(x,         y,         x + w,     y + 1,     CARD_BORDER);
        ctx.fill(x,         y + h - 1, x + w,     y + h,     CARD_BORDER);
        ctx.fill(x,         y,         x + 1,     y + h,     CARD_BORDER);
        ctx.fill(x + w - 1, y,         x + w,     y + h,     CARD_BORDER);
    }

    protected void drawToggleButton(DrawContext ctx, int x, int y, boolean on, String label) {
        int btnW = 36, btnH = 14;
        int bg   = on ? ACCENT_MED : 0xFF1A1A2E;
        int knob = on ? ACCENT     : TEXT_SECONDARY;

        ctx.fill(x, y, x + btnW, y + btnH, bg);
        ctx.fill(x, y, x + btnW, y + 1, knob);
        ctx.fill(x, y + btnH - 1, x + btnW, y + btnH, knob);
        ctx.fill(x, y, x + 1, y + btnH, knob);
        ctx.fill(x + btnW - 1, y, x + btnW, y + btnH, knob);

        int knobX = on ? x + btnW - 12 : x + 2;
        ctx.fill(knobX, y + 2, knobX + 10, y + btnH - 2, knob);
        ctx.drawText(client.textRenderer, label, x + btnW + 6, y + (btnH - 8) / 2, TEXT_PRIMARY, false);
    }

    protected boolean inBounds(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }
}
