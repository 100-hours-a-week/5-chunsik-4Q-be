package org.chunsik.pq.gallery.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
    public static int timeUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime midnight = now.toLocalDate().atStartOfDay().plusDays(1);

        return (int) ChronoUnit.SECONDS.between(now, midnight);
    }
}