/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew;

import org.joda.time.DateTime;

/**
 *
 * @author broder
 */
public class Result implements Comparable<Result> {

    private double _value;
    private DateTime _date;
    private long _datapoint;

    public Result(long datapoint, double val, DateTime date) {
        _datapoint = datapoint;
        _value = val;
        _date = date;
    }

    public double getValue() {
        return _value;
    }

    public DateTime getDate() {
        return _date;
    }

    public long getOnlineID() {
        return _datapoint;
    }

    @Override
    public int compareTo(Result o) {
        if (_date.isBefore(o.getDate())) {
            return -1;
        }
        if (_date.isAfter(o.getDate())) {
            return 1;
        }
        return 0; //they are equal
    }
}
