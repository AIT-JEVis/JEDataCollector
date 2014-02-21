/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsing.XML;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import java.io.IOException;
import java.io.StringReader;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jevis.jeapi.JEVisClass;
import org.jevis.jeapi.JEVisException;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.parsing.IParsing;
import org.jevis.jedatacollector.service.ParsedData;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;

import org.jevis.jeapi.JEVisObject;
import org.jevis.jeapi.JEVisType;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author max
 */
public abstract class XMLParsing implements IParsing {

    protected String _tagSample;
    protected String _tagTime;
    protected long _id;
    private ParsedData _parsedData;

    @Override
    public void initialize(JEVisObject pn) {
        try {
            JEVisClass type = pn.getJEVisClass();
            JEVisType sampleTag = type.getType("Sample Tag");
            JEVisType timestampTag = type.getType("Timestamp Tag");

            _id = pn.getID();
            _tagSample = (String) pn.getAttribute(sampleTag).getLatestSample().getValue();
            _tagTime = (String) pn.getAttribute(timestampTag).getLatestSample().getValue();
        } catch (JEVisException ex) {
            Logger.getLogger(XMLParsing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void parseData(InputHandler ic, TimeZone tz) throws FetchingException {
//        List<Document> docs = new LinkedList<Document>();

        Document doc;
        String s = ic.getStringInput();
        doc = parseDocument(s);
        getParsedData(doc);


    }

    protected DateTime getDate(Node dateNode) {
        XMLGregorianCalendar newDate = XMLGregorianCalendarImpl.parse(dateNode.getTextContent());
        GregorianCalendar gc = newDate.toGregorianCalendar();
        return new DateTime(gc.getTimeInMillis());
    }

    private Document parseDocument(String s) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(s)));
        } catch (SAXException ex) {
            Logger.getLogger(XMLParsing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XMLParsing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLParsing.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public ParsedData getData() {
        return _parsedData;
    }

    protected abstract void getParsedData(Document doc) throws FetchingException;
}
