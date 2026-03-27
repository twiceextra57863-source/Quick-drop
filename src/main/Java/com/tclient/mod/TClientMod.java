package com.tclient.mod;

import com.tclient.mod.config.TClientConfig;
import com.tclient.mod.features.FontChanger;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TClientMod implements ModInitializer {

    public static final String MOD_ID = "tclient";
    public static final String MOD_NAME = "T Client";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static TClientConfig CONFIG;

    @Override
    public void onInitialize() {
        LOGGER.info("[T Client] Initializing...");
        CONFIG = TClientConfig.load();
        FontChanger.init();
        LOGGER.info("[T Client] Loaded Successfully!");
    }
}
