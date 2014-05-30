/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsing.CSV;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisType;



/**
 *
 * @author max
 */
public class MultipleDataCoherentDataCSV extends CSVSingleLineEntry {

    private String _dataPointNameFormat;
    private Integer _dataLoggerIndex;
    private Integer _dataPointIndex;

    @Override
    public void initialize(JEVisObject n) {
        try {
            super.initialize(n);

            JEVisClass jeclass = n.getJEVisClass();
            JEVisType dataPointNameFormat = jeclass.getType("Data Point Name Format");
            JEVisType dataLoggerIndex = jeclass.getType("Data Logger Index");
            JEVisType dataPointIndex = jeclass.getType("Data Point Index");

            _dataPointNameFormat = (String) n.getAttribute(dataPointNameFormat).getLatestSample().getValue();

            if (n.getAttribute(dataLoggerIndex) != null) {
                _dataLoggerIndex = (Integer) n.getAttribute(dataLoggerIndex).getLatestSample().getValue();
            }

            if (n.getAttribute(dataPointIndex) != null) {
                _dataPointIndex = (Integer) n.getAttribute(dataPointIndex).getLatestSample().getValue();
            }
        } catch (JEVisException ex) {
            Logger.getLogger(MultipleDataCoherentDataCSV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    @Override
//    protected String getChannel(String line) {
//        String point;
//
//        if (_dataPointIndex == null) {
//            for (String s : _dataPointNameFormat.split("\\*DATAPOINT\\*")) {
//                line = line.replaceAll(s, "");
//            }
//
//            point = line;
//        } else {
//            point = line.split(_seperatorColumn, -1)[_dataPointIndex];
//        }
//
//        return removeEnclosing(point);
//    }
//
//    @Override
//    protected String getLogger(String line) {
//        if (_dataLoggerIndex == null) {
//            return null;
//        } else {
//            return removeEnclosing(line.split(_seperatorColumn, -1)[_dataLoggerIndex]);
//        }
//    }
}
