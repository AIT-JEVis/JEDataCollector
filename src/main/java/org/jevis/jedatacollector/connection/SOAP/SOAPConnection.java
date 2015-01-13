/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.connection.SOAP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisType;
import org.jevis.commons.DatabaseHelper;
import org.jevis.commons.JEVisTypes;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.commons.parsing.inputHandler.InputHandlerFactory;
import org.jevis.jedatacollector.Launcher;
import org.jevis.jedatacollector.connection.ConnectionHelper;
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
public class SOAPConnection implements DataCollectorConnection {

    private javax.xml.soap.SOAPConnection _conn;
//    private String _xmlTemplate;
//    private String _address;
//    private URL _serverURL;
//    private String _dateFormat;
//    private String _sampleCount;
//    private Long _triesRead;
//    private Long _timeoutRead;
//    private Long _triesConnection;
//    private Long _timeoutConnection;
//    private Long _maximumDayRequest;
//    private Long _id;
    private URL _serverURL;
    private String _server;
    private String _dateFormat;
    private Integer _port;
    private Integer _connectionTimeout;
    private Integer _readTimeout;
    private Integer _maximumDayRequest;
    private Long _id;
    private String _userName;
    private String _password;
    private Boolean _ssl = false;
    private String _timezone;
    private String _uri;
    private Boolean _enabled;
    private String _name;

    public SOAPConnection() {
    }

//    public SOAPConnection(String template, String address, String dateFormat, String sampleCount, Long triesRead, Long timeoutRead, Long maximumDayRequest) {
//        _xmlTemplate = template;
//        _address = address;
//        _dateFormat = dateFormat;
//        _sampleCount = sampleCount;
//        _triesRead = triesRead;
//        _timeoutRead = timeoutRead;
//        _maximumDayRequest = maximumDayRequest;
//    }
    public SOAPConnection(Long id, Boolean ssl, String server, Integer port, Integer connectionTimeout, Integer readTimeout, String username, String password, String timezone) {
        _id = id;
        _ssl = ssl;
        _server = server;
        _port = port;
        _connectionTimeout = connectionTimeout;
        _readTimeout = readTimeout;
        _userName = username;
        _password = password;
        _timezone = timezone;
    }

    @Override
    public void initialize(JEVisObject soapObject) throws FetchingException {
//        try {
//            //        _id = cn.getID();
//            //        _xmlTemplate = cn.<String>getPropertyValue("XML Template");
//            //        _address = cn.<String>getPropertyValue("Server URL");
//            //        _dateFormat = cn.<String>getPropertyValue("Date Format");
//            //        _sampleCount = String.valueOf(cn.<Long>getPropertyValue("Sample Count"));
//            //        _triesRead = cn.<Long>getPropertyValue("Read Tries");
//            //        _timeoutRead = cn.<Long>getPropertyValue("Read Timeout (in sec.)");
//            //        _triesConnection = cn.<Long>getPropertyValue("Connection Tries");
//            //        _maximumDayRequest = cn.<Long>getPropertyValue("Maximum days for Request");
//            //        _maximumDayRequest = cn.<Long>getPropertyValue("Maximum days for Request");
//
//            JEVisClass type = cn.getJEVisClass();
//            JEVisType template = type.getType("XML Template");
//            JEVisType address = type.getType("Server URL");
//            JEVisType dateFormat = type.getType("Date Format");
//            JEVisType sampleCound = type.getType("Sample Count");
//            JEVisType triesRead = type.getType("Read Tries");
//            JEVisType timeoutRead = type.getType("Read Timeout (in sec.)");
//            JEVisType triesConnection = type.getType("Connection Tries");
//            JEVisType timeoutConnection = type.getType("Connection Timeout (in sec.)");
//            JEVisType maxRequests = type.getType("Maximum days for Request");
//
//            _id = cn.getID();
//            _xmlTemplate = (String) cn.getAttribute(template).getLatestSample().getValue();
//            _address = (String) cn.getAttribute(address).getLatestSample().getValue();
//            _dateFormat = (String) cn.getAttribute(dateFormat).getLatestSample().getValue();
//            _sampleCount = (String) cn.getAttribute(sampleCound).getLatestSample().getValue();
//            _triesRead = (Long) cn.getAttribute(triesRead).getLatestSample().getValue();
//            _timeoutRead = (Long) cn.getAttribute(timeoutRead).getLatestSample().getValue();
//            _triesConnection = (Long) cn.getAttribute(triesConnection).getLatestSample().getValue();
//            _timeoutConnection = (Long) cn.getAttribute(timeoutConnection).getLatestSample().getValue();
//            _maximumDayRequest = (Long) cn.getAttribute(maxRequests).getLatestSample().getValue();
//
//            if (_maximumDayRequest == null || _maximumDayRequest < 1) {
//                _maximumDayRequest = 10l;
//            }
//
//            if (_sampleCount.equals("0")) {
//                _sampleCount = "1000";
//            }
//        } catch (JEVisException ex) {
//            Logger.getLogger(SOAPConnection.class.getName()).log(Level.SEVERE, null, ex);
//        }

        try {
            JEVisClass soapType = Launcher.getClient().getJEVisClass(JEVisTypes.DataServer.SOAP.NAME);
//            JEVisObject soapObject = node.getChildren(soapType, true).get(0);
//            JEVisType dateFormat = soapType.getType(JEVisTypes.Connection.SOAP.DateFormat);
            JEVisType server = soapType.getType(JEVisTypes.DataServer.SOAP.HOST);
            JEVisType port = soapType.getType(JEVisTypes.DataServer.SOAP.PORT);
            JEVisType sslType = soapType.getType(JEVisTypes.DataServer.SOAP.SSL);
            JEVisType connectionTimeout = soapType.getType(JEVisTypes.DataServer.SOAP.CONNECTION_TIMEOUT);
            JEVisType readTimeout = soapType.getType(JEVisTypes.DataServer.SOAP.READ_TIMEOUT);
            JEVisType user = soapType.getType(JEVisTypes.DataServer.SOAP.USER);
            JEVisType password = soapType.getType(JEVisTypes.DataServer.SOAP.PASSWORD);
            JEVisType timezoneType = soapType.getType(JEVisTypes.DataServer.SOAP.TIMEZONE);
            JEVisType enableType = soapType.getType(JEVisTypes.DataServer.ENABLE);

            _id = soapObject.getID();
            _name = soapObject.getName();
//            _dateFormat = DatabaseHelper.getObjectAsString(soapObject, dateFormat);
            _server = DatabaseHelper.getObjectAsString(soapObject, server);
            _port = DatabaseHelper.getObjectAsInteger(soapObject, port);
            _connectionTimeout = DatabaseHelper.getObjectAsInteger(soapObject, connectionTimeout);
            _readTimeout = DatabaseHelper.getObjectAsInteger(soapObject, readTimeout);
            _ssl = DatabaseHelper.getObjectAsBoolean(soapObject, sslType);
            JEVisAttribute userAttr = soapObject.getAttribute(user);
            if (!userAttr.hasSample()) {
                _userName = "";
            } else {
                _userName = (String) userAttr.getLatestSample().getValue();
            }
            JEVisAttribute passAttr = soapObject.getAttribute(password);
            if (!passAttr.hasSample()) {
                _password = "";
            } else {
                _password = (String) passAttr.getLatestSample().getValue();
            }

            _timezone = DatabaseHelper.getObjectAsString(soapObject, timezoneType);
            _enabled = DatabaseHelper.getObjectAsBoolean(soapObject, enableType);
        } catch (JEVisException ex) {
            org.apache.log4j.Logger.getLogger(SOAPConnection.class.getName()).log(org.apache.log4j.Level.ERROR, null, ex);
        }
    }

    @Override
    public boolean connect() throws FetchingException {
        try {
            _conn = SOAPConnectionFactory.newInstance().createConnection();
            _uri = "http://";
            if (_userName != null) {
                _uri += _userName;
                if (_password != null) {
                    _uri += ":" + _password + "@";
                } else {
                    _uri += "@";
                }
            }
            _uri += _server;
            if (_port != null) {
                _uri += ":" + _port + "/DL/";
            } else {
                _uri += ":80/DL/";
            }
            System.out.println(_uri);
            _serverURL = new URL(_uri);

        } catch (MalformedURLException ex) {
            throw new FetchingException(_id, FetchingExceptionType.URL_ERROR);
        } catch (SOAPException ex) {
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    @Override
    public List<InputHandler> sendSampleRequest(DataPoint dp, DateTime from, DateTime until) throws FetchingException {
//        SimpleDateFormat sdf = null;
        DateTimeFormatter fmt = null;
        if (_dateFormat != null && !_dateFormat.equals("")) {
            fmt = DateTimeFormat.forPattern(_dateFormat);
        }

        List<SOAPMessage> soapResponses = new LinkedList<SOAPMessage>();

        String templateQuery = dp.getFileName();
        boolean containsToken = ConnectionHelper.containsToken(templateQuery);
        String realQuery = null;
        if (containsToken) {
            realQuery = ConnectionHelper.replaceDate(templateQuery, from);
        }

        Document doc = buildDocument(realQuery);
        SOAPMessage buildSOAPMessage = buildSOAPMessage(doc);
        List<InputHandler> inputHandler = new ArrayList<InputHandler>();
        try {
            SOAPMessage call = _conn.call(buildSOAPMessage, _uri);
            soapResponses.add(call);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            call.writeTo(out);
            String strMsg = new String(out.toByteArray());
            System.out.println(strMsg);
            //        soapRequests.add(buildSOAPMessage);
            //        for (int i = 0; i < soapRequests.size(); i++) {
            //        }
            //        }
        } catch (SOAPException ex) {
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        inputHandler.add(InputHandlerFactory.getInputConverter(soapResponses));
        return inputHandler;
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
            Logger.getLogger(DataCollectorConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        MimeHeaders headers = message.getMimeHeaders();
        headers.addHeader("SOAPAction", "\"\"");

        return message;
    }

    public SOAPMessage sendRequest(SOAPMessage sm) throws FetchingException {
        SOAPMessage answer = null;

        try {
            for (int i = 0; i < _connectionTimeout; i++) {
                SOAPSender s = new SOAPSender(_conn, sm, _serverURL, this);
                s.start();

                int j = 0;
                while (true) {
                    j++;

                    if (j > _readTimeout) {
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
            Logger.getLogger(DataCollectorConnection.class.getName()).log(Level.SEVERE, null, ex);
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

    @Override
    public String getTimezone() {
        return _timezone;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public Boolean isEnabled() {
        return _enabled;
    }

    @Override
    public Long getID() {
        return _id;
    }
}
