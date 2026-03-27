package com.tclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.util.Identifier;

@Mixin(net.minecraft.client.font.FontManager.class)
public class FontMixin {

    @ModifyVariable(method = "method_27539", at = @At("HEAD"), argsOnly = true, remap = false)
    private Identifier changeFontOnLoad(Identifier id) {
        if (com.tclient.font.FontManager.currentFont != null && !com.tclient.font.FontManager.currentFont.equals("Default")) {
            if (id.getNamespace().equals("minecraft") && id.getPath().equals("default")) {
                return com.tclient.font.FontManager.getFontIdentifier();
            }
        }
        return id;
    }
}
