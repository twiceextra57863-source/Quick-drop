package com.tclient.mod.gui;

import com.tclient.mod.features.FontChanger;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TClientScreen extends Screen {

    private static final int SIDEBAR_WIDTH  = 130;
    private static final int HEADER_HEIGHT  = 36;
    private static final int FOOTER_HEIGHT  = 22;
    private static final int SIDEBAR_ITEM_H = 26;
    private static final int PADDING        = 8;

    private static final int BG_DARK        = 0xFF0A0A0F;
    private static final int BG_PANEL       = 0xFF0F0F1A;
    private static final int BG_SIDEBAR     = 0xFF080812;
    private static final int ACCENT         = 0xFF00E5FF;
    private static final int ACCENT_DIM     = 0x4400E5FF;
    private static final int TEXT_PRIMARY   = 0xFFECECEC;
    private static final int TEXT_SECONDARY = 0xFF7A7A9A;
    private static final int TEXT_ACCENT    = 0xFF00E5FF;
    private static final int TEXT_MUTED     = 0xFF3A3A5A;
    private static final int SIDEBAR_HOVER  = 0x1500E5FF;
    private static final int SIDEBAR_ACTIVE = 0x2A00E5FF;

    private int selectedCategory = 0;
    private final List<CategoryEntry> categories = new ArrayList<>();
    private final List<TClientPanel>  panels     = new ArrayList<>();

    public TClientScreen() {
        super(Text.literal("T Client"));
    }

    @Override
    protected void init() {
        super.init();
        categories.clear();
        panels.clear();

        categories.add(new CategoryEntry("VISUAL",   "■", "Visual"));
        categories.add(new CategoryEntry("COMBAT",   "⚔", "Combat"));
        categories.add(new CategoryEntry("MOVEMENT", "➤", "Movement"));
        categories.add(new CategoryEntry("PLAYER",   "★", "Player"));
        categories.add(new CategoryEntry("WORLD",    "◎", "World"));
        categories.add(new CategoryEntry("HUD",      "□", "HUD"));
        categories.add(new CategoryEntry("MISC",     "⚙", "Misc"));

        panels.add(new FontChangerPanel(this));
        panels.add(new ComingSoonPanel("Combat features coming soon..."));
        panels.add(new ComingSoonPanel("Movement features coming soon..."));
        panels.add(new ComingSoonPanel("Player features coming soon..."));
        panels.add(new ComingSoonPanel("World features coming soon..."));
        panels.add(new ComingSoonPanel("HUD features coming soon..."));
        panels.add(new ComingSoonPanel("Misc features coming soon..."));

        for (TClientPanel p : panels)
            p.init(client, width, height, SIDEBAR_WIDTH, HEADER_HEIGHT, FOOTER_HEIGHT);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        ctx.fill(0, 0, width, height, BG_DARK);
        ctx.fill(0, 0, width, 1, ACCENT_DIM);

        drawHeader(ctx, width);
        drawSidebar(ctx, mouseX, mouseY, height);

        ctx.fill(SIDEBAR_WIDTH, HEADER_HEIGHT, SIDEBAR_WIDTH + 1, height - FOOTER_HEIGHT, ACCENT_DIM);

        if (selectedCategory >= 0 && selectedCategory < panels.size())
            panels.get(selectedCategory).render(ctx, mouseX, mouseY, delta,
                    SIDEBAR_WIDTH + 1, HEADER_HEIGHT, width, height - FOOTER_HEIGHT);

        drawFooter(ctx, width, height);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawHeader(DrawContext ctx, int sw) {
        ctx.fill(0, 0, sw, HEADER_HEIGHT, BG_PANEL);
        ctx.fill(0, HEADER_HEIGHT - 1, sw, HEADER_HEIGHT, ACCENT_DIM);
        ctx.fill(0, 0, 3, HEADER_HEIGHT, ACCENT);

        String logo = "T CLIENT";
        int ly = (HEADER_HEIGHT - 8) / 2;
        ctx.drawText(textRenderer, logo, 15, ly, ACCENT, false);

        if (selectedCategory >= 0 && selectedCategory < categories.size()) {
            String cat = "/ " + categories.get(selectedCategory).label;
            ctx.drawText(textRenderer, cat, (sw - textRenderer.getWidth(cat)) / 2, ly, TEXT_ACCENT, false);
        }
        String ver = "v1.0";
        ctx.drawText(textRenderer, ver, sw - textRenderer.getWidth(ver) - 10, ly, TEXT_SECONDARY, false);
    }

    private void drawSidebar(DrawContext ctx, int mouseX, int mouseY, int sh) {
        ctx.fill(0, HEADER_HEIGHT, SIDEBAR_WIDTH, sh - FOOTER_HEIGHT, BG_SIDEBAR);
        ctx.drawText(textRenderer, "MODULES", PADDING, HEADER_HEIGHT + 8, TEXT_MUTED, false);

        int startY = HEADER_HEIGHT + 20;
        for (int i = 0; i < categories.size(); i++) {
            CategoryEntry cat = categories.get(i);
            int itemY = startY + i * SIDEBAR_ITEM_H;
            boolean hovered = mouseX >= 0 && mouseX < SIDEBAR_WIDTH
                    && mouseY >= itemY && mouseY < itemY + SIDEBAR_ITEM_H;
            boolean active  = i == selectedCategory;

            if (active) {
                ctx.fill(0, itemY, SIDEBAR_WIDTH - 1, itemY + SIDEBAR_ITEM_H, SIDEBAR_ACTIVE);
                ctx.fill(0, itemY, 3, itemY + SIDEBAR_ITEM_H, ACCENT);
            } else if (hovered) {
                ctx.fill(0, itemY, SIDEBAR_WIDTH - 1, itemY + SIDEBAR_ITEM_H, SIDEBAR_HOVER);
            }

            int color = active ? ACCENT : (hovered ? TEXT_PRIMARY : TEXT_SECONDARY);
            ctx.drawText(textRenderer, cat.icon, PADDING + 2, itemY + (SIDEBAR_ITEM_H - 8) / 2, color, false);
            ctx.drawText(textRenderer, cat.label, PADDING + 14, itemY + (SIDEBAR_ITEM_H - 8) / 2, color, false);
            ctx.fill(PADDING, itemY + SIDEBAR_ITEM_H - 1, SIDEBAR_WIDTH - PADDING,
                    itemY + SIDEBAR_ITEM_H, TEXT_MUTED);
        }
    }

    private void drawFooter(DrawContext ctx, int sw, int sh) {
        int fy = sh - FOOTER_HEIGHT;
        ctx.fill(0, fy, sw, sh, BG_PANEL);
        ctx.fill(0, fy, sw, fy + 1, ACCENT_DIM);
        ctx.drawText(textRenderer, "§7[ESC] Close  [RSHIFT] Open Menu",
                PADDING, fy + (FOOTER_HEIGHT - 8) / 2, TEXT_SECONDARY, false);
        String credit = "T Client § Fabric";
        ctx.drawText(textRenderer, credit, sw - textRenderer.getWidth(credit) - PADDING,
                fy + (FOOTER_HEIGHT - 8) / 2, TEXT_MUTED, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startY = HEADER_HEIGHT + 20;
        for (int i = 0; i < categories.size(); i++) {
            int itemY = startY + i * SIDEBAR_ITEM_H;
            if (mouseX >= 0 && mouseX < SIDEBAR_WIDTH
                    && mouseY >= itemY && mouseY < itemY + SIDEBAR_ITEM_H) {
                selectedCategory = i;
                return true;
            }
        }
        if (selectedCategory >= 0 && selectedCategory < panels.size())
            panels.get(selectedCategory).mouseClicked(mouseX, mouseY, button,
                    SIDEBAR_WIDTH + 1, HEADER_HEIGHT, width, height - FOOTER_HEIGHT);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double h, double v) {
        if (selectedCategory >= 0 && selectedCategory < panels.size())
            panels.get(selectedCategory).mouseScrolled(mouseX, mouseY, v);
        return super.mouseScrolled(mouseX, mouseY, h, v);
    }

    @Override
    public boolean shouldPause() { return false; }

    public static class CategoryEntry {
        public final String id, icon, label;
        public CategoryEntry(String id, String icon, String label) {
            this.id = id; this.icon = icon; this.label = label;
        }
    }
}
