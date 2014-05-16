/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author bf
 */
public class TestTimeConverter extends TestCase {

    private List<String> _summerToWinterOriginal;
    private List<String> _summerToWinterReference;

    protected void setUp() {
        System.out.println("setup");
        initializeOriginalCET();
        initializeReferenceUTC();
    }

    protected void tearDown() {
    }

    //summer to winter from 3 o'clock to 2 o'clock
    public void test_summer_to_winter_in_CET() throws Exception {

        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");

        DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("CET"));

        List<DateTime> expectedDatesUTC = new ArrayList<DateTime>();

        DateTimeZone.setDefault(DateTimeZone.UTC);
        for (String date : _summerToWinterReference) {
            DateTime parseDateTime = dtf.parseDateTime(date);
            expectedDatesUTC.add(parseDateTime);
        }

        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getTimeZone("CET")));
        List<DateTime> realDatesUTC = new ArrayList<DateTime>();
        
        DataCollector test = new DataCollector();
        for (String date : _summerToWinterOriginal) {
            DateTime parseDateTime = dtf.parseDateTime(date);
            
            DateTime newTime = test.convertTime(dateTimeZone, parseDateTime);
            realDatesUTC.add(newTime);
        }
        
        DateTime tmp = dtf.parseDateTime(_summerToWinterOriginal.get(0));
        for(int i = 0; i<80; i++){
            DateTime tmp2 = tmp.plusMillis(240000);
            System.out.println("#-#-#-#- "+tmp2.toDateTime(DateTimeZone.UTC));
            tmp = tmp2;
        }

//        String time = "25.10.2009 02:56";
//        DateTime parseDateTime = dtf.parseDateTime(time);
//        System.out.println("Zeitzone " + parseDateTime.getZone());
//        System.out.println(parseDateTime.toDateTime(DateTimeZone.UTC));
//        DateTime plusMinutes = parseDateTime.plusMinutes(4);
//        System.out.println(plusMinutes.toDateTime(DateTimeZone.UTC));
//        Assert.assertEquals(expectedDatesUTC.size(), realDatesUTC.size());
//        for (int i = 0; i < _summerToWinterOriginal.size(); i++) {
//            System.out.println("soll " + expectedDatesUTC.get(i) + " ist " + realDatesUTC.get(i));
//            Assert.assertEquals(expectedDatesUTC.get(i), realDatesUTC.get(i));
//        }
    }

    //-2 until 2 o'clock and -1 from 3 o'clock
    private void initializeReferenceUTC() {
        _summerToWinterReference = new ArrayList<String>();
        _summerToWinterReference.add("24.10.2009 22:00");
        _summerToWinterReference.add("24.10.2009 22:04");
        _summerToWinterReference.add("24.10.2009 22:08");
        _summerToWinterReference.add("24.10.2009 22:12");
        _summerToWinterReference.add("24.10.2009 22:16");
        _summerToWinterReference.add("24.10.2009 22:20");
        _summerToWinterReference.add("24.10.2009 22:24");
        _summerToWinterReference.add("24.10.2009 22:28");
        _summerToWinterReference.add("24.10.2009 22:32");
        _summerToWinterReference.add("24.10.2009 22:36");
        _summerToWinterReference.add("24.10.2009 22:40");
        _summerToWinterReference.add("24.10.2009 22:44");
        _summerToWinterReference.add("24.10.2009 22:48");
        _summerToWinterReference.add("24.10.2009 22:52");
        _summerToWinterReference.add("24.10.2009 22:56");
        _summerToWinterReference.add("24.10.2009 23:00");
        _summerToWinterReference.add("24.10.2009 23:04");
        _summerToWinterReference.add("24.10.2009 23:08");
        _summerToWinterReference.add("24.10.2009 23:12");
        _summerToWinterReference.add("24.10.2009 23:16");
        _summerToWinterReference.add("24.10.2009 23:20");
        _summerToWinterReference.add("24.10.2009 23:24");
        _summerToWinterReference.add("24.10.2009 23:28");
        _summerToWinterReference.add("24.10.2009 23:32");
        _summerToWinterReference.add("24.10.2009 23:36");
        _summerToWinterReference.add("24.10.2009 23:40");
        _summerToWinterReference.add("24.10.2009 23:44");
        _summerToWinterReference.add("24.10.2009 23:48");
        _summerToWinterReference.add("24.10.2009 23:52");
        _summerToWinterReference.add("24.10.2009 23:56");
        _summerToWinterReference.add("25.10.2009 00:00");
        _summerToWinterReference.add("25.10.2009 00:04");
        _summerToWinterReference.add("25.10.2009 00:08");
        _summerToWinterReference.add("25.10.2009 00:12");
        _summerToWinterReference.add("25.10.2009 00:16");
        _summerToWinterReference.add("25.10.2009 00:20");
        _summerToWinterReference.add("25.10.2009 00:24");
        _summerToWinterReference.add("25.10.2009 00:28");
        _summerToWinterReference.add("25.10.2009 00:32");
        _summerToWinterReference.add("25.10.2009 00:36");
        _summerToWinterReference.add("25.10.2009 00:40");
        _summerToWinterReference.add("25.10.2009 00:44");
        _summerToWinterReference.add("25.10.2009 00:48");
        _summerToWinterReference.add("25.10.2009 00:52");
        _summerToWinterReference.add("25.10.2009 00:56");
        _summerToWinterReference.add("25.10.2009 01:00");
        _summerToWinterReference.add("25.10.2009 01:04");
        _summerToWinterReference.add("25.10.2009 01:08");
        _summerToWinterReference.add("25.10.2009 01:12");
        _summerToWinterReference.add("25.10.2009 01:16");
        _summerToWinterReference.add("25.10.2009 01:20");
        _summerToWinterReference.add("25.10.2009 01:24");
        _summerToWinterReference.add("25.10.2009 01:28");
        _summerToWinterReference.add("25.10.2009 01:32");
        _summerToWinterReference.add("25.10.2009 01:36");
        _summerToWinterReference.add("25.10.2009 01:40");
        _summerToWinterReference.add("25.10.2009 01:44");
        _summerToWinterReference.add("25.10.2009 01:48");
        _summerToWinterReference.add("25.10.2009 01:52");
        _summerToWinterReference.add("25.10.2009 01:56");
        _summerToWinterReference.add("25.10.2009 02:00");
        _summerToWinterReference.add("25.10.2009 02:04");
        _summerToWinterReference.add("25.10.2009 02:08");
        _summerToWinterReference.add("25.10.2009 02:12");
        _summerToWinterReference.add("25.10.2009 02:16");
        _summerToWinterReference.add("25.10.2009 02:20");
        _summerToWinterReference.add("25.10.2009 02:24");
        _summerToWinterReference.add("25.10.2009 02:28");
        _summerToWinterReference.add("25.10.2009 02:32");
        _summerToWinterReference.add("25.10.2009 02:36");
    }

    private void initializeOriginalCET() {
        _summerToWinterOriginal = new ArrayList<String>();
        _summerToWinterOriginal.add("25.10.2009 00:00");
        _summerToWinterOriginal.add("25.10.2009 00:04");
        _summerToWinterOriginal.add("25.10.2009 00:08");
        _summerToWinterOriginal.add("25.10.2009 00:12");
        _summerToWinterOriginal.add("25.10.2009 00:16");
        _summerToWinterOriginal.add("25.10.2009 00:20");
        _summerToWinterOriginal.add("25.10.2009 00:24");
        _summerToWinterOriginal.add("25.10.2009 00:28");
        _summerToWinterOriginal.add("25.10.2009 00:32");
        _summerToWinterOriginal.add("25.10.2009 00:36");
        _summerToWinterOriginal.add("25.10.2009 00:40");
        _summerToWinterOriginal.add("25.10.2009 00:44");
        _summerToWinterOriginal.add("25.10.2009 00:48");
        _summerToWinterOriginal.add("25.10.2009 00:52");
        _summerToWinterOriginal.add("25.10.2009 00:56");
        _summerToWinterOriginal.add("25.10.2009 01:00");
        _summerToWinterOriginal.add("25.10.2009 01:04");
        _summerToWinterOriginal.add("25.10.2009 01:08");
        _summerToWinterOriginal.add("25.10.2009 01:12");
        _summerToWinterOriginal.add("25.10.2009 01:16");
        _summerToWinterOriginal.add("25.10.2009 01:20");
        _summerToWinterOriginal.add("25.10.2009 01:24");
        _summerToWinterOriginal.add("25.10.2009 01:28");
        _summerToWinterOriginal.add("25.10.2009 01:32");
        _summerToWinterOriginal.add("25.10.2009 01:36");
        _summerToWinterOriginal.add("25.10.2009 01:40");
        _summerToWinterOriginal.add("25.10.2009 01:44");
        _summerToWinterOriginal.add("25.10.2009 01:48");
        _summerToWinterOriginal.add("25.10.2009 01:52");
        _summerToWinterOriginal.add("25.10.2009 01:56");
        _summerToWinterOriginal.add("25.10.2009 02:00");
        _summerToWinterOriginal.add("25.10.2009 02:04");
        _summerToWinterOriginal.add("25.10.2009 02:08");
        _summerToWinterOriginal.add("25.10.2009 02:12");
        _summerToWinterOriginal.add("25.10.2009 02:16");
        _summerToWinterOriginal.add("25.10.2009 02:20");
        _summerToWinterOriginal.add("25.10.2009 02:24");
        _summerToWinterOriginal.add("25.10.2009 02:28");
        _summerToWinterOriginal.add("25.10.2009 02:32");
        _summerToWinterOriginal.add("25.10.2009 02:36");
        _summerToWinterOriginal.add("25.10.2009 02:40");
        _summerToWinterOriginal.add("25.10.2009 02:44");
        _summerToWinterOriginal.add("25.10.2009 02:48");
        _summerToWinterOriginal.add("25.10.2009 02:52");
        _summerToWinterOriginal.add("25.10.2009 02:56");
        _summerToWinterOriginal.add("25.10.2009 02:00");
        _summerToWinterOriginal.add("25.10.2009 02:04");
        _summerToWinterOriginal.add("25.10.2009 02:08");
        _summerToWinterOriginal.add("25.10.2009 02:12");
        _summerToWinterOriginal.add("25.10.2009 02:16");
        _summerToWinterOriginal.add("25.10.2009 02:20");
        _summerToWinterOriginal.add("25.10.2009 02:24");
        _summerToWinterOriginal.add("25.10.2009 02:28");
        _summerToWinterOriginal.add("25.10.2009 02:32");
        _summerToWinterOriginal.add("25.10.2009 02:36");
        _summerToWinterOriginal.add("25.10.2009 02:40");
        _summerToWinterOriginal.add("25.10.2009 02:44");
        _summerToWinterOriginal.add("25.10.2009 02:48");
        _summerToWinterOriginal.add("25.10.2009 02:52");
        _summerToWinterOriginal.add("25.10.2009 02:56");
        _summerToWinterOriginal.add("25.10.2009 03:00");
        _summerToWinterOriginal.add("25.10.2009 03:04");
        _summerToWinterOriginal.add("25.10.2009 03:08");
        _summerToWinterOriginal.add("25.10.2009 03:12");
        _summerToWinterOriginal.add("25.10.2009 03:16");
        _summerToWinterOriginal.add("25.10.2009 03:20");
        _summerToWinterOriginal.add("25.10.2009 03:24");
        _summerToWinterOriginal.add("25.10.2009 03:28");
        _summerToWinterOriginal.add("25.10.2009 03:32");
        _summerToWinterOriginal.add("25.10.2009 03:36");
    }
}