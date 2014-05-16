/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.csvParsing;

import java.util.TimeZone;
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

    public DateCSVParser(String timeFormat, Integer timeIndex, String dateFormat, Integer dateIndex) {
        _timeFormat = timeFormat;
        _dateFormat = dateFormat;
        if (timeIndex != null) {
            _timeIndex = timeIndex - 1;
        }
        if (dateIndex != null) {
            _dateIndex = dateIndex - 1;
        }
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

        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        _dateTime = fmt.parseDateTime(format);
        System.out.println("Current time "+_dateTime);
//        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getTimeZone("CET")));
    }
}
