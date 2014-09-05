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
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.commons.JEVisTypes;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.commons.parsing.inputHandler.InputHandlerFactory;
import org.jevis.jedatacollector.Launcher;
import org.jevis.jedatacollector.exception.FetchingException;
import org.joda.time.DateTime;

/**
 *
 * @author bf
 */
public class SFTPConnection implements DatacollectorConnection {

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
    private FTPClient _fc;
    private String _parsedPath;

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
            Logger.getLogger(SFTPConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connected;

    }

    @Override
    public InputHandler sendSampleRequest(DataPoint dp, DateTime from, DateTime until) throws FetchingException {
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
        return InputHandlerFactory.getInputConverter(answer);
    }

//    @Override
//    public boolean returnsLimitedSampleCount() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    @Override
    public void initialize(JEVisObject node) throws FetchingException {
        try {
            JEVisClass sftpType = Launcher.getClient().getJEVisClass(JEVisTypes.Connection.sFTP.Name);
            JEVisObject sftpObject = node.getChildren(sftpType, true).get(0);
            JEVisType dateFormat = sftpType.getType(JEVisTypes.Connection.sFTP.DateFormat);
            JEVisType filePath = sftpType.getType(JEVisTypes.Connection.sFTP.FilePath);
            JEVisType fileNameScheme = sftpType.getType(JEVisTypes.Connection.sFTP.FileNameScheme);
            JEVisType server = sftpType.getType(JEVisTypes.Connection.sFTP.Server);
            JEVisType port = sftpType.getType(JEVisTypes.Connection.sFTP.Port);
            JEVisType connectionTimeout = sftpType.getType(JEVisTypes.Connection.sFTP.ConnectionTimeout);
            JEVisType readTimeout = sftpType.getType(JEVisTypes.Connection.sFTP.ReadTimeout);
            //            JEVisType maxRequest = type.getType("Maxrequestdays");
            JEVisType user = sftpType.getType(JEVisTypes.Connection.sFTP.User);
            JEVisType password = sftpType.getType(JEVisTypes.Connection.sFTP.Password);

            _id = sftpObject.getID();
            if (sftpObject.getAttribute(dateFormat).hasSample()) {
                _dateFormat = sftpObject.getAttribute(dateFormat).getLatestSample().getValueAsString();
            }
            _filePath = sftpObject.getAttribute(filePath).getLatestSample().getValueAsString();
            _fileNameScheme = sftpObject.getAttribute(fileNameScheme).getLatestSample().getValueAsString();
            _serverURL = sftpObject.getAttribute(server).getLatestSample().getValueAsString();
            JEVisAttribute portAttr = sftpObject.getAttribute(port);
            if (!portAttr.hasSample()) {
                _port = 22;
            } else {
                _port = Integer.parseInt((String) sftpObject.getAttribute(port).getLatestSample().getValue());
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
                _username = (String) userAttr.getLatestSample().getValue();
            }
            JEVisAttribute passAttr = sftpObject.getAttribute(password);
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

//    @Override
//    public String getConnectionType() {
//        return JEVisTypes.Connection.sFTP.Name;
//    }
    @Override
    public String getWholeFilePath() {
        return _filePath + _fileNameScheme;
    }
}
