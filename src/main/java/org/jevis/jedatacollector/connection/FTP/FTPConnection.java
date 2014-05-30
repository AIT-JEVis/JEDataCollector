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
import org.jevis.api.JEVisObject;
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.data.NewDataPoint;
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
    private Long _timeoutRead;
    private Long _triesConnection;
    private Long _timeoutConnection;
    private String _seperator;
    private String _dateFormat;
    private String _filePath; //data/trend
    private String _fileNameScheme; //
    private String _URL;
    private String _password;
    private Long _port;
    private String _username;
    private FTPClient _fc;

    public FTPConnection(String dateFormat, String filePath, String fileNameScheme, String url, String user, String password, Long timeoutConnection, Long timeoutRead) {
        _dateFormat = dateFormat;
        _filePath = filePath;
        _fileNameScheme = fileNameScheme;
        _URL = url;
        _username = user;
        _password = password;
        _timeoutConnection = timeoutConnection;
        _timeoutRead = timeoutRead;

    }

    @Override
    public boolean connect() throws FetchingException {
        try {
            _fc = new FTPClient();
//            _fc.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));

            if (_timeoutConnection != 0) {
                _fc.setConnectTimeout(_timeoutConnection.intValue() * 1000);
            }
            if (_timeoutRead != 0) {
                _fc.setDataTimeout(_timeoutRead.intValue() * 1000);
            }

            _fc.connect(_URL);

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

    private String getNextFile(DateTime from, NewDataPoint dp) throws FetchingException {
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

    private DateTime getDate(String fileName, NewDataPoint dp) throws FetchingException {
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

    public List<Object> sendSampleRequest(NewDataPoint dp, DateTime from, DateTime until) throws FetchingException {
        List<Object> ret = new LinkedList<Object>();
        String fileName;

        if (_fileNameScheme.indexOf('*') != -1) {
            fileName = getNextFile(from, dp);
        } else {
            fileName = _fileNameScheme;
        }

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            String query = _filePath + fileName;
            System.out.println("FTPQuery " + query);
            boolean retrieveFile = _fc.retrieveFile(query, out);

            System.out.println("Request status: " + retrieveFile);

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

    public void initialize(JEVisObject object) throws FetchingException {
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
    }
}
