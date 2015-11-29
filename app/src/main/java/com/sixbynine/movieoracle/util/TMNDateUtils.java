package com.sixbynine.movieoracle.util;

import java.util.Calendar;

public final class TMNDateUtils {

    private TMNDateUtils() {}

    public static Calendar getLastTuesday() {
        Calendar cal = Calendar.getInstance();
        int daysSinceTuesday = cal.get(Calendar.DAY_OF_WEEK) - Calendar.TUESDAY;
        cal.add(Calendar.DAY_OF_WEEK, -Math.abs(daysSinceTuesday));
        return cal;
    }
}
