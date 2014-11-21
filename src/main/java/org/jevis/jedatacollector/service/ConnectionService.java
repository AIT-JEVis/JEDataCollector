/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service;

import java.util.*;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.jevis.jedatacollector.exception.FetchingException;
import org.joda.time.DateTime;

/**
 *
 * @author broder
 */
public class ConnectionService {

    private DataCollectorConnection _connection;

    public ConnectionService(DataCollectorConnection connection) {
        _connection = connection;
    }

    public void connect() throws FetchingException {
        System.out.println("---verbinde---");
        _connection.connect();
        System.out.println("---Verbinden erfolgreich---");
    }


//    /**
//     * Get Samples from the connected device with the specified time set and
//     * channel
//     *
//     * @param channel the channel to be read out
//     * @param timeSet the timeset of wanted data
//     * @return a list of samples
//     */
//    public List<Object> sendSampleRequest(NewDataPoint dp, TimeSet timeSet, TimeZone timeZone) throws FetchingException {
//        return _connection.sendSampleRequest(dp, timeSet, timeZone);
//    }

    public InputHandler sendSamplesRequest(DateTime from, DateTime until, List<DataPoint> dataPoints) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
