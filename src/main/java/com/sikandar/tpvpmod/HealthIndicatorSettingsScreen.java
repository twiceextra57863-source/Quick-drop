package com.sikandar.tpvpmod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import static com.sikandar.tpvpmod.TPVPConfig.HealthStyle;  // import

public class HealthIndicatorSettingsScreen extends Screen {

    public HealthIndicatorSettingsScreen() {
        super(Text.literal("§cHealth Indicator Settings"));
    }

    @Override
    protected void init() {
        int y = this.height / 4 + 20;

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Style: " + TPVPConfig.style.name()),
                button -> {
                    HealthStyle[] styles = HealthStyle.values();
                    TPVPConfig.style = styles[(TPVPConfig.style.ordinal() + 1) % styles.length];
                    client.setScreen(this);
                }
        ).dimensions(this.width / 2 - 100, y, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Show on Self: " + (TPVPConfig.showOnSelf ? "§aYES" : "§cNO")),
                button -> {
                    TPVPConfig.showOnSelf = !TPVPConfig.showOnSelf;
                    client.setScreen(this);
                }
        ).dimensions(this.width / 2 - 100, y + 30, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("§aBack"),
                button -> client.setScreen(new TPVPDashboardScreen())
        ).dimensions(this.width / 2 - 100, this.height - 50, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 30, 0xFFFFFF);
    }
}
