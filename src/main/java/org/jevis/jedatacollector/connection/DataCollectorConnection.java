/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.connection;

import java.io.InputStream;
import java.util.List;
import org.jevis.api.JEVisObject;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.jedatacollector.exception.FetchingException;
import org.joda.time.DateTime;

/**
 *
 * @author max
 */
public interface DataCollectorConnection {

    /**
     * Connects to a device.
     *
     * @return true if connection was successfull
     * @throws URLException thrown of URL is incorrect
     * @throws ConnectionTimeOutException thrown if time out occured
     */
    boolean connect() throws FetchingException;

    /**
     * Sends a sample request to the specified device.
     *
     * @param device The device the request shall be sent to
     * @param dp A device's datapoint
     * @param ts The wanted timeset for samples
     * @return Returns an object which's type is determined by the connection
     * type
     */
    InputHandler sendSampleRequest(DataPoint dp, DateTime from, DateTime until) throws FetchingException;

    /**
     * Indicates, whether a device returns all the values it was asked for or
     * whether it has a maximum value. If it's the latter case, the fetch thread
     * does not know if it has received all available data and therefore calls
     * the device several times. If the connection to the device is expansive,
     * the method should return false, if cost is no problem, it should return
     * true.
     *
     * @return true if data is limited, false otherwise
     */
//    boolean returnsLimitedSampleCount();

    void initialize(JEVisObject object) throws FetchingException;
    
    public String getWholeFilePath();
    
    public String getTimezone();
    
    public String getName();
    
//    public String getConnectionType();

}
