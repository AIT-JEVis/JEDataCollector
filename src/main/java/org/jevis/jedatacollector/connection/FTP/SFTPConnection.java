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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTPClient;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisType;
import org.jevis.jedatacollector.connection.ConnectionHelper;
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.data.DataPoint;
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

    public SFTPConnection(String dateFormat, String filePath, String fileNameScheme, String url, String user, String password, Long timeoutConnection, Long timeoutRead) {
        _dateFormat = dateFormat;
        _filePath = filePath;
        _fileNameScheme = fileNameScheme;
        _serverURL = url;
        _username = user;
        _password = password;
        _connectionTimeout = timeoutConnection;
        _readTimeout = timeoutRead;
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
            _session = ssh.getSession(login, hostname, 22);
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
    public List<Object> sendSampleRequest(DataPoint dp, DateTime from, DateTime until) throws FetchingException {
        List<Object> ret = new LinkedList<Object>();
        String fileName = ConnectionHelper.parseConnectionString(dp, from, until,_fileNameScheme,_dateFormat);
//        String query = _filePath + fileName;


        try {
//            String directory = "the directory";
//            String filename = "the filename";
            ChannelSftp sftp = (ChannelSftp) _channel;
            sftp.cd(_filePath);
//            Vector files = sftp.ls("*");
//            System.out.printf("Found %d files in dir %s%n", files.size(), directory);
            InputStream get = sftp.get(fileName);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(get));
            String inputLine;

            while ((inputLine = bufferedReader.readLine()) != null) {
                ret.add(inputLine);
            }

            _channel.disconnect();
            _session.disconnect();
        } catch (SftpException ex) {
            Logger.getLogger(SFTPConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SFTPConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public boolean returnsLimitedSampleCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        return null;
    }
}
