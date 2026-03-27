package com.tclient.mixin;

import com.tclient.font.FontManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextRenderer.class)
public class FontMixin {

    // Minecraft 1.21.4 draw method redirection
    @ModifyVariable(method = "draw(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I", at = @At("HEAD"), argsOnly = true)
    private Identifier redirectFont(Identifier identifier) {
        if (FontManager.currentFont != null && !FontManager.currentFont.equals("Default")) {
            return FontManager.getFontIdentifier();
        }
        return identifier;
    }

    // Secondary draw method for plain strings
    @ModifyVariable(method = "draw(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I", at = @At("HEAD"), argsOnly = true)
    private Identifier redirectFontString(Identifier identifier) {
        if (FontManager.currentFont != null && !FontManager.currentFont.equals("Default")) {
            return FontManager.getFontIdentifier();
        }
        return identifier;
    }
}
