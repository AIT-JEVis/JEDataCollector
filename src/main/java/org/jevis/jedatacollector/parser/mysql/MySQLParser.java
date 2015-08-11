/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parser.mysql;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import org.jevis.api.JEVisObject;
import org.jevis.commons.parsing.DataCollectorParser;
import org.jevis.commons.parsing.Result;
import org.jevis.commons.parsing.inputHandler.InputHandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisType;
import org.jevis.commons.DatabaseHelper;
import org.jevis.commons.JEVisTypes;
import org.jevis.commons.parsing.xmlParsing.XMLParsing;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author AIT Gschweicher
 */
public class MySQLParser implements DataCollectorParser {
    private Integer _dateIndex;
    private Integer _timeIndex;
    private String _dateFormat;
    private String _timeFormat;
    private String _decimalSeperator;
    private String _thousandSeperator;
    private List<MySQLDatapointParser> _datapointParsers = new ArrayList<MySQLDatapointParser>();
    private List<Result> _results = new ArrayList<Result>();
    
    @Override
    public List<Result> getResults() {
        return _results;
    }

    @Override
    public void parse(InputHandler ic) {

        ResultSet query = (ResultSet) ic.getRawInput();
        
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "Start MySQL parsing");
        //int columnCount = query.getMetaData().getColumnCount();
        
        try {
            while (query.next()) {
                String remote_id = query.getString(1);
                String remote_dateTime_str = query.getString(2);
                DateTime remote_dateTime = getDateTime(remote_dateTime_str);
                Double remote_value = query.getDouble(3);
                
                if (remote_dateTime == null)
                    continue;
                
                for (MySQLDatapointParser dpParser : _datapointParsers) {
                    String valId = dpParser.getValueIdentifier();
                    if (!valId.equals(remote_id))
                        continue;
                    
                    Long target = dpParser.getTarget();
                    
                    _results.add(new Result(target, remote_value, remote_dateTime));
                }

            }
        } catch (SQLException ex) {            
            Logger.getLogger(MySQLParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (!_results.isEmpty()) {
            String msg = "MySQLParser: Results size: " + _results.size() + "\n";
            Result last = _results.get(_results.size()-1);
            msg += "LastResult (Date,Target,Value): (" + last.getDate() + "," + last.getOnlineID() + "," + last.getValue() + ")";
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, msg);
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cant parse or cant find any parsable data");
        }
        
    }

    @Override
    public void initialize(JEVisObject equipmentObject) {

        try {
            JEVisClass parser = equipmentObject.getDataSource().getJEVisClass("MySQL Parser");
            JEVisObject parserObject = equipmentObject.getChildren(parser, true).get(0);
            JEVisClass jeClass = parserObject.getJEVisClass();

            JEVisType dateFormatType = jeClass.getType(JEVisTypes.Parser.DATE_FORMAT);
            JEVisType timeFormatType = jeClass.getType(JEVisTypes.Parser.TIME_FORMAT);
            JEVisType decimalSeperatorType = jeClass.getType(JEVisTypes.Parser.DECIMAL_SEPERATOR);
            JEVisType thousandSeperatorType = jeClass.getType(JEVisTypes.Parser.THOUSAND_SEPERATOR);

            _dateFormat = DatabaseHelper.getObjectAsString(parserObject, dateFormatType);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "DateFormat: " + _dateFormat);
            _timeFormat = DatabaseHelper.getObjectAsString(parserObject, timeFormatType);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "TimeFormat: " + _timeFormat);
            _decimalSeperator = DatabaseHelper.getObjectAsString(parserObject, decimalSeperatorType);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "DecimalSeperator: " + _decimalSeperator);
            _thousandSeperator = DatabaseHelper.getObjectAsString(parserObject, thousandSeperatorType);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "ThousandSeperator: " + _thousandSeperator);

          
        } catch (JEVisException ex) {
            Logger.getLogger(XMLParsing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void addDataPointParser(Long datapointID, String target, String mappingIdentifier, String valueIdentifier) {
        _datapointParsers.add(new MySQLDatapointParser(datapointID, target, mappingIdentifier, valueIdentifier, _decimalSeperator, _thousandSeperator));    
    }
    
    
    private DateTime getDateTime(String dateTime) {
        String input = dateTime;
        try {
            
            String pattern = _dateFormat;
            DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
            return fmt.parseDateTime(input);
        } catch (Exception ex) {
            ex.printStackTrace();
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.WARN, "Date not parsable: " + input);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.WARN, "DateFormat: " + _dateFormat);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.WARN, "DateIndex: " + _dateIndex);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.WARN, "TimeFormat: " + _timeFormat);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.WARN, "TimeIndex: " + _timeIndex);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.WARN, "Exception: " + ex);
        }

        if (_dateFormat == null) {
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "No Datetime found");
            return null;
        } else {
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "Current Datetime");
            return new DateTime();
        }

    }
}
