/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.connection.OPC;

import java.io.InputStream;
import java.util.List;
import org.jevis.api.JEVisObject;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.jevis.jedatacollector.exception.FetchingException;
import org.joda.time.DateTime;

/**
 *
 * @author max
 */
public class OPCConnection implements DataCollectorConnection
{

    @Override
    public boolean connect() throws FetchingException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<InputHandler> sendSampleRequest(DataPoint dp, DateTime from, DateTime until) throws FetchingException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

//    @Override
//    public boolean returnsLimitedSampleCount()
//    {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    public void initialize(JEVisObject node) throws FetchingException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
//    private SOAPConnectionFactory _soapConnectionFactory;
//    private javax.xml.soap.SOAPConnection _connection;
//    private URL _server;
//    private SOAPMessage _answer;
//
//    @Override
//    public boolean connect() throws FetchingException
//    {
//        try {
//
//            _server = new URL("http://" + user + ":" + password + "@" + endpoint +":"+port+ suffix);
//            _soapConnectionFactory = SOAPConnectionFactory.newInstance();
//            _connection = _soapConnectionFactory.createConnection();
//            _soapConnectionFactory.createConnection();
//
//            
//
//            //Close the Connection
//            _connection.close();
//
//        } catch (SOAPException ex) {
//            Logger.getLogger(OPCConnection.class.getName()).log(Level.SEVERE, null, ex);
//            System.err.print(ex);
//        } catch (UnsupportedOperationException ex) {
//            Logger.getLogger(OPCConnection.class.getName()).log(Level.SEVERE, null, ex);
//            System.err.print(ex);
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//            System.err.print(ex);
//        }
//    }
//
//    @Override
//    public List<Object> sendSampleRequest(DataPoint dp, TimeSet ts) throws FetchingException
//    {
//        _answer = _connection.call(_message, _server);
//    }
//
//    @Override
//    public boolean returnsLimitedSampleCount()
//    {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void initialize(INode node) throws FetchingException
//    {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public static void dumpItemState(Item item, ItemState state)
//    {
//        System.out.println(String.format(
//                "Item: %s, Value: %s, Timestamp: %tc, Quality: %d",
//                item.getId(), state.getValue(), state.getTimestamp(),
//                state.getQuality()));
//    }
//
//    public static void dumpTree(Branch branch, int level)
//    {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < level; i++)
//        {
//            sb.append("  ");
//        }
//        String indent = sb.toString();
//
//        for (Leaf leaf : branch.getLeaves())
//        {
//            System.out.println(indent + "Leaf: " + leaf.getName() + " ["
//                    + leaf.getItemId() + "]");
//        }
//        for (Branch subBranch : branch.getBranches())
//        {
//            System.out.println(indent + "Branch: " + subBranch.getName());
//            dumpTree(subBranch, level + 1);
//        }
//    }
    
//      @Override
//    public String getConnectionType() {
//        return null;
//    }

    @Override
    public String getTimezone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean isEnabled() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Long getID() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
