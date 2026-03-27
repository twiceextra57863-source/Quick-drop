package com.pvppractice.client.render;

import com.pvppractice.config.PVPConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import java.util.List;

public class HeartIndicatorRenderer {
    
    public static void render(DrawContext context) {
        PVPConfig config = PVPConfig.getInstance();
        
        if (!config.heartIndicatorEnabled) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        
        // Get all entities in render distance
        double renderDistance = 32.0;
        Box searchBox = client.player.getBoundingBox().expand(renderDistance);
        List<LivingEntity> entities = client.world.getEntitiesByClass(LivingEntity.class, searchBox, 
            entity -> entity != client.player && entity.isAlive());
        
        for (LivingEntity livingEntity : entities) {
            renderHealthIndicator(context, livingEntity, client, config);
        }
    }
    
    private static void renderHealthIndicator(DrawContext context, LivingEntity entity, MinecraftClient client, PVPConfig config) {
        float health = entity.getHealth();
        float maxHealth = entity.getMaxHealth();
        float healthPercent = health / maxHealth;
        
        // Calculate screen position (simplified for now - will be above entity)
        int x = client.getWindow().getScaledWidth() / 2;
        int y = client.getWindow().getScaledHeight() / 2 - 50;
        
        int width = (int)(80 * getScale(config.indicatorSize));
        int height = (int)(20 * getScale(config.indicatorSize));
        
        HeartIndicatorType.RenderData data = new HeartIndicatorType.RenderData(
            x, y, health, maxHealth, healthPercent, 
            config.healthBarColor.getColor(), width, height, entity
        );
        
        // Call the render method on the enum instance
        switch (config.heartStyle) {
            case STATUS_BAR:
                HeartIndicatorType.STATUS_BAR.render(context, data);
                break;
            case MINECRAFT_HEARTS:
                HeartIndicatorType.MINECRAFT_HEARTS.render(context, data);
                break;
            case PLAYER_HEAD:
                HeartIndicatorType.PLAYER_HEAD.render(context, data);
                break;
            case DIGITAL_NUMBERS:
                HeartIndicatorType.DIGITAL_NUMBERS.render(context, data);
                break;
            case CIRCULAR_METER:
                HeartIndicatorType.CIRCULAR_METER.render(context, data);
                break;
            case HORIZONTAL_WAVE:
                HeartIndicatorType.HORIZONTAL_WAVE.render(context, data);
                break;
            case VERTICAL_STACK:
                HeartIndicatorType.VERTICAL_STACK.render(context, data);
                break;
            case FLAME_EFFECT:
                HeartIndicatorType.FLAME_EFFECT.render(context, data);
                break;
            case CRYSTAL_HEART:
                HeartIndicatorType.CRYSTAL_HEART.render(context, data);
                break;
        }
    }
    
    private static float getScale(int size) {
        switch(size) {
            case 1: return 0.75f;
            case 3: return 1.5f;
            default: return 1.0f;
        }
    }
}
