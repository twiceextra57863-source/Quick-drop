package com.pvppractice.mixin.client;

import com.pvppractice.config.PVPConfig;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    
    @Inject(method = "render", at = @At(value = "TAIL"))
    private void onRender(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        PVPConfig config = PVPConfig.getInstance();
        
        // Only render if enabled and entity is living
        if (config.heartIndicatorEnabled && entity instanceof LivingEntity livingEntity) {
            // Additional entity-specific rendering can be added here
            // This allows for 3D floating health indicators above entities
        }
    }
    
    @Inject(method = "renderLabelIfPresent", at = @At(value = "HEAD"))
    private void onRenderLabelIfPresent(Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        // Modify nameplate rendering if needed
    }
}
