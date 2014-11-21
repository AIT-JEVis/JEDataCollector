/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.ConnectionTests;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.commons.parsing.DataCollectorParser;
import org.jevis.commons.parsing.csvParsing.CSVParsing;
import org.jevis.jedatacollector.DataCollector;
import org.jevis.jedatacollector.Request;
import org.jevis.jedatacollector.RequestGenerator;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.jevis.jedatacollector.connection.FTP.FTPConnection;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.jedatacollector.exception.FetchingException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author bf
 */
public class ConnectionParsingTest {

//    public void test_ftp_parsing() {
//        FTPConnection ftp = new FTPConnection(null, "/data/trend/", "Trend${DATAPOINT}.csv", "192.168.2.254", "admin", "envidatec4u", 2, 200);
//        DateTimeFormatter dtf = DateTimeFormat.forPattern("ddMMyyyyHHmmss");
//        DataPoint dp = new DataPoint("_L2_1_DI5_Input_18CB", null, 256l);
//        DateTime from = dtf.parseDateTime("01012013000000");
//        DateTime until = dtf.parseDateTime("31012013153045");
//        String parsedString = ConnectionHelper.parseConnectionString(dp, from, until, "Trend${DATAPOINT}.csv", "ddMMyyyyHHmmss");
//        System.out.println(parsedString);
//        Assert.assertEquals("Trend_L2_1_DI5_Input_18CB.csv", parsedString);
//    }
//
//    public void test_http_parsing_single() {
//        HTTPConnection http = new HTTPConnection("172.22.182.2", "/DP${DATAPOINT}-${DATE_FROM}-${DATE_TO}", 8350, 50, 300, "ddMMyyyyHHmmss");
//        DateTimeFormatter dtf = DateTimeFormat.forPattern("ddMMyyyyHHmmss");
//        DataPoint dp = new DataPoint("11", null, 256l);
//        DateTime from = dtf.parseDateTime("01012013000000");
//        DateTime until = dtf.parseDateTime("31012013153045");
//        String parsedString = ConnectionHelper.parseConnectionString(dp, from, until, "/DP${DATAPOINT}-${DATE_FROM}-${DATE_TO}", "ddMMyyyyHHmmss");
//        Assert.assertEquals("/DP11-01012013000000-31012013153045", parsedString);
////            http.sendSampleRequest(dp, from, until);
//    }
//
//    public void test_ftp_parsing_multi_folder() {
//        String pattern = ".*\\.csv";
//        //positive tests
//        List<String> fileNamesPositive = new ArrayList<String>();
//        fileNamesPositive.add("hallo.csv");
//        fileNamesPositive.add("t.csv");
//        fileNamesPositive.add("123.csv");
//        for (String s : fileNamesPositive) {
//            Assert.assertTrue(ConnectionHelper.fitsFileNameScheme(s, pattern));
//        }
//
//        //negative tests
//        List<String> fileNamesNegative = new ArrayList<String>();
//        fileNamesNegative.add("hallocsv");
//        fileNamesNegative.add("t.csv1");
//        fileNamesNegative.add("123.csv.");
//        for (String s : fileNamesNegative) {
//            Assert.assertFalse(ConnectionHelper.fitsFileNameScheme(s, pattern));
//        }
//    }
//    public void test_get_token_stream() {
//        String testtoken1 = "${D:yyyy}/${D:MM}/Daten_${D:dd}.csv";
//        String testtoken2 = "${D:yyyy}/${D:MM}/${D:dd}_daten.csv";
//        String testtoken3 = "${D:yyyy-MM-dd}_daten.csv";
//        String testtoken4 = "${DF:yyyy-MM:dd}-${DU:yyyy-MM:dd}.csv";
//        Assert.assertTrue(ConnectionHelper.getPathToken(testtoken1).length == 3);
//        Assert.assertTrue(ConnectionHelper.getPathToken(testtoken2).length == 3);
//        Assert.assertTrue(ConnectionHelper.getPathToken(testtoken3).length == 1);
//        Assert.assertTrue(ConnectionHelper.getPathToken(testtoken4).length == 2);
//    }
//    public void test_get_path_token_stream() {
//        String testtoken1 = "${D:yyyy}/${D:MM}/Daten_${D:dd}.csv";
//        String testtoken2 = "${D:yyyy}/${D:MM}/${D:dd}_daten.csv";
//        String testtoken3 = "${D:yyyy-MM-dd}_daten.csv";
//        String testtoken4 = "${DF:yyyy-MM:dd}-${DU:yyyy-MM:dd}.csv";
//        String testtoken5 = "Test/${D:yyyy}-Temperatur/daten.csv";
//        String testtoken6 = "Test/Temperatur/daten.csv";
//        Assert.assertTrue(ConnectionHelper.getPathTokens(testtoken1).length == 3);
//        Assert.assertTrue(ConnectionHelper.getPathTokens(testtoken2).length == 3);
//        Assert.assertTrue(ConnectionHelper.getPathTokens(testtoken3).length == 1);
//        Assert.assertTrue(ConnectionHelper.getPathTokens(testtoken4).length == 1);
//        Assert.assertTrue(ConnectionHelper.getPathTokens(testtoken5).length == 3);
//        Assert.assertTrue(ConnectionHelper.getPathTokens(testtoken6).length == 3);
//    }
//     public void test_ftp_folder() {
//         DataCollectorConnection connection = new FTPConnection(50l, false, "localhost", 21, 20, 200, "bf", "admin", "UTC");
//         DateTime lastReadout = new DateTime();
//         DataPoint datapoint = new DataPoint("Daten/${D:yyyy}/${D:MM}/${D:dd}.csv", null,null, "2", lastReadout, true);
//
//         List<DataPoint> datapoints = new ArrayList<DataPoint>();
//         datapoints.add(datapoint);
//         
//         Request request = RequestGenerator.createConnectionRequest(connection, datapoints);
//         DataCollector collector = new DataCollector(request);
//        try {
//            collector.run();
//        } catch (FetchingException ex) {
//            Logger.getLogger(ConnectionParsingTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//     }
//    public void test_matching_pathes() {
//        DataCollectorConnection connection = new FTPConnection(50l, false, "localhost", 21, 20, 200, "ftpuser", "ftp", "UTC");
//        DateTime lastReadout = new DateTime();
//        DataPoint datapoint = new DataPoint("Daten\\${D:yyyy}\\${D:MM}\\monatsdaten.csv", null, null, "2", lastReadout, true);
//
//        List<DataPoint> datapoints = new ArrayList<DataPoint>();
//        datapoints.add(datapoint);
//
//        Request request = RequestGenerator.createConnectionRequest(connection, datapoints);
//        DataCollector collector = new DataCollector(request);
//        try {
//            collector.run();
////        List<String> folderPathes = getMatchingPathes("", pathStream, new ArrayList<String>(), fc, lastReadout,new DateTimeFormatterBuilder11
//        } catch (FetchingException ex) {
//            Logger.getLogger(ConnectionParsingTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public void test_matching_pathes_kaust() {
        DataCollectorConnection connection = new FTPConnection(50l, true, "ftps.kaust.edu.sa", 21, 20, 200, "neo_ext1", "ftneo1", "UTC");
//        DataCollectorParser parser = new CSVParsing("\"", ",", 6, Integer dateIndex, Integer timeIndex, Integer dpIndex, String dateFormat, String timeFormat, String decimalSep, String thousandSep)
        DateTime lastReadout = new DateTime();

        DateTimeFormatter dtf = DateTimeFormat.forPattern("ddMMyyyyHHmmss");
        DateTime from = dtf.parseDateTime("10072014090000");

        DataPoint datapoint = new DataPoint("CR/${D:yyyy_MM_dd_HH_mm_ss}_Gen4_.{5}_IV.dat", null, null, "2", from, true);

        List<DataPoint> datapoints = new ArrayList<DataPoint>();
        datapoints.add(datapoint);

        Request request = RequestGenerator.createConnectionRequest(connection, datapoints);
        DataCollector collector = new DataCollector(request);
        try {
            collector.run();
//        List<String> folderPathes = getMatchingPathes("", pathStream, new ArrayList<String>(), fc, lastReadout,new DateTimeFormatterBuilder11
        } catch (FetchingException ex) {
            Logger.getLogger(ConnectionParsingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public void test_tmp(){
//        Pattern p = Pattern.compile(".{4}");
//        Matcher m = p.matcher("AAAA");
//        System.out.println(m.matches());
//    }
}
