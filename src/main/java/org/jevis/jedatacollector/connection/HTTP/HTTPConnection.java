/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.connection.HTTP;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisType;
import org.jevis.commons.DatabaseHelper;
import org.jevis.jedatacollector.connection.ConnectionHelper;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.jevis.commons.JEVisTypes;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.commons.parsing.inputHandler.InputHandlerFactory;
import org.jevis.jedatacollector.Launcher;
import org.jevis.jedatacollector.exception.FetchingException;
import org.joda.time.DateTime;

/**
 *
 * @author max
 */
public class HTTPConnection implements DataCollectorConnection {

    private String _serverURL;
    private Integer _port;
    private Integer _connectionTimeout;
    private Integer _readTimeout;
    private Long _id;
    private String _userName;
    private String _password;
    private Boolean _ssl = false;
    private String _timezone;
    private Boolean _enabled;
    private String _name;

    public HTTPConnection() {
        super();
    }

    public HTTPConnection(String serverURL, String filePath, Integer port, Integer connectionTimeout, Integer readTimeout) {
        _serverURL = serverURL;
        _port = port;
        _connectionTimeout = connectionTimeout;
        _readTimeout = readTimeout;
    }

    public HTTPConnection(String serverURL, Integer port, Integer connectionTimeout, Integer readTimeout, Long id, String userName, String password, Boolean ssl, String timezone, Boolean enabled, String name) {
        _serverURL = serverURL;
        _port = port;
        _connectionTimeout = connectionTimeout;
        _readTimeout = readTimeout;
        _id = id;
        _userName = userName;
        _password = password;
        _ssl = ssl;
        _timezone = timezone;
        _enabled = enabled;
        _name = name;
    }

    public HTTPConnection(String serverURL, String filePath, Integer port, Integer connectionTimeout, Integer readTimeout, String dateFormat) {
        _serverURL = serverURL;
        _port = port;
        _connectionTimeout = connectionTimeout;
        _readTimeout = readTimeout;
    }

    @Override
    public boolean connect() {

        if (_port == null) {
            _port = 80;
        }
        return true;
    }

    @Override
    public void initialize(JEVisObject httpObject) throws FetchingException {
        try {
            JEVisClass httpType = Launcher.getClient().getJEVisClass(JEVisTypes.DataServer.HTTP.NAME);
//            JEVisObject httpObject = node.getChildren(httpType, true).get(0);
//            JEVisType dateFormat = httpType.getType(JEVisTypes.DataServer.HTTP.);
//            JEVisType filePath = httpType.getType(JEVisTypes.DataServer.HTTP.FilePath);
            JEVisType server = httpType.getType(JEVisTypes.DataServer.HTTP.HOST);
            JEVisType port = httpType.getType(JEVisTypes.DataServer.HTTP.PORT);
            JEVisType sslType = httpType.getType(JEVisTypes.DataServer.HTTP.SSL);
            JEVisType connectionTimeout = httpType.getType(JEVisTypes.DataServer.HTTP.CONNECTION_TIMEOUT);
            JEVisType readTimeout = httpType.getType(JEVisTypes.DataServer.HTTP.READ_TIMEOUT);
            JEVisType user = httpType.getType(JEVisTypes.DataServer.HTTP.USER);
            JEVisType password = httpType.getType(JEVisTypes.DataServer.HTTP.PASSWORD);
            JEVisType timezoneType = httpType.getType(JEVisTypes.DataServer.HTTP.TIMEZONE);
            JEVisType enableType = httpType.getType(JEVisTypes.DataServer.HTTP.ENABLE);

            _id = httpObject.getID();
            _name = httpObject.getName();
//            _dateFormat = DatabaseHelper.getObjectAsString(httpObject, dateFormat);
//            _filePath = DatabaseHelper.getObjectAsString(httpObject, filePath);
            _serverURL = DatabaseHelper.getObjectAsString(httpObject, server);
            _port = DatabaseHelper.getObjectAsInteger(httpObject, port);
            _connectionTimeout = DatabaseHelper.getObjectAsInteger(httpObject, connectionTimeout);
            _readTimeout = DatabaseHelper.getObjectAsInteger(httpObject, readTimeout);
            _ssl = DatabaseHelper.getObjectAsBoolean(httpObject, sslType);
            JEVisAttribute userAttr = httpObject.getAttribute(user);
            if (!userAttr.hasSample()) {
                _userName = "";
            } else {
                _userName = (String) userAttr.getLatestSample().getValue();
            }
            JEVisAttribute passAttr = httpObject.getAttribute(password);
            if (!passAttr.hasSample()) {
                _password = "";
            } else {
                _password = (String) passAttr.getLatestSample().getValue();
            }

            _timezone = DatabaseHelper.getObjectAsString(httpObject, timezoneType);
            _enabled = DatabaseHelper.getObjectAsBoolean(httpObject, enableType);
        } catch (JEVisException ex) {
            Logger.getLogger(HTTPConnection.class.getName()).log(Level.ERROR, null, ex);
        }
    }

    @Override
    public List<InputHandler> sendSampleRequest(DataPoint dp, DateTime from, DateTime until) {
//        List<Object> res = new LinkedList<Object>();
        URL requestUrl;
        Object answer = null;
        if (_userName == null || _password == null || _userName.equals("") || _password.equals("")) {

            HttpURLConnection request = null;
            try {
                List<String> l;

                l = new LinkedList<String>();
                String filePath = dp.getFilePath();
                String path = ConnectionHelper.replaceDateFromUntil(from, until, filePath);
                if (path.startsWith("/")) {
                    path = path.substring(1, path.length());
                }

                if (!_serverURL.contains("://")) {
                    _serverURL = "http://" + _serverURL;
                }

                if (_ssl) {
                    _serverURL = _serverURL.replace("http", "https");
                }

                if (_port != null) {
                    requestUrl = new URL(_serverURL + ":" + _port + "/" + path);
                } else {
                    requestUrl = new URL(_serverURL + "/" + path);
                }
                if (_ssl) {
                    ConnectionHelper.doTrustToCertificates();
                }
                Logger.getLogger(HTTPConnection.class.getName()).log(Level.INFO, "Connection URL: " + requestUrl);
                request = (HttpURLConnection) requestUrl.openConnection();

//                    if (_connectionTimeout == null) {
                _connectionTimeout = _connectionTimeout * 1000;
//                    }
                //                System.out.println("Connect timeout: " + _connectionTimeout.intValue() / 1000 + "s");
                request.setConnectTimeout(_connectionTimeout.intValue());

//                    if (_readTimeout == null) {
                _readTimeout = _readTimeout * 1000;
//                    }
                //                System.out.println("read timeout: " + _readTimeout.intValue() / 1000 + "s");
                request.setReadTimeout(_readTimeout.intValue());
                InputStream inputStream = request.getInputStream();
//                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "InputHTTPBEF " + IOUtils.toString((InputStream) inputStream, "UTF-8"));

                answer = new BufferedInputStream(inputStream);
//                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "InputHTTPAFTER " + IOUtils.toString((InputStream) inputStream, "UTF-8"));
            } catch (MalformedURLException ex) {
                org.apache.log4j.Logger.getLogger(HTTPConnection.class.getName()).log(org.apache.log4j.Level.ERROR, ex.getMessage());
            } catch (Exception ex) {
                org.apache.log4j.Logger.getLogger(HTTPConnection.class.getName()).log(org.apache.log4j.Level.ERROR, ex.getMessage());
            }
        } else {
            DefaultHttpClient _httpClient;
            HttpHost _targetHost;
            HttpGet _httpGet;
            BasicHttpContext _localContext = new BasicHttpContext();
            _httpClient = new DefaultHttpClient();

            try {
                if (_ssl) {
                    ConnectionHelper.doTrustToCertificates();
                    _targetHost = new HttpHost(_serverURL, ((int) (long) _port), "https");
                } else {
                    _targetHost = new HttpHost(_serverURL, ((int) (long) _port), "http");
                }
                /*
                 * set the sope for the authentification
                 */
                _httpClient.getCredentialsProvider().setCredentials(
                        new AuthScope(_targetHost.getHostName(), _targetHost.getPort()),
                        new UsernamePasswordCredentials(_userName, _password));

                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();

                //set Authenticication scheme
                BasicScheme basicAuth = new BasicScheme();
                authCache.put(_targetHost, basicAuth);

                String filePath = dp.getFilePath();
                String path = ConnectionHelper.replaceDateFromUntil(from, until, filePath);
                if (path.startsWith("/")) {
                    path = path.substring(1, path.length());
                }
                _httpGet = new HttpGet(path);
                //TODO: Connection timeouts and error handling

                HttpResponse oResponse = _httpClient.execute(_targetHost, _httpGet, _localContext);

                HttpEntity oEntity = oResponse.getEntity();
                String oXmlString = EntityUtils.toString(oEntity);
                EntityUtils.consume(oEntity);


                answer = oXmlString;
            } catch (ClientProtocolException ex) {
                Logger.getLogger(HTTPConnection.class.getName()).log(Level.ERROR, ex.getMessage());
            } catch (IOException ex) {
                Logger.getLogger(HTTPConnection.class.getName()).log(Level.ERROR, ex.getMessage());
            } catch (Exception ex) {
                Logger.getLogger(HTTPConnection.class.getName()).log(Level.ERROR, ex.getMessage());
            }
        }
        List<InputHandler> answerList = new ArrayList<InputHandler>();
        answerList.add(InputHandlerFactory.getInputConverter(answer));
        return answerList;
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
        return _serverURL;
    }

    @Override
    public Integer getPort() {
        return _port;
    }
}
