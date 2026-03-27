package com.tclient.mixin;

import com.tclient.font.FontManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TextRenderer.class)
public class FontMixin {

    // Hum 'draw' method ke andar jo font ID pass hoti hai, usse raste mein hi badal denge
    @ModifyArg(
        method = "draw(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawInternal(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"),
        index = 0 // OrderedText argument ko target kar rahe hain
    )
    private Identifier injectCustomFont(Identifier original) {
        if (FontManager.currentFont != null && !FontManager.currentFont.equals("Default")) {
            return FontManager.getFontIdentifier();
        }
        return original;
    }

    @ModifyArg(
        method = "draw(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawInternal(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"),
        index = 0 // String argument ko target kar rahe hain
    )
    private Identifier injectCustomFontString(Identifier original) {
        if (FontManager.currentFont != null && !FontManager.currentFont.equals("Default")) {
            return FontManager.getFontIdentifier();
        }
        return original;
    }
}
