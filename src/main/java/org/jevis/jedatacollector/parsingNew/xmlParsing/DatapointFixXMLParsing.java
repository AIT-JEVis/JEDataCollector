/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.xmlParsing;

import org.jevis.jedatacollector.parsingNew.GeneralMappingParser;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;

/**
 *
 * @author bf
 */
public class DatapointFixXMLParsing implements GeneralMappingParser {

    private boolean _isInFile;
    private long _datapoint;

    public DatapointFixXMLParsing(boolean incsv, long datapoint) {
        _isInFile = incsv;
        _datapoint = datapoint;
    }

    @Override
    public boolean isInFile() {
        return _isInFile;
    }

    @Override
    public long getDatapoint() {
        return _datapoint;
    }

    @Override
    public void parse(InputHandler ic) {
        //no parsing necessary
    }

    @Override
    public String getMappingValue() {
        return null; //no mapping value
    }

    @Override
    public boolean isMappingSuccessfull() {
        return true; //no mapping needed
    }
}
