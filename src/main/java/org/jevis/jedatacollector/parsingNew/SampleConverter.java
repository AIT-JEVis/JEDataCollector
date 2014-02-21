/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author broder
 */
public class SampleConverter {

    private static boolean checkForValidParsing(List<Long> dpsp, List<Double> vsp, List<DateTime> dsp) {
        if (dpsp.size() == vsp.size() && vsp.size() == dsp.size()) {
            return true;
        }
        if (dpsp.size() == vsp.size() && dsp.size() == 1) {
            return true;
        }
        if (dpsp.size() == 1 && vsp.size() == dsp.size()) {
            return true;
        }
        return false;
    }

    public SampleConverter() {
    }

    public static List<Result> convertSamples(List<Long> dpsp, List<Double> vsp, List<DateTime> dsp) {
        List<Result> results = new ArrayList<Result>();
        boolean checkForValidParsing = checkForValidParsing(dpsp, vsp, dsp);
        if (!checkForValidParsing) {
            return null;
        }
        if (dpsp.size() == vsp.size() && vsp.size() == dsp.size()) {  //same length for all
            for (int i = 0; i < dpsp.size(); i++) {
                long datapoint = dpsp.get(i);
                double val = vsp.get(i);
                DateTime date = dsp.get(i);
                results.add(new Result(datapoint, val, date));
            }
            return results;
        }
        if (dpsp.size() == vsp.size() && dsp.size() == 1) {  //only one date
            for (int i = 0; i < dpsp.size(); i++) {
                long datapoint = dpsp.get(i);
                double val = vsp.get(i);
                DateTime date = dsp.get(0);
                results.add(new Result(datapoint, val, date));
            }
            return results;
        }
        if (dpsp.size() == 1 && vsp.size() == dsp.size()) {  //only one datapoint
            for (int i = 0; i < vsp.size(); i++) {
                long datapoint = dpsp.get(0);
                double val = vsp.get(i);
                DateTime date = dsp.get(i);
                results.add(new Result(datapoint, val, date));
            }
            return results;
        }
        return results;
    }
}
