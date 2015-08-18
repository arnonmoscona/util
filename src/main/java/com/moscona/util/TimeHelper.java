/*
 * Copyright (c) 2015. Arnon Moscona
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.moscona.util;

import com.moscona.exceptions.InvalidArgumentException;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;


import org.apache.commons.lang3.StringUtils;


/**
 * Created: Mar 18, 2010 1:59:27 PM
 * By: Arnon Moscona
 * A utility class to help with time calculation and abstract the complicated java time classes
 */
@SuppressWarnings({"UtilityClass"})
public class TimeHelper {
    public static final TimeZone INTERNAL_TIMEZONE = TimeZone.getTimeZone("US/Eastern");
    /*
        private static final DateTimeZone INTERNAL_DATETIMEZONE = DateTimeZone.forTimeZone(INTERNAL_TIMEZONE);
    */
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS z";
    public static final long MILLIS_PER_HOUR = 3600000;
    public static final long MILLIS_PER_MINUTE = 60000;

    // in normal mode the time helper tracks real time east coast time
    public static final int MODE_NORMAL = 0;
    // in simulation mode the time helper tracks the currently running simulator (assumed that there is no more than one)
    public static final int MODE_SIMULATION = 1;

    private static SimpleDateFormat dateFormat = null;
    private static long nextMidNightRecalculation = -1; // millis of the next time midnight should be recalculated
    private static long lastMidnightMillis = -1;
    private static final int RECALCULATION_SAFETY_WINDOW_MILLIS = 10000;  // we will continue recalculating last midnight as long as we didn't pass theis time beyond the next midnight...
    private static ITimeHelperDelegate delegate = new RealTimeHelperDelegate(); // used to support simulation and mode switching
    private static boolean inSimulationMode = false;
    private static AtomicInteger ntpOffset=new AtomicInteger(0);
    private static Pattern timestampPattern = null;
    public static final int TWENTY_FOUR_HOURS = 24*3600*1000;
    public static final int ONE_MINUTE = 60000;
    private static boolean debug = false;

    private TimeHelper() {
    }

    public static void setDebug(boolean newValue) {
        debug = newValue;
    }

    private static Pattern getTimestampPattern() {
        if (timestampPattern==null) {
            String regex = "^([0-2])?[0-9]:[0-5][0-9](:[0-5][0-9])?$";
            timestampPattern = Pattern.compile(regex);
        }
        return timestampPattern;
    }

    /**
     * Converts a Calendar time object (which includes a time zone) to an internal server timestamp.
     * The internal timestamp is relative to today in terms of the US/Eastern time zone and does not include
     * date information, as the server only works on real-time data, and only during trading hours, and can safely
     * assume that the date is fixed at any given time.
     * @param cal - the time object
     * @return an offset in milliseconds from midnight US/Eastern time
     * @throws com.moscona.exceptions.InvalidArgumentException - if the difference in millis exceeds the capacity of int
     */
    public static int convertToInternalTs(Calendar cal) throws InvalidArgumentException {
        long millis = cal.getTimeInMillis();
        return convertToInternalTs(millis);
    }

    /**
     * A variant of the method that allows you to ignore the date
     * @param cal a Calendar object
     * @param ignoreDate use the current date regardless of the one implied by the Calendar object
     * @return a system-internal timestamp
     * @throws InvalidArgumentException if thrown from dependencies
     */
    public static int convertToInternalTs(Calendar cal, boolean ignoreDate) throws InvalidArgumentException {
        if (!ignoreDate) {
            return convertToInternalTs(cal);
        }
        // this is the case where we want a timestamp relative to the given date's midnight, not relative to last midnight
        Calendar date = clone(cal);
        clearTime(date);
        return (int)(cal.getTimeInMillis()-date.getTimeInMillis());
    }

    public static int convertToInternalTs(long tsMillis) throws InvalidArgumentException {
        long baseMillis = lastMidnightInMillis();
        long diff = tsMillis - baseMillis;
        if (diff > Integer.MAX_VALUE) {
            throw new InvalidArgumentException("Cannot convert calendar object to internal timestamp. Offset too large to fit in an integer");
        }
        return (int)diff;
    }

    public static Calendar convertToCalendar(int ts) {
        Calendar retval = Calendar.getInstance(INTERNAL_TIMEZONE);
        long millis = lastMidnightInMillis() + ts;
        retval.setTimeInMillis(millis);
        return retval;
    }

    public static String convertToString(int ts) {
        Calendar cal = convertToCalendar(ts);
        return getDateFormat().format(cal.getTime(),new StringBuffer(),new FieldPosition(DateFormat.YEAR_FIELD)).toString();
    }

    @SuppressWarnings({"NonThreadSafeLazyInitialization"})
    private static SimpleDateFormat getDateFormat() {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(TIME_FORMAT);
            dateFormat.setTimeZone(INTERNAL_TIMEZONE);
        }
        return dateFormat;
    }

    /**
     * Produces an instance of a Calendar in the internal timezone
     * @return Calendar.getInstance(INTERNAL_TIMEZONE);
     */
    public static Calendar getCalendarInstance() {
        return Calendar.getInstance(INTERNAL_TIMEZONE);
    }

    public static int now() {
        int retval;
        if (inSimulationMode) {
            retval = delegate.now(lastMidnightInMillis());
        }
        else {
            retval = delegate.now(lastMidnightInMillis()-ntpOffset.get());
            if (retval>TWENTY_FOUR_HOURS) {
                recalculateLastMidnight();
                retval = delegate.now(lastMidnightInMillis()-ntpOffset.get());
            }
        }


        return retval;
    }

    public static Calendar today() {
        return delegate.today();
    }

    public static long lastMidnightInMillis() {
        if(inSimulationMode ||             // always recalculate in simulation as today may have changed
                lastMidnightMillis<0 ||    // not calculated yet
                (nextMidNightRecalculation - System.currentTimeMillis()) < RECALCULATION_SAFETY_WINDOW_MILLIS) {
            recalculateLastMidnight();
        }

        return lastMidnightMillis;
    }

    private static void recalculateLastMidnight() {
        Calendar cal = Calendar.getInstance(INTERNAL_TIMEZONE);
        Calendar today = today(); // don't rely on the delegate to get the TZ right
        // set the date to whatever the delegate says...
        cal.set(Calendar.YEAR, today.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, today.get(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));

        // now set to midnight
        long now = cal.getTimeInMillis();
        cal.set(today.get(Calendar.YEAR),today.get(Calendar.MONTH),today.get(Calendar.DAY_OF_MONTH),0,0,0);

        cal.set(Calendar.MILLISECOND,0);
        lastMidnightMillis = cal.getTimeInMillis();
        nextMidNightRecalculation = System.currentTimeMillis() + (now - lastMidnightMillis);
    }

    /**
     * What mode is time in?
     * @return MODE_NORMAL or MODE_SIMULATION
     */
    public static int getMode() {
        if (delegate instanceof RealTimeHelperDelegate) {
            return MODE_NORMAL;
        }
        else {
            return MODE_SIMULATION;
        }
    }

    public static void switchToSimulationMode(ITimeHelperDelegate newDelegate) {
        delegate = newDelegate;
        inSimulationMode = true;
    }

    public static void switchToNormalMode() {
        delegate = new RealTimeHelperDelegate();
        // recalculate the midnight time before turning simulation off
        lastMidnightInMillis();
        inSimulationMode = false;
    }

    /**
     * Converts strings of the form hh:mm and hh:mm::ss to internal server timestamps
     * @param timeStr the string to convert
     * @return an internal server timestamp (millis) corresponding to the value of the string
     * @throws com.moscona.exceptions.InvalidArgumentException if any problems are found with the string format
     */
    public static int timeStringToMillis(String timeStr) throws InvalidArgumentException {
        if (timeStr == null) {
            throw new InvalidArgumentException("Time string may not be null");
        }

        String parseString = StringUtils.trim(timeStr);
        if (parseString.endsWith("t")) {
            parseString = StringUtils.chop(parseString);
        }

        Pattern pattern = getTimestampPattern();
        if (!pattern.matcher(parseString).matches()) {
            throw new InvalidArgumentException("The argument string \""+parseString+"\" does not match the regular expression /"+pattern+"/");
        }

        String[] parts = parseString.split(":");


        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        int second = 0;
        if (parts.length == 3) {
            second = Integer.parseInt(parts[2]);
        }

        if (hour > 23) {
            throw new InvalidArgumentException("Hour may not exceed 23");
        }


        return (hour*3600 + minute*60 + second)*1000;
    }

    public static long systemWallClock() {
        return System.currentTimeMillis();
    }

    public static int getNtpOffset() {
        return ntpOffset.get();
    }

    public static void setNtpOffset(int ntpOffset) {
        TimeHelper.ntpOffset.set(ntpOffset);
    }

    /**
     * Given a string in the format "HH:mm" produces the nearest future local time represented by the string (today or tomorrow)
     * @param timeStr a string in the format "HH:mm"
     * @return a Date object for the nearest future local time represented by the argument
     * @throws InvalidArgumentException if any problems are found with the string format
     */
    public static Date nextFutureTimeInLocalTime(String timeStr) throws InvalidArgumentException {
        int hour = 0;
        int minutes = 0;
        try {
            String[] fields = timeStr.split(":");
            hour = Integer.parseInt(fields[0].trim());
            ensureIsValidHour(hour);
            minutes = Integer.parseInt(fields[1].trim());
            ensureIsValidMinute(minutes);
        }
        catch (Exception e) {
            throw new InvalidArgumentException("Could not parse the string \""+timeStr+"\" into hours and minutes. Required to be in the format: \"HH:mm\" :"+e,e);
        }



        Calendar now = Calendar.getInstance();
        int nowHour = now.get(Calendar.HOUR_OF_DAY);
        int nowMinutes = now.get(Calendar.MINUTE);
        int nowSeconds = now.get(Calendar.SECOND);

        int oneDayOffset = (hour>nowHour) ? 0: TWENTY_FOUR_HOURS; // if the hour is passed already the schedule will start tomorrow

        int offsetFromNow = ((hour - nowHour) * 3600 + (minutes - nowMinutes) * 60 - nowSeconds) * 1000 + oneDayOffset;
        return new Date(now.getTimeInMillis() + offsetFromNow);
    }

    public static Date nextFutureTimeInInternalTimeZone(String timeStr) throws InvalidArgumentException {
        Date inLocalTime = nextFutureTimeInLocalTime(timeStr);
        long now = now() + lastMidnightInMillis();
        long nowLocalTime = new Date().getTime();
        long diff = now - nowLocalTime;
        return new Date(inLocalTime.getTime()+diff);
    }

    /**
     * Checks that an integer representing an hour is actually within range
     * @param hour the hour value to verify
     * @throws InvalidArgumentException if it is not valid
     */
    public static void ensureIsValidHour(int hour) throws InvalidArgumentException {
        if (hour < 0 || hour>23) {
            throw new InvalidArgumentException("The hour "+hour+" is not in the range of 0..23");
        }
    }

    /**
     * Checks that an integer representing a minute is actually within range
     * @param minute the minute value to verify
     * @throws InvalidArgumentException if it is not valid
     */
    public static void ensureIsValidMinute(int minute) throws InvalidArgumentException {
        if (minute < 0 || minute>59) {
            throw new InvalidArgumentException("The minute "+minute+" is not in the range of 0..59");
        }
    }

    public static String toString(Calendar date) {
        StringBuilder s = new StringBuilder();
        s.append(date.get(Calendar.YEAR)).append("-");
        s.append(StringHelper.toPaddedString(date.get(Calendar.MONTH)+1)).append("-");
        s.append(StringHelper.toPaddedString(date.get(Calendar.DAY_OF_MONTH))).append(" ");
        s.append(StringHelper.toPaddedString(date.get(Calendar.HOUR_OF_DAY))).append(":");
        s.append(StringHelper.toPaddedString(date.get(Calendar.MINUTE))).append(":");
        s.append(StringHelper.toPaddedString(date.get(Calendar.SECOND))).append(".");
        s.append(date.get(Calendar.MILLISECOND)).append(" ");
        s.append(date.getTimeZone().getDisplayName());

        return s.toString();
    }

    /**
     * makes a string in the format "yyyy-MM-dd hh:mm:ss"
     * @param date the date value to convert
     * @return the formatted string
     */
    public static String toSecondString(Calendar date) {
        StringBuilder s = new StringBuilder();
        s.append(date.get(Calendar.YEAR)).append("-");
        s.append(date.get(Calendar.MONTH)+1).append("-");
        s.append(date.get(Calendar.DAY_OF_MONTH)).append(" ");
        s.append(date.get(Calendar.HOUR_OF_DAY)).append(":");
        s.append(date.get(Calendar.MINUTE)).append(":");
        s.append(date.get(Calendar.SECOND));

        return s.toString();
    }

    /**
     * makes a string in the format "yyyy-MM-dd"
     * @param date the date value to format
     * @return the formatted string
     */
    public static String toDayString(Calendar date) {
        StringBuilder s = new StringBuilder();
        s.append(StringHelper.toPaddedString(date.get(Calendar.YEAR))).append("-");
        s.append(StringHelper.toPaddedString(date.get(Calendar.MONTH)+1)).append("-");
        s.append(StringHelper.toPaddedString(date.get(Calendar.DAY_OF_MONTH)));

        return s.toString();
    }

    /**
     * makes a string in the format "MM/dd/yyyy"
     * @param date the date value to format
     * @return the formatted string
     */
    public static String toDayStringMmDdYyyy(Calendar date) {
        StringBuilder s = new StringBuilder();
        s.append(StringHelper.toPaddedString(date.get(Calendar.MONTH)+1)).append("/");
        s.append(StringHelper.toPaddedString(date.get(Calendar.DAY_OF_MONTH))).append("/");
        s.append(date.get(Calendar.YEAR));

        return s.toString();
    }

    public static String toMmDdYyyyTimeStamp(Calendar date) {
        return toMmDdYyyyTimeStamp(date, true);
    }

    public static String toMmDdYyyyTimeStamp(Calendar date, boolean withMillis) {
        StringBuilder s = new StringBuilder();
        s.append(toDayStringMmDdYyyy(date)).append(" ");
        s.append(StringHelper.toPaddedString(date.get(Calendar.HOUR_OF_DAY))).append(":");
        s.append(StringHelper.toPaddedString(date.get(Calendar.MINUTE))).append(":");
        s.append(StringHelper.toPaddedString(date.get(Calendar.SECOND)));
        if (withMillis) {
            s.append(".").append(date.get(Calendar.MILLISECOND));
        }
        return s.toString();
    }

    public static String nowAsString() {
        return toString(nowAsCalendar());
    }

    public static String todayAsPathFragment() {
        Calendar today = today();
        String month = StringUtils.leftPad(Integer.toString(today.get(Calendar.MONTH) + 1),2,"0");
        String day = StringUtils.leftPad(Integer.toString(today.get(Calendar.DAY_OF_MONTH)),2,"0");
        return today.get(Calendar.YEAR) + "/" + month + "/" + day;
    }

    public static int timeStampRelativeToMidnight(Calendar cal) {
        Calendar midnight = (Calendar)cal.clone();
        clearTime(midnight);
        return (int)(cal.getTimeInMillis() - midnight.getTimeInMillis());
    }

    public static Calendar clearTime(Calendar midnight) {
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);
        return midnight;
    }

    public static int timeStampRelativeToMidnight(long millis) {
        Calendar cal = Calendar.getInstance(INTERNAL_TIMEZONE);
        cal.setTimeInMillis(millis);
        return timeStampRelativeToMidnight(cal);
    }

    /**
     * Creates a Calendar in the internal timezone for today using the provided time expression
     * @param expr a string in the format "hh:mm:ss" representing a time of day
     * @param defaultExpr null or another string in the same format that is used if the expr string is blank or null . If both are blank the return value will be null too.
     * @return a Calendar object with the passed date (or default)
     * @throws InvalidArgumentException if parsing failed
     */
    public static Calendar parseTimeOfDayExpression(String expr, String defaultExpr) throws InvalidArgumentException {
        return parseTimeOfDayExpression(expr,defaultExpr,":");
    }

    /**
     * Creates a Calendar in the internal timezone for today using the provided time expression
     * @param expr a string in the format "hh:mm:ss" representing a time of day (or the delimiter, really)
     * @param defaultExpr null or another string in the same format that is used if the expr string is blank or null . If both are blank the return value will be null too.
     * @param delimiter the delimiter to use (default ":")
     * @return the parsed Calendar value
     * @throws InvalidArgumentException if parsing failed
     */
    public static Calendar parseTimeOfDayExpression(String expr, String defaultExpr, String delimiter) throws InvalidArgumentException {
        String actualDelimiter = delimiter;
        if (delimiter==null) {
            actualDelimiter = ":";
        }

        String expression = expr;
        if (StringUtils.isBlank(expr)) {
            expression = defaultExpr;
        }

        if (StringUtils.isBlank(expr)) {
            return null;
        }

        try {
            String[] parts = expression.trim().split(actualDelimiter);
            //StringUtils.split(expression.trim(), actualDelimiter); // FIXME review (might change sematics)
            int hours = Integer.parseInt(parts[0].trim());
            int minutes = Integer.parseInt(parts[1].trim());
            int seconds = 0;
            if (parts.length>=3) {
                seconds = Integer.parseInt(parts[2].trim());
            }

            Calendar retval = Calendar.getInstance(INTERNAL_TIMEZONE);
            retval.set(Calendar.HOUR_OF_DAY, hours);
            retval.set(Calendar.MINUTE, minutes);
            retval.set(Calendar.SECOND, seconds);
            retval.set(Calendar.MILLISECOND, 0);

            return retval;
        }
        catch (Exception e) {
            throw new InvalidArgumentException("failed to parse");
        }
    }

    /**
     * Parses strings of the form '2010-07-08 09:31:00'
     * @param timestampString the string to parse
     * @return the parsed Calendar value.
     * @throws InvalidArgumentException if the parsing fails
     */
    public static Calendar parse(String timestampString) throws InvalidArgumentException {
        return parse(timestampString,"-");
    }

    /*
        private static Map<String, DateTimeFormatter> parsers = new HashMap<String, DateTimeFormatter>();

    */
    public static Calendar parse(String timestampString, String dateDelimiter) throws InvalidArgumentException { // FIXME this is iqfeed specific
        if (timestampString==null) {
            return null;
        }

        String[] parts = timestampString.trim().split(" +"); // FIXME review
        //StringUtils.split(timestampString, " ");
        Calendar retval = nowAsCalendar();
        clearTime(retval);
        if (parts.length>=2) {
            retval = parseTimeOfDayExpression(parts[1],parts[1]);
        }

        String[] dateParts = parts[0].split(dateDelimiter);
        retval.set(Calendar.DAY_OF_MONTH,Integer.parseInt(dateParts[2]));
        retval.set(Calendar.MONTH,Integer.parseInt(dateParts[1])-1);
        retval.set(Calendar.YEAR,Integer.parseInt(dateParts[0]));

        return retval;

/*
        try {
            if(timestampString.indexOf(' ') >= 0) {
                return getBuilder(timestampString, dateDelimiter, dateDelimiter, true);
            } else {
                return getBuilder(timestampString, dateDelimiter, "d" + dateDelimiter, false);
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidArgumentException("Could not parse iso date time from " + timestampString, e);
        }
*/

/*
        String[] parts =
                StringUtils.split(timestampString, " ");
                // timestampString.trim().split(" +");
        Calendar retval;
        if (parts.length>=2) {
        } else {
            retval = nowAsCalendar();
            clearTime(retval);
            String[] dateParts =
                    StringUtils.split(parts[0], dateDelimiter);
            // parts[0].split(dateDelimiter);
            retval.set(Calendar.DAY_OF_MONTH,Integer.parseInt(dateParts[2]));
            retval.set(Calendar.MONTH,Integer.parseInt(dateParts[1])-1);
            retval.set(Calendar.YEAR,Integer.parseInt(dateParts[0]));

                return retval;
        }
*/
    }

/*
    private static Calendar getBuilder(String timestampString, String dateDelimiter, String key, boolean appendHourMinuteSecond) {
        DateTimeFormatter parser = parsers.get(key);
        if(parser == null) {
            DateTimeFormatterBuilder dayBuilder = getDayBuilder(dateDelimiter);
            DateTimeFormatterBuilder builder = appendHourMinuteSecond ? dayBuilder.appendLiteral(" ").append(ISODateTimeFormat.hourMinuteSecond()) : dayBuilder;
            parser = builder.toFormatter();
            parsers.put(key, parser);
        }
        return parser.parseDateTime(timestampString).toDateTime(INTERNAL_DATETIMEZONE).toCalendar(Locale.getDefault());
    }

    private static DateTimeFormatterBuilder getDayBuilder(String dateDelimiter) {
        return new DateTimeFormatterBuilder()
                .append(ISODateTimeFormat.year()).appendLiteral(dateDelimiter)
                .appendMonthOfYear(2).appendLiteral(dateDelimiter)
                .appendDayOfMonth(2);
    }
*/

    /**
     * Parses a string of the format d/m/yyyy or dd/mm/yyyy
     * @param dateStr the string to parse
     * @return the parsed Calendar result
     * @throws com.moscona.exceptions.InvalidArgumentException if parsing fails
     */
    public static Calendar parseMmDdYyyyDate(String dateStr) throws InvalidArgumentException {
        String [] parts = dateStr.split("/");
        if (parts.length!=3) {
            throw new InvalidArgumentException("The string \""+dateStr+"\" does not look like a date dd/mm/yyyy");
        }
        String newString = parts[2]+"-"+parts[0]+"-"+parts[1];
        Calendar cal = parse(newString);
        clearTime(cal);
        return cal;
    }

    /**
     * Parses a timestamp of the format mm/dd/yyyy hh:MM:ss.milis
     * @param dateStr the string to parse
     * @return the parsed Calendar object
     * @throws InvalidArgumentException if the parsing fails
     */
    public static Calendar parseMmDdYyyyTimeStamp(String dateStr) throws InvalidArgumentException {
        String message = "Invalid format of \"" + dateStr + "\" expected mm/dd/yyyy hh:MM:ss.milis";

        String[] parts = dateStr.trim().split(" +"); // FIXME review
        //StringUtils.split(dateStr.trim(), ' ');
        String timePart = null;
        Calendar retval = parseMmDdYyyyDate(parts[0]);

        if (parts.length > 2) {
            throw new InvalidArgumentException(message);
        }
        if (parts.length == 2) {
            timePart = parts[1];
        }
        else {
            // only date - no time
            return retval;
        }

        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        int millis = 0;

        try {
            if (timePart.contains(".")) {
                String[] split = timePart.split("\\.");
                millis = Integer.parseInt(split[1]);
                timePart = split[0];
            }

            String[] timeParts = timePart.split(":");
            hours = Integer.parseInt(timeParts[0]);
            if (timeParts.length > 1) {
                minutes = Integer.parseInt(timeParts[1]);
            }
            if (timeParts.length > 2) {
                seconds = Integer.parseInt(timeParts[2]);
            }
        }
        catch (Exception e) {
            throw new InvalidArgumentException(message, e);
        }

        retval.set(Calendar.HOUR_OF_DAY, hours);
        retval.set(Calendar.MINUTE, minutes);
        retval.set(Calendar.SECOND, seconds);
        retval.set(Calendar.MILLISECOND, millis);

        return retval;
    }
    /**
     * Parses a string of the format yyyy/mm/dd hh:MM:ss
     * @param ts the string to parse
     * @return the parsed Calendar object
     * @throws InvalidArgumentException if the parsing fails
     */
    public static Calendar parseMessageConventionTimeStamp(String ts) throws InvalidArgumentException {
        String[] mainParts = ts.trim().split(" +");
        String format = "yyyy/mm/dd hh:MM:ss";
        if (mainParts.length != 2) {
            throw new InvalidArgumentException("Expected a string in the format "+format);
        }
        Calendar retval = parse(ts.trim(), "/");
        return retval;
    }

    /**
     * Compares date of a Calendar to a year/month/day string
     * @param cal the first value to compare
     * @param dateStr a string with a loose format of year/day/month separated by '/' and requiring a 4 digit year
     * @return true if they are the same date.
     */
    public static boolean isSameDay(Calendar cal, String dateStr) {
        String[] parts = dateStr.split("[/-]");
        return cal.get(Calendar.YEAR) == Integer.parseInt(parts[0].trim()) &&
                cal.get(Calendar.MONTH) + 1 == Integer.parseInt(parts[1].trim()) &&
                cal.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(parts[2].trim());
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Determines whether the provided calendar is the same day as now
     * @param cal the value to compare to today
     * @return true if now is the same day as the provided calendar
     */
    public static boolean isSameDay(Calendar cal) {
        Calendar now = nowAsCalendar();
        return now.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                now.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
                now.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH);
    }

    public static Calendar nowAsCalendar() {
        return convertToCalendar(now());
    }

    /**
     * Parses a simple time string with no milliseconds like "09:30:00"
     * @param timeString the string to break up
     * @return an array of three integers representing te parts [hour,minute,second]
     * @throws InvalidArgumentException if there are parsing problems
     */
    public static int[] toTimeParts(String timeString) throws InvalidArgumentException {
        int[] retval = new int[3];
        int i=0;
        for (String part : timeString.split(":")) {
            if (i>2) {
                throw new InvalidArgumentException("Time string parsing error for \""+timeString+"\": too many parts (should be 2 or 3)");
            }
            try {
                retval[i++] = Integer.parseInt(part);
            }
            catch (NumberFormatException e) {
                throw new InvalidArgumentException("Time string parsing error for \""+timeString+"\": could not parse \""+part+"\" as integer",e);
            }
        }
        if (i<2) {
            throw new InvalidArgumentException("Time string parsing error for \""+timeString+"\": too few parts (should be 2 or 3)");
        }
        if (i==2) {
            // no seconds were provided
            retval[2] = 0;
        }

        validateHour(retval[0]);
        validateMinute(retval[1]);
        validateSecond(retval[2]);

        return retval;
    }

    public static void validateDayOfMonth(int i) throws InvalidArgumentException {
        validateRange(i,1,31,"day of month");
    }

    public static void validateMonth(int i) throws InvalidArgumentException {
        validateRange(i,1,12,"month");
    }

    public static void validateYear(int i) throws InvalidArgumentException {
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        validateRange(i,thisYear-40,thisYear+10,"year");
    }

    public static void validateHour(int i) throws InvalidArgumentException {
        validateRange(i,0,23,"hour");
    }

    public static void validateMinute(int i) throws InvalidArgumentException {
        validateRange(i,0,59,"minute");
    }

    public static void validateSecond(int i) throws InvalidArgumentException {
        validateRange(i,0,59,"second");
    }

    private static void validateRange(int i, int from, int to, String name) throws InvalidArgumentException {
        if (i<from || i>to) {
            throw new InvalidArgumentException("Value out of range for "+name+": "+i+" must be in ["+from+".."+to+"]");
        }
    }

    public static void adjustTime(Calendar cal, String timeString) throws InvalidArgumentException {
        int[] parts = toTimeParts(timeString);
        cal.set(Calendar.HOUR_OF_DAY, parts[0]);
        cal.set(Calendar.MINUTE, parts[1]);
        cal.set(Calendar.SECOND, parts[2]);
    }

    /**
     * Makes a time string of the format hh:mm:ss (no millis)
     * @param cal the value to format
     * @return the formatted string
     */
    public static String toTimeString(Calendar cal) {
        StringBuilder s = new StringBuilder();
        s.append(StringHelper.toPaddedString(cal.get(Calendar.HOUR_OF_DAY))).append(":");
        s.append(StringHelper.toPaddedString(cal.get(Calendar.MINUTE))).append(":");
        s.append(StringHelper.toPaddedString(cal.get(Calendar.SECOND)));
        return s.toString();
    }

    /**
     * The difference in days between the two calendars
     * @param lValue one date
     * @param rValue another date
     * @return the difference in days
     */
    public static int daysDiff(Calendar lValue, Calendar rValue) {
        long diffMillis = lValue.getTimeInMillis() - rValue.getTimeInMillis();
        return (int)(diffMillis / (24*3600*1000));
    }

    /**
     * Checks whether a date is within a range of dates (inclusive)
     * @param timestamp the value to test
     * @param min the lower bound of the range
     * @param max the upper bound of the range
     * @return true if the tested value is within the range
     */
    public static boolean isDayWithinRange(Calendar timestamp, Calendar min, Calendar max) {
        long testValue = clearTime(clone(timestamp)).getTimeInMillis();
        return testValue >= clearTime(clone(min)).getTimeInMillis() &&
                testValue <= clearTime(clone(max)).getTimeInMillis();
    }

    public static Calendar clone(Calendar date) {
        if (date==null) {
            return null;
        }
        return (Calendar)date.clone();
    }

    public static String prettyTimeSpanSec(long spanMillis) {
        long hours = spanMillis/MILLIS_PER_HOUR;
        spanMillis -= hours*MILLIS_PER_HOUR;
        long minutes = spanMillis/ MILLIS_PER_MINUTE;
        spanMillis -= minutes*MILLIS_PER_MINUTE;
        long seconds = spanMillis/1000;
        spanMillis -= seconds*1000;

        StringBuilder retval = new StringBuilder();
        retval.append(StringHelper.toPaddedString(hours)).append(":");
        retval.append(StringHelper.toPaddedString(minutes)).append(":");
        retval.append(StringHelper.toPaddedString(seconds));
        return retval.toString();
    }

    public static void adjustLocalTimeZone(Calendar current, boolean up) {
        int offset = Calendar.getInstance().getTimeZone().getRawOffset() - INTERNAL_TIMEZONE.getRawOffset();
        if (up) {
            current.add(Calendar.MILLISECOND, -offset);
        }
        else {
            current.add(Calendar.MILLISECOND, offset);
        }
    }

    public static String millisCountToEnglish(long millis) {
        long dayInMillis = 24l*3600*1000;
        long days = millis / dayInMillis;
        long remainder = millis - dayInMillis*days;
        int hoursInMillis = 3600000;
        long hours = remainder / hoursInMillis;
        remainder -= hours * hoursInMillis;
        int minutesInMillis = 60000;
        long minutes = remainder / minutesInMillis;
        remainder -= minutesInMillis*minutes;
        long seconds = remainder / 1000;
        ArrayList<String> result = new ArrayList<String>();
        if (days>0) {
            result.add(Long.toString(days)+" days");
        }
        if (hours>0) {
            result.add(Long.toString(hours)+" hrs");
        }
        if (minutes>0) {
            result.add(Long.toString(minutes)+" min");
        }
        if (seconds>0) {
            result.add(Long.toString(seconds)+" sec");
        }
        return StringUtils.join(result," ");
    }
}

