package com.tclient.mixin;

import com.tclient.font.FontManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextRenderer.class)
public class FontMixin {
    
    // Ye method text render hone se pehle font ID ko badal deta hai
    @ModifyVariable(method = "draw(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I", at = @At("HEAD"), ordinal = 0)
    private Identifier changeFont(Identifier identifier) {
        // Agar font "default" nahi hai toh humara custom font return karega
        if (!FontManager.currentFont.equals("default")) {
            return FontManager.getFontIdentifier();
        }
        return identifier;
    }
}
