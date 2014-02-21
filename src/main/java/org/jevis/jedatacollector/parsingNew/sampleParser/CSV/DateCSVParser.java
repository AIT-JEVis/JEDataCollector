/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.sampleParser.CSV;

import org.jevis.jedatacollector.parsingNew.sampleParser.GeneralDateParser;
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
        _timeIndex = timeIndex;
        _dateIndex = dateIndex;
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
        String[] line = ic.getLineInput();
        String date = line[_dateIndex];
//        String dateFormat = _dateFormat;

        String pattern = _dateFormat;
        String string = date;

        if (_timeFormat != null && _timeIndex != -1) {
            String time = line[_timeIndex];
            pattern += " " + _timeFormat;
            string += " " + time;
        }

        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        _dateTime = fmt.parseDateTime(string).toDateTime(DateTimeZone.UTC);
    }
}
