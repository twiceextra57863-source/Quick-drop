package com.tclient.mod.gui;

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

    // Colors
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

    public TClientScreen() {
        super(Text.literal("T Client"));
    }

    @Override
    protected void init() {
        categories.clear();
        categories.add(new CategoryEntry("VISUAL",   "■", "Visual"));
        categories.add(new CategoryEntry("COMBAT",   "⚔", "Combat"));
        categories.add(new CategoryEntry("MOVEMENT", "➤", "Movement"));
        categories.add(new CategoryEntry("PLAYER",   "★", "Player"));
        categories.add(new CategoryEntry("WORLD",    "◎", "World"));
        categories.add(new CategoryEntry("HUD",      "□", "HUD"));
        categories.add(new CategoryEntry("MISC",     "⚙", "Misc"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // 1.21.4 standard background rendering
        super.renderBackground(ctx, mouseX, mouseY, delta);
        
        // Custom Dashboard Background
        ctx.fill(0, 0, width, height, BG_DARK);
        
        drawHeader(ctx);
        drawSidebar(ctx, mouseX, mouseY);
        
        // Seperator Line
        ctx.fill(SIDEBAR_WIDTH, HEADER_HEIGHT, SIDEBAR_WIDTH + 1, height - FOOTER_HEIGHT, ACCENT_DIM);

        // Content Area (Coming Soon placeholder)
        String msg = categories.get(selectedCategory).label + " features coming soon...";
        ctx.drawCenteredTextWithShadow(textRenderer, msg, SIDEBAR_WIDTH + (width - SIDEBAR_WIDTH) / 2, height / 2, 0xAAAAAA);

        drawFooter(ctx);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawHeader(DrawContext ctx) {
        ctx.fill(0, 0, width, HEADER_HEIGHT, BG_PANEL);
        ctx.fill(0, HEADER_HEIGHT - 1, width, HEADER_HEIGHT, ACCENT_DIM);
        ctx.fill(0, 0, 3, HEADER_HEIGHT, ACCENT);

        ctx.drawText(textRenderer, "T CLIENT", 15, (HEADER_HEIGHT - 8) / 2, ACCENT, false);
        
        if (selectedCategory < categories.size()) {
            String cat = "/ " + categories.get(selectedCategory).label;
            ctx.drawText(textRenderer, cat, SIDEBAR_WIDTH + 10, (HEADER_HEIGHT - 8) / 2, TEXT_ACCENT, false);
        }
        ctx.drawText(textRenderer, "v1.0", width - 35, (HEADER_HEIGHT - 8) / 2, TEXT_SECONDARY, false);
    }

    private void drawSidebar(DrawContext ctx, int mouseX, int mouseY) {
        ctx.fill(0, HEADER_HEIGHT, SIDEBAR_WIDTH, height - FOOTER_HEIGHT, BG_SIDEBAR);
        ctx.drawText(textRenderer, "MODULES", PADDING, HEADER_HEIGHT + 8, TEXT_MUTED, false);

        int startY = HEADER_HEIGHT + 24;
        for (int i = 0; i < categories.size(); i++) {
            CategoryEntry cat = categories.get(i);
            int itemY = startY + i * SIDEBAR_ITEM_H;
            
            boolean hovered = mouseX >= 0 && mouseX < SIDEBAR_WIDTH && mouseY >= itemY && mouseY < itemY + SIDEBAR_ITEM_H;
            boolean active  = (i == selectedCategory);

            if (active) {
                ctx.fill(0, itemY, SIDEBAR_WIDTH, itemY + SIDEBAR_ITEM_H, SIDEBAR_ACTIVE);
                ctx.fill(0, itemY, 2, itemY + SIDEBAR_ITEM_H, ACCENT);
            } else if (hovered) {
                ctx.fill(0, itemY, SIDEBAR_WIDTH, itemY + SIDEBAR_ITEM_H, SIDEBAR_HOVER);
            }

            int color = active ? ACCENT : (hovered ? TEXT_PRIMARY : TEXT_SECONDARY);
            ctx.drawText(textRenderer, cat.icon + " " + cat.label, PADDING + 4, itemY + 8, color, false);
        }
    }

    private void drawFooter(DrawContext ctx) {
        int fy = height - FOOTER_HEIGHT;
        ctx.fill(0, fy, width, height, BG_PANEL);
        ctx.fill(0, fy, width, fy + 1, ACCENT_DIM);
        ctx.drawText(textRenderer, "§7[ESC] Close Dashboard", PADDING, fy + 7, TEXT_SECONDARY, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startY = HEADER_HEIGHT + 24;
        for (int i = 0; i < categories.size(); i++) {
            int itemY = startY + i * SIDEBAR_ITEM_H;
            if (mouseX >= 0 && mouseX < SIDEBAR_WIDTH && mouseY >= itemY && mouseY < itemY + SIDEBAR_ITEM_H) {
                selectedCategory = i;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() { return false; }

    public record CategoryEntry(String id, String icon, String label) {}
}
