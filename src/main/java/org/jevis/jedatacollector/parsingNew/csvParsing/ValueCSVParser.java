/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.csvParsing;

import org.jevis.jedatacollector.parsingNew.GeneralValueParser;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;

/**
 *
 * @author broder
 */
public class ValueCSVParser implements GeneralValueParser {

    private int _valueIndex;
    private String _decSep;
    private String _thousandSep;
    private Double _value;
    private boolean _valueValid;
    private boolean _outofBounce;

    public ValueCSVParser(int valueIndex, String decS, String thousSep) {
        _valueIndex = valueIndex - 1;
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
    public Double getValue() {
        return _value;
    }

    @Override
    public void parse(InputHandler ic) {
        _valueValid = false;
        _outofBounce = false;
        String[] line = ic.getTmpInput();
        String sVal = null;
        try {
            sVal = line[_valueIndex];
            sVal = sVal.replaceAll("\\" + _thousandSep, "");
            sVal = sVal.replaceAll("\\" + _decSep, ".");
            _value = Double.parseDouble(sVal);
            _valueValid = true;
        } catch (NumberFormatException nfe) {
//            System.out.println("Value is wrong " + sVal);
        } catch (ArrayIndexOutOfBoundsException oob) {
            _outofBounce = true;
        }
    }

    @Override
    public boolean isValueValid() {
        return _valueValid;
    }

    public boolean outOfBounce() {
        return _outofBounce;
    }
}
