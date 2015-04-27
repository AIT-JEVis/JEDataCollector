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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
        try {
            JEVisClass soapType = Launcher.getClient().getJEVisClass(JEVisTypes.DataServer.SOAP.NAME);
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
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.ERROR, null, ex);
        }
    }

    @Override
    public boolean connect() {
        try {
            _conn = SOAPConnectionFactory.newInstance().createConnection();

        } catch (SOAPException ex) {
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.ERROR, ex.getMessage());
        }

        return true;
    }

    @Override
    public List<InputHandler> sendSampleRequest(DataPoint dp, DateTime from, DateTime until) {
        _uri = "";
        if (_userName != null) {
            _uri += _userName;
            if (_password != null) {
                _uri += ":" + _password + "@";
            } else {
                _uri += "@";
            }
        }
        String path = dp.getDirectory().getFolderName();
        _uri += _server;
        if (_port != null) {
            _uri += ":" + _port + path;
        } else {
            _uri += ":80" + path;
        }

        if (!_uri.contains("://")) {
            _uri = "http://" + _uri;
        }

        if (_ssl) {
            _uri = _uri.replace("http", "https");
        }
        Logger.getLogger(SOAPConnection.class.getName()).log(Level.ALL, "SOAP Uri: " + _uri);
        try {
            _serverURL = new URL(_uri);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.ERROR, ex.getMessage());
        }

        DateTimeFormatter fmt = null;
        if (_dateFormat != null && !_dateFormat.equals("")) {
            fmt = DateTimeFormat.forPattern(_dateFormat);
        }

        List<SOAPMessage> soapResponses = new LinkedList<SOAPMessage>();

        String templateQuery = dp.getFileName();
        boolean containsToken = ConnectionHelper.containsTokens(templateQuery);
        String realQuery = null;
        if (containsToken) {
            realQuery = ConnectionHelper.replaceDateFrom(templateQuery, from);
        } else {
            realQuery = templateQuery;
        }

        Document doc = buildDocument(realQuery);
        SOAPMessage buildSOAPMessage = buildSOAPMessage(doc);
        List<InputHandler> inputHandler = new ArrayList<InputHandler>();
        try {
            if (_ssl) {
                ConnectionHelper.doTrustToCertificates();
            }
            SOAPMessage call = _conn.call(buildSOAPMessage, _serverURL);
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
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.ERROR, ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.ERROR, ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.ERROR, ex.getMessage());
        } finally {
            try {
                _conn.close();
            } catch (SOAPException ex) {
                Logger.getLogger(SOAPConnection.class.getName()).log(Level.ERROR, ex.getMessage());
            }
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
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.ERROR, ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.ERROR, ex.getMessage());
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.ERROR, ex.getMessage());
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
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.ERROR, ex.getMessage());
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
            Logger.getLogger(SOAPConnection.class.getName()).log(Level.ERROR, ex.getMessage());
        }

        if (answer == null) {
            throw new FetchingException(_id, FetchingExceptionType.READING_TIMEOUT);
        }

        return answer;
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

    @Override
    public String getHost() {
        return _server;
    }

    @Override
    public Integer getPort() {
        return _port;
    }

    public void setServerURL(URL _serverURL) {
        this._serverURL = _serverURL;
    }

    public void setServer(String _server) {
        this._server = _server;
    }

    public void setDateFormat(String _dateFormat) {
        this._dateFormat = _dateFormat;
    }

    public void setPort(Integer _port) {
        this._port = _port;
    }

    public void setConnectionTimeout(Integer _connectionTimeout) {
        this._connectionTimeout = _connectionTimeout;
    }

    public void setReadTimeout(Integer _readTimeout) {
        this._readTimeout = _readTimeout;
    }

    public void setMaximumDayRequest(Integer _maximumDayRequest) {
        this._maximumDayRequest = _maximumDayRequest;
    }

    public void setId(Long _id) {
        this._id = _id;
    }

    public void setUserName(String _userName) {
        this._userName = _userName;
    }

    public void setPassword(String _password) {
        this._password = _password;
    }

    public void setSsl(Boolean _ssl) {
        this._ssl = _ssl;
    }

    public void setTimezone(String _timezone) {
        this._timezone = _timezone;
    }

    public void setUri(String _uri) {
        this._uri = _uri;
    }

    public void setEnabled(Boolean _enabled) {
        this._enabled = _enabled;
    }

    public void setName(String _name) {
        this._name = _name;
    }

}
