/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.CLIProperties;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author broder
 */
public class ConnectionCLIParser {

    private String _connectionType;
    private String _ip;
    private String _dateFormat;
    private String _path;
    private int _port;
    private int _connectionTimeout;
    private int _readTimeout;

    public ConnectionCLIParser(String path) {
        Properties prop = new Properties();

        try {
            //load a properties file
            prop.load(new FileInputStream(path));

            //get the property value and print it out
            _connectionType = prop.getProperty("type");
            _dateFormat = prop.getProperty("dateformat");
            _ip = prop.getProperty("ip");
            _port = Integer.parseInt(prop.getProperty("port"));
            _path = prop.getProperty("path");
            _connectionTimeout = Integer.parseInt(prop.getProperty("connection-timeout"));
            _readTimeout = Integer.parseInt(prop.getProperty("read-timeout"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public String getConnectionType() {
        return _connectionType;
    }

    public int getPort() {
        return _port;
    }

    public String getIP() {
        return _ip;
    }

    public String getPath() {
        return _path;
    }

    public Integer getConnectionTimeout() {
        return _connectionTimeout;
    }

    public Integer getReadTimeout() {
        return _readTimeout;
    }

    public String getDateFormat() {
        return _dateFormat;
    }
}
