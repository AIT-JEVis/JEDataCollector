/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsing.XML;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.jeapi.JEVisClass;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jeapi.JEVisType;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.exception.FetchingExceptionType;

import org.joda.time.DateTime;
import org.w3c.dom.*;

/**
 *
 * @author max
 */
public class XMLParsingMultipleData extends XMLParsing {

    private String _dataLoggerAttribute;
    private String _dataLoggerTag;

    @Override
    public void initialize(JEVisObject n) {
        try {
            super.initialize(n);

            JEVisClass type = n.getJEVisClass();
            JEVisType dataLoggerAttr = type.getType("Data Logger Attribute");
            JEVisType dataLoggerTag = type.getType("Data Logger Tag");

            _dataLoggerAttribute = (String) n.getAttribute(dataLoggerAttr).getLatestSample().getValue();
            _dataLoggerTag = (String) n.getAttribute(dataLoggerTag).getLatestSample().getValue();
        } catch (JEVisException ex) {
            Logger.getLogger(XMLParsingMultipleData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void getParsedData(Document doc) throws FetchingException {
        NodeList nl = doc.getElementsByTagName(_dataLoggerTag);
        Node logger, child;
        String loggerName;
        String channelID;

        if (nl == null) {
            throw new FetchingException(_id, FetchingExceptionType.PARSING_DATALOGGER_TAG_ERROR);
        }

        for (int i = 0; i < nl.getLength(); i++) {
            logger = nl.item(i);
            loggerName = null;

            NamedNodeMap attributes = (NamedNodeMap) logger.getAttributes();
            for (int g = 0; g < attributes.getLength(); g++) {
                Attr attr = (Attr) attributes.item(g);
                if (attr.getName().equals(_dataLoggerAttribute)) {
                    loggerName = attr.getValue();
                }
            }

            if (loggerName == null) {
                throw new FetchingException(_id, FetchingExceptionType.PARSING_DATALOGGER_ATTRIBUTE_ERROR);
            }

            for (int j = 0; j < logger.getChildNodes().getLength(); j++) {
                if (logger.getChildNodes().item(j).getNodeName().equals(_tagSample)) {
                    DateTime cal = null;
                    NodeList vl = logger.getChildNodes().item(j).getChildNodes();

                    for (int k = 0; k < vl.getLength(); k++) {
                        child = vl.item(k);

                        if (child.getNodeName().equals(_tagTime)) {
                            cal = getDate(child);
                            continue;
                        }

                        channelID = child.getNodeName();
                        Object val = child.getTextContent();

//                        pd.addSample(loggerName, channelID, JevSampleImpl.createJevSample(child.getTextContent(), cal));
                    }

                    if (cal == null) {
//                        if (cal == null)
//                        {
//                            for (int j = 0; j < logger.getAttributes().getLength(); j++)
//                            {
//                                attr = logger.getAttributes().item(j);
//                                if (attr.getNodeName().equals(_tagTime))
//                                {
//                                    cal = getDate(attr);
//                                    break;
//                                }
//                            }
//                        }

                        throw new FetchingException(_id, FetchingExceptionType.PARSING_TIME_TAG_ERROR);
                    }
                }
            }
        }
    }
}
