/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.data;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisType;

/**
 *
 * @author Broder
 */
public class DataPoint {

    public static String CHANNEL_ID = "ChannelID";
    public static String DATA_LOGGER_NAME = "Data Logger Name";
//    public static String FETCH_RATE = "Fetch Rate";
//    public static String JOB_STATE = "Job State";
    public static String ONLINE_ID = "OnlineID";
    public static String VALUE_SPEC = "Value Specification";
    private String _channelID;
    private Long _onlineID;
    private String _valueSpec;
    private String _dataLoggerName;
    private JEVisObject _jevisDatapoint;
    private Long _datapointID;
//    private Integer _fetchRate;
//    private String _jobState;
    JEVisObject _onlineData;
//    JEVisObject _dataPoint;

    public DataPoint(String channelID, String dataLoggerName, Long onlineID) {
        _channelID = channelID;
        _dataLoggerName = dataLoggerName;
        _onlineID = onlineID;
    }

    public DataPoint(JEVisObject dp) {
        _jevisDatapoint = dp;
        _datapointID = dp.getID();

        try {
//            _dataPoint = dp;
            JEVisClass type = dp.getJEVisClass();
            JEVisType channelIDType = type.getType(CHANNEL_ID);
            JEVisType dataLoggerNameType = type.getType(DATA_LOGGER_NAME);
//            JEVisType fetchRateType = type.getType(FETCH_RATE);
//            JEVisType jobStateType = type.getType(JOB_STATE);
            JEVisType onlineIDType = type.getType(ONLINE_ID);
            JEVisType valueSpecType = type.getType(VALUE_SPEC);

            _channelID = dp.getAttribute(channelIDType).getLatestSample().getValueAsString();
            if (dp.getAttribute(dataLoggerNameType).getLatestSample() != null) {
                _dataLoggerName = dp.getAttribute(dataLoggerNameType).getLatestSample().getValueAsString();
            }
//            _fetchRate = (Integer) dp.getAttribute(fetchRateType).getLatestSample().getValue();
//            _jobState = (String) dp.getAttribute(jobStateType).getLatestSample().getValue();
            _onlineID = dp.getAttribute(onlineIDType).getLatestSample().getValueAsLong();
            _valueSpec = dp.getAttribute(valueSpecType).getLatestSample().getValueAsString();
            _onlineData = dp.getDataSource().getObject(_onlineID);
//            _onlineData = dp.getDataSource().getObject(onlineID);
        } catch (JEVisException ex) {
            Logger.getLogger(DataPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getChannelID() {
        return _channelID;
    }

    public long getDatapointId() {
        return _datapointID;
    }

    public JEVisObject getJEVisDatapoint() {
        return _jevisDatapoint;
    }

    public JEVisObject getJEVisOnlineData() {
        return _onlineData;
    }

    public String getDataLoggerName() {
        return _dataLoggerName;
    }
//
//    public JEVisObject getDataPoint() {
//        return _dataPoint;
//    }
//
//    public Integer getFetchRate() {
//        return _fetchRate;
//    }
//
//    public String getJobState() {
//        return _jobState;
//    }

    public long getOnlineID() {
        return _onlineID;
    }

    public String getValueSpec() {
        return _valueSpec;
    }
}
