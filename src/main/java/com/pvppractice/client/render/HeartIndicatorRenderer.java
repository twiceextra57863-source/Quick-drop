package com.pvppractice.client.render;

import com.pvppractice.config.PVPConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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
        List<Entity> entities = client.world.getEntitiesByClass(LivingEntity.class, searchBox, 
            entity -> entity != client.player && entity.isAlive());
        
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity) {
                renderHealthIndicator(context, livingEntity, client, config);
            }
        }
    }
    
    private static void renderHealthIndicator(DrawContext context, LivingEntity entity, MinecraftClient client, PVPConfig config) {
        float health = entity.getHealth();
        float maxHealth = entity.getMaxHealth();
        float healthPercent = health / maxHealth;
        
        // Calculate screen position
        Vec3d entityPos = entity.getPos().add(0, entity.getHeight() + 0.5, 0);
        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        Matrix4f matrix4f = new Matrix4f().identity();
        Vector3f screenPos = new Vector3f();
        
        // Simple world to screen calculation
        double dx = entityPos.x - cameraPos.x;
        double dy = entityPos.y - cameraPos.y;
        double dz = entityPos.z - cameraPos.z;
        
        float scale = getScale(config.indicatorSize);
        int x = client.getWindow().getScaledWidth() / 2;
        int y = client.getWindow().getScaledHeight() / 2;
        
        // Basic rendering logic
        renderByStyle(context, entity, x, y, (int)(80 * scale), (int)(20 * scale), health, maxHealth, healthPercent, config);
    }
    
    private static void renderByStyle(DrawContext context, LivingEntity entity, int x, int y, int width, int height, 
                                     float health, float maxHealth, float percent, PVPConfig config) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int color = config.healthBarColor.getColor();
        
        switch (config.heartStyle) {
            case STATUS_BAR:
                renderStatusBar(context, x, y, width, height, percent, color);
                break;
                
            case MINECRAFT_HEARTS:
                renderMinecraftHearts(context, x, y, health, maxHealth, color);
                break;
                
            case PLAYER_HEAD:
                renderPlayerHead(context, entity, x, y, percent, color);
                break;
                
            case DIGITAL_NUMBERS:
                renderDigitalNumbers(context, textRenderer, x, y, health, maxHealth, color);
                break;
                
            case CIRCULAR_METER:
                renderCircularMeter(context, x, y, percent, color);
                break;
        }
        
        // Show health numbers if enabled
        if (config.showHealthNumbers) {
            String healthText = String.format("%.1f/%.0f", health, maxHealth);
            context.drawTextWithShadow(textRenderer, healthText, x - 20, y - 8, 0xFFFFFF);
        }
    }
    
    private static void renderStatusBar(DrawContext context, int x, int y, int width, int height, float percent, int color) {
        int filledWidth = (int)(width * percent);
        context.fill(x - width/2, y, x + width/2, y + height, 0x88000000);
        context.fill(x - width/2, y, x - width/2 + filledWidth, y + height, color);
        
        // Border
        context.drawBorder(x - width/2, y, width, height, 0xFFFFFFFF);
    }
    
    private static void renderMinecraftHearts(DrawContext context, int x, int y, float health, float maxHealth, int color) {
        int heartCount = (int)Math.ceil(health / 2);
        int maxHearts = (int)Math.ceil(maxHealth / 2);
        int heartSize = 8;
        int startX = x - (maxHearts * heartSize) / 2;
        
        for (int i = 0; i < maxHearts; i++) {
            int heartX = startX + i * heartSize;
            int heartY = y;
            
            if (i < heartCount) {
                // Full heart
                context.fill(heartX, heartY, heartX + heartSize, heartY + heartSize, color);
                context.fill(heartX + 2, heartY + 1, heartX + 6, heartY + 7, 0xFFFF5555);
            } else {
                // Empty heart outline
                context.drawBorder(heartX, heartY, heartSize, heartSize, 0x88FFFFFF);
            }
        }
    }
    
    private static void renderPlayerHead(DrawContext context, LivingEntity entity, int x, int y, float percent, int color) {
        int size = 16;
        context.fill(x - size/2, y - size/2, x + size/2, y + size/2, 0xFF888888);
        context.fill(x - size/2 + 2, y - size/2 + 2, x + size/2 - 2, y + size/2 - 2, 0xFFDDDDDD);
        
        // Draw health ring around head
        int ringWidth = (int)(size * percent);
        context.drawBorder(x - size/2, y - size/2, size, size, color);
        
        // Draw simple face
        context.fill(x - 4, y - 2, x - 2, y, 0xFF000000);
        context.fill(x + 2, y - 2, x + 4, y, 0xFF000000);
        context.fill(x - 3, y + 2, x + 3, y + 4, 0xFF000000);
    }
    
    private static void renderDigitalNumbers(DrawContext context, TextRenderer textRenderer, int x, int y, float health, float maxHealth, int color) {
        String healthText = String.format("%.0f", health);
        String maxText = String.format("%.0f", maxHealth);
        
        int fontSize = 16;
        context.drawTextWithShadow(textRenderer, healthText, x - 12, y - 8, color);
        context.drawTextWithShadow(textRenderer, "/", x - 4, y - 8, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, maxText, x + 4, y - 8, 0xAAAAAA);
        
        // Draw background
        context.fill(x - 24, y - 12, x + 24, y + 4, 0x88000000);
    }
    
    private static void renderCircularMeter(DrawContext context, int x, int y, float percent, int color) {
        int radius = 12;
        
        // Draw background circle
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (i*i + j*j <= radius*radius) {
                    context.fill(x + i, y + j, x + i + 1, y + j + 1, 0x88FFFFFF);
                }
            }
        }
        
        // Draw filled arc
        int angle = (int)(360 * percent);
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (i*i + j*j <= radius*radius) {
                    double anglePoint = Math.atan2(j, i) * 180 / Math.PI;
                    if (anglePoint < 0) anglePoint += 360;
                    if (anglePoint <= angle) {
                        context.fill(x + i, y + j, x + i + 1, y + j + 1, color);
                    }
                }
            }
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
