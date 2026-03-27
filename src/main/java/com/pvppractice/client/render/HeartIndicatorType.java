package com.pvppractice.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import java.awt.Color;
import java.util.function.BiConsumer;

public enum HeartIndicatorType {
    STATUS_BAR("Status Bar", "Linear health bar with gradient", (context, data) -> {
        int width = data.width;
        int height = data.height;
        int x = data.x - width / 2;
        int y = data.y;
        float percent = data.percent;
        int color = data.color;
        
        // Background
        context.fill(x, y, x + width, y + height, 0x88000000);
        
        // Health fill with gradient
        int filledWidth = (int)(width * percent);
        for (int i = 0; i < filledWidth; i++) {
            float progress = (float) i / width;
            int gradientColor = getGradientColor(color, progress);
            context.fill(x + i, y, x + i + 1, y + height, gradientColor);
        }
        
        // Border with glow effect
        context.drawBorder(x, y, width, height, 0xFFFFFFFF);
        if (percent < 0.2f) {
            // Low health warning pulse
            long time = System.currentTimeMillis() % 1000;
            int alpha = (int)(128 + Math.sin(time * 0.01) * 64);
            context.fill(x, y, x + width, y + height, new Color(255, 0, 0, alpha).getRGB());
        }
    }),
    
    MINECRAFT_HEARTS("Minecraft Hearts", "Classic Minecraft heart display", (context, data) -> {
        float health = data.health;
        float maxHealth = data.maxHealth;
        int x = data.x;
        int y = data.y;
        int color = data.color;
        
        int heartCount = (int)Math.ceil(health / 2);
        int maxHearts = (int)Math.ceil(maxHealth / 2);
        int heartSize = 8;
        int startX = x - (maxHearts * heartSize) / 2;
        
        for (int i = 0; i < maxHearts; i++) {
            int heartX = startX + i * heartSize;
            int heartY = y;
            
            if (i < heartCount) {
                float remainingHealth = health - (i * 2);
                if (remainingHealth >= 2) {
                    // Full heart
                    drawHeart(context, heartX, heartY, heartSize, color, true);
                } else if (remainingHealth >= 1) {
                    // Half heart
                    drawHalfHeart(context, heartX, heartY, heartSize, color);
                } else {
                    // Empty heart
                    drawHeart(context, heartX, heartY, heartSize, 0x88AAAAAA, false);
                }
            } else {
                // Empty heart outline
                drawHeartOutline(context, heartX, heartY, heartSize, 0x88FFFFFF);
            }
        }
    }),
    
    PLAYER_HEAD("Player Head", "3D style head with health ring", (context, data) -> {
        LivingEntity entity = data.entity;
        int x = data.x;
        int y = data.y;
        float percent = data.percent;
        int color = data.color;
        int size = 20;
        
        // Draw shadow
        context.fill(x - size/2 + 2, y - size/2 + 2, x + size/2 + 2, y + size/2 + 2, 0x44000000);
        
        // Draw head base
        context.fill(x - size/2, y - size/2, x + size/2, y + size/2, 0xFFDDAA77);
        
        // Draw face details
        context.fill(x - 5, y - 3, x - 2, y, 0xFF000000); // Left eye
        context.fill(x + 2, y - 3, x + 5, y, 0xFF000000); // Right eye
        context.fill(x - 4, y + 2, x + 4, y + 5, 0xFF000000); // Mouth
        
        // Draw health ring with animation
        int ringWidth = (int)(size * percent);
        for (int i = 0; i < ringWidth; i++) {
            int ringX = x - size/2 + i;
            int ringY = y - size/2;
            context.fill(ringX, ringY, ringX + 1, ringY + 2, color);
            context.fill(ringX, y + size/2 - 1, ringX + 1, y + size/2 + 1, color);
        }
        
        // Draw crown for high health
        if (percent > 0.8f) {
            context.fill(x - 4, y - size/2 - 2, x + 4, y - size/2, 0xFFFFD700);
            context.fill(x - 2, y - size/2 - 4, x + 2, y - size/2 - 2, 0xFFFFD700);
        }
        
        // Draw damage indicator if low health
        if (percent < 0.2f) {
            long time = System.currentTimeMillis() % 500;
            if (time < 250) {
                context.fill(x - size/2, y - size/2, x + size/2, y + size/2, 0x44FF0000);
            }
        }
    }),
    
    DIGITAL_NUMBERS("Digital Numbers", "Large digital display with effects", (context, data) -> {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        float health = data.health;
        float maxHealth = data.maxHealth;
        int x = data.x;
        int y = data.y;
        int color = data.color;
        float percent = data.percent;
        
        String healthText = String.format("%.0f", health);
        String maxText = String.format("%.0f", maxHealth);
        
        // Draw background with blur effect
        context.fill(x - 32, y - 12, x + 32, y + 10, 0xAA000000);
        context.drawBorder(x - 32, y - 12, 64, 22, 0xFFFFFFFF);
        
        // Main health number with glow
        int glowSize = 2;
        for (int i = 0; i < glowSize; i++) {
            context.drawText(textRenderer, healthText, x - 12 - i, y - 8, 0x44000000, false);
            context.drawText(textRenderer, healthText, x - 12 + i, y - 8, 0x44000000, false);
            context.drawText(textRenderer, healthText, x - 12, y - 8 - i, 0x44000000, false);
            context.drawText(textRenderer, healthText, x - 12, y - 8 + i, 0x44000000, false);
        }
        context.drawText(textRenderer, healthText, x - 12, y - 8, color, false);
        
        // Separator
        context.drawText(textRenderer, "/", x + 4, y - 8, 0xFFFFFF, false);
        
        // Max health
        context.drawText(textRenderer, maxText, x + 12, y - 8, 0xAAAAAA, false);
        
        // Percentage bar below
        int barWidth = 50;
        int filledWidth = (int)(barWidth * percent);
        context.fill(x - 25, y + 4, x + 25, y + 6, 0x88000000);
        context.fill(x - 25, y + 4, x - 25 + filledWidth, y + 6, color);
        
        // Low health warning
        if (percent < 0.2f) {
            long time = System.currentTimeMillis() % 1000;
            if (time < 500) {
                context.drawText(textRenderer, "!", x - 28, y - 8, 0xFF0000, false);
            }
        }
    }),
    
    CIRCULAR_METER("Circular Meter", "Stylish circular progress meter", (context, data) -> {
        int x = data.x;
        int y = data.y;
        float percent = data.percent;
        int color = data.color;
        int radius = 14;
        
        // Draw outer ring with shadow
        for (int i = -radius - 1; i <= radius + 1; i++) {
            for (int j = -radius - 1; j <= radius + 1; j++) {
                if (i*i + j*j <= (radius + 1) * (radius + 1)) {
                    context.fill(x + i, y + j, x + i + 1, y + j + 1, 0x44000000);
                }
            }
        }
        
        // Draw background circle
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (i*i + j*j <= radius * radius) {
                    context.fill(x + i, y + j, x + i + 1, y + j + 1, 0xFF222222);
                }
            }
        }
        
        // Draw filled arc (using pixel-perfect circle filling)
        int angle = (int)(360 * percent);
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (i*i + j*j <= radius * radius) {
                    double anglePoint = Math.atan2(j, i) * 180 / Math.PI;
                    if (anglePoint < 0) anglePoint += 360;
                    if (anglePoint <= angle) {
                        // Gradient based on angle
                        float intensity = (float) anglePoint / 360;
                        int gradientColor = getGradientColor(color, intensity);
                        context.fill(x + i, y + j, x + i + 1, y + j + 1, gradientColor);
                    }
                }
            }
        }
        
        // Draw center with health percentage
        String percentText = String.format("%.0f%%", percent * 100);
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        context.drawCenteredTextWithShadow(textRenderer, percentText, x, y - 4, 0xFFFFFF);
        
        // Draw needle for analog feel
        if (percent > 0) {
            double needleAngle = Math.toRadians(angle - 90);
            int needleX = x + (int)(radius * 0.7 * Math.cos(needleAngle));
            int needleY = y + (int)(radius * 0.7 * Math.sin(needleAngle));
            drawLine(context, x, y, needleX, needleY, 0xFFFFFFFF);
        }
        
        // Pulsing effect for full health
        if (percent > 0.99f) {
            long time = System.currentTimeMillis() % 1000;
            int alpha = (int)(128 + Math.sin(time * 0.02) * 64);
            context.fill(x - radius, y - radius, x + radius, y + radius, new Color(0, 255, 0, alpha).getRGB());
        }
    }),
    
    HORIZONTAL_WAVE("Horizontal Wave", "Flowing wave animation", (context, data) -> {
        int width = 80;
        int height = 16;
        int x = data.x - width / 2;
        int y = data.y;
        float percent = data.percent;
        int color = data.color;
        
        long time = System.currentTimeMillis();
        float waveOffset = (time % 2000) / 2000.0f * (float)Math.PI * 2;
        
        // Background
        context.fill(x, y, x + width, y + height, 0x88000000);
        
        // Draw wave pattern
        for (int i = 0; i < width; i++) {
            float progress = (float) i / width;
            if (progress <= percent) {
                float waveHeight = (float)(Math.sin(progress * Math.PI * 2 + waveOffset) * 0.3f + 0.5f);
                int waveY = y + (int)(height * (1 - waveHeight * 0.8f));
                int fillHeight = y + height - waveY;
                
                // Gradient based on position
                int gradientColor = getGradientColor(color, progress);
                context.fill(x + i, waveY, x + i + 1, y + height, gradientColor);
            }
        }
        
        // Border
        context.drawBorder(x, y, width, height, 0xFFFFFFFF);
        
        // Floating particles
        if (percent > 0.5f) {
            int particleCount = (int)(Math.sin(waveOffset) * 3 + 3);
            for (int p = 0; p < particleCount; p++) {
                float particleX = x + (float)Math.sin(waveOffset + p) * width * percent;
                context.fill((int)particleX, y - 2, (int)particleX + 1, y - 1, color);
            }
        }
    }),
    
    VERTICAL_STACK("Vertical Stack", "Stacked bars for each heart", (context, data) -> {
        float health = data.health;
        float maxHealth = data.maxHealth;
        int x = data.x;
        int y = data.y;
        int color = data.color;
        
        int heartCount = (int)Math.ceil(health);
        int maxHearts = (int)Math.ceil(maxHealth);
        int barWidth = 8;
        int barHeight = 4;
        int spacing = 2;
        int totalWidth = maxHearts * (barWidth + spacing) - spacing;
        int startX = x - totalWidth / 2;
        
        for (int i = 0; i < maxHearts; i++) {
            int barX = startX + i * (barWidth + spacing);
            int barY = y;
            
            if (i < heartCount) {
                float remainingHealth = health - i;
                if (remainingHealth >= 1) {
                    // Full bar
                    context.fill(barX, barY, barX + barWidth, barY + barHeight, color);
                } else if (remainingHealth > 0) {
                    // Partial bar
                    int filledWidth = (int)(barWidth * remainingHealth);
                    context.fill(barX, barY, barX + filledWidth, barY + barHeight, color);
                    context.fill(barX + filledWidth, barY, barX + barWidth, barY + barHeight, 0x88AAAAAA);
                }
            } else {
                // Empty bar
                context.fill(barX, barY, barX + barWidth, barY + barHeight, 0x88AAAAAA);
            }
            
            // Border
            context.drawBorder(barX, barY, barWidth, barHeight, 0xFFFFFFFF);
        }
    }),
    
    FLAME_EFFECT("Flame Effect", "Fire-based health indicator", (context, data) -> {
        int width = 60;
        int height = 20;
        int x = data.x - width / 2;
        int y = data.y;
        float percent = data.percent;
        
        long time = System.currentTimeMillis();
        
        // Background
        context.fill(x, y, x + width, y + height, 0x88222222);
        
        // Draw flame effect
        for (int i = 0; i < width; i++) {
            float progress = (float) i / width;
            if (progress <= percent) {
                float flameHeight = (float)(Math.sin(progress * Math.PI * 4 + time * 0.01) * 0.3f + 0.5f);
                int flameY = y + (int)(height * (1 - flameHeight));
                
                // Gradient from red to orange to yellow
                int gradientColor;
                if (progress < 0.33f) {
                    gradientColor = 0xFFFF4444;
                } else if (progress < 0.66f) {
                    gradientColor = 0xFFFFAA44;
                } else {
                    gradientColor = 0xFFFFFF44;
                }
                
                context.fill(x + i, flameY, x + i + 1, y + height, gradientColor);
            }
        }
        
        // Border with fire effect
        context.drawBorder(x, y, width, height, 0xFFFFAA00);
        
        // Sparks
        if (percent > 0.5f) {
            int sparkCount = (int)(Math.sin(time * 0.02) * 3 + 2);
            for (int s = 0; s < sparkCount; s++) {
                float sparkX = x + (float)Math.sin(time * 0.05 + s) * width * percent;
                context.fill((int)sparkX, y - 2, (int)sparkX + 1, y - 1, 0xFFFF6600);
            }
        }
    }),
    
    CRYSTAL_HEART("Crystal Heart", "Gemstone-style heart", (context, data) -> {
        int x = data.x;
        int y = data.y;
        float percent = data.percent;
        int color = data.color;
        int size = 16;
        
        long time = System.currentTimeMillis();
        float shine = (float)(Math.sin(time * 0.01) * 0.3f + 0.7f);
        
        // Draw crystal facets
        int[] xPoints = {x, x - size/2, x, x + size/2};
        int[] yPoints = {y - size/2, y, y + size/2, y};
        
        for (int i = 0; i < 4; i++) {
            int nextI = (i + 1) % 4;
            drawLine(context, xPoints[i], yPoints[i], xPoints[nextI], yPoints[nextI], color);
        }
        
        // Fill with gradient based on health
        int fillColor = getGradientColor(color, percent);
        context.fill(x - size/3, y - size/3, x + size/3, y + size/3, fillColor);
        
        // Draw shine effect
        int shineX = x - size/4 + (int)(shine * 2);
        int shineY = y - size/4;
        context.fill(shineX, shineY, shineX + 3, shineY + 3, 0x88FFFFFF);
        
        // Draw health percentage inside
        String percentText = String.format("%.0f", percent * 100);
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        context.drawCenteredTextWithShadow(textRenderer, percentText, x, y - 2, 0xFFFFFF);
    });
    
    private final String displayName;
    private final String description;
    private final BiConsumer<DrawContext, RenderData> renderer;
    
    HeartIndicatorType(String displayName, String description, BiConsumer<DrawContext, RenderData> renderer) {
        this.displayName = displayName;
        this.description = description;
        this.renderer = renderer;
    }
    
    public void render(DrawContext context, RenderData data) {
        renderer.accept(context, data);
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static class RenderData {
        public final int x;
        public final int y;
        public final float health;
        public final float maxHealth;
        public final float percent;
        public final int color;
        public final int width;
        public final int height;
        public final LivingEntity entity;
        
        public RenderData(int x, int y, float health, float maxHealth, float percent, int color, int width, int height, LivingEntity entity) {
            this.x = x;
            this.y = y;
            this.health = health;
            this.maxHealth = maxHealth;
            this.percent = percent;
            this.color = color;
            this.width = width;
            this.height = height;
            this.entity = entity;
        }
    }
    
    private static int getGradientColor(int baseColor, float progress) {
        int r = (baseColor >> 16) & 0xFF;
        int g = (baseColor >> 8) & 0xFF;
        int b = baseColor & 0xFF;
        
        // Create gradient based on progress
        if (progress < 0.5f) {
            float t = progress * 2;
            r = (int)(r * (1 - t) + 255 * t);
            g = (int)(g * (1 - t) + 100 * t);
            b = (int)(b * (1 - t) + 0 * t);
        } else {
            float t = (progress - 0.5f) * 2;
            r = (int)(255 * (1 - t) + 100 * t);
            g = (int)(100 * (1 - t) + 255 * t);
            b = (int)(0 * (1 - t) + 100 * t);
        }
        
        return (r << 16) | (g << 8) | b;
    }
    
    private static void drawHeart(DrawContext context, int x, int y, int size, int color, boolean filled) {
        int halfSize = size / 2;
        int quarterSize = size / 4;
        
        if (filled) {
            // Top curves
            context.fill(x + quarterSize, y, x + halfSize, y + quarterSize, color);
            context.fill(x + halfSize, y, x + halfSize + quarterSize, y + quarterSize, color);
            // Middle
            context.fill(x + quarterSize / 2, y + quarterSize, x + size - quarterSize / 2, y + halfSize, color);
            // Bottom point
            context.fill(x + quarterSize, y + halfSize, x + size - quarterSize, y + halfSize + quarterSize, color);
            context.fill(x + halfSize - quarterSize / 2, y + halfSize + quarterSize, x + halfSize + quarterSize / 2, y + size, color);
        } else {
            // Outline only
            drawHeartOutline(context, x, y, size, color);
        }
    }
    
    private static void drawHalfHeart(DrawContext context, int x, int y, int size, int color) {
        int halfSize = size / 2;
        int quarterSize = size / 4;
        
        // Draw left half filled
        context.fill(x + quarterSize, y, x + halfSize, y + quarterSize, color);
        context.fill(x + quarterSize / 2, y + quarterSize, x + halfSize, y + halfSize, color);
        context.fill(x + quarterSize, y + halfSize, x + halfSize, y + halfSize + quarterSize, color);
        
        // Draw right half empty
        drawHeartOutline(context, x + halfSize, y, halfSize, 0x88AAAAAA);
    }
    
    private static void drawHeartOutline(DrawContext context, int x, int y, int size, int color) {
        context.drawBorder(x, y, size, size, color);
        context.drawBorder(x + size/4, y, size/2, size/4, color);
        context.drawBorder(x + size/8, y + size/4, size * 3/4, size/2, color);
        context.drawBorder(x + size/4, y + size * 3/4, size/2, size/4, color);
    }
    
    private static void drawLine(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        
        while (true) {
            context.fill(x1, y1, x1 + 1, y1 + 1, color);
            if (x1 == x2 && y1 == y2) break;
            int e2 = err * 2;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }
}
