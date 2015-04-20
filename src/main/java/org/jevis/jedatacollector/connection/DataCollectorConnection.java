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
     */
    public boolean connect();

    /**
     * Sends a sample request to the specified device.
     *
     * @param dp A device's datapoint
     * @param from 
     * @param until
     * @return Returns a list of InputHandler. The InputHandler contains the fetched information
     * type
     */
    public List<InputHandler> sendSampleRequest(DataPoint dp, DateTime from, DateTime until);

    /**
     * Initialization of the DataSource Object.
     * 
     * @param object The Data Source Object from the JEVis system.
     * @throws FetchingException 
     */
    public void initialize(JEVisObject object) throws FetchingException;

    /**
     * Returns the timezone of the DataSource
     * 
     * @return 
     */
    public String getTimezone();

    /**
     * Returns the Name of the DataSource in the JEVis system.
     * 
     * @return 
     */
    public String getName();

    /**
     * Returns the ID of the DataSource in the JEVis system.
     * @return 
     */
    public Long getID();

    /**
     * Returns true, if the DataSource is enabled in the JEVis system
     * 
     * @return 
     */
    public Boolean isEnabled();
    
    /**
     * Returns the Host given in the JEVis system
     * 
     * @return 
     */
    public String getHost();
    
    /**
     * Returns the Port given in the JEVis system,
     * 
     * @return 
     */
    public Integer getPort();
}
