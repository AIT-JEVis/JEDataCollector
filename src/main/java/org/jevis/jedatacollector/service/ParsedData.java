/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service;

import java.util.*;
import org.jevis.jedatacollector.service.inputHandler.Sample;
import org.joda.time.DateTime;

/**
 *
 * @author Broder
 */
public class ParsedData {

    private List<Sample> _samples = new ArrayList<Sample>();

    public ParsedData() {
    }

    public void add(String sVal, DateTime cal) {
        System.out.println("Sampleval " + sVal);
        System.out.println("dateval " + cal.toString());
        _samples.add(new Sample(sVal, cal));
    }

    public List<String> getValues() {
        List<String> values = new ArrayList<String>();
        for (Sample s : _samples) {
            values.add(s.getVal());
        }
        return values;
    }

    public List<DateTime> getCal() {
        List<DateTime> dates = new ArrayList<DateTime>();
        for (Sample s : _samples) {
            dates.add(s.getCal());
        }
        return dates;
    }

    public List<String> getStringDates() {
        List<String> dates = new ArrayList<String>();
        for (Sample s : _samples) {
            dates.add(s.getCal().toString());
        }
        return dates;
    }

    public List<Sample> getSamples() {
        return _samples;
    }
}
