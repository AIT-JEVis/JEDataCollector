/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.connection;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
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

    private static final String HTTP_CONNECTION = ("HTTP");
    private static final String FTP_CONNECTION = ("FTP");
    private static final String SOAP_CONNECTION = ("SOAP");
    private static final String SQL_CONNECTION = ("SQL");

    public static DatacollectorConnection getConnection(JEVisObject connectionData) throws FetchingException {
        DatacollectorConnection connection = null;
        if (connectionData == null) {
            throw new FetchingException(connectionData.getID(), FetchingExceptionType.CONNECTION_DRIVER_ERROR);
        }
        String identifier = null;
        try {
            identifier = connectionData.getJEVisClass().getName();
        } catch (JEVisException ex) {
            Logger.getLogger(ConnectionFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (identifier.equals(HTTP_CONNECTION)) {
            connection = new HTTPConnection();
        } else if (identifier.equals(SOAP_CONNECTION)) {
            connection = new SOAPConnection();
        } else if (identifier.equals(SQL_CONNECTION)) {
            connection = new SQLConnection();
        }
        connection.initialize(connectionData);
        return connection;
    }
}
