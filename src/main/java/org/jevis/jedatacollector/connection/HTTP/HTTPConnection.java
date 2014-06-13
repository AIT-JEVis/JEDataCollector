/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.connection.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
import org.jevis.jedatacollector.connection.ConnectionHelper;
import org.jevis.jedatacollector.data.NewDataPoint;
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.exception.FetchingExceptionType;
import org.joda.time.DateTime;

/**
 *
 * @author max
 */
public class HTTPConnection implements DatacollectorConnection {

    private String _serverURL;
    private String _filePath;
    private String _dateFormat;
    private String _wholePath;
    private Integer _port;
    private Integer _connectionTimeout;
    private Integer _readTimeout;
    private Integer _maximumDayRequest;
    private Long _id;
    private String _userName;
    private String _password;

    public HTTPConnection() {
        super();
    }

    public HTTPConnection(String serverURL, String filePath, Integer port, Integer connectionTimeout, Integer readTimeout) {
        _serverURL = serverURL;
        _filePath = filePath;
        _port = port;
        _connectionTimeout = connectionTimeout;
        _readTimeout = readTimeout;
    }
    
       public HTTPConnection(String serverURL, String filePath, Integer port, Integer connectionTimeout, Integer readTimeout,String dateFormat) {
        _serverURL = serverURL;
        _filePath = filePath;
        _port = port;
        _connectionTimeout = connectionTimeout;
        _readTimeout = readTimeout;
        _dateFormat = dateFormat;
    }

    @Override
    public boolean connect() throws FetchingException {
        int port = 0;

        if (_port == null) {
            _port = 80;
        }

        if (_filePath.startsWith("/") && _serverURL.endsWith("/")) {
            _serverURL = _serverURL.substring(0, _serverURL.length() - 1);
        }

//        if (_serverURL.contains("://"))
//        {
//            _serverURL = "http://" + _serverURL;
//        }

        if (port != 0) {
            _wholePath = _serverURL + ":" + port + _filePath;
        } else {
            _wholePath = _serverURL + _filePath;
        }

        return true;
    }

    @Override
    public List<Object> sendSampleRequest(NewDataPoint dp, DateTime from, DateTime until) throws FetchingException {
        List<Object> res = new LinkedList<Object>();
        URL requestUrl;
        List<String> paths = getAllPaths(dp, from, until);

        if (_userName == null || _password == null || _userName.equals("") && _password.equals("")) {

            try {
                List<String> l;

                for (String path : paths) {
                    l = new LinkedList<String>();
                    URLConnection request;

                    if (path.startsWith("/")) {
                        path = path.substring(1, path.length());
                    }

                    if (!_serverURL.contains("://")) {
                        _serverURL = "http://" + _serverURL;
                    }

                    if (_port != null) {
                        requestUrl = new URL(_serverURL + ":" + _port + "/" + path);
                    } else {
                        requestUrl = new URL(_serverURL + "/" + path);
                    }

                    request = requestUrl.openConnection();
                    System.out.println("Requesting " + requestUrl);

//                    if (_connectionTimeout == null) {
                    _connectionTimeout = 600 * 1000;
//                    }
                    //                System.out.println("Connect timeout: " + _connectionTimeout.intValue() / 1000 + "s");
                    request.setConnectTimeout(_connectionTimeout.intValue());

//                    if (_readTimeout == null) {
                    _readTimeout = 600 * 1000;
//                    }
                    //                System.out.println("read timeout: " + _readTimeout.intValue() / 1000 + "s");
                    request.setReadTimeout(_readTimeout.intValue());

                    System.out.println("start buffer reader");
                    InputStream inputStream = request.getInputStream();
                    System.out.println("inputstream");
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    System.out.println("inputstreamreader");
                    BufferedReader bufReader = new BufferedReader(inputStreamReader);
                    System.out.println("end buffer reader");
                    boolean firstLine = true;
                    String output;

                    while ((output = bufReader.readLine()) != null) {
                        System.out.println(output);
                        if (firstLine && output.equals(" ")) {
                            firstLine = false;
                            continue;
                        }

                        l.add(output);
                    }

                    res.add(l);
                }
            } catch (MalformedURLException ex) {
                throw new FetchingException(_id, FetchingExceptionType.URL_ERROR);
            } catch (Exception ex) {
                throw new FetchingException(_id, FetchingExceptionType.CONNECTION_TIMEOUT);
            }
        } else {
            DefaultHttpClient _httpClient;
            HttpHost _targetHost;
            HttpGet _httpGet;
            BasicHttpContext _localContext = new BasicHttpContext();

            _httpClient = new DefaultHttpClient();
            /*
             * Define Authetification
             */
            /*
             * Username & password
             */

            _targetHost = new HttpHost(_serverURL, ((int) (long) _port), "http");
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

            _httpGet = new HttpGet(_filePath);


            try {
                //TODO: Connection timeouts and error handling


                HttpResponse oResponse = _httpClient.execute(_targetHost, _httpGet, _localContext);

                HttpEntity oEntity = oResponse.getEntity();
                String oXmlString = EntityUtils.toString(oEntity);
                EntityUtils.consume(oEntity);



                res.add(oXmlString);
            } catch (ClientProtocolException ex) {
                throw new FetchingException(_id, FetchingExceptionType.CONNECTION_ERROR);
                //Logger.getLogger(HTTPAuthetificationConnection.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                throw new FetchingException(_id, FetchingExceptionType.CONNECTION_ERROR);
                //Logger.getLogger(HTTPAuthetificationConnection.class.getName()).log(Level.SEVERE, null, ex);

            }
        }
        System.out.println("outputsize "+res.size());
        return res;
    }

    @Override
    public void initialize(JEVisObject node) throws FetchingException {
        try {
            //        _dateFormat = node.<String>getPropertyValue("Date Format");
            //        _filePath = node.<String>getPropertyValue("File Path");
            //        _serverURL = node.<String>getPropertyValue("Server URL");
            //        _port = node.<Long>getPropertyValue("Port");
            //        _connectionTimeout = node.<Long>getPropertyValue("Connection Timeout");
            //        _readTimeout = node.<Long>getPropertyValue("Read Timeout");
            //        _maximumDayRequest = node.<Long>getPropertyValue("Maximum days for Request");
            //        _id = node.getID();
            //        
            //        _userName = node.<String>getPropertyValue("User name");
            //        if(_userName==null)_userName="";
            //        if(_password ==null)_password="";
            //        if(_password ==null)_password="";

            JEVisClass type = node.getJEVisClass();
            JEVisType dateFormat = type.getType("Date format");
            JEVisType filePath = type.getType("File Path");
            JEVisType server = type.getType("Server URL");
            JEVisType port = type.getType("Port");
            JEVisType connectionTimeout = type.getType("Connection timeout");
            JEVisType readTimeout = type.getType("Read timeout");
//            JEVisType maxRequest = type.getType("Maxrequestdays");
            JEVisType user = type.getType("User");
            JEVisType password = type.getType("Password");

            _id = node.getID();
            if (node.getAttribute(dateFormat).hasSample()) {
                _dateFormat = (String) node.getAttribute(dateFormat).getLatestSample().getValue();
            }
            _filePath = (String) node.getAttribute(filePath).getLatestSample().getValue();
            _serverURL = (String) node.getAttribute(server).getLatestSample().getValue();
            _port = Integer.parseInt((String) node.getAttribute(port).getLatestSample().getValue());
            _connectionTimeout = Integer.parseInt((String) node.getAttribute(connectionTimeout).getLatestSample().getValue());
            _readTimeout = Integer.parseInt((String) node.getAttribute(readTimeout).getLatestSample().getValue());
//            if (node.getAttribute(maxRequest).hasSample()) {
//                _maximumDayRequest = Integer.parseInt((String) node.getAttribute(maxRequest).getLatestSample().getValue());
//            }
            JEVisAttribute userAttr = node.getAttribute(user);
            if (!userAttr.hasSample()) {
                _userName = "";
            } else {
                _userName = (String) userAttr.getLatestSample().getValue();
            }
            JEVisAttribute passAttr = node.getAttribute(password);
            if (!passAttr.hasSample()) {
                _password = "";
            } else {
                _password = (String) passAttr.getLatestSample().getValue();
            }
//            _id = 61l;
//            _dateFormat = "ddMMyyyyHHmmss";
//            _filePath = "/DP*DATAPOINT*-*DATE_FROM*-*DATE_TO*";
//            _serverURL = "172.22.182.2";
//            _port = 8350;
//            _connectionTimeout = 30;
//            _readTimeout = 300;
//            _userName = "";
//            _password = "";
        } catch (JEVisException ex) {
            Logger.getLogger(HTTPConnection.class.getName()).log(Level.ERROR, null, ex);
        }
    }

    @Override
    public boolean returnsLimitedSampleCount() {
        return false;
    }

    private List<String> getAllPaths(NewDataPoint dp, DateTime from, DateTime until) {
        List<String> paths = new ArrayList<String>();
        if (_filePath.contains("TIME_START") || _filePath.contains("TIME_END")) {
            _filePath = _filePath.replaceAll("TIME_START", "DATE_FROM");
            _filePath = _filePath.replaceAll("TIME_END", "DATE_TO");
        }

        if (_filePath.contains("DATE_FROM") || _filePath.contains("DATE_TO")) {
//                TimeSetVector tsv = new TimeSetVector(ts);
//                tsv.splitIntoChunks(10, 0, 0);

//                for (TimeSet tsPart : tsv) {
            paths.addAll(ConnectionHelper.parseString(_filePath, dp.getChannelID(), _dateFormat, from, until));
//                }
        } else {
            paths.add(_filePath);
        }

        return paths;
    }
}
