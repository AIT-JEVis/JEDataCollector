/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service;

import org.jevis.commons.parsing.DataCollectorParser;
import org.jevis.commons.parsing.SampleParserContainer;
import org.jevis.commons.parsing.inputHandler.InputHandler;

/**
 *
 * @author broder
 */
public class ParsingService {

    public static boolean checkValue(SampleParserContainer parser) {
        boolean validValue = parser.getValueParser().isValueValid();
        return validValue;
    }

    public static boolean checkDatapoint(SampleParserContainer parser) {
        return parser.getDpParser().isMappingFailing();
    }
    private DataCollectorParser _fileParser;

    public ParsingService(DataCollectorParser parser) {
        _fileParser = parser;
    }

    public void parseData(InputHandler ic) {
        _fileParser.parse(ic);
    }

    public DataCollectorParser getFileParser() {
        return _fileParser;
    }
}
