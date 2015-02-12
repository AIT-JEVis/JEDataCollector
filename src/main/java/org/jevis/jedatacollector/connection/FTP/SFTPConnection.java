/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.connection.FTP;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTPClient;
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
import org.jevis.commons.cli.JEVisCommandLine;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.commons.parsing.inputHandler.InputHandlerFactory;
import org.jevis.jedatacollector.Launcher;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.exception.FetchingExceptionType;
import org.joda.time.DateTime;

/**
 *
 * @author bf
 */
public class SFTPConnection implements DataCollectorConnection {

    private Channel _channel;
    private Session _session;
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
    private Integer _port = 22;
    private String _username;
    private String _parsedPath;
    private String _timezone;
    private Boolean _enabled;
    private String _name;

    public SFTPConnection(String dateFormat, String filePath, String fileNameScheme, String url, String user, String password, Integer timeoutConnection, Integer timeoutRead) {
        _dateFormat = dateFormat;
        _filePath = filePath;
        _fileNameScheme = fileNameScheme;
        _serverURL = url;
        _username = user;
        _password = password;
        _connectionTimeout = timeoutConnection;
        _readTimeout = timeoutRead;
    }

    public SFTPConnection(String dateFormat, String filePath, String fileNameScheme, String url, Integer port, String user, String password, Integer timeoutConnection, Integer timeoutRead) {
        _dateFormat = dateFormat;
        _filePath = filePath;
        _fileNameScheme = fileNameScheme;
        _serverURL = url;
        _username = user;
        _password = password;
        _connectionTimeout = timeoutConnection;
        _readTimeout = timeoutRead;
        if (port != null) {
            _port = port;
        }
    }

    public SFTPConnection() {
    }

    @Override
    public boolean connect() throws FetchingException {
        boolean connected = false;
        try {
            String hostname = _serverURL;
            String login = _username;
            String password = _password;


            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");

            JSch ssh = new JSch();
            _session = ssh.getSession(login, hostname, _port);
            _session.setConfig(config);
            _session.setPassword(password);
            _session.connect();
            _channel = _session.openChannel("sftp");
            _channel.connect();
            connected = true;
        } catch (JSchException ex) {
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ERROR, "No connection possible");
            org.apache.log4j.Logger.getLogger(FTPConnection.class).setLevel(org.apache.log4j.Level.ALL);
            printConnectionData();
            org.apache.log4j.Logger.getLogger(FTPConnection.class).setLevel(JEVisCommandLine.getInstance().getDebugLevel());
            throw new FetchingException(_id, FetchingExceptionType.CONNECTION_ERROR);
        }

        return connected;

    }

    public List<InputHandler> sendSampleRequestOld(DataPoint dp, DateTime from, DateTime until) throws FetchingException {
        Object answer = null;
        String fileName = ConnectionHelper.parseConnectionString(dp, from, until, _fileNameScheme, _dateFormat);
//        String query = _filePath + fileName;


        try {
//            String directory = "the directory";
//            String filename = "the filename";
            ChannelSftp sftp = (ChannelSftp) _channel;
            Vector files = sftp.ls("*");
            System.out.printf("Found %d files in dir %s%n", files.size(), _filePath);
            sftp.cd(_filePath);
//            Vector files = sftp.ls("*");
//            System.out.printf("Found %d files in dir %s%n", files.size(), _filePath);
            InputStream get = sftp.get(fileName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;
            try {
                while ((len = get.read(buffer)) > -1) {
                    baos.write(buffer, 0, len);
                }
                baos.flush();
            } catch (IOException ex) {
                Logger.getLogger(SFTPConnection.class.getName()).log(Level.SEVERE, null, ex);
            }

            answer = new ByteArrayInputStream(baos.toByteArray());

//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(get));
//            String inputLine;
//
//            while ((inputLine = bufferedReader.readLine()) != null) {
//                ret.add(inputLine);
//            }

            _channel.disconnect();
            _session.disconnect();
        } catch (SftpException ex) {
            Logger.getLogger(SFTPConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<InputHandler> answerList = new ArrayList<InputHandler>();
        answerList.add(InputHandlerFactory.getInputConverter(answer));
        return answerList;
    }

    @Override
    public List<InputHandler> sendSampleRequest(DataPoint dp, DateTime from, DateTime until) throws FetchingException {
        Object answer = null;
        //multiple File pathes neccessary?
//        String filePath = ConnectionHelper.parseConnectionString(dp, from, until, dp.getFilePath(), dp.getDateFormat());
        //this should be outsourced
        String filePath = dp.getFilePath();

        ChannelSftp sftp = (ChannelSftp) _channel;
        org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "SendSampleRequest");
        List<String> fileNames = ConnectionHelper.getSFTPMatchedFileNames(sftp, dp, filePath);
//        String currentFilePath = Paths.get(filePath).getParent().toString();

        List<InputHandler> answerList = new ArrayList<InputHandler>();
        for (String fileName : fileNames) {
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "FileInputName: " + fileName);

            try {
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                String query = Paths.get(fileName);
                InputStream get = sftp.get(fileName);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int len;
                try {
                    while ((len = get.read(buffer)) > -1) {
                        baos.write(buffer, 0, len);
                    }
                    baos.flush();
                } catch (IOException ex) {
                    Logger.getLogger(SFTPConnection.class.getName()).log(Level.SEVERE, null, ex);
                }

                answer = new ByteArrayInputStream(baos.toByteArray());
                InputHandler inputConverter = InputHandlerFactory.getInputConverter(answer);
                inputConverter.setFilePath(fileName);
                if (dp.getDirectory().containsCompressedFolder()) {

                    String pattern = dp.getDirectory().getFolderPathFromComp() + dp.getFileName();
                    inputConverter.setFilePattern(pattern);
                    inputConverter.setDateTime(dp.getLastReadout());
                }
                answerList.add(inputConverter);


            } catch (SftpException ex) {
                org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ERROR, ex.getMessage());
            }
        }

        _channel.disconnect();
        _session.disconnect();
        
        if (answerList.isEmpty()) {
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ERROR, "Cant get any data from the device");
        }

        return answerList;
    }

    @Override
    public void initialize(JEVisObject sftpObject) throws FetchingException {
        try {
            JEVisClass sftpType = Launcher.getClient().getJEVisClass(JEVisTypes.DataServer.sFTP.NAME);
            JEVisType server = sftpType.getType(JEVisTypes.DataServer.sFTP.HOST);
            JEVisType port = sftpType.getType(JEVisTypes.DataServer.sFTP.PORT);
            JEVisType connectionTimeout = sftpType.getType(JEVisTypes.DataServer.sFTP.CONNECTION_TIMEOUT);
            JEVisType readTimeout = sftpType.getType(JEVisTypes.DataServer.sFTP.READ_TIMEOUT);
            //            JEVisType maxRequest = type.getType("Maxrequestdays");
            JEVisType user = sftpType.getType(JEVisTypes.DataServer.sFTP.USER);
            JEVisType password = sftpType.getType(JEVisTypes.DataServer.sFTP.PASSWORD);
            JEVisType timezoneType = sftpType.getType(JEVisTypes.DataServer.sFTP.TIMEZONE);
            JEVisType enableType = sftpType.getType(JEVisTypes.DataServer.ENABLE);

            _id = sftpObject.getID();
            _name = sftpObject.getName();
            _serverURL = sftpObject.getAttribute(server).getLatestSample().getValueAsString();
            JEVisAttribute portAttr = sftpObject.getAttribute(port);
            if (!portAttr.hasSample()) {
                _port = 22;
            } else {
                _port = DatabaseHelper.getObjectAsInteger(sftpObject, port);
            }

            _connectionTimeout = DatabaseHelper.getObjectAsInteger(sftpObject, connectionTimeout);
            _readTimeout = DatabaseHelper.getObjectAsInteger(sftpObject, readTimeout);
            //            if (node.getAttribute(maxRequest).hasSample()) {
            //                _maximumDayRequest = Integer.parseInt((String) node.getAttribute(maxRequest).getLatestSample().getValue());
            //            }
            JEVisAttribute userAttr = sftpObject.getAttribute(user);
            if (!userAttr.hasSample()) {
                _username = "";
            } else {
                _username = DatabaseHelper.getObjectAsString(sftpObject, user);
            }
            JEVisAttribute passAttr = sftpObject.getAttribute(password);
            if (!passAttr.hasSample()) {
                _password = "";
            } else {
                _password = DatabaseHelper.getObjectAsString(sftpObject, password);
            }

            _timezone = DatabaseHelper.getObjectAsString(sftpObject, timezoneType);
            _enabled = DatabaseHelper.getObjectAsBoolean(sftpObject, enableType);
        } catch (JEVisException ex) {
            Logger.getLogger(FTPConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    private void printConnectionData() {
        org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "Data Source ID: " + _id);
        org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "Server: " + _serverURL);
        org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "Port: " + _port);
        org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "ConnectionTimeout: " + _connectionTimeout);
        org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "ReadTimeout: " + _readTimeout);
        org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "Username: " + _username);
        org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "Password: " + _password);
        org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "Timezone: " + _timezone);
        org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "Enabled: " + _enabled);
    }
}
