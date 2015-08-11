package org.jevis.jedatacollector.connection.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.parser.mysql.MySQLInputHandler;
import org.joda.time.DateTime;

/**
 *
 * @author AIT Gschweicher
 */
public class MySQLConnection implements DataCollectorConnection {
    
    private Connection _con = null;
    
    private long _id;
    private String _name;
    
    private Boolean _enabled = false;
    private String  _dbHost;
    private Integer _dbPort = 3306;
    private String  _dbSchema;
    private String  _dbUser;
    private String  _dbPW;
    private Integer _dbTimeout = 2000;
    private String  _timezone;
    
    private String  _dbTable;
    private String  _dbColID;
    private String  _dbColDateTime;
    private String  _dbColValue;
    
    // "<identifier string>"; // <GUI-Type>
    private final String str_enabled =      "Enabled"; // Boolean
    private final String str_dbHost =       "Host";
    private final String str_dbPort =       "Port"; // Long
    private final String str_dbSchema =     "Schema";
    private final String str_dbUser =       "User";
    private final String str_password =     "Password";
    private final String str_dbTimeout =    "Connection Timeout"; // Long
    private final String str_timezone =     "Timezone"; // String
    
    private final String str_dbTable =      "Table"; // String
    private final String str_dbColID =      "Column ID"; // String
    private final String str_dbColDateTime ="Column DateTime"; // String
    private final String str_dbColValue =   "Column Value"; // String
    
    
    
    @Override
    public void initialize(JEVisObject object) throws FetchingException {
        // Load MySQL-driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Get connection-details
        try {
            _id = object.getID();
            _name = object.getName();
            
            // Enabled
            _enabled = getLatestAttributeValueAsBoolean(object, str_enabled, _enabled);
            
            // Host + Port + Schema + Table
            _dbHost = getLatestAttributeValue(object, str_dbHost);
            _dbPort = getLatestAttributeValueAsLong(object, str_dbPort,
                    (long) _dbPort).intValue();
            _dbSchema = getLatestAttributeValue(object, str_dbSchema);
            
            // Username + Password
            _dbUser = getLatestAttributeValue(object, str_dbUser);
            _dbPW = getLatestAttributeValue(object, str_password);
            
            // Connection Timeout + timezone
            _dbTimeout = getLatestAttributeValueAsLong(object, str_dbTimeout,
                    (long) _dbTimeout).intValue();
            _timezone = getLatestAttributeValue(object, str_timezone);
            
            // Table-information
            _dbTable = getLatestAttributeValue(object, str_dbTable);
            _dbColID = getLatestAttributeValue(object, str_dbColID);
            _dbColDateTime = getLatestAttributeValue(object, str_dbColDateTime);
            _dbColValue = getLatestAttributeValue(object, str_dbColValue);
            
            /*
            JEVisAttribute conTimeoutAttr = object.getAttribute(str_con_timeout);
            if (hasNonEmptySample(conTimeoutAttr))
                _connectionTimeout = conTimeoutAttr.getLatestSample().getValueAsLong();
            
            JEVisAttribute readTimeoutAttr = object.getAttribute(str_read_timeout);
            if (hasNonEmptySample(readTimeoutAttr))
                _readTimeout = readTimeoutAttr.getLatestSample().getValueAsLong();
            
            JEVisAttribute timezoneAttr = object.getAttribute(str_timezone);
            if (hasNonEmptySample(timezoneAttr))
                _timezone = timezoneAttr.getLatestSample().getValueAsString();
            
            */
        } catch (JEVisException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public boolean connect() {
        boolean connected = false;
        String conString = "jdbc:mysql://" + _dbHost + ":" + _dbPort + "/" 
                + _dbSchema + "?"
                + "user=" + _dbUser + "&password=" + _dbPW;
        try {
            _con = DriverManager.getConnection(conString);
            connected = _con.isValid(_dbTimeout);
        } catch (SQLException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return connected;
    }

    @Override
    public List<InputHandler> sendSampleRequest(DataPoint dp, DateTime from, DateTime until) {
        ArrayList<InputHandler> answerList = new ArrayList<InputHandler>();
        // SELECT id, value, date FROM table
        // WHERE (id = dp.getIdentifier) AND
        //   (date BETWEEN from AND until)
        String str_SQL_statement = "SELECT ?, ?, ? FROM " + _dbTable +
                " WHERE ? > ?";
        
        
        try {
            PreparedStatement p = _con.prepareStatement(str_SQL_statement);
            p.setString(1, _dbColID); // col_id
            p.setString(2, _dbColValue); // col_value
            p.setString(3, _dbColDateTime); // col_datetime
            p.setString(4, _dbTable);
            
            p.setString(5, _dbColDateTime); // col_datetime
            p.setTime(6, java.sql.Time(dp.getLastReadout().getMillis()));
            
            ResultSet query = p.executeQuery();
            
            MySQLInputHandler ih = new MySQLInputHandler(query);
            answerList.add(ih);
        } catch (SQLException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        if (answerList.isEmpty()) {
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ERROR, "Cant get any data from the device");
        }

        return answerList;
    }

    
    // ########################
    // Getters
    // ########################
    
    @Override
    public String getTimezone() {
        return _timezone;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public Boolean isEnabled() {
        return _enabled;
    }

    @Override
    public Long getID() {
        return _id;
    }

    
    @Override
    public String getHost() {
        return _dbHost;
    }

    @Override
    public Integer getPort() {
        return _dbPort;
    }
    
    // ########################
    // Helper-functions
    // ########################
    
    private void printConnectionData() {
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass().getName());
        
        logger.log(org.apache.log4j.Level.ALL, "Data Source ID: " + _id);
        logger.log(org.apache.log4j.Level.ALL, "Data Source Name: " + _name);
        logger.log(org.apache.log4j.Level.ALL, "Enabled: " + _enabled);
        logger.log(org.apache.log4j.Level.ALL, "Server: " + _dbHost);
        logger.log(org.apache.log4j.Level.ALL, "Port: " + _dbPort);
        logger.log(org.apache.log4j.Level.ALL, "ConnectionTimeout: " + _dbTimeout);
        //logger.log(org.apache.log4j.Level.ALL, "ReadTimeout: " + _readTimeout);
        logger.log(org.apache.log4j.Level.ALL, "Username: " + _dbUser);
        logger.log(org.apache.log4j.Level.ALL, "Password: " + _dbPW);
        logger.log(org.apache.log4j.Level.ALL, "Timezone: " + _timezone);
    }
    
    private boolean hasNonEmptySample(JEVisAttribute attr) throws JEVisException {
        if (attr == null)
            return false;
        if (!attr.hasSample())
            return false;
        if (attr.getLatestSample().getValueAsString().isEmpty())
            return false;
        
        return true;
    }
    
    private String getLatestAttributeValue(JEVisObject object, String attr)
            throws JEVisException {
        return getLatestAttributeValue(object, attr, "");
    }
    private String getLatestAttributeValue(JEVisObject object, String attr,
            String default_value) throws JEVisException {
        JEVisAttribute tmp_attr = object.getAttribute(attr);
        if (hasNonEmptySample(tmp_attr)) {
            return tmp_attr.getLatestSample().getValueAsString();
        }
        return default_value;
    }
    private Long getLatestAttributeValueAsLong(JEVisObject object, String attr,
            Long default_value) throws JEVisException {
        JEVisAttribute tmp_attr = object.getAttribute(attr);
        if (hasNonEmptySample(tmp_attr)) {
            return tmp_attr.getLatestSample().getValueAsLong();
        }
        return default_value;
    }
    private Boolean getLatestAttributeValueAsBoolean(JEVisObject object, String attr,
            Boolean default_value) throws JEVisException {
        JEVisAttribute tmp_attr = object.getAttribute(attr);
        if (hasNonEmptySample(tmp_attr)) {
            return tmp_attr.getLatestSample().getValueAsBoolean();
        }
        return default_value;
    }
}
