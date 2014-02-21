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
public class Result {

    private double _value;
    private DateTime _date;
    private long _datapoint;
    
    public Result(long datapoint, double val, DateTime date){
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

    public long getDatapoint() {
        return _datapoint;
    }
}
