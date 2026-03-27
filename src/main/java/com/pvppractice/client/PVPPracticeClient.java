package com.pvppractice.client;

import com.pvppractice.PVPPracticeMod;
import com.pvppractice.client.render.HeartIndicatorRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class PVPPracticeClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        PVPPracticeMod.LOGGER.info("PVP Practice Client Initialized!");
        
        // Register HUD rendering
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            HeartIndicatorRenderer.render(drawContext);
        });
    }
}
