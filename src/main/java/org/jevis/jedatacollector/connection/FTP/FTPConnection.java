package org.jevis.jedatacollector.connection.FTP;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisType;
import org.jevis.commons.DatabaseHelper;
import org.jevis.jedatacollector.connection.ConnectionHelper;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.commons.JEVisTypes;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.commons.parsing.inputHandler.InputHandlerFactory;
import org.jevis.jedatacollector.Launcher;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.exception.FetchingExceptionType;
import org.joda.time.DateTime;

/**
 *
 * @author max
 */
public class FTPConnection implements DataCollectorConnection {

    private long _id;
    private Long _triesRead;
    private Integer _readTimeout;
    private Long _triesConnection;
    private Integer _connectionTimeout;
    private String _seperator;
    private String _dateFormat;
    private String _filePath; //data/trend
    private String _fileNameScheme; //
    private String _serverURL;
    private String _password;
    private Integer _port = 21;
    private String _username;
    protected FTPClient _fc;
    private String _parsedPath;
    private Boolean _ssl = false;
    private String _startCollectingData;
    private String _timezone;

    public FTPConnection() {
        super();
    }

    public FTPConnection(String dateFormat, String filePath, String fileNameScheme, String url, String user, String password, Integer timeoutConnection, Integer timeoutRead) {
        _dateFormat = dateFormat;
        _filePath = filePath;
        _fileNameScheme = fileNameScheme;
        _serverURL = url;
        _username = user;
        _password = password;
        _connectionTimeout = timeoutConnection;
        _readTimeout = timeoutRead;
    }

    public FTPConnection(String dateFormat, String filePath, String fileNameScheme, String url, Integer port, String user, String password, Integer timeoutConnection, Integer timeoutRead, Boolean ssl) {
        _dateFormat = dateFormat;
        _filePath = filePath;
        _fileNameScheme = fileNameScheme;
        _serverURL = url;
        _username = user;
        _password = password;
        _connectionTimeout = timeoutConnection;
        _readTimeout = timeoutRead;
        _ssl = ssl;
        if (port != null) {
            _port = port;
        }
    }

    @Override
    public boolean connect() throws FetchingException {
        try {
            if (_ssl) {
                System.out.println("ftps connection");
                _fc = new FTPSClient();
            } else {
                _fc = new FTPClient();
            }
//            _fc.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));

            if (_connectionTimeout != 0) {
                _fc.setConnectTimeout(_connectionTimeout.intValue() * 1000);
            }
            if (_readTimeout != 0) {
                _fc.setDataTimeout(_readTimeout.intValue() * 1000);
            }

            _fc.connect(_serverURL, _port);

            if (_fc.login(_username, _password) == false) {
                throw new FetchingException(_id, FetchingExceptionType.CONNECTION_ERROR);
            }

            _fc.setUseEPSVwithIPv4(false);
            _fc.enterLocalPassiveMode();

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new FetchingException(_id, FetchingExceptionType.CONNECTION_ERROR);
        }

        return true;
    }

//    private String getNextFile(DateTime from, DataPoint dp) throws FetchingException {
//        String retFile = null;
//        DateTime earliestDate = null;
//
//        try {
//            String fileName;
//            DateTime dateFrom, dateTo;
//
//            for (FTPFile file : _fc.listFiles(_filePath)) {
//                fileName = file.getName();
//
//                if (!fitsFileNameScheme(fileName)) {
//                    continue;
//                }
//
//                dateFrom = getDate(fileName, dp);
//
//                if (dateFrom == null) {
//                    continue;
//                }
//
//                if (dateFrom.isAfter(from)) {
//                    if (earliestDate != null
//                            && earliestDate.isBefore(dateFrom)) {
//                        continue;
//                    }
//                    retFile = file.getName();
//                    earliestDate = dateFrom;
//                }
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(FTPConnection.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return retFile;
//    }
//
//    private boolean fitsFileNameScheme(String fileName) {
//        String parts[] = _fileNameScheme.split("\\*", -1);
//
//        for (int i = 0; i < parts.length; i += 2) {
//            if (!fileName.contains(parts[i])) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//    private DateTime getDate(String fileName, DataPoint dp) throws FetchingException {
//        boolean fromBeforeTo = (_fileNameScheme.indexOf("*DATE_FROM") < _fileNameScheme.indexOf("*DATE_TO*"));
//
//        if (_fileNameScheme.indexOf("*DATE_TO*") == -1) {
//            fromBeforeTo = true;
//        }
//
//        String dateString;
//        DateFormat df = new SimpleDateFormat(_dateFormat);
//        Date cal = null;
//
//        if (fromBeforeTo) {
//            for (int i = 0; i < fileName.length() - _dateFormat.length(); i++) {
//                try {
//                    dateString = fileName.substring(i, i + _dateFormat.length());
//                    cal = df.parse(dateString);
//                    return new DateTime(cal);
//                } catch (Exception e) {
//                }
//            }
//        } else {
//            for (int i = fileName.length(); i >= fileName.length(); i--) {
//                try {
//                    dateString = fileName.substring(i, i + _dateFormat.length());
//                    cal = df.parse(dateString);
//                    return new DateTime(cal);
//                } catch (Exception e) {
//                }
//            }
//        }
//
//        if (cal == null) {
//            throw new FetchingException(_id, FetchingExceptionType.DATE_PARSE_ERROR);
//        }
//
//        return null;
//    }
//    private Character getSeperator(String fileNameScheme) {
//        fileNameScheme = fileNameScheme.replaceAll("\\*DATAPOINT\\*", "");
//        fileNameScheme = fileNameScheme.replaceAll("\\*DATE_FROM\\*", "");
//        fileNameScheme = fileNameScheme.replaceAll("\\*DATE_TO\\*", "");
//
//        for (char c : fileNameScheme.toCharArray()) {
//            if (c != '.') {
//                return c;
//            }
//        }
//
//        return null;
//    }
//    @Override
//    public boolean returnsLimitedSampleCount() {
//        return false;
//    }
    @Override
    public InputHandler sendSampleRequest(DataPoint dp, DateTime from, DateTime until) throws FetchingException {
        Object answer = null;
        //multiple File pathes neccessary?
        String filePath = ConnectionHelper.parseConnectionString(dp, from, until, dp.getFilePath(), dp.getDateFormat());
        List<String> fileNames = ConnectionHelper.getFTPMatchedFileNames(_fc, filePath);
        
        String currentFilePath = Paths.get(filePath).getParent().toString();

        for (String fileName : fileNames) {
            System.out.println("file " + fileName);
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                String query = currentFilePath + "/"+ fileName;
                org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.INFO, "FTPQuery " + query);
                boolean retrieveFile = _fc.retrieveFile(query, out);
                String testString = new String(out.toByteArray());
                org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.INFO, "Request status: " + retrieveFile);
                InputStream inputStream = new ByteArrayInputStream(out.toByteArray());
                answer = new BufferedInputStream(inputStream);

//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                String inputLine;
//
//                while ((inputLine = bufferedReader.readLine()) != null) {
//                    ret.add(inputLine);
//                }
            } catch (IOException ex) {
                throw new FetchingException(_id, FetchingExceptionType.CONNECTION_TIMEOUT);
            }
        }
        
        
        return InputHandlerFactory.getInputConverter(answer);
    }

    @Override
    public void initialize(JEVisObject node) throws FetchingException {
        try {
            JEVisClass ftpType = Launcher.getClient().getJEVisClass(JEVisTypes.DataServer.FTP.NAME);
            JEVisObject ftpObject = node;
            JEVisType sslType = ftpType.getType(JEVisTypes.DataServer.FTP.SSL);
            JEVisType serverType = ftpType.getType(JEVisTypes.DataServer.FTP.HOST);
            JEVisType portType = ftpType.getType(JEVisTypes.DataServer.FTP.PORT);
            JEVisType connectionTimeoutType = ftpType.getType(JEVisTypes.DataServer.FTP.CONNECTION_TIMEOUT);
            JEVisType readTimeoutType = ftpType.getType(JEVisTypes.DataServer.FTP.READ_TIMEOUT);
            JEVisType userType = ftpType.getType(JEVisTypes.DataServer.FTP.USER);
            JEVisType passwordType = ftpType.getType(JEVisTypes.DataServer.FTP.PASSWORD);
            JEVisType startCollectingType = ftpType.getType(JEVisTypes.DataServer.FTP.START_DATA_COLLECTING);
            JEVisType timezoneType = ftpType.getType(JEVisTypes.DataServer.FTP.TIMEZONE);

            _id = ftpObject.getID();
//            _dateFormat = DatabaseHelper.getObjectAsString(ftpObject, dateFormat);
//            _filePath = DatabaseHelper.getObjectAsString(ftpObject, filePath);
//            _fileNameScheme = DatabaseHelper.getObjectAsString(ftpObject, fileNameScheme);
            _ssl = DatabaseHelper.getObjectAsBoolean(ftpObject, sslType);
            _serverURL = DatabaseHelper.getObjectAsString(ftpObject, serverType);
            _port = DatabaseHelper.getObjectAsInteger(ftpObject, portType);
            if (_port == null) {
                _port = 21;
            }
            _connectionTimeout = DatabaseHelper.getObjectAsInteger(ftpObject, connectionTimeoutType);
            _readTimeout = DatabaseHelper.getObjectAsInteger(ftpObject, readTimeoutType);
            //            if (node.getAttribute(maxRequest).hasSample()) {
            //                _maximumDayRequest = Integer.parseInt((String) node.getAttribute(maxRequest).getLatestSample().getValue());
            //            }
            JEVisAttribute userAttr = ftpObject.getAttribute(userType);
            if (!userAttr.hasSample()) {
                _username = "";
            } else {
                _username = DatabaseHelper.getObjectAsString(ftpObject, userType);
            }

            JEVisAttribute passAttr = ftpObject.getAttribute(passwordType);
            if (!passAttr.hasSample()) {
                _password = "";
            } else {
                _password = DatabaseHelper.getObjectAsString(ftpObject, passwordType);
            }

//            _startCollectingData = DatabaseHelper.getObjectAsString(ftpObject, startCollectingType);
            _timezone = DatabaseHelper.getObjectAsString(ftpObject, timezoneType);
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
            ex.printStackTrace();
            Logger.getLogger(FTPConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    @Override
//    public String getConnectionType() {
//        return JEVisTypes.Connection.FTP.Name;
//    }
    @Override
    public String getWholeFilePath() {
        return _filePath + _fileNameScheme;
    }

    @Override
    public String getTimezone() {
        return _timezone;
    }

    @Override
    public String getName() {
        return String.valueOf(_id);
    }
}
