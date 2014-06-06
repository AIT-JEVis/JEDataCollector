/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.data;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.connection.ConnectionFactory;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.parsing.ParsingFactory;
import org.jevis.jedatacollector.parsingNew.DataCollectorParser;
import org.joda.time.DateTime;

/**
 *
 * @author Broder
 */
public class Data {

    public static TimeZone _defaultTimezone = TimeZone.getDefault();
    private DatacollectorConnection _connection;
    private DataCollectorParser _parsingData;
    private Equipment _dataLoggerData;
    private List<NewDataPoint> _datapoints;
    private JEVisDataSource _datasource;
//    private OnlineData _onlineData;
//    private JEVisObject _newParsingData;

    public Data(JEVisObject parser, JEVisObject connection, JEVisObject equipment, List<JEVisObject> datapoints) {
        try {
            _datasource = equipment.getDataSource();
        } catch (JEVisException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
        _dataLoggerData = new Equipment(equipment);
        _datapoints = new ArrayList<NewDataPoint>();
        for (JEVisObject dp : datapoints) {
            _datapoints.add(new NewDataPoint(dp));
        }
        try {
            _connection = ConnectionFactory.getConnection(connection);
            _parsingData = ParsingFactory.getParsing(parser);
        } catch (FetchingException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
//        _newParsingData = parser;
//        loadOnlineData();
    }

//    public JEVisObject getJEVisParsingData(){
//        return _newParsingData;
//    }
    public TimeZone getTimeZone() {
        return _defaultTimezone;    //TODP ÜBERARBEITEN!!
    }

    public List<NewDataPoint> getDatapoints() {
        return _datapoints;
    }

    public Equipment getEquipment() {
        return _dataLoggerData;
    }

    public DatacollectorConnection getConnection() {
        return _connection;
    }

    public DataCollectorParser getParsing() {
        return _parsingData;
    }

    public DateTime getFrom(NewDataPoint dp) {
        if (dp != null) {
            try {
                JEVisObject onlineData = _datasource.getObject(dp.getOnlineID());
                JEVisAttribute attribute = onlineData.getAttribute("Raw Data"); //TODO iwo auslagern
                if (attribute.getLatestSample() != null) {
                    return attribute.getTimestampFromLastSample();
                } else {
                    //TODO iwas überlegen
                }
            } catch (JEVisException ex) {
                Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            //TODO wie oben das gleiche!
        }
        return null;
    }

    public JEVisDataSource getDatasource() {
        return _datasource;
    }

//    public OnlineData getOnlineData() {
//        return _onlineData;
//    }
//    private void loadOnlineData() {
//        _onlineData = new OnlineData(_datapointData.getOnlineData());
//    }
}
