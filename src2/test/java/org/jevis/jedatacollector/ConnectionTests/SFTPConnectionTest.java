///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.jevis.jedatacollector.ConnectionTests;
//
//import junit.framework.Assert;
//import org.jevis.jedatacollector.DataCollector;
//import org.jevis.jedatacollector.Request;
//import org.jevis.jedatacollector.RequestGenerator;
//import org.jevis.jedatacollector.connection.DataCollectorConnection;
//import org.jevis.jedatacollector.connection.FTP.SFTPConnection;
//import org.jevis.jedatacollector.data.DataPoint;
//import org.jevis.jedatacollector.exception.FetchingException;
//import org.joda.time.DateTime;
//
///**
// *
// * @author bf
// */
//public class SFTPConnectionTest {
//        public void test_loytec_sftp() throws FetchingException {
////        DatacollectorConnection connection = new SFTPConnection(null, "/data/trend/", "Trend_L1_1_UI3_Input1_10DF.csv", "192.168.2.254", "admin", "envidatec4u", 200l, 2l);
////        DateTime until = new DateTime();
////        DateTime from = until.minusDays(10);
////        DataPoint datapoint = new DataPoint("test", "0",null);
////
////        Request request = RequestGenerator.createConnectionRequestWithTimeperiod(connection, datapoint, from, until);
////        DataCollector collector = new DataCollector(request);
////        collector.run();
////
////        String[] stringArrayInput = collector.getInputHandler().getStringArrayInput();
////        Assert.assertTrue(stringArrayInput.length >= 1);
//
//    }
//}
