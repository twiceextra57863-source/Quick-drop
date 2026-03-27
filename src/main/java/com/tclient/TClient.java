package com.tclient;

import com.tclient.font.FontManager;
import net.fabricmc.api.ModInitializer;

public class TClient implements ModInitializer {
    @Override
    public void onInitialize() {
        // Folder creation and font list preparation
        FontManager.init();
    }
}
