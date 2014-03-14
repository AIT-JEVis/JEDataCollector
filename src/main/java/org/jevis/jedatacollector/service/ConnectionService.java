/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service;

import java.util.*;
import org.jevis.jedatacollector.data.NewDataPoint;
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;
import org.jevis.jedatacollector.service.inputHandler.InputFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author broder
 */
public class ConnectionService {

    private DatacollectorConnection _connection;

    public ConnectionService(DatacollectorConnection connection) {
        _connection = connection;
    }

    public void connect() throws FetchingException {
        System.out.println("---verbinde---");
        _connection.connect();
        System.out.println("---Verbinden erfolgreich---");
    }

    public InputHandler sendSamplesRequest(DateTime from, DateTime until, NewDataPoint dp) throws FetchingException {
        System.out.println("---Send Sample Request---");

//        Calendar fromAfter = GregorianCalendar.getInstance();
//        fromAfter.setTimeInMillis(from.getMillis());
//        fromAfter.setTimeZone(timeZone);
//        Calendar toAfter = GregorianCalendar.getInstance();
//        toAfter.setTimeZone(timeZone);
//        TimeSet timeSet = new TimeSet(new Date(fromAfter.getTimeInMillis()), new Date(toAfter.getTimeInMillis()));
        List<Object> answer = _connection.sendSampleRequest(dp, from, until);  //TODO dp in den converter
        System.out.println("###Initialisiere InputConverter###");
        InputHandler inputConverter = InputFactory.getInputConverter(answer);
        inputConverter.setInput(answer);
        inputConverter.setDataPoint(dp);
        return inputConverter;
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
}
