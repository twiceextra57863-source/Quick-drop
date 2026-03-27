package com.tclient.mod.gui;

import com.tclient.mod.TClientMod;
import com.tclient.mod.features.FontChanger;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class FontChangerPanel extends TClientPanel {

    private static final int CARD_PADDING = 12;
    private static final int FONT_CARD_H  = 68;
    private static final int COLS         = 2;
    private static final int COL_GAP      = 10;
    private static final int ROW_GAP      = 8;

    private int scrollOffset = 0;
    private int maxScroll    = 0;

    private FontChanger.FontCategory filterCategory = null;

    private static final FontChanger.FontCategory[] FILTER_CATS = {
            null,
            FontChanger.FontCategory.STANDARD,
            FontChanger.FontCategory.CLEAN,
            FontChanger.FontCategory.BOLD,
            FontChanger.FontCategory.DECORATIVE,
            FontChanger.FontCategory.STYLIZED
    };
    private static final String[] FILTER_LABELS = {
            "ALL", "Standard", "Clean", "Bold", "Decorative", "Stylized"
    };

    public FontChangerPanel(Screen parent) {
        super(parent);
    }

    // ─── Render ───────────────────────────────────────────────────────────────

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta,
                       int x1, int y1, int x2, int y2) {

        // Background
        ctx.fill(x1, y1, x2, y2, BG_PANEL);

        int cx = x1 + CARD_PADDING;
        int cy = y1 + CARD_PADDING;

        // Section Title
        drawSectionTitle(ctx, "FONT CHANGER", cx, cy);
        cy += 20;

        // Master Toggle
        boolean enabled = TClientMod.CONFIG.fontChangerEnabled;
        drawToggleButton(ctx, cx, cy, enabled, enabled ? "Enabled" : "Disabled");
        cy += 22;

        // Active font info
        FontChanger.FontEntry current = FontChanger.getCurrentFont();
        ctx.drawText(client.textRenderer,
                "Active: §b" + current.displayName, cx, cy, TEXT_SECONDARY, false);
        cy += 16;

        // Separator line
        ctx.fill(cx, cy, x2 - CARD_PADDING, cy + 1, ACCENT_DIM);
        cy += 8;

        // Filter bar
        cy = drawFilterBar(ctx, mouseX, mouseY, cx, cy);
        cy += 10;

        // Font grid (scrollable)
        int gridTop    = cy;
        int gridBottom = y2 - CARD_PADDING;
        int gridH      = gridBottom - gridTop;

        if (gridH > 10) {
            ctx.enableScissor(x1, gridTop, x2, gridBottom);
            drawFontGrid(ctx,
                    mouseX,
                    mouseY + scrollOffset,
                    cx,
                    gridTop - scrollOffset,
                    x2 - CARD_PADDING,
                    gridH);
            ctx.disableScissor();

            drawScrollBar(ctx, x2 - 6, gridTop, gridBottom, gridH);
        }
    }

    // ─── Filter Bar ───────────────────────────────────────────────────────────

    private int drawFilterBar(DrawContext ctx, int mouseX, int mouseY,
                               int cx, int cy) {
        int btnH    = 14;
        int btnGap  = 5;
        int currentX = cx;

        for (int i = 0; i < FILTER_LABELS.length; i++) {
            String label = FILTER_LABELS[i];
            int bw = client.textRenderer.getWidth(label) + 10;

            boolean isActive  = (filterCategory == FILTER_CATS[i]);
            boolean isHovered = inBounds(mouseX, mouseY, currentX, cy, bw, btnH);

            int bg     = isActive  ? ACCENT_MED    : (isHovered ? 0xFF1A1A30 : 0xFF101020);
            int border = isActive  ? ACCENT         : (isHovered ? ACCENT_DIM : TEXT_MUTED);
            int text   = isActive  ? ACCENT         : (isHovered ? TEXT_PRIMARY : TEXT_SECONDARY);

            // Fill
            ctx.fill(currentX, cy, currentX + bw, cy + btnH, bg);
            // Borders
            ctx.fill(currentX,          cy,          currentX + bw, cy + 1,          border);
            ctx.fill(currentX,          cy + btnH - 1, currentX + bw, cy + btnH,     border);
            ctx.fill(currentX,          cy,          currentX + 1,  cy + btnH,       border);
            ctx.fill(currentX + bw - 1, cy,          currentX + bw, cy + btnH,       border);
            // Label
            ctx.drawText(client.textRenderer, label, currentX + 5, cy + 3, text, false);

            currentX += bw + btnGap;
        }
        return cy + btnH;
    }

    // ─── Font Grid ────────────────────────────────────────────────────────────

    private void drawFontGrid(DrawContext ctx, int mouseX, int mouseY,
                               int startX, int startY, int endX, int gridH) {

        List<Map.Entry<String, FontChanger.FontEntry>> filtered = getFilteredFonts();

        int colW      = (endX - startX - COL_GAP) / COLS;
        int totalRows = (int) Math.ceil((double) filtered.size() / COLS);
        maxScroll = Math.max(0, totalRows * (FONT_CARD_H + ROW_GAP) - gridH);

        for (int i = 0; i < filtered.size(); i++) {
            int col   = i % COLS;
            int row   = i / COLS;
            int cardX = startX + col * (colW + COL_GAP);
            int cardY = startY + row * (FONT_CARD_H + ROW_GAP);

            String fontId              = filtered.get(i).getKey();
            FontChanger.FontEntry entry = filtered.get(i).getValue();
            boolean selected = fontId.equals(FontChanger.currentFontId);
            boolean hovered  = inBounds(mouseX, mouseY, cardX, cardY, colW, FONT_CARD_H);

            drawFontCard(ctx, cardX, cardY, colW, FONT_CARD_H, fontId, entry, selected, hovered);
        }
    }

    // ─── Font Card ────────────────────────────────────────────────────────────

    private void drawFontCard(DrawContext ctx, int x, int y, int w, int h,
                               String fontId, FontChanger.FontEntry entry,
                               boolean selected, boolean hovered) {

        // Card background
        int bgColor = selected ? 0xFF0D1D28 : (hovered ? 0xFF101525 : CARD_BG);
        ctx.fill(x, y, x + w, y + h, bgColor);

        // Border
        int borderColor = selected ? ACCENT : (hovered ? ACCENT_DIM : CARD_BORDER);
        ctx.fill(x,         y,         x + w,     y + 1,     borderColor);
        ctx.fill(x,         y + h - 1, x + w,     y + h,     borderColor);
        ctx.fill(x,         y,         x + 1,     y + h,     borderColor);
        ctx.fill(x + w - 1, y,         x + w,     y + h,     borderColor);

        // Left accent stripe when selected
        if (selected) {
            ctx.fill(x, y, x + 3, y + h, ACCENT);
        }

        int innerX = x + (selected ? 8 : 6);
        int innerY = y + 6;

        // Font display name
        ctx.drawText(client.textRenderer, entry.displayName,
                innerX, innerY, selected ? ACCENT : TEXT_PRIMARY, false);

        // Category badge
        ctx.drawText(client.textRenderer,
                "[" + entry.category.name() + "]",
                innerX, innerY + 10,
                getCategoryColor(entry.category), false);

        // Description
        ctx.drawText(client.textRenderer, entry.description,
                innerX, innerY + 22, TEXT_SECONDARY, false);

        // Preview text
        ctx.drawText(client.textRenderer, buildPreview(fontId),
                innerX, innerY + 36, TEXT_PRIMARY, false);

        // Checkmark if selected
        if (selected) {
            ctx.drawText(client.textRenderer, "\u2713",
                    x + w - 14, y + (h - 8) / 2, SUCCESS, false);
        }
    }

    // ─── Scroll Bar ───────────────────────────────────────────────────────────

    private void drawScrollBar(DrawContext ctx, int x, int top, int bottom, int viewH) {
        if (maxScroll <= 0) return;
        int trackH = bottom - top;
        int thumbH = Math.max(20, (int) ((float) viewH / (viewH + maxScroll) * trackH));
        int thumbY = top + (int) ((float) scrollOffset / maxScroll * (trackH - thumbH));

        ctx.fill(x, top,    x + 4, bottom,        0xFF0A0A18);
        ctx.fill(x, thumbY, x + 4, thumbY + thumbH, ACCENT_DIM);
        ctx.fill(x, thumbY, x + 4, thumbY + 1,     ACCENT);
        ctx.fill(x, thumbY + thumbH - 1, x + 4, thumbY + thumbH, ACCENT);
    }

    // ─── Mouse Click ──────────────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button,
                                int x1, int y1, int x2, int y2) {

        int cx = x1 + CARD_PADDING;
        int cy = y1 + CARD_PADDING;
        cy += 20; // section title

        // Toggle button click
        if (inBounds(mouseX, mouseY, cx, cy, 36, 14)) {
            TClientMod.CONFIG.fontChangerEnabled = !TClientMod.CONFIG.fontChangerEnabled;
            TClientMod.CONFIG.save();
            return true;
        }
        cy += 22; // toggle height
        cy += 16; // active font text
        cy += 8;  // separator

        // Filter bar clicks
        int filterX = cx;
        for (int i = 0; i < FILTER_LABELS.length; i++) {
            int bw = client.textRenderer.getWidth(FILTER_LABELS[i]) + 10;
            if (inBounds(mouseX, mouseY, filterX, cy, bw, 14)) {
                filterCategory = FILTER_CATS[i];
                scrollOffset   = 0;
                return true;
            }
            filterX += bw + 5;
        }
        cy += 14; // filter bar height
        cy += 10; // gap after filter bar

        // Font card clicks
        clickFontCard(mouseX, mouseY + scrollOffset, cx, cy, x2 - CARD_PADDING);
        return true;
    }

    private void clickFontCard(double mouseX, double mouseY,
                                int startX, int gridTop, int endX) {

        List<Map.Entry<String, FontChanger.FontEntry>> filtered = getFilteredFonts();
        int colW = (endX - startX - COL_GAP) / COLS;

        for (int i = 0; i < filtered.size(); i++) {
            int cardX = startX + (i % COLS) * (colW + COL_GAP);
            int cardY = gridTop + (i / COLS) * (FONT_CARD_H + ROW_GAP);

            if (inBounds(mouseX, mouseY, cardX, cardY, colW, FONT_CARD_H)) {
                FontChanger.setFont(filtered.get(i).getKey());
                return;
            }
        }
    }

    // ─── Mouse Scroll ─────────────────────────────────────────────────────────

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scrollOffset = (int) MathHelper.clamp(scrollOffset - amount * 10, 0, maxScroll);
        return true;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private List<Map.Entry<String, FontChanger.FontEntry>> getFilteredFonts() {
        List<Map.Entry<String, FontChanger.FontEntry>> result = new ArrayList<>();
        for (Map.Entry<String, FontChanger.FontEntry> e : FontChanger.FONTS.entrySet()) {
            if (filterCategory == null || e.getValue().category == filterCategory) {
                result.add(e);
            }
        }
        return result;
    }

    private String buildPreview(String fontId) {
        switch (fontId) {
            case "BOLD_CLEAN":  return "§lABCDEFG 123";
            case "NEON":        return "§a§lABCDEFG §r§2123";
            case "MATRIX":      return "§2ABCDEFG §a123";
            case "RETRO":       return "§c§lABC §4DEF §cG";
            case "SHADOW":      return "§8ABCDEFG §7123";
            case "MONO":        return "§3ABCDEFG 123";
            case "THIN":        return "§fABCDEFG 123";
            case "SMALL_CAPS":  return "§eABC §6DEF §eG";
            case "ENCHANTING":  return "§5ABCDEFG 123";
            case "ILLAGER":     return "§6ABCDEFG 123";
            case "UNICODE":     return "§bABCDEFG 123";
            default:            return "§rABCDEFG 123";
        }
    }

    private int getCategoryColor(FontChanger.FontCategory cat) {
        switch (cat) {
            case STANDARD:   return 0xFF7A7AEA;
            case CLEAN:      return 0xFF00E5FF;
            case BOLD:       return 0xFFFFAA00;
            case DECORATIVE: return 0xFFFF55AA;
            case STYLIZED:   return 0xFF55FF88;
            default:         return TEXT_SECONDARY;
        }
    }
}
