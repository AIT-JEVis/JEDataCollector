/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.data;

import org.jevis.commons.JEVisTypes;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisType;
import org.jevis.commons.DatabaseHelper;

/**
 *
 * @author Broder
 */
public class DataPoint {

    private String _channelID;
    private Long _onlineID;
    private String _valueSpec;
    private String _dataLoggerName;
    private JEVisObject _jevisDatapoint;
    private Long _datapointID;
    JEVisObject _onlineData;

    public DataPoint(String channelID, String dataLoggerName, Long onlineID) {
        _channelID = channelID;
        _dataLoggerName = dataLoggerName;
        _onlineID = onlineID;
    }

    public DataPoint(JEVisObject dp) {
        _jevisDatapoint = dp;
        _datapointID = dp.getID();

        try {
            JEVisClass type = dp.getJEVisClass();
            JEVisType channelIDType = type.getType(JEVisTypes.DataPoint.CHANNEL_ID);
            JEVisType dataLoggerNameType = type.getType(JEVisTypes.DataPoint.DATA_LOGGER_NAME);
            JEVisType onlineIDType = type.getType(JEVisTypes.DataPoint.ONLINE_ID);
            JEVisType valueSpecType = type.getType(JEVisTypes.DataPoint.VALUE_SPEC);
            _channelID = DatabaseHelper.getObjectAsString(dp, channelIDType);
            _dataLoggerName = DatabaseHelper.getObjectAsString(dp, dataLoggerNameType);
            _onlineID = DatabaseHelper.getObjectAsLong(dp, onlineIDType);
            _valueSpec = DatabaseHelper.getObjectAsString(dp, valueSpecType);
            _onlineData = dp.getDataSource().getObject(_onlineID);
        } catch (JEVisException ex) {
            Logger.getLogger(DataPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getChannelID() {
        return _channelID;
    }

    public Long getDatapointId() {
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

    public long getOnlineID() {
        return _onlineID;
    }

    public String getValueSpec() {
        return _valueSpec;
    }
}
