/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.connection.SOAP;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.jevis.jedatacollector.connection.DataCollectorConnection;

/**
 *
 * @author max
 */
public class SOAPSender extends Thread
{

    private SOAPConnection _connection;
    private SOAPMessage _answer;
    private SOAPMessage _message;
    private URL _server;
    private DataCollectorConnection _con;

    public SOAPSender(SOAPConnection c, SOAPMessage m, URL s, DataCollectorConnection con)
    {
        _con = con;
        _connection = c;
        _server = s;
        _message = m;
    }

    @Override
    public void run()
    {
        try
        {
//            _message.writeTo(new NullOutputStream());
            System.out.println("Requesting "+_server);
            _answer = _connection.call(_message, _server);
        } catch (SOAPException ex)
        {
            System.out.println(ex.getMessage());
            Logger.getLogger(SOAPSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SOAPMessage getAnswer()
    {
        return _answer;
    }
    
    public class NullOutputStream extends OutputStream {
    @Override
      public void write(int b) throws IOException {
      }
    }
}
