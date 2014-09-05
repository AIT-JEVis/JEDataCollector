/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.connection.SOAP;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisType;
import org.jevis.jedatacollector.connection.ConnectionFactory;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.commons.JEVisTypes;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.exception.FetchingExceptionType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author max
 */
public class SOAPConnection implements DatacollectorConnection {

    private javax.xml.soap.SOAPConnection _conn;
    private String _xmlTemplate;
    private String _address;
    private URL _serverURL;
    private String _dateFormat;
    private String _sampleCount;
    private Long _triesRead;
    private Long _timeoutRead;
    private Long _triesConnection;
    private Long _timeoutConnection;
    private Long _maximumDayRequest;
    private Long _id;

    public SOAPConnection() {
    }

    public SOAPConnection(String template, String address, String dateFormat, String sampleCount, Long triesRead, Long timeoutRead, Long maximumDayRequest) {
        _xmlTemplate = template;
        _address = address;
        _dateFormat = dateFormat;
        _sampleCount = sampleCount;
        _triesRead = triesRead;
        _timeoutRead = timeoutRead;
        _maximumDayRequest = maximumDayRequest;
    }

    @Override
    public void initialize(JEVisObject cn) throws FetchingException {
        try {
            //        _id = cn.getID();
            //        _xmlTemplate = cn.<String>getPropertyValue("XML Template");
            //        _address = cn.<String>getPropertyValue("Server URL");
            //        _dateFormat = cn.<String>getPropertyValue("Date Format");
            //        _sampleCount = String.valueOf(cn.<Long>getPropertyValue("Sample Count"));
            //        _triesRead = cn.<Long>getPropertyValue("Read Tries");
            //        _timeoutRead = cn.<Long>getPropertyValue("Read Timeout (in sec.)");
            //        _triesConnection = cn.<Long>getPropertyValue("Connection Tries");
            //        _maximumDayRequest = cn.<Long>getPropertyValue("Maximum days for Request");
            //        _maximumDayRequest = cn.<Long>getPropertyValue("Maximum days for Request");

            JEVisClass type = cn.getJEVisClass();
            JEVisType template = type.getType("XML Template");
            JEVisType address = type.getType("Server URL");
            JEVisType dateFormat = type.getType("Date Format");
            JEVisType sampleCound = type.getType("Sample Count");
            JEVisType triesRead = type.getType("Read Tries");
            JEVisType timeoutRead = type.getType("Read Timeout (in sec.)");
            JEVisType triesConnection = type.getType("Connection Tries");
            JEVisType timeoutConnection = type.getType("Connection Timeout (in sec.)");
            JEVisType maxRequests = type.getType("Maximum days for Request");

            _id = cn.getID();
            _xmlTemplate = (String) cn.getAttribute(template).getLatestSample().getValue();
            _address = (String) cn.getAttribute(address).getLatestSample().getValue();
            _dateFormat = (String) cn.getAttribute(dateFormat).getLatestSample().getValue();
            _sampleCount = (String) cn.getAttribute(sampleCound).getLatestSample().getValue();
            _triesRead = (Long) cn.getAttribute(triesRead).getLatestSample().getValue();
            _timeoutRead = (Long) cn.getAttribute(timeoutRead).getLatestSample().getValue();
            _triesConnection = (Long) cn.getAttribute(triesConnection).getLatestSample().getValue();
            _timeoutConnection = (Long) cn.getAttribute(timeoutConnection).getLatestSample().getValue();
            _maximumDayRequest = (Long) cn.getAttribute(maxRequests).getLatestSample().getValue();

            if (_maximumDayRequest == null || _maximumDayRequest < 1) {
                _maximumDayRequest = 10l;
            }

            if (_sampleCount.equals("0")) {
                _sampleCount = "1000";
            }
        } catch (JEVisException ex) {
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean connect() throws FetchingException {
        try {
            _conn = SOAPConnectionFactory.newInstance().createConnection();

            if (_address.contains("://")) {
                _serverURL = new URL(_address);
            } else {
                _serverURL = new URL("http", _address, "");
            }



        } catch (MalformedURLException ex) {
            throw new FetchingException(_id, FetchingExceptionType.URL_ERROR);
        } catch (SOAPException ex) {
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    @Override
    public List<Object> sendSampleRequest(DataPoint dp, DateTime from, DateTime until) throws FetchingException {
//        SimpleDateFormat sdf = null;
        DateTimeFormatter fmt = null;
        if (_dateFormat != null && !_dateFormat.equals("")) {
            fmt = DateTimeFormat.forPattern(_dateFormat);
        }

        List<SOAPMessage> soapRequests = new LinkedList<SOAPMessage>();
        List<Object> soapResponses = new LinkedList<Object>();

        if (_maximumDayRequest != null && _maximumDayRequest > 0) {
//            TimeSetVector tsv = new TimeSetVector(ts);
//            tsv.splitIntoChunks(_maximumDayRequest.intValue(), 0, 0);

//            for (TimeSet tsPart : tsv) {
            soapRequests.add(getSOAPMessage(new String(_xmlTemplate), from, until, _sampleCount, dp.getChannelID(), dp.getDataLoggerName(), fmt));
//            }
        } else {
            soapRequests.add(getSOAPMessage(new String(_xmlTemplate), from, until, _sampleCount, dp.getChannelID(), dp.getDataLoggerName(), fmt));
        }

        for (int i = 0; i < soapRequests.size(); i++) {
            soapResponses.add(sendRequest(soapRequests.get(i)));
        }

        return soapResponses;
    }

    private SOAPMessage getSOAPMessage(String template, DateTime from, DateTime until, String sampleCount, String dpName, String loggerName, DateTimeFormatter fmt) {
        if (dpName != null) {
            template = template.replace("*DATAPOINT*", dpName);
        }

        if (loggerName != null) {
            template = template.replace("*DATALOGGER*", loggerName);
        }

        if (sampleCount != null) {
            template = template.replace("*SAMPLE_COUNT*", sampleCount);
        }

        if (fmt != null) {
            template = template.replace("*DATE_FROM*", fmt.print(from));
            template = template.replace("*DATE_TO*", fmt.print(until));
        }
//        else {
//            template = template.replace("*DATE_FROM*", fmt.getFrom().toXMLCalendar().toString());
//            template = template.replace("*DATE_TO*", fmt.getUntil().toXMLCalendar().toString());
//        }

        template = template.replace("*SYSTEM_TIME*", String.valueOf(System.nanoTime()));

        System.out.println("Soap Message #### " + template);
        Document doc = buildDocument(template);
        return buildSOAPMessage(doc);
    }

    private Document buildDocument(String s) {
        Document document = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = (Document) builder.parse(new InputSource(
                    new StringReader(s)));
        } catch (SAXException ex) {
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return document;
        }
    }

    private SOAPMessage buildSOAPMessage(Document doc) {
        MessageFactory msgFactory;
        SOAPMessage message = null;

        try {
            msgFactory = MessageFactory.newInstance();
            message = msgFactory.createMessage();
            SOAPPart soapPart = message.getSOAPPart();
            //         Load the SOAP text into a stream source
//            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            soapPart.setContent(new DOMSource(doc));

        } catch (Exception ex) {
            Logger.getLogger(DatacollectorConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        MimeHeaders headers = message.getMimeHeaders();
        headers.addHeader("SOAPAction", "\"\"");

        return message;
    }

    public SOAPMessage sendRequest(SOAPMessage sm) throws FetchingException {
        SOAPMessage answer = null;

        try {
            for (int i = 0; i < _triesRead; i++) {
                SOAPSender s = new SOAPSender(_conn, sm, _serverURL, this);
                s.start();

                int j = 0;
                while (true) {
                    j++;

                    if (j > _timeoutRead) {
                        break;
                    }

                    Thread.sleep(1000);
                    answer = s.getAnswer();

                    if (answer != null) {
                        break;
                    }
                }

                if (answer != null) {
                    break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DatacollectorConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (answer == null) {
            throw new FetchingException(_id, FetchingExceptionType.READING_TIMEOUT);
        }

        return answer;
    }

    @Override
    public String getWholeFilePath() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
//    public boolean returnsLimitedSampleCount() {
//        return true;
//    }

    public class NullOutputStream extends OutputStream {

        @Override
        public void write(int b) throws IOException {
        }
    }
    
//      @Override
//    public String getConnectionType() {
//        return JEVisTypes.Connection.SOAP.Name;
//    }
}
