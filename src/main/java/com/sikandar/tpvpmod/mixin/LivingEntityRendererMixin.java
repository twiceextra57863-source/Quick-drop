package com.sikandar.tpvpmod.mixin;

import com.sikandar.tpvpmod.TPVPConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Inject(method = "renderNameTag", at = @At("HEAD"))
    private void renderHealthAboveName(LivingEntity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!TPVPConfig.healthIndicatorEnabled) return;
        if (!(entity instanceof PlayerEntity player)) return;
        if (!TPVPConfig.showOnSelf && player == MinecraftClient.getInstance().player) return;

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        int color = health > maxHealth * 0.6f ? 0x00FF00 : health > maxHealth * 0.3f ? 0xFFFF00 : 0xFF0000;

        String healthStr = (int) health + "/" + (int) maxHealth;

        matrices.push();
        matrices.translate(0, -0.6, 0);  // name ke upar adjust kar sakta hai (higher = -0.8 ya -1.0)

        TextRenderer textRenderer = ((LivingEntityRenderer<?, ?>) (Object) this).textRenderer;

        // Progress bar style (simple filled bar)
        if (TPVPConfig.style == TPVPConfig.HealthStyle.PROGRESS_BAR) {
            float barWidth = 40;
            float fill = (health / maxHealth) * barWidth;

            // Background
            matrices.translate(-barWidth / 2, -5, 0);
            drawRect(matrices, vertexConsumers, barWidth, 4, 0x80000000); // dark bg
            // Filled
            drawRect(matrices, vertexConsumers, fill, 4, color);
        }

        // Number draw (sab style me common)
        float x = -textRenderer.getWidth(healthStr) / 2f;
        textRenderer.draw(healthStr, x, 0, color, false, matrices.peek().getPositionMatrix(), vertexConsumers,
                TextRenderer.TextLayerType.NORMAL, 0, light);

        matrices.pop();

        // Future me Hearts aur Head+Skin yaha extend kar sakta hai (texture use karke)
    }

    // Helper to draw rectangle in world (progress bar ke liye)
    private void drawRect(MatrixStack matrices, VertexConsumerProvider vertices, float width, float height, int color) {
        var buffer = vertices.getBuffer(net.minecraft.client.render.RenderLayer.getGui());
        var mat = matrices.peek().getPositionMatrix();
        buffer.vertex(mat, 0, height, 0).color(color).next();
        buffer.vertex(mat, width, height, 0).color(color).next();
        buffer.vertex(mat, width, 0, 0).color(color).next();
        buffer.vertex(mat, 0, 0, 0).color(color).next();
    }
}
