package com.tclient.mod.mixin;

import com.tclient.mod.render.TierNametagRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class NametagRendererMixin<T extends LivingEntity> {
    
    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void renderCustomNametag(T entity, Text text, MatrixStack matrices, 
                                      VertexConsumerProvider vertexConsumers, int light,
                                      CallbackInfo ci) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            // Cancel vanilla rendering
            ci.cancel();
            // Render custom tier nametag
            TierNametagRenderer.renderNametag(player, matrices, vertexConsumers, 
                                              (EntityRenderDispatcher) null);
        }
    }
}
