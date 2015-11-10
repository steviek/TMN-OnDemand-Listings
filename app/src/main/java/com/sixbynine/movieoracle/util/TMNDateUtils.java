package com.sixbynine.movieoracle.util;

import java.util.Calendar;

public final class TMNDateUtils {

    private TMNDateUtils() {}

    public static Calendar getLastTuesday() {
        Calendar cal = Calendar.getInstance();
        int daysSinceTuesday = cal.get(Calendar.DAY_OF_WEEK) - Calendar.TUESDAY;
        if (daysSinceTuesday > 0) {
            cal.add(Calendar.DAY_OF_WEEK, daysSinceTuesday - 7);
        } else {
            cal.add(Calendar.DAY_OF_WEEK, daysSinceTuesday);
        }
        return cal;
    }
}
