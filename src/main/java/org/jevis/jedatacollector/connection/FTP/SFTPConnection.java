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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisObject;
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

    @Override
    public boolean connect() throws FetchingException {
        boolean connected = false;
        try {
            String hostname = "hostname";
            String login = "login";
            String password = "password";


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
        List<Object> sampleList = new ArrayList<Object>();
        try {
            String directory = "the directory";
            String filename = "the filename";
            ChannelSftp sftp = (ChannelSftp) _channel;
            sftp.cd(directory);
//            Vector files = sftp.ls("*");
//            System.out.printf("Found %d files in dir %s%n", files.size(), directory);
            InputStream get = sftp.get(filename);



            _channel.disconnect();
            _session.disconnect();
        } catch (SftpException ex) {
            Logger.getLogger(SFTPConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sampleList;
    }

    @Override
    public boolean returnsLimitedSampleCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void initialize(JEVisObject object) throws FetchingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getConnectionType() {
        return null;
    }
}
