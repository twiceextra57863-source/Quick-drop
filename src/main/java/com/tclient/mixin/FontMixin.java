package com.tclient.mixin;

import com.tclient.font.FontManager;
import net.minecraft.client.font.FontManager; // Minecraft's FontManager
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(net.minecraft.client.font.FontManager.class)
public class FontMixin {

    // Minecraft jab font list dhoondta hai, hum wahi par apna custom ID ghusa denge
    @ModifyVariable(method = "method_27539", at = @At("HEAD"), argsOnly = true, remap = false)
    private Identifier changeFontOnLoad(Identifier id) {
        if (com.tclient.font.FontManager.currentFont != null && !com.tclient.font.FontManager.currentFont.equals("Default")) {
            // Agar T-Client mein font selected hai toh default font ko replace kar do
            if (id.getNamespace().equals("minecraft") && id.getPath().equals("default")) {
                return com.tclient.font.FontManager.getFontIdentifier();
            }
        }
        return id;
    }
}
