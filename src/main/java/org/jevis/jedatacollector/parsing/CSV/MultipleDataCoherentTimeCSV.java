/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsing.CSV;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.jeapi.JEVisClass;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jeapi.JEVisType;


/**
 *
 * @author max
 */
public class MultipleDataCoherentTimeCSV extends CSVSingleLineEntry {

    private Integer _dataPointIndex;
    private Integer _dataLoggerIndex;

    @Override
    public void initialize(JEVisObject n) {
        try {
            super.initialize(n);

            JEVisClass type = n.getJEVisClass();
            JEVisType dataLoggerIndex = type.getType("Index Data Logger");
            JEVisType dataPointIndex = type.getType("Index Data Point");

            if (n.getAttribute(dataLoggerIndex) != null) {
                _dataLoggerIndex = (Integer) n.getAttribute(dataLoggerIndex).getLatestSample().getValue();
            }

            if (n.getAttribute(dataPointIndex) != null) {
                _dataPointIndex = (Integer) n.getAttribute(dataPointIndex).getLatestSample().getValue();
            }
        } catch (JEVisException ex) {
            Logger.getLogger(MultipleDataCoherentTimeCSV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    @Override
//    protected String getChannel(String line) {
//        try {
//            return removeEnclosing(line.split(_seperatorColumn, -1)[_dataPointIndex - 1]);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    @Override
//    protected String getLogger(String line) {
//        if (_dataLoggerIndex == null) {
//            return null;
//        } else {
//            return removeEnclosing(line.split(_seperatorColumn, -1)[_dataLoggerIndex - 1]);
//        }
//    }
}
