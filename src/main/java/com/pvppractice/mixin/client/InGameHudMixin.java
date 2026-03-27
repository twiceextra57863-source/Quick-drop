package com.pvppractice.mixin.client;

import com.pvppractice.client.render.HeartIndicatorRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    
    @Inject(method = "render", at = @At(value = "RETURN"))
    private void onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        // This ensures our heart indicators render after the vanilla HUD
        // The HeartIndicatorRenderer is already registered via HUD callback,
        // but this provides an additional injection point if needed
    }
    
    @Inject(method = "renderStatusBars", at = @At(value = "RETURN"))
    private void onRenderStatusBars(DrawContext context, CallbackInfo ci) {
        // Custom status bar modifications
    }
    
    @Inject(method = "renderHealthBar", at = @At(value = "HEAD"), cancellable = true)
    private void onRenderHealthBar(DrawContext context, int x, int y, int width, int height, float health, int color, CallbackInfo ci) {
        // Custom health bar rendering can be implemented here if needed
        // This allows us to override vanilla health bars
    }
}
