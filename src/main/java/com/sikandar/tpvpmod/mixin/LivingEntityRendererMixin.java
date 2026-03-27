package com.sikandar.tpvpmod.mixin;

import com.sikandar.tpvpmod.TPVPConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        int color = health > maxHealth * 0.6f ? 0x00FF00 :
                    health > maxHealth * 0.3f ? 0xFFFF00 : 0xFF0000;

        String healthStr = (int) Math.ceil(health) + "/" + (int) maxHealth;

        matrices.push();
        matrices.translate(0.0, -0.8, 0.0);   // name ke upar distance (tune kar sakta hai)

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        if (TPVPConfig.style == TPVPConfig.HealthStyle.PROGRESS_BAR) {
            float barWidth = 40f;
            float fillWidth = (health / maxHealth) * barWidth;

            matrices.translate(-barWidth / 2f, -10, 0);

            // Background bar (semi-transparent black)
            drawRect(matrices, vertexConsumers, barWidth, 6, 0x80000000);
            // Health bar
            drawRect(matrices, vertexConsumers, fillWidth, 6, color | 0xFF000000);  // full opacity for health
        }

        // Health number draw
        float x = -textRenderer.getWidth(healthStr) / 2f;
        textRenderer.draw(healthStr, x, -4, color, false,
                matrices.peek().getPositionMatrix(), vertexConsumers,
                TextRenderer.TextLayerType.NORMAL, 0, light);

        matrices.pop();
    }

    // Fixed drawRect for 1.21 (no .next())
    private void drawRect(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float width, float height, int color) {
        VertexConsumer buffer = vertexConsumers.getBuffer(net.minecraft.client.render.RenderLayer.getGui());
        Matrix4f mat = matrices.peek().getPositionMatrix();

        buffer.vertex(mat, 0, height, 0).color(color).next();
        buffer.vertex(mat, width, height, 0).color(color).next();
        buffer.vertex(mat, width, 0, 0).color(color).next();
        buffer.vertex(mat, 0, 0, 0).color(color).next();
    }
}
