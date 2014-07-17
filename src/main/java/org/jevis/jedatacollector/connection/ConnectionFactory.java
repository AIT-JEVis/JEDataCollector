/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.connection;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.jedatacollector.connection.FTP.FTPConnection;
import org.jevis.jedatacollector.connection.FTP.SFTPConnection;
import org.jevis.jedatacollector.connection.HTTP.HTTPConnection;
import org.jevis.jedatacollector.connection.SOAP.SOAPConnection;
import org.jevis.jedatacollector.connection.SQL.SQLConnection;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.exception.FetchingExceptionType;

/**
 *
 * @author broder
 */
public class ConnectionFactory {

    public static final String HTTP_CONNECTION = ("HTTPCon");
    public static final String FTP_CONNECTION = ("FTP");
    public static final String SFTP_CONNECTION = ("sFTP");
    public static final String SOAP_CONNECTION = ("SOAP");
    public static final String SQL_CONNECTION = ("SQL");

    public static DatacollectorConnection getConnection(JEVisObject connectionData) throws FetchingException {
        DatacollectorConnection connection = null;
        if (connectionData == null) {
            throw new FetchingException(connectionData.getID(), FetchingExceptionType.CONNECTION_DRIVER_ERROR);
        }
        String identifier = null;
        try {
            identifier = connectionData.getJEVisClass().getName();
             Logger.getLogger(ConnectionFactory.class.getName()).log(Level.INFO, "---Indentifier: "+identifier);
        } catch (JEVisException ex) {
            Logger.getLogger(ConnectionFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (identifier.equals(HTTP_CONNECTION)) {
            connection = new HTTPConnection();
        } else if (identifier.equals(SOAP_CONNECTION)) {
            connection = new SOAPConnection();
        } else if (identifier.equals(SQL_CONNECTION)) {
            connection = new SQLConnection();
        } else if (identifier.equals(FTP_CONNECTION)) {
            connection = new FTPConnection();
        } else if (identifier.equals(SFTP_CONNECTION)) {
            connection = new SFTPConnection();
        }
        connection.initialize(connectionData);
        return connection;
    }
}
