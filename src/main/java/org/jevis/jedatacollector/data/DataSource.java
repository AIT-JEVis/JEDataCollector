/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.data;

import org.jevis.commons.JEVisTypes;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisType;
import org.jevis.commons.DatabaseHelper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Broder
 */
public class DataSource {

//    private boolean _singleReader;
    private DateTimeZone _timezone;
    private DateTime _startDate;
    private String _startDateFormat;
    private Long _id;

//    public boolean isSingleConnection() {
//        return _singleReader;
//    }

    public DateTimeZone getTimezone() {
        return _timezone;
    }

    public DateTime getStartCollectingTime() {
        return _startDate;
    }

    public Long getID() {
        return _id;
    }

    public DataSource(JEVisObject equipment) {

        try {
            _id = equipment.getID();
            JEVisClass type = equipment.getJEVisClass();
            JEVisType singleReaderType = type.getType(JEVisTypes.Equipment.SINGLE_CONNECTION);
            JEVisType timezoneType = type.getType(JEVisTypes.Equipment.TIMEZONE);
            JEVisType startDateCollecting = type.getType(JEVisTypes.Equipment.STARTDATE);
            JEVisType startDateFormat = type.getType(JEVisTypes.Equipment.STARTDATEFORMAT);

//            _singleReader = DatabaseHelper.getObjectAsBoolean(equipment, singleReaderType);
            String timezoneName = DatabaseHelper.getObjectAsString(equipment, timezoneType);
            _timezone = DateTimeZone.forID(timezoneName);
            _startDateFormat = DatabaseHelper.getObjectAsString(equipment, startDateFormat);
            DateTimeFormatter fmt = DateTimeFormat.forPattern(_startDateFormat);
            String date = DatabaseHelper.getObjectAsString(equipment, startDateCollecting);
            _startDate = fmt.parseDateTime(date);
        } catch (JEVisException ex) {
            Logger.getLogger(DataSource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Long getJEVisID() {
        return _id;
    }
}
