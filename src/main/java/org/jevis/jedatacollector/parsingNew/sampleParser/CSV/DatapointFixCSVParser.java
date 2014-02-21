/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.sampleParser.CSV;

import org.jevis.jedatacollector.parsingNew.sampleParser.GeneralDatapointParser;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;

/**
 *
 * @author broder
 */
public class DatapointFixCSVParser implements GeneralDatapointParser {

    private boolean _inCSV;
    private int _index;
    private long _datapoint;

    public DatapointFixCSVParser(boolean incsv, long datapoint) {
        _inCSV = incsv;
        _datapoint = datapoint;
    }

    public int getDatapointIndex() {
        return _index;
    }

    @Override
    public boolean isInFile() {
        return _inCSV;
    }

    @Override
    public long getDatapoint() {
        return _datapoint;
    }

    @Override
    public void parse(InputHandler ic) {
        // no parsing necessary
    }
}
