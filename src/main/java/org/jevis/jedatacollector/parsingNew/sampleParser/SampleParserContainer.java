/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.sampleParser;

/**
 * Interface for parsing a line in a csv File
 *
 * @author broder
 */
public class SampleParserContainer {

    private GeneralDatapointParser _dpParser;
    private GeneralDateParser _dateParser;
    private GeneralValueParser _valueParser;

    public SampleParserContainer(GeneralDatapointParser _dpParser, GeneralDateParser _dateParser, GeneralValueParser _valueParser) {
        this._dpParser = _dpParser;
        this._dateParser = _dateParser;
        this._valueParser = _valueParser;
    }

    public GeneralDateParser getDateParser() {
        return _dateParser;
    }

    public GeneralDatapointParser getDpParser() {
        return _dpParser;
    }

    public GeneralValueParser getValueParser() {
        return _valueParser;
    }
}
