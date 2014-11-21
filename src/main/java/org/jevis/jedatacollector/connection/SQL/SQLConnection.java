/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.connection.SQL;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisType;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.jedatacollector.connection.ConnectionHelper;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.jevis.jedatacollector.exception.FetchingException;

import org.joda.time.DateTime;

/**
 *
 * @author max
 */
public class SQLConnection implements DataCollectorConnection {

    private String _dbDriver;
    private String _dbHost;
    private String _dbUser;
    private String _dbPass;
    private String _dbQuery;
    private String _dbDateFormat = "yyyy-MM-dd HH:mm:ss";
//    private String _dbDataPoint;
    private String _dbColumnValue;
    private String _dbColumnDate;
    private String _database;
    private Connection _connection;
    private Statement _statement;
    private TimeZone _tz;

    public SQLConnection() {
        _tz = TimeZone.getDefault();  //TODO wo kommt die her?
    }

    @Override
    public boolean connect() throws FetchingException {
        try {
            Class.forName(_dbDriver).newInstance();

            String con = "jdbc:mysql://" + _dbHost + ":3306/" + _database + "?" + "user=" + _dbUser + "&"
                    + "password=" + _dbPass;
            _connection = DriverManager.getConnection(con);
            _statement = _connection.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(SQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(SQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    @Override
    public List<InputHandler> sendSampleRequest(DataPoint dp, DateTime from, DateTime until) throws FetchingException {
        List<Object> returnList = new LinkedList<Object>();

        try {
            String q = URLDecoder.decode(_dbQuery, "UTF-8");
            q = ConnectionHelper.parseDateFrom(q, dp, _dbDateFormat, from);
            q = ConnectionHelper.parseDateTo(q, dp, _dbDateFormat, from);
            ResultSet resultSet = _statement.executeQuery(q);
            Calendar c = Calendar.getInstance();

            while (resultSet.next()) {
                c.setTimeInMillis(resultSet.getTimestamp(_dbColumnDate).getTime());
//                returnList.add(new JevSampleImpl(new JevCalendar(c.getTime(), _tz), resultSet.getDouble(_dbColumnValue)));
            }
            resultSet.close();
            _statement.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
//InputHandlerFactory.getInputConverter(returnList)
        return new ArrayList<InputHandler>();
    }

//    @Override
//    public boolean returnsLimitedSampleCount() {
//        return false;
//    }

    @Override
    public void initialize(JEVisObject node) throws FetchingException {
        try {
            //        _dbDriver = node.<String>getPropertyValue("Database Driver");
            //        _dbHost = node.<String>getPropertyValue("Server URL");
            //        _dbUser = node.<String>getPropertyValue("Username");
            //        _dbPass = node.<String>getPropertyValue("Password");
            //        _dbQuery = node.<String>getPropertyValue("SELECT Template");
            //        _database = node.<String>getPropertyValue("Database Name");
            ////        _dbDateFormat = node.<String>getPropertyValue("Date Format");
            ////        _dbDataPoint = node.<String>getPropertyValue("Date Format");
            //        _dbColumnDate = node.<String>getPropertyValue("Date Column");
            //        _dbColumnValue = node.<String>getPropertyValue("Value Column");
            ////        _connectionTimeout = node.<Long>getPropertyValue("Connection Timeout");
            ////        _readTimeout = node.<Long>getPropertyValue("Read Timeout");
            ////        _maximumDayRequest = node.<Long>getPropertyValue("Maximum days for Request");
            ////        _id = node.getID();
            JEVisClass type = node.getJEVisClass();
            JEVisType databaseDriver = type.getType("Database Driver");
            JEVisType serverURL = type.getType("Server URL");
            JEVisType user = type.getType("Username");
            JEVisType pass = type.getType("Password");
            JEVisType template = type.getType("SELECT Template");
            JEVisType database = type.getType("Database Name");
            JEVisType date = type.getType("Date Column");
            JEVisType value = type.getType("Value Column");

            _dbDriver = (String) node.getAttribute(databaseDriver).getLatestSample().getValue();
            _dbHost = (String) node.getAttribute(serverURL).getLatestSample().getValue();
            _dbUser = (String) node.getAttribute(user).getLatestSample().getValue();
            _dbPass = (String) node.getAttribute(pass).getLatestSample().getValue();
            _dbQuery = (String) node.getAttribute(template).getLatestSample().getValue();
            _database = (String) node.getAttribute(database).getLatestSample().getValue();
            //        _dbDateFormat = node.<String>getPropertyValue("Date Format");
            //        _dbDataPoint = node.<String>getPropertyValue("Date Format");
            _dbColumnDate = (String) node.getAttribute(date).getLatestSample().getValue();
            _dbColumnValue = (String) node.getAttribute(value).getLatestSample().getValue();
        } catch (JEVisException ex) {
            Logger.getLogger(SQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    @Override
//    public String getConnectionType() {
//        return JEVisTypes.Connection.SQL.Name;
//    }

    @Override
    public String getWholeFilePath() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTimezone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
