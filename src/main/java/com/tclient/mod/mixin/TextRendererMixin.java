package com.tclient.mod.mixin;

import com.tclient.mod.TClientMod;
import com.tclient.mod.features.FontChanger;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

    // Font identifier ko replace karo jab bhi getFontStorage call ho
    @ModifyVariable(
        method = "getFontStorage",
        at = @At("HEAD"),
        argsOnly = true
    )
    private Identifier modifyFontId(Identifier original) {
        if (!FontChanger.isFontChangerActive()) return original;

        // Sirf default font ko replace karo — special fonts ko mat chhuo
        if (original.equals(Identifier.of("minecraft", "default"))
         || original.equals(Identifier.of("minecraft", "uniform"))) {
            return FontChanger.getCurrentFontIdentifier();
        }
        return original;
    }
}
