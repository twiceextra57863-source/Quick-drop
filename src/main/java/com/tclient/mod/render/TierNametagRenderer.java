package com.tclient.mod.render;

import com.tclient.mod.data.PlayerProfile;
import com.tclient.mod.data.ProfileManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class TierNametagRenderer {
    
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final ProfileManager profileManager = ProfileManager.getInstance();
    
    public static void renderNametag(PlayerEntity player, MatrixStack matrices, 
                                      VertexConsumerProvider vertexConsumers, 
                                      EntityRenderDispatcher dispatcher) {
        if (player == null || client.player == null) return;
        
        // Don't render on self
        if (player.equals(client.player)) return;
        
        PlayerProfile profile = profileManager.getProfile(player.getUuid());
        if (profile == null) return;
        
        // Get tier and color
        String tierName = profile.getTier().name();
        String tierColor = getTierColor(profile);
        String tierPrefix = profile.getTier().prefix;
        
        // Create tier text
        String tierText = tierColor + "[" + tierName + "] " + tierPrefix + " " + player.getName().getString();
        
        // Get player position
        double x = player.getX();
        double y = player.getY() + player.getHeight() + 0.5;
        double z = player.getZ();
        
        // Calculate distance
        double distance = client.player.distanceTo(player);
        float scale = (float) (0.025f * (Math.max(5, Math.min(20, 30 - distance))));
        
        // Render the nametag
        renderText(matrices, vertexConsumers, tierText, x, y, z, scale, 
                   getTierColorCode(profile));
    }
    
    private static String getTierColor(PlayerProfile profile) {
        switch(profile.getTier()) {
            case BASIC:
                return "§c"; // Red - Low tier
            case ADVANCED:
                return "§e"; // Yellow - Medium tier
            case PRO:
                return "§a"; // Green - High tier
            case EXTREME:
                return "§b"; // Aqua - Very high
            case LEGENDARY:
                return "§6"; // Gold - Legendary
            default:
                return "§7";
        }
    }
    
    private static int getTierColorCode(PlayerProfile profile) {
        switch(profile.getTier()) {
            case BASIC:
                return 0xFF5555; // Red
            case ADVANCED:
                return 0xFFFF55; // Yellow
            case PRO:
                return 0x55FF55; // Green
            case EXTREME:
                return 0x55FFFF; // Aqua
            case LEGENDARY:
                return 0xFFAA00; // Gold
            default:
                return 0xFFFFFF;
        }
    }
    
    private static void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                   String text, double x, double y, double z, float scale, int color) {
        TextRenderer textRenderer = client.textRenderer;
        
        matrices.push();
        matrices.translate(x, y, z);
        matrices.multiply(client.getEntityRenderDispatcher().getRotation());
        matrices.scale(-scale, -scale, scale);
        
        float textWidth = textRenderer.getWidth(text) / 2.0f;
        float textHeight = textRenderer.fontHeight / 2.0f;
        
        // Draw background
        int bgColor = 0x80000000;
        textRenderer.draw(text, -textWidth, -textHeight, bgColor, false, 
                         matrices.peek().getPositionMatrix(), vertexConsumers, 
                         TextRenderer.TextLayerType.SEE_THROUGH, bgColor, false);
        
        // Draw text
        textRenderer.draw(text, -textWidth, -textHeight, color, false,
                         matrices.peek().getPositionMatrix(), vertexConsumers,
                         TextRenderer.TextLayerType.NORMAL, 0, true);
        
        matrices.pop();
    }
              }
