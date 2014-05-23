/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.CLIProperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author bf
 */
public class ParsingCLIParser {

    private String _parsingType;
    private String _quote;
    private String _delim;
    private int _headerlines;
    private int _valueIndex;
    private String _thousandSep;
    private String _decimalSep;
    private int _dateIndex;
    private String _dateformat;
    private int _timeIndex;
    private String _timeformat;

    public ParsingCLIParser(String path) {
        Properties prop = new Properties();

        try {
            //load a properties file
            prop.load(new FileInputStream(path));

            //get the property value and print it out
            _parsingType = prop.getProperty("type");
            _quote = prop.getProperty("quote");
            _delim = prop.getProperty("delim");
            _headerlines = Integer.parseInt(prop.getProperty("headerlines"));
            _valueIndex = Integer.parseInt(prop.getProperty("valueindex"));
            _thousandSep = prop.getProperty("thousandsep");
            _decimalSep = prop.getProperty("decimalsep");
            _dateIndex = Integer.parseInt(prop.getProperty("dateindex"));
            _dateformat = prop.getProperty("dateformat");
            _timeIndex = Integer.parseInt(prop.getProperty("timeindex"));
            _timeformat = prop.getProperty("timeformat");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * @return the _parsingType
     */
    public String getParsingType() {
        return _parsingType;
    }

    /**
     * @return the _quote
     */
    public String getQuote() {
        return _quote;
    }

    /**
     * @return the _delim
     */
    public String getDelim() {
        return _delim;
    }

    /**
     * @return the _headerlines
     */
    public int getHeaderlines() {
        return _headerlines;
    }

    /**
     * @return the _valueIndex
     */
    public int getValueIndex() {
        return _valueIndex;
    }

    /**
     * @return the _thousandSep
     */
    public String getThousandSep() {
        return _thousandSep;
    }

    /**
     * @return the _decimalSep
     */
    public String getDecimalSep() {
        return _decimalSep;
    }

    /**
     * @return the _dateIndex
     */
    public int getDateIndex() {
        return _dateIndex;
    }

    /**
     * @return the _dateformat
     */
    public String getDateformat() {
        return _dateformat;
    }

    public int getTimeIndex() {
        return _timeIndex;
    }

    public String getTimeformat() {
        return _timeformat;
    }
}
