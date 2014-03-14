/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.ConnectionTests;

import org.jevis.jedatacollector.DataCollector;
import org.jevis.jedatacollector.Request;
import org.jevis.jedatacollector.RequestGenerator;
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.connection.FTP.FTPConnection;
import org.jevis.jedatacollector.data.NewDataPoint;
import org.jevis.jedatacollector.exception.FetchingException;
import org.joda.time.DateTime;

/**
 *
 * @author bf
 */
public class FTPConnectionTest {

    public void test_loytec_ftp() throws FetchingException {
        DatacollectorConnection connection = new FTPConnection("", "/data/trend/", "Trend_L1_1_UI1_Input_10C6.csv", "192.168.2.254", "admin", "envidatec4u", 200l, 2l);
        DateTime until = new DateTime();
        DateTime from = until.minusDays(10);
        NewDataPoint datapoint = new NewDataPoint("test", "0");

        Request request = RequestGenerator.createConnectionRequestWithTimeperiod(connection, datapoint, from, until);
        DataCollector collector = new DataCollector(request);
        collector.run();

        System.out.println(collector.getInputHandler().getStringArrayInput()[0]);
        System.out.println(collector.getInputHandler().getStringArrayInput()[1]);
        System.out.println(collector.getInputHandler().getStringArrayInput()[2]);
        System.out.println(collector.getInputHandler().getStringArrayInput()[3]);
        System.out.println(collector.getInputHandler().getStringArrayInput()[4]);

//        Document doc = ((SOAPMessageInputHandler) collector.getInputHandler()).getDocument().get(0);
//        NodeList nodeNames = doc.getElementsByTagName("UCPTpointName");
//        NodeList nodeDates = doc.getElementsByTagName("UCPTlogTime");
//        NodeList nodeValues = doc.getElementsByTagName("UCPTvalue");
//        Assert.assertTrue(nodeNames.getLength() > 0);
//        Assert.assertTrue(nodeDates.getLength() > 0);
//        Assert.assertTrue(nodeValues.getLength() > 0);
//        Assert.assertTrue(nodeNames.getLength() == nodeDates.getLength());
//        Assert.assertTrue(nodeValues.getLength() == nodeDates.getLength());
    }
}
