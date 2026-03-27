package com.sikandar.tpvpmod.mixin;

import com.sikandar.tpvpmod.TPVPConfig;
import net.minecraft.client.MinecraftClient;
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
import org.joml.Matrix4f;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Inject(method = "renderNameTag(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"))
    private void renderHealthAboveName(LivingEntity entity, Text text, MatrixStack matrices,
                                       VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {

        if (!TPVPConfig.healthIndicatorEnabled) return;
        if (!(entity instanceof PlayerEntity player)) return;
        if (!TPVPConfig.showOnSelf && player == MinecraftClient.getInstance().player) return;

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        int color = health > maxHealth * 0.6f ? 0x00FF00 : (health > maxHealth * 0.3f ? 0xFFFF00 : 0xFF0000);

        String healthStr = (int) Math.ceil(health) + "/" + (int) maxHealth;

        matrices.push();
        matrices.translate(0, -0.7f, 0);   // name ke upar distance

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;  // safe way

        if (TPVPConfig.style == TPVPConfig.HealthStyle.PROGRESS_BAR) {
            float barWidth = 40f;
            float fill = (health / maxHealth) * barWidth;

            matrices.translate(-barWidth / 2, -8, 0);

            // Background bar
            drawRect(matrices, vertexConsumers, barWidth, 5, 0x80000000);
            // Health bar
            drawRect(matrices, vertexConsumers, fill, 5, color);
        }

        // Health number
        float x = -textRenderer.getWidth(healthStr) / 2f;
        textRenderer.draw(healthStr, x, -2, color, false,
                matrices.peek().getPositionMatrix(), vertexConsumers,
                TextRenderer.TextLayerType.NORMAL, 0, light);

        matrices.pop();
    }

    private void drawRect(MatrixStack matrices, VertexConsumerProvider vertices, float w, float h, int color) {
        var buffer = vertices.getBuffer(net.minecraft.client.render.RenderLayer.getGui());
        Matrix4f mat = matrices.peek().getPositionMatrix();

        buffer.vertex(mat, 0, h, 0).color(color).next();      // fixed chaining
        buffer.vertex(mat, w, h, 0).color(color).next();
        buffer.vertex(mat, w, 0, 0).color(color).next();
        buffer.vertex(mat, 0, 0, 0).color(color).next();
    }
}
