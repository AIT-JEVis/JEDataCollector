package org.jevis.jedatacollector.connection.FTP;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisType;
import org.jevis.jedatacollector.connection.ConnectionFactory;
import org.jevis.jedatacollector.connection.ConnectionHelper;
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.exception.FetchingExceptionType;
import org.joda.time.DateTime;

/**
 *
 * @author max
 */
public class FTPConnection implements DatacollectorConnection {

    private long _id;
    private Long _triesRead;
    private Long _readTimeout;
    private Long _triesConnection;
    private Long _connectionTimeout;
    private String _seperator;
    private String _dateFormat;
    private String _filePath; //data/trend
    private String _fileNameScheme; //
    private String _serverURL;
    private String _password;
    private Integer _port;
    private String _username;
    private FTPClient _fc;
    private String _parsedPath;

    public FTPConnection() {
        super();
    }

    public FTPConnection(String dateFormat, String filePath, String fileNameScheme, String url, String user, String password, Long timeoutConnection, Long timeoutRead) {
        _dateFormat = dateFormat;
        _filePath = filePath;
        _fileNameScheme = fileNameScheme;
        _serverURL = url;
        _username = user;
        _password = password;
        _connectionTimeout = timeoutConnection;
        _readTimeout = timeoutRead;

    }

    @Override
    public boolean connect() throws FetchingException {
        try {
            _fc = new FTPClient();
//            _fc.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));

            if (_connectionTimeout != 0) {
                _fc.setConnectTimeout(_connectionTimeout.intValue() * 1000);
            }
            if (_readTimeout != 0) {
                _fc.setDataTimeout(_readTimeout.intValue() * 1000);
            }

            _fc.connect(_serverURL);

            if (_fc.login(_username, _password) == false) {
                throw new FetchingException(_id, FetchingExceptionType.CONNECTION_ERROR);
            }

            _fc.setUseEPSVwithIPv4(false);
            _fc.enterLocalPassiveMode();

        } catch (IOException ex) {
            throw new FetchingException(_id, FetchingExceptionType.CONNECTION_ERROR);
        }

        return true;
    }

    private String getNextFile(DateTime from, DataPoint dp) throws FetchingException {
        String retFile = null;
        DateTime earliestDate = null;

        try {
            String fileName;
            DateTime dateFrom, dateTo;

            for (FTPFile file : _fc.listFiles(_filePath)) {
                fileName = file.getName();

                if (!fitsFileNameScheme(fileName)) {
                    continue;
                }

                dateFrom = getDate(fileName, dp);

                if (dateFrom == null) {
                    continue;
                }

                if (dateFrom.isAfter(from)) {
                    if (earliestDate != null
                            && earliestDate.isBefore(dateFrom)) {
                        continue;
                    }
                    retFile = file.getName();
                    earliestDate = dateFrom;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FTPConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retFile;
    }

    private boolean fitsFileNameScheme(String fileName) {
        String parts[] = _fileNameScheme.split("\\*", -1);

        for (int i = 0; i < parts.length; i += 2) {
            if (!fileName.contains(parts[i])) {
                return false;
            }
        }

        return true;
    }

    private DateTime getDate(String fileName, DataPoint dp) throws FetchingException {
        boolean fromBeforeTo = (_fileNameScheme.indexOf("*DATE_FROM") < _fileNameScheme.indexOf("*DATE_TO*"));

        if (_fileNameScheme.indexOf("*DATE_TO*") == -1) {
            fromBeforeTo = true;
        }

        String dateString;
        DateFormat df = new SimpleDateFormat(_dateFormat);
        Date cal = null;

        if (fromBeforeTo) {
            for (int i = 0; i < fileName.length() - _dateFormat.length(); i++) {
                try {
                    dateString = fileName.substring(i, i + _dateFormat.length());
                    cal = df.parse(dateString);
                    return new DateTime(cal);
                } catch (Exception e) {
                }
            }
        } else {
            for (int i = fileName.length(); i >= fileName.length(); i--) {
                try {
                    dateString = fileName.substring(i, i + _dateFormat.length());
                    cal = df.parse(dateString);
                    return new DateTime(cal);
                } catch (Exception e) {
                }
            }
        }

        if (cal == null) {
            throw new FetchingException(_id, FetchingExceptionType.DATE_PARSE_ERROR);
        }

        return null;
    }

    private Character getSeperator(String fileNameScheme) {
        fileNameScheme = fileNameScheme.replaceAll("\\*DATAPOINT\\*", "");
        fileNameScheme = fileNameScheme.replaceAll("\\*DATE_FROM\\*", "");
        fileNameScheme = fileNameScheme.replaceAll("\\*DATE_TO\\*", "");

        for (char c : fileNameScheme.toCharArray()) {
            if (c != '.') {
                return c;
            }
        }

        return null;
    }

    @Override
    public boolean returnsLimitedSampleCount() {
        return false;
    }

    @Override
    public List<Object> sendSampleRequest(DataPoint dp, DateTime from, DateTime until) throws FetchingException {
        List<Object> ret = new LinkedList<Object>();
        String fileName = ConnectionHelper.parseConnectionString(dp, from, until,_fileNameScheme,_dateFormat);
//        _fileNameScheme = _fileNameScheme.replaceAll(ConnectionHelper.DATAPOINT, dp.getChannelID());
//
//        if (_fileNameScheme.indexOf('*') != -1) {
//            fileName = getNextFile(from, dp);
//        } else {
//            fileName = _fileNameScheme;
//        }
        System.out.println("file "+fileName);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            String query = _filePath + fileName;
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.INFO, "FTPQuery " + query);
            boolean retrieveFile = _fc.retrieveFile(query, out);

            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.INFO, "Request status: " + retrieveFile);

            InputStream inputStream = new ByteArrayInputStream(out.toByteArray());

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;

            while ((inputLine = bufferedReader.readLine()) != null) {
                ret.add(inputLine);
            }
        } catch (IOException ex) {
            throw new FetchingException(_id, FetchingExceptionType.CONNECTION_TIMEOUT);
        }

        return ret;
    }

    @Override
    public void initialize(JEVisObject node) throws FetchingException {
        try {
            JEVisClass type = node.getJEVisClass();
            JEVisType dateFormat = type.getType("Date format");
            JEVisType filePath = type.getType("File path");
            JEVisType fileNameScheme = type.getType("File name scheme");
            JEVisType server = type.getType("Server URL");
            JEVisType port = type.getType("Port");
            JEVisType connectionTimeout = type.getType("Connection Timeout");
            JEVisType readTimeout = type.getType("Read Timeout");
            //            JEVisType maxRequest = type.getType("Maxrequestdays");
            JEVisType user = type.getType("User");
            JEVisType password = type.getType("Password");

            _id = node.getID();
            if (node.getAttribute(dateFormat).hasSample()) {
                _dateFormat = node.getAttribute(dateFormat).getLatestSample().getValueAsString();
            }
            _filePath = node.getAttribute(filePath).getLatestSample().getValueAsString();
            _fileNameScheme = node.getAttribute(fileNameScheme).getLatestSample().getValueAsString();
            _serverURL = node.getAttribute(server).getLatestSample().getValueAsString();
            JEVisAttribute portAttr = node.getAttribute(port);
            if (!portAttr.hasSample()) {
                _port = 20;
            } else {
                _port = Integer.parseInt((String) node.getAttribute(port).getLatestSample().getValue());
            }
            _connectionTimeout = node.getAttribute(connectionTimeout).getLatestSample().getValueAsLong();
            _readTimeout = node.getAttribute(readTimeout).getLatestSample().getValueAsLong();
            //            if (node.getAttribute(maxRequest).hasSample()) {
            //                _maximumDayRequest = Integer.parseInt((String) node.getAttribute(maxRequest).getLatestSample().getValue());
            //            }
            JEVisAttribute userAttr = node.getAttribute(user);
            if (!userAttr.hasSample()) {
                _username = "";
            } else {
                _username = (String) userAttr.getLatestSample().getValue();
            }
            JEVisAttribute passAttr = node.getAttribute(password);
            if (!passAttr.hasSample()) {
                _password = "";
            } else {
                _password = (String) passAttr.getLatestSample().getValue();
            }
            //        _id = cn.getID();
            //        _dateFormat = cn.<String>getPropertyValue("Date Format");
            //        _triesRead = cn.<Long>getPropertyValue("Read Tries");
            //        _timeoutRead = cn.<Long>getPropertyValue("Read Timeout (in sec.)");
            //        _triesConnection = cn.<Long>getPropertyValue("Connection Tries");
            //        _timeoutConnection = cn.<Long>getPropertyValue("Connection Timeout (in sec.)");
            //        _filePath = cn.<String>getPropertyValue("File Path");
            //        _fileNameScheme = cn.<String>getPropertyValue("File Name Scheme");
            //        _password = cn.<String>getPropertyValue("Password");
            //        _port = cn.<Long>getPropertyValue("Port");
            //        _username = cn.<String>getPropertyValue("Username");
            //        _URL = cn.<String>getPropertyValue("Server URL");
            //        _seperator = cn.<String>getPropertyValue("File Detail Seperator");
        } catch (JEVisException ex) {
            Logger.getLogger(FTPConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getConnectionType() {
        return ConnectionFactory.FTP_CONNECTION;
    }

}
