package pk.ajneb97.util;

import org.bukkit.configuration.file.FileConfiguration;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtils {

    private static final String HOUR_FORMAT = "%02d:%02d:%02d";
    private static final String MINUTE_FORMAT = "%02d:%02d";

    private static String days;
    private static String day;
    private static String hours;
    private static String hour;
    private static String minutes;
    private static String minute;
    private static String seconds;
    private static String second;
    private static String ready;

    public void setStrings(FileConfiguration messages) {
        days = messages.getString("cooldown.days");
        day = messages.getString("cooldown.day");
        hours = messages.getString("cooldown.hours");
        hour = messages.getString("cooldown.hour");
        minutes = messages.getString("cooldown.minutes");
        minute = messages.getString("cooldown.minute");
        seconds = messages.getString("cooldown.seconds");
        second = messages.getString("cooldown.second");
        ready = messages.getString("cooldown.ready");
    }

    public static String millisToTimer(long millis) {
        long seconds = millis / 1000L;

        if (seconds > 3600L) {
            return String.format(HOUR_FORMAT, seconds / 3600L, seconds % 3600L / 60L, seconds % 60L);
        }

        return String.format(MINUTE_FORMAT, seconds / 60L, seconds % 60L);
    }

    public static String millisToPlainSeconds(long millis) {
        return String.valueOf(millis / 1000L);
    }

    /**
     * Return the amount of seconds from milliseconds.
     * Note: We explicitly use 1000.0F (float) instead of 1000L (long).
     *
     * @param millis the amount of time in milliseconds
     * @return the seconds
     */
    public static String millisToSeconds(long millis) {
        return new DecimalFormat("#0.0").format(millis / 1000.0F);
    }

    public static String millisToRoundedTime(long millis) {
        millis += 1L;

        long roundedSeconds = millis / 1000L;
        long roundedMinutes = roundedSeconds / 60L;
        long roundedHours = roundedMinutes / 60L;
        long roundedDays = roundedHours / 24L;
//        long weeks = days / 7L;
//        long months = weeks / 4L;
//        long years = months / 12L;
//
//        if (years > 0) {
//            return years + " year" + (years == 1 ? "" : "s");
//        } else if (months > 0) {
//            return months + " month" + (months == 1 ? "" : "s");
//        } else if (weeks > 0) {
//            return weeks + " week" + (weeks == 1 ? "" : "s");
//        } else
        if (roundedDays > 0) {
            return roundedDays + (roundedDays == 1 ? day : days);
        } else if (roundedHours > 0) {
            return roundedHours + (roundedHours == 1 ? hour : hours);
        } else if (roundedMinutes > 0) {
            return roundedMinutes + (roundedMinutes == 1 ? minute : minutes);
        } else {
            return roundedSeconds + (roundedSeconds == 1 ? second : seconds);
        }
    }

    public static long parseTime(String time) {
        long totalTime = 0L;
        boolean found = false;
        Matcher matcher = Pattern.compile("\\d+\\D+").matcher(time);

        while (matcher.find()) {
            String s = matcher.group();
            Long value = Long.parseLong(s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
            String type = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1];

            switch (type) {
                case "s":
                    totalTime += value;
                    found = true;
                    break;
                case "m":
                    totalTime += value * 60;
                    found = true;
                    break;
                case "h":
                    totalTime += value * 60 * 60;
                    found = true;
                    break;
                case "d":
                    totalTime += value * 60 * 60 * 24;
                    found = true;
                    break;
                case "w":
                    totalTime += value * 60 * 60 * 24 * 7;
                    found = true;
                    break;
                case "M":
                    totalTime += value * 60 * 60 * 24 * 30;
                    found = true;
                    break;
                case "y":
                    totalTime += value * 60 * 60 * 24 * 365;
                    found = true;
                    break;
            }
        }

        return !found ? -1 : totalTime * 1000;
    }

}