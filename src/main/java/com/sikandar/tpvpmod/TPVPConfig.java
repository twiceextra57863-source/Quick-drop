package com.sikandar.tpvpmod;

public class TPVPConfig {
    public static boolean healthIndicatorEnabled = false;
    public static boolean showOnSelf = true;

    public enum HealthStyle {
        PROGRESS_BAR,
        HEARTS,
        HEAD_WITH_NUMBER
    }

    public static HealthStyle style = HealthStyle.PROGRESS_BAR;
}
