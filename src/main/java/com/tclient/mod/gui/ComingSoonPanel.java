package com.tclient.mod.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public class ComingSoonPanel extends TClientPanel {

    private final String message;

    public ComingSoonPanel(String message) {
        super(null);
        this.message = message;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta,
                       int x1, int y1, int x2, int y2) {
        ctx.fill(x1, y1, x2, y2, BG_PANEL);

        int cx = (x1 + x2) / 2;
        int cy = (y1 + y2) / 2;

        String title = "COMING SOON";
        ctx.drawText(client.textRenderer, title,
                cx - client.textRenderer.getWidth(title) / 2, cy - 8, ACCENT, false);
        ctx.drawText(client.textRenderer, message,
                cx - client.textRenderer.getWidth(message) / 2, cy + 6, TEXT_SECONDARY, false);

        ctx.fill(cx - 40, cy - 14, cx + 40, cy - 13, ACCENT_DIM);
        ctx.fill(cx - 40, cy + 18, cx + 40, cy + 19, ACCENT_DIM);
    }
}
