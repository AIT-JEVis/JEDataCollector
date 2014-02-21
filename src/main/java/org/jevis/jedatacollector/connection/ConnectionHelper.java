/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.connection;

import java.text.DecimalFormat;
import java.util.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Broder
 */
public class ConnectionHelper {

    public static final String DATE_FROM = "\\*DATE_FROM\\*";
    public static final String DATE_TO = "\\*DATE_TO\\*";
    public static final String DATE_FROM_YEAR = "\\*DATE_FROM_YEAR\\*";
    public static final String DATE_FROM_MONTH = "\\*DATE_FROM_MONTH\\*";
    public static final String DATE_FROM_DAY = "\\*DATE_FROM_DAY\\*";
    public static final String DATE_FROM_HOUR = "\\*DATE_FROM_HOUR\\*";
    public static final String DATE_FROM_MINUTE = "\\*DATE_FROM_MINUTE\\*";
    public static final String DATE_FROM_SECOND = "\\*DATE_FROM_SECOND\\*";
    public static final String DATE_TO_YEAR = "\\*DATE_TO_YEAR\\*";
    public static final String DATE_TO_MONTH = "\\*DATE_TO_MONTH\\*";
    public static final String DATE_TO_DAY = "\\*DATE_TO_DAY\\*";
    public static final String DATE_TO_HOUR = "\\*DATE_TO_HOUR\\*";
    public static final String DATE_TO_MINUTE = "\\*DATE_TO_MINUTE\\*";
    public static final String DATE_TO_SECOND = "\\*DATE_TO_SECOND\\*";
    public static final String DATAPOINT = "\\*DATAPOINT\\*";
    public static final String SYSTEM_TIME = "\\*SYSTEM_TIME\\*";

    public static List<String> parseString(String string, String datapoint, String datePattern, DateTime from, DateTime until) {
//        string = Pattern.quote(string);
        if (string.matches(".*\\{.*\\}.*")) {
            String replace = string.substring(string.indexOf("{"), string.indexOf("}") + 1);
            String tmp = string.substring(string.indexOf("{") + 1, string.indexOf("}"));
            String[] options = null;

            if (tmp.contains(",")) {
                options = tmp.split(",");
            } else if (tmp.contains("-")) {
                String[] minmax = tmp.split("-");

                int digits = Math.max(minmax[0].length(), minmax[1].length());
                int min = Math.min(Integer.parseInt(minmax[0]), Integer.parseInt(minmax[1]));
                int max = Math.max(Integer.parseInt(minmax[0]), Integer.parseInt(minmax[1]));

                options = new String[max - min + 1];
                for (int i = 0; i + min <= max; i++) {
                    options[i] = intToString(i + min, digits);
                }
            }


            List<String> paths = new ArrayList<String>(options.length);
            String path;
            for (String s : options) {
                path = string.replace(replace, s);
                path = parseDateFrom(path, datapoint, datePattern, from);
                path = parseDateTo(path, datapoint, datePattern, until);
                paths.add(path);
            }
            return paths;
        } else {
            string = parseDateFrom(string, datapoint, datePattern, from);
            List<String> returnList = new ArrayList<String>(1);
            returnList.add(parseDateTo(string, datapoint, datePattern, until));
            return returnList;
        }
    }

    private static String intToString(int num, int digits) {
        char[] zeros = new char[digits];
        Arrays.fill(zeros, '0');
        DecimalFormat df = new DecimalFormat(String.valueOf(zeros));
        return df.format(num);
    }

    public static String parseDateFrom(String string, String datapoint, String datePattern, DateTime from) {
        if (datePattern != null) {
//            DateFormat df = new 
//            df.setTimeZone(deviceTimeZone);

//            Calendar c = Calendar.getInstance();
//            c.setTimeZone(TimeZone.getTimeZone("UTC"));
//            c.setTime(date.getTime());
//            c.add(Calendar.SECOND, deviceTimeZone.getRawOffset());

            DateTimeFormatter fmt = DateTimeFormat.forPattern(datePattern);

            string = string.replaceAll(DATE_FROM, fmt.print(from));
            string = string.replaceAll(DATE_FROM_YEAR, from.getYear() + "");
            string = string.replaceAll(DATE_FROM_MONTH, from.getMonthOfYear() + "");
            string = string.replaceAll(DATE_FROM_DAY, from.getDayOfMonth() + "");
            string = string.replaceAll(DATE_FROM_HOUR, from.getHourOfDay() + "");
            string = string.replaceAll(DATE_FROM_MINUTE, from.getMinuteOfHour() + "");
            string = string.replaceAll(DATE_FROM_SECOND, from.getSecondOfMinute() + "");

//            string = string.replaceAll(DATE_FROM, df.format(c.getTime()));
//            string = string.replaceAll(DATE_FROM_YEAR, df.format(c.get(Calendar.YEAR)));
//            string = string.replaceAll(DATE_FROM_MONTH, df.format(c.get(Calendar.MONTH)));
//            string = string.replaceAll(DATE_FROM_DAY, df.format(c.get(Calendar.DAY_OF_MONTH)));
//            string = string.replaceAll(DATE_FROM_HOUR, df.format(c.get(Calendar.HOUR)));
//            string = string.replaceAll(DATE_FROM_MINUTE, df.format(c.get(Calendar.MINUTE)));
//            string = string.replaceAll(DATE_FROM_SECOND, df.format(c.get(Calendar.SECOND)));
        }

        string = string.replaceAll(SYSTEM_TIME, String.valueOf(System.nanoTime()));

        if (datapoint != null) {
            string = string.replaceAll(DATAPOINT, datapoint);
        }

        return string;
    }

    public static String parseDateTo(String string, String datapoint, String datePattern, DateTime until) {
        if (datePattern != null) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern(datePattern);

            string = string.replaceAll(DATE_TO, fmt.print(until));
            string = string.replaceAll(DATE_TO_YEAR, until.getYear() + "");
            string = string.replaceAll(DATE_TO_MONTH, until.getMonthOfYear() + "");
            string = string.replaceAll(DATE_TO_DAY, until.getDayOfMonth() + "");
            string = string.replaceAll(DATE_TO_HOUR, until.getHourOfDay() + "");
            string = string.replaceAll(DATE_TO_MINUTE, until.getMinuteOfHour() + "");
            string = string.replaceAll(DATE_TO_SECOND, until.getSecondOfMinute() + "");
        }

        string = string.replaceAll(SYSTEM_TIME, String.valueOf(System.nanoTime()));

        if (datapoint != null) {
            string = string.replaceAll(DATAPOINT, datapoint);
        }

        return string;
    }
}
