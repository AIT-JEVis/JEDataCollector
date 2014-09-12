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
import org.jevis.commons.JEVisTypes;
import org.jevis.jedatacollector.CLIProperties.ConnectionCLIParser;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.exception.FetchingExceptionType;

/**
 *
 * @author broder
 */
public class ConnectionFactory {



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

        if (identifier.equals(JEVisTypes.Connection.HTTP.Name)) {
            connection = new HTTPConnection();
        } else if (identifier.equals(JEVisTypes.Connection.SOAP.Name)) {
            connection = new SOAPConnection();
        } else if (identifier.equals(JEVisTypes.Connection.SQL.Name)) {
            connection = new SQLConnection();
        } else if (identifier.equals(JEVisTypes.Connection.FTP.Name)) {
            connection = new FTPConnection();
        } else if (identifier.equals(JEVisTypes.Connection.sFTP.Name)) {
            connection = new SFTPConnection();
        }
        return connection;
    }

    public static DatacollectorConnection getConnection(ConnectionCLIParser con) {
                DatacollectorConnection connection = null;
        String identifier = con.getConnectionType();

        if (identifier.equals(JEVisTypes.Connection.HTTP.Name)) {
            connection = new HTTPConnection(con.getIP(), con.getPath(), con.getPort(), con.getConnectionTimeout(), con.getReadTimeout(), con.getDateFormat());
        } else if (identifier.equals(JEVisTypes.Connection.SOAP.Name)) {
            connection = new SOAPConnection();
        } else if (identifier.equals(JEVisTypes.Connection.SQL.Name)) {
            connection = new SQLConnection();
        } else if (identifier.equals(JEVisTypes.Connection.FTP.Name)) {
            connection = new FTPConnection(con.getDateFormat(), con.getPath(), con.getFileName(), con.getIP(),con.getPort(), con.getUser(), con.getPassword(), con.getConnectionTimeout(), con.getReadTimeout(),con.getSecureConnection());
        } else if (identifier.equals(JEVisTypes.Connection.sFTP.Name)) {
            connection = new SFTPConnection(con.getDateFormat(), con.getPath(), con.getFileName(), con.getIP(),con.getPort(), con.getUser(), con.getPassword(), con.getConnectionTimeout(), con.getReadTimeout());
        }
        return connection;
    }
}
