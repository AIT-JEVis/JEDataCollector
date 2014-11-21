/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.IntegrationTestsNew;

import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTPClient;
import org.jevis.jedatacollector.connection.FTP.FTPConnection;
import org.jevis.jedatacollector.exception.FetchingException;

/**
 *
 * @author bf
 */
public class FakeFTPConnection extends FTPConnection{

    FakeFTPConnection(boolean b) {
        super(null, b, null, null, null, null, null, null, null);
    }
    
    @Override
    public boolean connect() throws FetchingException {
        System.out.println("Connect to ftp fake connection");
        try {
            ((FakeFTPClient)_fc).connect("testConnection", 1);
        } catch (SocketException ex) {
            Logger.getLogger(FakeFTPConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FakeFTPConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    public void setClient (FakeFTPClient client){
        _fc = client;
    }
}
