/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.csvParsing;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jevis.jedatacollector.parsingNew.GeneralDateParser;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author broder
 */
public class DateCSVParser implements GeneralDateParser {

    private String _timeFormat;
    private String _dateFormat;
    private Integer _timeIndex;
    private Integer _dateIndex;
    private DateTime _dateTime;
    private DateTimeZone _timeZone;

    public DateCSVParser(String timeFormat, Integer timeIndex, String dateFormat, Integer dateIndex, DateTimeZone timezone) {
        _timeFormat = timeFormat;
        _dateFormat = dateFormat;
        if (timeIndex != null) {
            _timeIndex = timeIndex - 1;
        }
        if (dateIndex != null) {
            _dateIndex = dateIndex - 1;
        }
        _timeZone = timezone;
    }

    @Override
    public String getTimeFormat() {
        return _timeFormat;
    }

    @Override
    public String getDateFormat() {
        return _dateFormat;
    }

    public int getTimeIndex() {
        return _timeIndex;
    }

    public int getDateIndex() {
        return _dateIndex;
    }

    @Override
    public DateTime getDateTime() {
        return _dateTime;
    }

    @Override
    public void parse(InputHandler ic) {
        String[] line = ic.getCSVInput();
        String date = line[_dateIndex];
//        String dateFormat = _dateFormat;

        String pattern = _dateFormat;
        String format = date;

        if (_timeFormat != null && _timeIndex != -1) {
            String time = line[_timeIndex];
            pattern += " " + _timeFormat;
            format += " " + time;
        }
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "complete time " + format);
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "complete pattern " + pattern);

        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        _dateTime = fmt.parseDateTime(format);
//        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getTimeZone("CET")));
    }

    @Override
    public DateTimeZone getTimeZone() {
        return _timeZone;
    }
}
