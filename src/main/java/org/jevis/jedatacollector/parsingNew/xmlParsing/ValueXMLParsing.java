/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.xmlParsing;

import org.jevis.jedatacollector.parsingNew.GeneralValueParser;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;
import org.w3c.dom.Node;

/**
 *
 * @author bf
 */
public class ValueXMLParsing implements GeneralValueParser {

    private String _valueTag;
    private boolean _valueAttribute;
    private String _decSep;
    private String _thousandSep;
    private Double _value;
    private boolean _valueValid;
    private boolean _outofBounce;

    public ValueXMLParsing(String valueTag, boolean valueAttribute, String decS, String thousSep) {
        _valueTag = valueTag;
        _valueAttribute = valueAttribute;
        _decSep = decS;
        _thousandSep = thousSep;
    }

    @Override
    public Double getValue() {
        return _value;
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
    public boolean isValueValid() {
        return _valueValid;
    }

    @Override
    public void parse(InputHandler ic) {
        _valueValid = false;
        Node xmlInput = ic.getXMLInput();
        String sVal = null;
        try {
            if (_valueAttribute) {
                sVal = xmlInput.getAttributes().getNamedItem(_valueTag).getNodeValue();
            } else {
                for (int i = 0; i < xmlInput.getChildNodes().getLength(); i++) {
                    Node currentNode = xmlInput.getChildNodes().item(i);
                    String currentName = currentNode.getNodeName();
                    if (currentName.equals(_valueTag)) {
                        sVal = currentNode.getNodeValue();
                    }
                }
            }
            sVal = sVal.replaceAll("\\" + _thousandSep, "");
            sVal = sVal.replaceAll("\\" + _decSep, ".");
            _value = Double.parseDouble(sVal);
            _valueValid = true;
        } catch (NumberFormatException nfe) {
//            System.out.println("Value is wrong " + sVal);
        }
    }
}
