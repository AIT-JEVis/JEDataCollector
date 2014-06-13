/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.xmlParsing;

import org.jevis.jedatacollector.parsingNew.GeneralDateParser;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Node;

/**
 *
 * @author bf
 */
public class DateXMLParsing implements GeneralDateParser {

    private String _dateFormat;
    private String _dateTag;
    private boolean _dateAttribute;
    private String _timeFormat;
    private String _timeTag;
    private boolean _timeAttribute;
    private DateTime _dateTime;

    public DateXMLParsing(String dateFormat, String dateTag, Boolean dateAttribute, String timeFormat, String timeTag, Boolean timeAttribute) {
        _dateFormat = dateFormat;
        _dateTag = dateTag;
        _dateAttribute = dateAttribute;
        _timeFormat = timeFormat;
        _timeTag = timeTag;
        _timeAttribute = timeAttribute;
    }

    public DateXMLParsing(String dateFormat, String dateTag, boolean dateAttribute) {
        _dateFormat = dateFormat;
        _dateTag = dateTag;
        _dateAttribute = dateAttribute;
    }

    @Override
    public String getTimeFormat() {
        return _dateFormat;
    }

    @Override
    public String getDateFormat() {
        return _dateFormat;
    }

    @Override
    public DateTime getDateTime() {
        return _dateTime;
    }

    @Override
    public void parse(InputHandler ic) {
        Node xmlInput = ic.getXMLInput();

        //get the pattern
        String pattern = _dateFormat;
        if (_timeFormat != null) {
            pattern += " " + _timeFormat;
        }

        //get the date
        String date = null;
        if (_dateAttribute) {
            date = xmlInput.getAttributes().getNamedItem(_dateTag).getTextContent();
        } else {
            for (int i = 0; i < xmlInput.getChildNodes().getLength(); i++) {
                Node currentNode = xmlInput.getChildNodes().item(i);
                String currentName = currentNode.getNodeName();
                if (currentName.equals(_dateTag)) {
                    date = currentNode.getTextContent();
                    break;
                }
            }
        }
        
        //get the time
        String time = null;
        if (_timeAttribute) {
            time = xmlInput.getAttributes().getNamedItem(_timeTag).getTextContent();
        } else {
            for (int i = 0; i < xmlInput.getChildNodes().getLength(); i++) {
                Node currentNode = xmlInput.getChildNodes().item(i);
                String currentName = currentNode.getNodeName();
                if (currentName.equals(_timeTag)) {
                    time = currentNode.getTextContent();
                    break;
                }
            }
        }
        
        //save the date and time
        String dateAndTime = "";
        if(date!=null){
            dateAndTime = date;
        }
        
        if(time!=null){
            dateAndTime = dateAndTime + time;
        }

        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        _dateTime = fmt.parseDateTime(dateAndTime);
    }

//    @Override
//    public DateTimeZone getTimeZone() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
