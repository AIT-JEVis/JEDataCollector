/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsing.CSV;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.jeapi.JEVisClass;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jeapi.JEVisType;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.parsing.IParsing;
import org.jevis.jedatacollector.service.ParsedData;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author max
 */
public abstract class CSVParsing implements IParsing {

    protected String _dateFormat;
    protected String _timeFormat;
    protected Integer _ignoreFirstNLines = 0;
    protected Integer _indexDate;
    protected Integer _indexTime;
    protected Integer _indexValue;
    protected String _seperatorColumn;
    protected String _seperatorDecimal;
    protected String _seperatorThousand;
    protected String _enclosedBy;
    protected long _id;
    protected ParsedData _parsedData;

    @Override
    public void initialize(JEVisObject pn) {
        try {
            //        _id = pn.getID();
            //        _dateFormat = pn.<String>getPropertyValue("Format Date");
            //        _timeFormat = pn.<String>getPropertyValue("Format Time");
            //        _indexDate = pn.<Long>getPropertyValue("Index Date").intValue();
            //        _seperatorColumn = pn.<String>getPropertyValue("Seperator Column");
            //        _seperatorDecimal = pn.<String>getPropertyValue("Seperator Decimal");
            //        _seperatorThousand = pn.<String>getPropertyValue("Seperator Thousand");
            //        _enclosedBy = pn.<String>getPropertyValue("Enclosed By");
            //
            //        if (pn.<Long>getPropertyValue("Index Time") != null)
            //        {
            //            _indexTime = pn.<Long>getPropertyValue("Index Time").intValue();
            //        }
            //        if (pn.<Long>getPropertyValue("Index Value") != null)
            //        {
            //            _indexValue = pn.<Long>getPropertyValue("Index Value").intValue();
            //        }
            //
            //        if (pn.<Long>getPropertyValue("Ignore first N lines") != null)
            //        {
            //        }
            //        }


                    JEVisClass jeClass = pn.getJEVisClass();
                    JEVisType dateFormat = jeClass.getType("Format Date");
                    JEVisType timeFormat = jeClass.getType("Format Time");
                    JEVisType indexDate = jeClass.getType("Index Date");
                    JEVisType seperatorColumn = jeClass.getType("Seperator Column");
                    JEVisType seperatorDecimal = jeClass.getType("Seperator Decimal");
                    JEVisType seperatorThousand = jeClass.getType("Seperator Thousand");
                    JEVisType enclosedBy = jeClass.getType("Enclosed By");
                    JEVisType indexTime = jeClass.getType("Index Time");
                    JEVisType indexValue = jeClass.getType("Index Value");
                    JEVisType ignoreFirstNLines = jeClass.getType("Ignore first N lines");

                    _id = pn.getID();
                    _dateFormat = (String) pn.getAttribute(dateFormat).getLatestSample().getValue();
                    System.out.println("DateFormat " + _dateFormat);
                    _timeFormat = (String) pn.getAttribute(timeFormat).getLatestSample().getValue();
                    System.out.println("ZeitFormat " + _timeFormat);
                    _indexDate = (Integer) pn.getAttribute(indexDate).getLatestSample().getValue();
                    System.out.println("INDEXDATE " + _indexDate);
                    _seperatorColumn = (String) pn.getAttribute(seperatorColumn).getLatestSample().getValue();
                    System.out.println("SepColumn " + _seperatorColumn);
                    _seperatorColumn = ";"; //TODO krieg dsa nicht vom WEbservice?
                    _seperatorDecimal = (String) pn.getAttribute(seperatorDecimal).getLatestSample().getValue();
                    System.out.println("SepDecimal " + _seperatorDecimal);
                    _seperatorThousand = (String) pn.getAttribute(seperatorThousand).getLatestSample().getValue();
                    System.out.println("SepThousand " + _seperatorThousand);
                    _enclosedBy = (String) pn.getAttribute(enclosedBy).getLatestSample().getValue();
                    if (pn.getAttribute(indexTime) != null) {
                        _indexTime = (Integer) pn.getAttribute(indexTime).getLatestSample().getValue();
                        System.out.println("IndexTime " + _indexTime);
                    }
                    if (pn.getAttribute(indexValue) != null) {
                        _indexValue = (Integer) pn.getAttribute(indexValue).getLatestSample().getValue();
                        System.out.println("IndexVal " + _indexValue);
                    }

                    if (pn.getAttribute(ignoreFirstNLines) != null) {
                        _ignoreFirstNLines = (Integer) pn.getAttribute(ignoreFirstNLines).getLatestSample().getValue();
                    }
        } catch (JEVisException ex) {
            Logger.getLogger(CSVParsing.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    @Override
    public void parseData(InputHandler ic, TimeZone tz) throws FetchingException {

        _parsedData = new ParsedData();

        String[] s = ic.getStringArrayInput();

        System.out.println("Size: " + s.length);

        try {
            int i = _ignoreFirstNLines;
            for (; i < s.length; i++) {
                parseLine(s[i], tz);
            }
            System.out.println("Zeile " + i + " fertig");
        } catch (RuntimeException e) {
            e.printStackTrace();
//            for (String ss : s) {
//                System.out.println(s);
//            }
        }
    }

    protected abstract void parseLine(String line, TimeZone tz) throws FetchingException;

    protected String removeEnclosing(String val) {
        if (_enclosedBy != null) {
            int vl = val.length();
            int el = _enclosedBy.length();

            val = val.substring(el, vl - el);
        }

        return val;
    }

    protected DateTime getCal(String sTime, String sDate, TimeZone tz) throws FetchingException {
        String pattern = _dateFormat;
        String string = sDate;

        if (_timeFormat != null) {
            pattern += " " + _timeFormat;
            string += " " + sTime;
        }

        System.out.println("sTime " + sTime);
        System.out.println("sDate " + sDate);
        System.out.println("pattern " + _dateFormat);


        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        DateTime date = fmt.parseDateTime(string);


        return date.toDateTime(DateTimeZone.UTC);
    }

    @Override
    public ParsedData getData() {
        return _parsedData;
    }
}
