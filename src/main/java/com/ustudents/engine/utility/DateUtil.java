package com.ustudents.engine.utility;

public class DateUtil {
    public static String secondsToText(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;

        if (mins == 0 && secs == 0) {
            return "0s";
        }

        return (mins > 0 ? mins  + "min" : "") + (secs > 0 ? secs + "s" : "");
    }
}
