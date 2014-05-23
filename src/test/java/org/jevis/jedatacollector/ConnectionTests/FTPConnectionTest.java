/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.ConnectionTests;

import junit.framework.Assert;
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

        String[] stringArrayInput = collector.getInputHandler().getStringArrayInput();
        Assert.assertTrue(stringArrayInput.length == 3007);

    }
}
