/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.sampleParser.CSV;

import org.jevis.jedatacollector.parsingNew.sampleParser.GeneralDatapointParser;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;

/**
 * The config file for a simple csv file
 *
 * @author broder
 */
public class DatapointCSVParser implements GeneralDatapointParser {

    private boolean _inCSV;
    private int _index;
    private long _datapoint;

    public DatapointCSVParser(boolean incsv, int index) {
        _inCSV = incsv;
        _index = index;
    }

    public int getDatapointIndex() {
        return _index;
    }

    @Override
    public boolean isInFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getDatapoint() {
        return _datapoint;
    }

    @Override
    public void parse(InputHandler ic) {
        String[] line = ic.getStringArrayInput();
        _datapoint = Long.parseLong(line[_index]);
    }
}
