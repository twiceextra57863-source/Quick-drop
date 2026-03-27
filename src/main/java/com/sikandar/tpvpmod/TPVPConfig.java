package com.sikandar.tpvpmod;

public class TPVPConfig {
    public static boolean healthIndicatorEnabled = false;
    public static boolean showOnSelf = true;           // apna health bhi dikhe ya sirf opponent
    public static HealthStyle style = HealthStyle.PROGRESS_BAR;

    public enum HealthStyle {
        PROGRESS_BAR,
        HEARTS,
        HEAD_WITH_NUMBER
    }
}
