package org.opentripplanner.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Frank Purcell (p u r c e l l f @ t r i m e t . o r g)
 * @date October 20, 2009
 */
public class DateUtils implements DateConstants {

    private static final Logger LOG = LoggerFactory.getLogger(DateUtils.class);

    private static final int SANITY_CHECK_CUTOFF_YEAR = 1000;

    private static final Pattern INT_PATTERN = Pattern.compile("\\d+");

    /**
     * Returns a Date object based on input date & time parameters Defaults to today / now (when
     * date / time are null)
     * 
     * @param date
     * @param time
     */
    static public Date toDate(String date, String time, TimeZone tz) {
        //LOG.debug("JVM default timezone is {}", TimeZone.getDefault());
        LOG.debug("Parsing date {} and time {}", date, time);
        LOG.debug( "using timezone {}", tz);
        Date retVal = new Date();
        if (date != null) {
            Date d = parseDate(date, tz);
            if (d == null) {
                return null; //unparseable date
            }
            Calendar cal = new GregorianCalendar(tz);
            cal.setTime(d);
            boolean timed = false;
            if (time != null) {
                int[] hms = parseTime (time);
                if (hms != null) {
                    cal.set(Calendar.HOUR_OF_DAY, hms[0]);
                    cal.set(Calendar.MINUTE, hms[1]);
                    cal.set(Calendar.SECOND, hms[2]);
                    cal.set(Calendar.MILLISECOND, 0);
                    timed = true;
                }
            }
            if (!timed) {
                //assume t = now
                Calendar today = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, today.get(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, today.get(Calendar.MINUTE));
                cal.set(Calendar.SECOND, today.get(Calendar.SECOND));
                cal.set(Calendar.MILLISECOND, today.get(Calendar.MILLISECOND));
            }
            retVal = cal.getTime();
        } else if (time != null) {
            int[] hms = parseTime (time);
            if (hms != null) {
                Calendar cal = new GregorianCalendar(tz);

                cal.set(Calendar.HOUR_OF_DAY, hms[0]);
                cal.set(Calendar.MINUTE, hms[1]);
                cal.set(Calendar.SECOND, hms[2]);
                cal.set(Calendar.MILLISECOND, 0);
                retVal = cal.getTime();
            }
        }
        LOG.debug( "resulting date is {}", retVal);
        return retVal;
    }

    /**
     * Returns H,M,S
     *
     * @param time a String representing time of day
     *             valid formats: "hh:mm", "hh:mm:ss", "hh:mmAM", "hh:mm:ssAM", "hh:mm:ss AM"
     *             The input also can be a string of an integer value between 0 and 86399; seconds past midnight;
     * @return
     */
    static int[] parseTime(String time) {

        // If the input is a string representation of an integer, consider it seconds past midnight
        if (time != null && INT_PATTERN.matcher(time).matches()) {
            int secondsPastMidnight = getIntegerFromString(time);
            if (secondsPastMidnight > 86399) { // secondsPastMidnight must be less than 24 x 3600 seconds
                return null;
            }
            return new int[]{secondsPastMidnight / 3600, (secondsPastMidnight % 3600) / 60, secondsPastMidnight % 60};
        }

        int[] retVal = null;

        boolean amPm = false;
        int addHours = 0;
        int hour, min, sec = 0;
        try {
            String[] hms = time.toUpperCase().split(":");

            final int end = hms.length - 1;
            if (hms.length > 1) {
                if (hms[end].endsWith("PM") || hms[end].endsWith("AM")) {
                    amPm = true;

                    if (hms[end].contains("PM"))
                        addHours = 12;

                    int suffex = hms[end].lastIndexOf(' ');
                    if (suffex < 1) {
                        suffex = hms[end].lastIndexOf("AM");
                        if (suffex < 1) {
                            suffex = hms[end].lastIndexOf("PM");
                        }
                    }
                    hms[end] = hms[end].substring(0, suffex);
                }
                if (hms.length == 3) {
                    sec = Integer.parseInt(trim(hms[end]));
                }
            }

            int h = Integer.parseInt(trim(hms[0]));
            if (amPm && h == 12)
                h = 0;
            hour = h + addHours;

            min = Integer.parseInt(trim(hms[1]));

            retVal = new int[] {hour, min, sec};
        } catch (Exception ignore) {
            LOG.info("Time '{}' didn't parse", time);
        }

        return retVal;
    }

    // TODO: could be replaced with Apache's DateFormat.parseDate ???
    static public Date parseDate(String input, TimeZone tz) {
        Date retVal = null;
        try {
            String newString = input.trim().replace('_', '.').replace('-', '.').replace(':', '.').replace(
                    '/', '.');
            if (newString != null) {
                List<String> dl = DF_LIST;

                if (newString.length() <= 8) {
                    if (newString.matches("\\d\\d\\d\\d\\d\\d\\d\\d")) {
                        // Accept dates without punctuation if they consist of exactly eight digits.
                        newString = newString.substring(0, 4)
                                + '.' + newString.substring(4, 6)
                                + '.' + newString.substring(6, 8);
                    } else if (!(newString.matches(".*20\\d\\d.*"))) {
                        // if it looks like we have a small date format, ala 11.4.09, then use
                        // another set of compares
                        dl = SMALL_DF_LIST;
                    }
                }

                for (String df : dl) {
                    SimpleDateFormat sdf = new SimpleDateFormat(df);
                    sdf.setTimeZone(tz);
                    retVal = DateUtils.parseDate(sdf, newString);
                    if (retVal != null) {
                        Calendar cal = new GregorianCalendar(tz);
                        cal.setTime(retVal);
                        int year = cal.get(Calendar.YEAR);
                        if (year >= SANITY_CHECK_CUTOFF_YEAR) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not parse " + input);
        }

        return retVal;
    }

    public static int getIntegerFromString(String input) {
        try {
            return new Integer(input);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Converts time in seconds to a <code>String</code> in the format h:mm.
     * 
     * @param time
     *            the time in seconds.
     * @return a <code>String</code> representing the time in the format h:mm
     */
    public static String secondsToString(int time) {
        return secondsToString(time, false);
    }

    public static String secondsToString(int time, boolean withAmPm) {
        if (time < 0)
            return null;

        String minutesStr = secondsToMinutes(time);
        String hoursStr = secondsToHour(time);
        String amPmStr = withAmPm ? getAmPm(time) : "";

        return hoursStr + ":" + minutesStr + amPmStr;
    }

    public static String secondsToHour(int time) {
        if (time < 0)
            return null;
        int hours = (time / 3600) % 12;
        String hoursStr = hours == 0 ? "12" : hours + "";
        return hoursStr;
    }

    public static String secondsToMinutes(int time) {
        if (time < 0)
            return null;

        int minutes = (time / 60) % 60;
        String minutesStr = (minutes < 10 ? "0" : "") + minutes;
        return minutesStr;
    }

    public static String getAmPm(int time) {
        return getAmPm(time, AM, PM);
    }

    public static String getAmPm(int time, String am, String pm) {
        if (time % 86400 >= 43200)
            return pm;
        else
            return am;
    }

    public static String trim(String str) {
        String retVal = str;
        try {
            retVal = str.trim();
            retVal = retVal.replaceAll("%20;", "");
            retVal = retVal.replaceAll("%20", "");
        } catch (Exception ex) {
        }
        return retVal;
    }

    public static String formatDate(String sdfFormat, Date date, TimeZone tz) {
        return formatDate(sdfFormat, date, null, tz);
    }

    public static String formatDate(String sdfFormat, Date date, String defValue, TimeZone tz) {
        String retVal = defValue;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(sdfFormat);
            sdf.setTimeZone(tz);
            retVal = sdf.format(date);
        } catch (Exception e) {
            retVal = defValue;
        }

        return retVal;
    }

    public static Date parseDate(String sdf, String string) {
        return parseDate(new SimpleDateFormat(sdf), string);
    }

    public synchronized static Date parseDate(SimpleDateFormat sdf, String string) {
        sdf.setLenient(false);
        try {
            return sdf.parse(string);
        } catch (Exception e) {
            // log.debug(string + " was not recognized by " + format.toString());
        }
        return null;
    }
    
    public static long absoluteTimeout(double relativeTimeoutSeconds) {
        if (relativeTimeoutSeconds <= 0)
            return Long.MAX_VALUE;
        else
            return System.currentTimeMillis() + (long)(relativeTimeoutSeconds * 1000.0);
    }
}
