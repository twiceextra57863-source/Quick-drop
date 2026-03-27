package com.tclient.mod.mixin;

import com.tclient.mod.TClientMod;
import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

    @ModifyVariable(
        method = "draw(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private String modifyDrawString(String text) {
        if (TClientMod.CONFIG == null || !TClientMod.CONFIG.fontChangerEnabled) return text;
        if (text.contains("§")) return text;

        StringBuilder prefix = new StringBuilder();
        if (TClientMod.CONFIG.boldEnabled)   prefix.append("§l");
        if (TClientMod.CONFIG.italicEnabled) prefix.append("§o");
        if (prefix.length() == 0) return text;

        return prefix + text + "§r";
    }
}
