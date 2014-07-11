/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.ConnectionTests;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.jevis.jedatacollector.connection.FTP.FTPConnection;
import org.jevis.jedatacollector.connection.HTTP.HTTPConnection;
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

    public void test_ftp_parsing() {
        FTPConnection ftp = new FTPConnection(null, "/data/trend/", "Trend${DATAPOINT}.csv", "192.168.2.254", "admin", "envidatec4u", 2l, 200l);
        DateTimeFormatter dtf = DateTimeFormat.forPattern("ddMMyyyyHHmmss");
        DataPoint dp = new DataPoint("_L2_1_DI5_Input_18CB", null, 256l);
        DateTime from = dtf.parseDateTime("01012013000000");
        DateTime until = dtf.parseDateTime("31012013153045");
        String parsedString = ftp.parseString(dp, from, until);
        System.out.println(parsedString);
        Assert.assertEquals("Trend_L2_1_DI5_Input_18CB.csv", parsedString);
    }

    public void test_http_parsing_single() {
        HTTPConnection http = new HTTPConnection("172.22.182.2", "/DP${DATAPOINT}-${DATE_FROM}-${DATE_TO}", 8350, 50, 300, "ddMMyyyyHHmmss");
        DateTimeFormatter dtf = DateTimeFormat.forPattern("ddMMyyyyHHmmss");
        DataPoint dp = new DataPoint("11", null, 256l);
        DateTime from = dtf.parseDateTime("01012013000000");
        DateTime until = dtf.parseDateTime("31012013153045");
        String parsedString = http.parseString(dp, from, until);
        Assert.assertEquals("/DP11-01012013000000-31012013153045", parsedString);
//            http.sendSampleRequest(dp, from, until);
    }
}