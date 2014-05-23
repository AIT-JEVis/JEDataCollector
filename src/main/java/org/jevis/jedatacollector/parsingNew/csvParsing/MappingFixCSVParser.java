/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.csvParsing;

import org.jevis.jedatacollector.parsingNew.GeneralMappingParser;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;

/**
 *
 * @author broder
 */
public class MappingFixCSVParser implements GeneralMappingParser {

    private boolean _inFile;
    private int _index;
    private long _datapoint;

    public MappingFixCSVParser(boolean incsv, long datapoint) {
        _inFile = incsv;
        _datapoint = datapoint;
    }

    public int getDatapointIndex() {
        return _index;
    }

    @Override
    public boolean isInFile() {
        return _inFile;
    }

    @Override
    public long getDatapoint() {
        return _datapoint;
    }

    @Override
    public void parse(InputHandler ic) {
        // no parsing necessary
    }

    @Override
    public String getMappingValue() {
        return null; //has no mapping value, cause the onlineId is fix
    }

    @Override
    public boolean isMappingSuccessfull() {
        return true;
    }
}
