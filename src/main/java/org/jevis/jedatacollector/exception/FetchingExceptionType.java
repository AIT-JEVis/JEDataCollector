/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.exception;

/**
 *
 * @author max
 */
public class FetchingExceptionType
{

    public static final FetchingExceptionType DSEQ_ERROR = new FetchingExceptionType(2, true, true, "Could not find the corresponding device with id ID");
    public static final FetchingExceptionType CONNECTION_DRIVER_ERROR = new FetchingExceptionType(3, true, true, "The connection driver could not be found for device ID");
    public static final FetchingExceptionType CONNECTION_DRIVER_UNKNOWN = new FetchingExceptionType(22, true, true, "For the given connection node for device ID, no suitable class could be found");
    public static final FetchingExceptionType PARSING_DRIVER_ERROR = new FetchingExceptionType(4, true, true, "The parsing driver could not be found for device ID");
    public static final FetchingExceptionType PARSING_DRIVER_UNKNOWN = new FetchingExceptionType(23, true, true, "For the given connection node for ID, no suitable class could be found");
//    public static final FetchingException STARTTIME_ERROR = 5;
    public static final FetchingExceptionType CONNECTION_ERROR = new FetchingExceptionType(6, true, true, "It was not possible to establish a connection when reading out device ID");
    public static final FetchingExceptionType CONNECTION_TIMEOUT = new FetchingExceptionType(7, true, true, "There was a timeout when trying to connect to device ID");
    public static final FetchingExceptionType READING_TIMEOUT = new FetchingExceptionType(8, true, true, "There was a timeout when trying to read data from device ID");
    public static final FetchingExceptionType PARSING_DATALOGGER_TAG_ERROR = new FetchingExceptionType(30, true, true, "The tag for the datalogger could not be found when parsing XML from device ID");
    public static final FetchingExceptionType PARSING_DATALOGGER_ATTRIBUTE_ERROR = new FetchingExceptionType(26, true, true, "The attribute for the datalogger could not be found when parsing XML from device ID");
    public static final FetchingExceptionType PARSING_SAMPLE_TAG_ERROR = new FetchingExceptionType(9, true, true, "The given sample tag was not found when trying to parse response from device ID");
    public static final FetchingExceptionType PARSING_VALUE_TAG_ERROR = new FetchingExceptionType(10, true, true, "The given value tag was not found when trying to parse response from device ID");
    public static final FetchingExceptionType PARSING_TIME_TAG_ERROR = new FetchingExceptionType(11, true, true, "The given time tag was not found when trying to parse response from device ID");
    public static final FetchingExceptionType INSERTING_ERROR = new FetchingExceptionType(12, false, true, "The system could not insert data from device ID");
    public static final FetchingExceptionType INCOMPETIBLE_DRIVER_PARTS = new FetchingExceptionType(13, true, true, "The given driver parts for device ID are not compatible");
    public static final FetchingExceptionType URL_ERROR = new FetchingExceptionType(14, true, true, "The given URL from device ID could not be parsed");
    public static final FetchingExceptionType DATE_PARSE_ERROR = new FetchingExceptionType(16, true, true, "The date template from device ID could not be parsed");
    public static final FetchingExceptionType FTP_TIME_PARSE_ERROR = new FetchingExceptionType(17, true, true, "The time format from device ID could not be parsed");
    public static final FetchingExceptionType OLD_STRUCTURE = new FetchingExceptionType(18, false, false, "Device ID seems to be created with the old structure");
    public static final FetchingExceptionType CALCULATION = new FetchingExceptionType(27, false, true, "The given datapoint ID seems to be a calculation node");
    public static final FetchingExceptionType OWN_DRIVER_PARSING_CLASS = new FetchingExceptionType(20, true, true, "The class given by the user for parsing node ID could not be found");
    public static final FetchingExceptionType OWN_DRIVER_CONNECTION_CLASS = new FetchingExceptionType(21, true, true, "The class given by the user for connecting node ID could not be found");;
    public static final FetchingExceptionType CLEAN_DATA_NODE_ERROR = new FetchingExceptionType(24, true, true, "The class given by the user for parsing node ID could not be found");
    public static final FetchingExceptionType NO_ONLINENODE_FOR_DATAPOINT = new FetchingExceptionType(28, true, false, "The given datapoint ID has no given online node");
    public static final FetchingExceptionType NO_DATAPOINT_FOR_ONLINENODE = new FetchingExceptionType(29, true, false, "The given online node ID has no given data point");
    public static final FetchingExceptionType DATAPOINT_NOT_FOUND = new FetchingExceptionType(31, true, false, "The given datapoint ID could not be found");
    
    private long _typeID;
    private boolean _todo;
    protected String _msg;
    private boolean _createAlarm;
    
    public FetchingExceptionType(long typeid, boolean todo, boolean createAlarm, String msg)
    {
        _createAlarm = createAlarm;
        _typeID = typeid;
        _todo = todo;
        _msg = msg;
    }

    public long getTypeID()
    {
        return _typeID;
    }

    public String getMessage()
    {
        return _msg;
    }

    public boolean hasTodo()
    {
        return _todo;
    }
    
    public boolean createAlarm()
    {
        return _createAlarm;
    }
}
