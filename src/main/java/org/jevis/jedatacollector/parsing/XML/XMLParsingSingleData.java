/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsing.XML;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisType;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.exception.FetchingExceptionType;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author max
 */
public class XMLParsingSingleData extends XMLParsing {

    private String _tagValue;

    @Override
    public void initialize(JEVisObject n) {
        try {
            super.initialize(n);

            JEVisClass type = n.getJEVisClass();
            JEVisType valueTag = type.getType("Value Tag");
            _tagValue = (String) n.getAttribute(valueTag).getLatestSample().getValue();
        } catch (JEVisException ex) {
            Logger.getLogger(XMLParsingSingleData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void getParsedData(Document doc) throws FetchingException {
        NodeList nl = doc.getElementsByTagName(_tagSample);
        Node logger, child, attr;
        Object val = null;
        DateTime cal = null;

        if (nl == null) {
            throw new FetchingException(_id, FetchingExceptionType.PARSING_SAMPLE_TAG_ERROR);
        }

        for (int i = 0; i < nl.getLength(); i++) {
            val = null;
            cal = null;

            logger = nl.item(i);

            for (int j = 0; j < logger.getChildNodes().getLength(); j++) {
                child = logger.getChildNodes().item(j);
                if (child.getNodeName().equals(_tagValue)) {
                    val = child.getTextContent();
                }
                if (child.getNodeName().equals(_tagTime)) {
                    cal = getDate(child);
                }
            }

            if (val == null) {
                for (int j = 0; j < logger.getAttributes().getLength(); j++) {
                    attr = logger.getAttributes().item(j);
                    if (attr.getNodeName().equals(_tagValue)) {
                        val = attr.getTextContent();
                        break;
                    }
                }
            }

            if (cal == null) {
                for (int j = 0; j < logger.getAttributes().getLength(); j++) {
                    attr = logger.getAttributes().item(j);
                    if (attr.getNodeName().equals(_tagTime)) {
                        cal = getDate(attr);
                        break;
                    }
                }
            }

            if (val != null && cal != null) {
//                pd.addSample(JevSampleImpl.createJevSample(val, cal));
            }
        }

        if (val == null) {
            throw new FetchingException(_id, FetchingExceptionType.PARSING_VALUE_TAG_ERROR);
        }

        if (cal == null) {
            throw new FetchingException(_id, FetchingExceptionType.PARSING_TIME_TAG_ERROR);
        }

    }
}
