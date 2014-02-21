/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.sampleParser.CSV;

import org.jevis.jedatacollector.parsingNew.sampleParser.GeneralValueParser;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;

/**
 *
 * @author broder
 */
public class ValueCSVParser implements GeneralValueParser {

    private int _valueIndex;
    private String _decSep;
    private String _thousandSep;
    private double _value;

    public ValueCSVParser(int valueIndex, String decS, String thousSep) {
        _valueIndex = valueIndex;
        _decSep = decS;
        _thousandSep = thousSep;
    }

    public int getValueIndex() {
        return _valueIndex;
    }

    @Override
    public String getThousandSeperator() {
        return _thousandSep;
    }

    @Override
    public String getDecimalSeperator() {
        return _decSep;
    }

    @Override
    public double getValue() {
        return _value;
    }

    @Override
    public void parse(InputHandler ic) {
        String[] line = ic.getLineInput();
        String sVal = line[_valueIndex];
        sVal = sVal.replaceAll("\\" + _thousandSep, "");
        sVal = sVal.replaceAll("\\" + _decSep, ".");
        _value = Double.parseDouble(sVal);
    }
}
