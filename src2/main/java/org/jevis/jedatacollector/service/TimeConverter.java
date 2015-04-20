/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jevis.jedatacollector.DataCollector;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author bf
 */
public class TimeConverter {

    public static DateTime convertTime(DateTimeZone from, DateTime time) {
        long timeInMillis = time.getMillis();
        DateTime dateTime = new DateTime(timeInMillis, from);
        Logger.getLogger(DataCollector.class.getName()).log(Level.ALL, "DateTime before: " + dateTime);
//        long nextTransition = from.nextTransition(timeInMillis) - timeInMillis;
//        long currentOffset = from.getOffset(timeInMillis);
//        from.getStandardOffset(timeInMillis);
        DateTime tmpTime = dateTime;
//        if (_lastDateInUTC != null) {
//            tmpTime = convertTimeInTransitionRange(from, dateTime, _lastDateInUTC);
//        }
        dateTime = tmpTime.toDateTime(DateTimeZone.UTC);
//        _lastDateInUTC = dateTime;
        Logger.getLogger(DataCollector.class.getName()).log(Level.ALL, "DateTime after: " + dateTime);
        return dateTime;
    }
}
