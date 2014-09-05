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
import org.jevis.commons.parsing.GenericParser;
import org.jevis.commons.parsing.ParsingFactory;
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.connection.ConnectionFactory;
import org.jevis.jedatacollector.exception.FetchingException;

/**
 *
 * @author Broder
 */
public class Data {

//    public static TimeZone _defaultTimezone = TimeZone.getDefault();
    private DatacollectorConnection _connection;
    private GenericParser _parsingData;
    private Equipment _dataLoggerData;
    private List<DataPoint> _datapoints;

    public Data(JEVisObject parser, JEVisObject connection, JEVisObject equipment, List<JEVisObject> datapoints) {
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

    public List<DataPoint> getDatapoints() {
        return _datapoints;
    }

    public Equipment getEquipment() {
        return _dataLoggerData;
    }

    public DatacollectorConnection getConnection() {
        return _connection;
    }

    public GenericParser getParsing() {
        return _parsingData;
    }
}
