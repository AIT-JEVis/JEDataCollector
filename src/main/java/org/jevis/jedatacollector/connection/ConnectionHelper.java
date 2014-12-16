/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.connection;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.jevis.jedatacollector.data.DataPoint;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

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
    public static final String NEW_DATAPOINT = "${DATAPOINT}";
    public static final String NEW_DATEFROM = "${DATE_FROM}";
    public static final String NEW_DATEUNTIL = "${DATE_TO}";
    public static final String NEW_DATAPOINT_PATTERN = "\\$\\{DATAPOINT\\}";
    public static final String NEW_DATEFROM_PATTERN = "\\$\\{DATE_FROM\\}";
    public static final String NEW_DATEUNTIL_PATTERN = "\\$\\{DATE_TO\\}";
    public static final String DATEUNTIL_IDENTIFIER = "DU";
    public static final String DATEFROM_IDENTIFIER = "DF";
    public static final String DATE_IDENTIFIER = "D";

    private static String getCompactDateString(String name, String[] pathStream) {
        String[] realTokens = StringUtils.split(name, "/");
        String compactDateString = null;
        for (int i = 0; i < realTokens.length; i++) {
            String currentString = pathStream[i];
            if (currentString.contains("${D:")) {
                int startindex = currentString.indexOf("${D:");
                int endindex = currentString.indexOf("}");
                if (compactDateString == null) {
                    compactDateString = realTokens[i].substring(startindex, endindex - 4);
                } else {
                    compactDateString += " " + realTokens[i].substring(startindex, endindex - 4);
                }
            }
        }
        return compactDateString;
    }

    private static Boolean containsTokens(String path) {
        if (path.contains("${")) {
            return true;
        } else {
            return false;
        }
    }

    private static String getCompactDateFormatString(String name, String[] pathStream) {
        String[] realTokens = StringUtils.split(name, "/");
        String compactDateString = null;
        //contains more than one date token?
        for (int i = 0; i < realTokens.length; i++) {
            String currentString = pathStream[i];
            if (currentString.contains("${")) {
                int startindex = currentString.indexOf("${");
                int endindex = currentString.indexOf("}");
                if (compactDateString == null) {
                    compactDateString = currentString.substring(startindex + 4, endindex);
                } else {
                    compactDateString += " " + currentString.substring(startindex + 4, endindex);
                }
            }
        }
        return compactDateString;
    }

    private static boolean matchDateString(String currentFolder, String nextToken) {
        String[] substringsBetween = StringUtils.substringsBetween(nextToken, "${D:", "}");
        for (int i = 0; i < substringsBetween.length; i++) {
            nextToken = nextToken.replace("${D:" + substringsBetween[i] + "}", ".{" + substringsBetween[i].length() + "}");
        }
        Pattern p = Pattern.compile(nextToken);
        Matcher m = p.matcher(currentFolder);
        return m.matches();
    }

    public static boolean fitsFileNameScheme(String fileName, String fileNameScheme) {
        String[] substringsBetween = StringUtils.substringsBetween(fileNameScheme, "${", "}");
        Pattern p = Pattern.compile(fileNameScheme);
        Matcher m = p.matcher(fileName);
        return m.matches();
    }

    private static boolean fitsDateTime(String fileName, String fileNameScheme, DateTime lastReadout) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static String removeFoler(String fileName, String folder) {
        if (fileName.startsWith(folder)) {
            return fileName.substring(folder.length(), fileName.length());
        }
        return fileName;
    }
    public int _year;
    public int _month;
    public int _day;
    public int _hour;
    public int _min;
    public int _sec;

    public static List<String> parseString(String string, DataPoint datapoint, String datePattern, DateTime from, DateTime until) {
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

    public static String parseDateFrom(String string, DataPoint datapoint, String datePattern, DateTime from) {
        if (datePattern != null && !datePattern.equals("")) {
//            DateFormat df = new 
//            df.setTimeZone(deviceTimeZone);

//            Calendar c = Calendar.getInstance();
//            c.setTimeZone(TimeZone.getTimeZone("UTC"));
//            c.setTime(date.getTime());
//            c.add(Calendar.SECOND, deviceTimeZone.getRawOffset());
            DateTimeFormatter fmt = DateTimeFormat.forPattern(datePattern);

            string = string.replaceAll(NEW_DATEFROM_PATTERN, fmt.print(from));
//            string = string.replaceAll(DATE_FROM_YEAR, from.getYear() + "");
//            string = string.replaceAll(DATE_FROM_MONTH, from.getMonthOfYear() + "");
//            string = string.replaceAll(DATE_FROM_DAY, from.getDayOfMonth() + "");
//            string = string.replaceAll(DATE_FROM_HOUR, from.getHourOfDay() + "");
//            string = string.replaceAll(DATE_FROM_MINUTE, from.getMinuteOfHour() + "");
//            string = string.replaceAll(DATE_FROM_SECOND, from.getSecondOfMinute() + "");

//            string = string.replaceAll(DATE_FROM, df.format(c.getTime()));
//            string = string.replaceAll(DATE_FROM_YEAR, df.format(c.get(Calendar.YEAR)));
//            string = string.replaceAll(DATE_FROM_MONTH, df.format(c.get(Calendar.MONTH)));
//            string = string.replaceAll(DATE_FROM_DAY, df.format(c.get(Calendar.DAY_OF_MONTH)));
//            string = string.replaceAll(DATE_FROM_HOUR, df.format(c.get(Calendar.HOUR)));
//            string = string.replaceAll(DATE_FROM_MINUTE, df.format(c.get(Calendar.MINUTE)));
//            string = string.replaceAll(DATE_FROM_SECOND, df.format(c.get(Calendar.SECOND)));
        }

        string = string.replaceAll(SYSTEM_TIME, String.valueOf(System.nanoTime()));

//        if (datapoint != null) {
//            string = string.replaceAll(DATAPOINT, datapoint.g());
//        }
        return string;
    }

    public static String parseDateTo(String string, DataPoint datapoint, String datePattern, DateTime until) {
        if (datePattern != null && !datePattern.equals("")) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern(datePattern);

            string = string.replaceAll(NEW_DATEUNTIL_PATTERN, fmt.print(until));
//            string = string.replaceAll(DATE_TO_YEAR, until.getYear() + "");
//            string = string.replaceAll(DATE_TO_MONTH, until.getMonthOfYear() + "");
//            string = string.replaceAll(DATE_TO_DAY, until.getDayOfMonth() + "");
//            string = string.replaceAll(DATE_TO_HOUR, until.getHourOfDay() + "");
//            string = string.replaceAll(DATE_TO_MINUTE, until.getMinuteOfHour() + "");
//            string = string.replaceAll(DATE_TO_SECOND, until.getSecondOfMinute() + "");
        }

        string = string.replaceAll(SYSTEM_TIME, String.valueOf(System.nanoTime()));

//        if (datapoint != null) {
//            string = string.replaceAll(DATAPOINT, datapoint.getChannelID());
//        }
        return string;
    }

    public static String replaceTime(String path) {
        if (path.contains("TIME_START") || path.contains("TIME_END")) {
            path = path.replaceAll("TIME_START", "DATE_FROM");
            path = path.replaceAll("TIME_END", "DATE_TO");
        }
        return path;
    }

    public static String replaceDatapoint(String parsedString, DataPoint dp) {
        if (dp != null) {
            return parsedString.replaceAll(ConnectionHelper.NEW_DATAPOINT_PATTERN, dp.getMappingIdentifier());
        } else {
            return parsedString;
        }

    }
    //gets the whole String of the Connection with all replacements

    public static String parseConnectionString(DataPoint dp, DateTime from, DateTime until, String fileNameScheme, String dateFormat) {
        String parsedString = fileNameScheme;
//        parsedString = ConnectionHelper.replaceTime(_filePath);
//        parsedString = ConnectionHelper.replaceDatapoint(parsedString, dp);
        parsedString = ConnectionHelper.parseDateFrom(parsedString, dp, dateFormat, from);
        parsedString = ConnectionHelper.parseDateTo(parsedString, dp, dateFormat, until);
        return parsedString;
    }

    public static List<String> getFTPMatchedFileNames(FTPClient fc, DataPoint dp, String filePath) {
        filePath = filePath.replace("\\", "/");
        String[] pathStream = getPathTokens(filePath);
        DateTime lastReadout = dp.getLastReadout();

        String startPath = "";
        if (filePath.startsWith("/")) {
            startPath = "/";
        }

        List<String> folderPathes = getMatchingPathes(startPath, pathStream, new ArrayList<String>(), fc, lastReadout, new DateTimeFormatterBuilder());
//        System.out.println("foldersize,"+folderPathes.size());
        List<String> fileNames = new ArrayList<String>();

        if (folderPathes.isEmpty()) {
            org.apache.log4j.Logger.getLogger(ConnectionHelper.class).log(org.apache.log4j.Level.ERROR, "Cant find suitable folder on the device");
            return fileNames;
        }

//        String fileName = null;
        String fileNameScheme = pathStream[pathStream.length - 1];
        String currentfolder = null;
        try {
            for (String folder : folderPathes) {
                //                fc.changeWorkingDirectory(folder);
                //                System.out.println("currentFolder,"+folder);
                currentfolder = folder;
//                for (FTPFile file : fc.listFiles(folder)) {
//                    System.out.println(file.getName());
//                }
                for (String fileName : fc.listNames(folder)) {
//                    org.apache.log4j.Logger.getLogger(Launcher.class.getName()).log(org.apache.log4j.Level.ALL, "CurrentFileName: " + fileName);
                    fileName = removeFoler(fileName, folder);
//                    fileName = file.getName();
                    boolean match = false;
                    System.out.println(fileName);
                    if (ConnectionHelper.containsTokens(fileNameScheme)) {
                        boolean matchDate = matchDateString(fileName, fileNameScheme);
                        DateTime folderTime = getFileTime(folder + fileName, pathStream);
                        boolean isLater = folderTime.isAfter(lastReadout);
                        if (matchDate && isLater) {
                            match = true;
                        }
                    } else {
                        Pattern p = Pattern.compile(fileNameScheme);
                        Matcher m = p.matcher(fileName);
                        match = m.matches();
                    }
                    if (match) {
                        fileNames.add(folder + fileName);
                    }
                }
            }
        } catch (IOException ex) {
            org.apache.log4j.Logger.getLogger(ConnectionHelper.class).log(org.apache.log4j.Level.ERROR, ex.getMessage());
        } catch (Exception ex) {
            org.apache.log4j.Logger.getLogger(ConnectionHelper.class).log(org.apache.log4j.Level.ERROR, "Error while searching a matching file");
            org.apache.log4j.Logger.getLogger(ConnectionHelper.class).log(org.apache.log4j.Level.ERROR, "Folder: " + currentfolder);
            org.apache.log4j.Logger.getLogger(ConnectionHelper.class).log(org.apache.log4j.Level.ERROR, "FileName: " + fileNameScheme);
            org.apache.log4j.Logger.getLogger(ConnectionHelper.class).log(org.apache.log4j.Level.ERROR, ex.getMessage());
        }
        if (folderPathes.isEmpty()) {
            org.apache.log4j.Logger.getLogger(ConnectionHelper.class).log(org.apache.log4j.Level.ERROR, "Cant find suitable files on the device");
        }
//        System.out.println("filenamesize"+fileNames.size());
        return fileNames;
    }

    public static List<String> getSFTPMatchedFileNames(ChannelSftp _channel, DataPoint dp, String filePath) {
        filePath = filePath.replace("\\", "/");
        String[] pathStream = getPathTokens(filePath);
        DateTime lastReadout = dp.getLastReadout();

        String startPath = "";
        if (filePath.startsWith("/")) {
            startPath = "/";
        }

        List<String> folderPathes = getSFTPMatchingPathes(startPath, pathStream, new ArrayList<String>(), _channel, lastReadout, new DateTimeFormatterBuilder());
//        System.out.println("foldersize,"+folderPathes.size());
        List<String> fileNames = new ArrayList<String>();
        if (folderPathes.isEmpty()) {
            org.apache.log4j.Logger.getLogger(ConnectionHelper.class).log(org.apache.log4j.Level.ERROR, "Cant find suitable folder on the device");
            return fileNames;
        }


        if (folderPathes.isEmpty()) {
            org.apache.log4j.Logger.getLogger(ConnectionHelper.class).log(org.apache.log4j.Level.ERROR, "Cant find suitable folder on the device");
            return fileNames;
        }

//        String fileName = null;
        String fileNameScheme = pathStream[pathStream.length - 1];
        String currentfolder = null;
        try {
            for (String folder : folderPathes) {
                //                fc.changeWorkingDirectory(folder);
                //                System.out.println("currentFolder,"+folder);
                currentfolder = folder;
                //                for (FTPFile file : fc.listFiles(folder)) {
                //                    System.out.println(file.getName());
                //                }
//                Vector ls = _channel.ls(folder);
                for (Object fileName : _channel.ls(folder)) {
                    LsEntry currentFile = (LsEntry) fileName;
                    String currentFileName = currentFile.getFilename();
                    currentFileName = removeFoler(currentFileName, folder);
                    boolean match = false;
                    System.out.println(currentFileName);
                    if (ConnectionHelper.containsTokens(fileNameScheme)) {
                        boolean matchDate = matchDateString(currentFileName, fileNameScheme);
                        DateTime folderTime = getFileTime(folder + currentFileName, pathStream);
                        boolean isLater = folderTime.isAfter(lastReadout);
                        if (matchDate && isLater) {
                            match = true;
                        }
                    } else {
                        Pattern p = Pattern.compile(fileNameScheme);
                        Matcher m = p.matcher(currentFileName);
                        match = m.matches();
                    }
                    if (match) {
                        fileNames.add(folder + currentFileName);
                    }
                }
            }
        } catch (Exception ex) {
            org.apache.log4j.Logger.getLogger(ConnectionHelper.class).log(org.apache.log4j.Level.ERROR, "Error while searching a matching file");
            org.apache.log4j.Logger.getLogger(ConnectionHelper.class).log(org.apache.log4j.Level.ERROR, "Folder: " + currentfolder);
            org.apache.log4j.Logger.getLogger(ConnectionHelper.class).log(org.apache.log4j.Level.ERROR, "FileName: " + fileNameScheme);
            org.apache.log4j.Logger.getLogger(ConnectionHelper.class).log(org.apache.log4j.Level.ERROR, ex.getMessage());
        }
        if (folderPathes.isEmpty()) {
            org.apache.log4j.Logger.getLogger(ConnectionHelper.class).log(org.apache.log4j.Level.ERROR, "Cant find suitable files on the device");
        }
//        System.out.println("filenamesize"+fileNames.size());
        return fileNames;
    }

    public static String[] getPathToken(String filePath) {
//        List<String> tokens = new ArrayList<String>();
        //        filePath.substring("\\$\\{","\\}");
        String[] tokens = StringUtils.substringsBetween(filePath, "${", "}");
//         String[] tokens = filePath.trim().split("\\%");
        for (int i = 0; i < tokens.length; i++) {
            System.out.println(tokens[i]);
        }
        return tokens;
    }

    public static String[] getPathTokens(String filePath) {
//        List<String> tokens = new ArrayList<String>();
        //        filePath.substring("\\$\\{","\\}");
        String[] tokens = StringUtils.split(filePath, "/");
//         String[] tokens = filePath.trim().split("\\%");
        for (int i = 0; i < tokens.length; i++) {
            System.out.println(tokens[i]);
        }
        return tokens;
    }

    public static List<String> getDateToken(String[] tokenStream) {
        List<String> dateTokens = new ArrayList<String>();
        for (int i = 0; i < tokenStream.length; i++) {
            if (tokenStream[i].startsWith(DATE_IDENTIFIER)) {
                dateTokens.add(tokenStream[i].replace(DATE_IDENTIFIER, ""));
            }
        }
        return dateTokens;
    }

    public static List<String> getDateFromToken(String[] tokenStream) {
        List<String> dateTokens = new ArrayList<String>();
        for (int i = 0; i < tokenStream.length; i++) {
            if (tokenStream[i].startsWith(DATEFROM_IDENTIFIER)) {
                dateTokens.add(tokenStream[i].replace(DATEFROM_IDENTIFIER, ""));
            }
        }
        return dateTokens;
    }

    public static List<String> getDateUntilToken(String[] tokenStream) {
        List<String> dateTokens = new ArrayList<String>();
        for (int i = 0; i < tokenStream.length; i++) {
            if (tokenStream[i].startsWith(DATEUNTIL_IDENTIFIER)) {
                dateTokens.add(tokenStream[i].replace(DATEUNTIL_IDENTIFIER, ""));
            }
        }
        return dateTokens;
    }

    public static boolean containsToken(String tokenString) {
        if (tokenString.contains(NEW_DATAPOINT)) {
            return true;
        } else {
            return false;
        }
    }

//    public static boolean containsToken(List<DataPoint> datapointsJEVis) {
//        //if its a reagular expression you need more than one 
//    }
    public static List<String> getFTPMatchedFileNames(FTPClient fc, String filePath) {
        Path p = Paths.get(filePath);
        String file = p.getFileName().toString();
        String path = p.getParent().toString();

        List<String> fileNames = new ArrayList<String>();
        String fileName = null;
        try {
            for (FTPFile f : fc.listFiles(path)) {
                fileName = f.getName();

                if (fitsFileNameScheme(fileName, file)) {
                    fileNames.add(fileName);
                }

            }
        } catch (IOException ex) {
            org.apache.log4j.Logger.getLogger(ConnectionHelper.class).log(org.apache.log4j.Level.ERROR, ex.getMessage());
        }
        return fileNames;
    }

    private static boolean containsDateToken(String string) {
        if (string.contains("${D:")) {
            return true;
        } else {
            return false;
        }
    }

//    private static DateTimeFormatter getDateFormatter(String string) {
//        if (containsDateToken(string)) {
//            String substringBetween = StringUtils.substringBetween(string, "${D:", "}");
//            int firstIndexOf = string.indexOf("${D:");
//            int lastIndexOf = string.indexOf("}");
//            String firstString = string.substring(0, firstIndexOf);
//            String lastString = string.substring(lastIndexOf, string.length() - 1);
//            DateTimeFormatter dtf = DateTimeFormat.forPattern(firstString + substringBetween + lastString);
//            return dtf;
//        } else {
//            DateTimeFormatter dtf = DateTimeFormat.forPattern("Dae" + "/");
//            return dtf;
//        }
//
//    }
//
//    private static boolean containsDate(String name, DateTimeFormatter dtf) {
//        DateTime parseDateTime = dtf.parseDateTime(name);
//        if (parseDateTime != null) {
//            return true;
//        } else {
//            return false;
//        }
//    }
    private static DateTime getFolderTime(String name, String[] pathStream) {
        String compactDateString = getCompactDateString(name, pathStream);
        String compactDataFormatString = getCompactDateFormatString(name, pathStream);

        DateTimeFormatter dtf = DateTimeFormat.forPattern(compactDataFormatString);

        DateTime parseDateTime = dtf.parseDateTime(compactDateString);
        if (parseDateTime.year().get() == parseDateTime.year().getMinimumValue()) {
            parseDateTime = parseDateTime.year().withMaximumValue();
        }
        if (parseDateTime.monthOfYear().get() == parseDateTime.monthOfYear().getMinimumValue()) {
            parseDateTime = parseDateTime.monthOfYear().withMaximumValue();
        }
        if (parseDateTime.dayOfMonth().get() == parseDateTime.dayOfMonth().getMinimumValue()) {
            parseDateTime = parseDateTime.dayOfMonth().withMaximumValue();
        }
        if (parseDateTime.hourOfDay().get() == parseDateTime.hourOfDay().getMinimumValue()) {
            parseDateTime = parseDateTime.hourOfDay().withMaximumValue();
        }
        if (parseDateTime.minuteOfHour().get() == parseDateTime.minuteOfHour().getMinimumValue()) {
            parseDateTime = parseDateTime.minuteOfHour().withMaximumValue();
        }
        if (parseDateTime.secondOfMinute().get() == parseDateTime.secondOfMinute().getMinimumValue()) {
            parseDateTime = parseDateTime.secondOfMinute().withMaximumValue();
        }
        if (parseDateTime.millisOfSecond().get() == parseDateTime.millisOfSecond().getMinimumValue()) {
            parseDateTime = parseDateTime.millisOfSecond().withMaximumValue();
        }
        return parseDateTime;
    }

    private static DateTime getFileTime(String name, String[] pathStream) {
        String compactDateString = getCompactDateString(name, pathStream);
        String compactDataFormatString = getCompactDateFormatString(name, pathStream);

        DateTimeFormatter dtf = DateTimeFormat.forPattern(compactDataFormatString);

        DateTime parseDateTime = dtf.parseDateTime(compactDateString);
        return parseDateTime;
    }

    private static List<String> getMatchingPathes(String path, String[] pathStream, ArrayList<String> arrayList, FTPClient fc, DateTime lastReadout, DateTimeFormatterBuilder dtfbuilder) {
        int nextTokenPos = getPathTokens(path).length;
        if (nextTokenPos == pathStream.length - 1) {
            arrayList.add(path);
            return arrayList;
        }

        String nextToken = pathStream[nextTokenPos];
        String nextFolder = null;

        try {
            if (containsDateToken(nextToken)) {
                FTPFile[] listDirectories = fc.listFiles(path);
//                DateTimeFormatter ftmTemp = getDateFormatter(nextToken);
                for (FTPFile folder : listDirectories) {
                    if (!matchDateString(folder.getName(), nextToken)) {
                        continue;
                    }
//                    System.out.println("listdir," + folder.getName());
//                    if (containsDate(folder.getName(), ftmTemp)) {
                    DateTime folderTime = getFolderTime(path + folder.getName() + "/", pathStream);
//                    System.out.println("foldertime," + folderTime);
                    if (folderTime.isAfter(lastReadout)) {
                        nextFolder = folder.getName();
//                        System.out.println("dateFolder," + nextFolder);
                        getMatchingPathes(path + nextFolder + "/", pathStream, arrayList, fc, lastReadout, dtfbuilder);
                    }
//                    }
                }
            } else {
                nextFolder = nextToken;
//                System.out.println("normalFolder," + nextFolder);
                getMatchingPathes(path + nextFolder + "/", pathStream, arrayList, fc, lastReadout, dtfbuilder);
            }
        } catch (IOException ex) {
            org.apache.log4j.Logger.getLogger(ConnectionHelper.class).log(org.apache.log4j.Level.ERROR, ex.getMessage());
        }
        return arrayList;
    }

    private static List<String> getSFTPMatchingPathes(String path, String[] pathStream, ArrayList<String> arrayList, ChannelSftp fc, DateTime lastReadout, DateTimeFormatterBuilder dtfbuilder) {
        int nextTokenPos = getPathTokens(path).length;
        if (nextTokenPos == pathStream.length - 1) {
            arrayList.add(path);
            return arrayList;
        }

        String nextToken = pathStream[nextTokenPos];
        String nextFolder = null;

        try {
            if (containsDateToken(nextToken)) {
                Vector listDirectories = fc.ls(path);
                for (Object folder : listDirectories) {
                    LsEntry currentFolder = (LsEntry) folder;

                    if (!matchDateString(currentFolder.getFilename(), nextToken)) {
                        continue;
                    }
                    DateTime folderTime = getFolderTime(path + currentFolder.getFilename() + "/", pathStream);
                    if (folderTime.isAfter(lastReadout)) {
                        nextFolder = currentFolder.getFilename();
                        getSFTPMatchingPathes(path + nextFolder + "/", pathStream, arrayList, fc, lastReadout, dtfbuilder);
                    }
//                    }
                }
            } else {
                nextFolder = nextToken;
                getSFTPMatchingPathes(path + nextFolder + "/", pathStream, arrayList, fc, lastReadout, dtfbuilder);
            }
        } catch (SftpException ex) {
            Logger.getLogger(ConnectionHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return arrayList;
    }
}
