/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service.inputHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.w3c.dom.Document;

/**
 *
 * @author bf
 */
public class SOAPMessageInputHandler extends InputHandler {

    private List<Document> _document;

    public SOAPMessageInputHandler(List<SOAPMessage> input) {
        super(input);
        _document = new ArrayList<Document>();
    }

    @Override
    public void convertInput() {
        List<SOAPMessage> input = (List<SOAPMessage>) _rawInput;
        for (SOAPMessage m : input) {
            try {
                _document.add(m.getSOAPBody().extractContentAsDocument());
            } catch (SOAPException ex) {
                Logger.getLogger(SOAPMessageInputHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public List<Document> getDocument() {
        return _document;
    }
}