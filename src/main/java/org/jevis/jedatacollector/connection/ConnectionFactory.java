/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.connection;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.jedatacollector.connection.FTP.FTPConnection;
import org.jevis.jedatacollector.connection.FTP.SFTPConnection;
import org.jevis.jedatacollector.connection.HTTP.HTTPConnection;
import org.jevis.commons.JEVisTypes;
import org.jevis.jedatacollector.CLIProperties.ConnectionCLIParser;

/**
 *
 * @author broder
 */
public class ConnectionFactory {

    public static DataCollectorConnection getConnection(JEVisObject jevisObject) throws JEVisException {

        JEVisClass httpClass = jevisObject.getDataSource().getJEVisClass(JEVisTypes.DataServer.HTTP.NAME);
        //workaround for inherit bug, normally only with jevic class parser and connection
        JEVisClass ftpClass = jevisObject.getDataSource().getJEVisClass(JEVisTypes.DataServer.FTP.NAME);
        JEVisClass sftpClass = jevisObject.getDataSource().getJEVisClass(JEVisTypes.DataServer.sFTP.NAME);

        JEVisObject connectionObject = null;

        if (jevisObject.getJEVisClass().getName().equals(httpClass.getName()) || jevisObject.getJEVisClass().getName().equals(sftpClass.getName()) || jevisObject.getJEVisClass().getName().equals(ftpClass.getName())) {
            connectionObject = jevisObject;
        } else {
            List<JEVisObject> connectionObjects = jevisObject.getChildren(httpClass, true);
            if (connectionObjects.size() == 1) {
                connectionObject = connectionObjects.get(0);
                org.apache.log4j.Logger.getLogger(ConnectionFactory.class.getName()).log(org.apache.log4j.Level.INFO, "http Connection");
                //same workaround as above
            } else {
                connectionObjects = jevisObject.getChildren(ftpClass, true);
                if (connectionObjects.size() == 1) {
                    connectionObject = connectionObjects.get(0);
                    org.apache.log4j.Logger.getLogger(ConnectionFactory.class.getName()).log(org.apache.log4j.Level.INFO, "ftp Connection");
                    //same workaround as above
                } else {
                    connectionObjects = jevisObject.getChildren(sftpClass, true);
                    if (connectionObjects.size() == 1) {
                        connectionObject = connectionObjects.get(0);
                        org.apache.log4j.Logger.getLogger(ConnectionFactory.class.getName()).log(org.apache.log4j.Level.INFO, "sftp Connection");
                        //same workaround as above
                    } else {
                        throw new JEVisException("Number of Connection Objects != 1 under: " + jevisObject.getID(), 1);
                    }
                }
            }
        }

        DataCollectorConnection connection = null;
        String identifier = null;

        try {
            identifier = connectionObject.getJEVisClass().getName();
        } catch (JEVisException ex) {
            Logger.getLogger(ConnectionFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (identifier.equals(JEVisTypes.DataServer.HTTP.NAME)) {
            connection = new HTTPConnection();
//        } else if (identifier.equals(JEVisTypes.DataServer.SOAP.NAME)) {
//            connection = new SOAPConnection();
//        } else if (identifier.equals(JEVisTypes.DataServer.SQL.NAME)) {
//            connection = new SQLConnection();
        } else if (identifier.equals(JEVisTypes.DataServer.FTP.NAME)) {
            connection = new FTPConnection();
        } else if (identifier.equals(JEVisTypes.DataServer.sFTP.NAME)) {
            connection = new SFTPConnection();
        }
        return connection;
    }

    public static DataCollectorConnection getConnection(ConnectionCLIParser con) {
        DataCollectorConnection connection = null;
        String identifier = con.getConnectionType();

        if (identifier.equals(JEVisTypes.DataServer.HTTP.NAME)) {
            connection = new HTTPConnection(con.getIP(), con.getPath(), con.getPort(), con.getConnectionTimeout(), con.getReadTimeout(), con.getDateFormat());
//        } else if (identifier.equals(JEVisTypes.DataServer.SOAP.NAME)) {
//            connection = new SOAPConnection();
//        } else if (identifier.equals(JEVisTypes.DataServer.SQL.NAME)) {
//            connection = new SQLConnection();
        } else if (identifier.equals(JEVisTypes.DataServer.FTP.NAME)) {
            connection = new FTPConnection(con.getDateFormat(), con.getPath(), con.getFileName(), con.getIP(), con.getPort(), con.getUser(), con.getPassword(), con.getConnectionTimeout(), con.getReadTimeout(), con.getSecureConnection());
        } else if (identifier.equals(JEVisTypes.DataServer.sFTP.NAME)) {
            connection = new SFTPConnection(con.getDateFormat(), con.getPath(), con.getFileName(), con.getIP(), con.getPort(), con.getUser(), con.getPassword(), con.getConnectionTimeout(), con.getReadTimeout());
        }
        return connection;
    }
}
