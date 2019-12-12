package com.airhockey.android.common.util;

import android.content.pm.ConfigurationInfo;
import android.os.Build;

public class GLESUtils {
    public static boolean isSupportsGLES2(ConfigurationInfo configurationInfo) {
        return configurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")));
    }
}
