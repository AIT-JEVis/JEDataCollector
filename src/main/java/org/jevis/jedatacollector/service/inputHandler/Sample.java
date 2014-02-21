/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service.inputHandler;

import org.joda.time.DateTime;

/**
 *
 * @author Broder
 */
public class Sample {

    private String _val;
    private DateTime _cal;

    public Sample(String val, DateTime cal) {
        _val = val;
        _cal = cal;
    }

    public DateTime getCal() {
        return _cal;
    }

    public String getVal() {
        return _val;
    }
}
