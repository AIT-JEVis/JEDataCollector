/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.data;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.commons.parsing.DataCollectorParser;
import org.jevis.commons.parsing.GenericParser;
import org.jevis.commons.parsing.ParsingFactory;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.jevis.jedatacollector.connection.ConnectionFactory;
import org.jevis.jedatacollector.exception.FetchingException;

/**
 *
 * @author Broder
 */
public class Data {

//    public static TimeZone _defaultTimezone = TimeZone.getDefault();
    private DataCollectorConnection _connection;
    private DataCollectorParser _parsingData;
    private Equipment _dataLoggerData;
    private List<DataPoint> _datapoints;
    private List<JEVisObject> _datapointsJEVIS;
    private JEVisObject _parserJEVIS;

    public Data(JEVisObject parser, JEVisObject connection, JEVisObject equipment, List<JEVisObject> datapoints) {
        _parserJEVIS = parser;
        _datapointsJEVIS = datapoints;
        _dataLoggerData = new Equipment(equipment);
        _datapoints = new ArrayList<DataPoint>();
        for (JEVisObject dp : datapoints) {
            _datapoints.add(new DataPoint(dp));
        }
        try {
            _connection = ConnectionFactory.getConnection(connection);
            _connection.initialize(equipment);
            _parsingData = ParsingFactory.getParsing(parser);
            _parsingData.initialize(equipment);
        } catch (FetchingException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JEVisException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Data(DataCollectorParser parser, DataCollectorConnection connection, Equipment equip, List<DataPoint> tmpList) {
        _dataLoggerData = equip;
        _connection = connection;
        _parsingData = parser;
        _datapoints = tmpList;
    }

    public List<DataPoint> getDatapoints() {
        return _datapoints;
    }

    public Equipment getEquipment() {
        return _dataLoggerData;
    }

    public DataCollectorConnection getConnection() {
        return _connection;
    }

    public DataCollectorParser getParsing() {
        return _parsingData;
    }
    
    public JEVisObject getJEVisParser(){
        return _parserJEVIS;
    }
    
    public List<JEVisObject> getJEVisDatapoints(){
        return _datapointsJEVIS;
    }
}
