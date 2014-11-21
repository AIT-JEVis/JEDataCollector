/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.LoytecTests;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jevis.commons.parsing.inputHandler.SOAPMessageInputHandler;
import org.jevis.jedatacollector.DataCollector;
import org.jevis.jedatacollector.Request;
import org.jevis.jedatacollector.RequestGenerator;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.jevis.jedatacollector.connection.SOAP.SOAPConnection;
import org.jevis.jedatacollector.data.DataPoint;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 *
 * @author bf
 */
public class LinxSOAPConnectionTest {

    @Test
    public void test_alphaConnect() throws Exception {
        String template = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "    <SOAP-ENV:Header/>\n"
                + "    <SOAP-ENV:Body>\n"
                + "        <LogRead xmlns=\"http://www.loytec.com/wsdl/XMLDL/1.0/\" NumItems=\"500\" ReturnCompleteSet=\"false\" StartDateTime=\"2013-09-15T22:15:00\">\n"
                + "            <ReqBase clientItemHandle=\"360003409\" logHandle=\"00/var/lib//dpal/trend-15ED.bin\"/>\n"
                + "        </LogRead>\n"
                + "    </SOAP-ENV:Body>\n"
                + "</SOAP-ENV:Envelope>";
        String server = "http://admin:JjwwidHg!@212.17.98.149:80/DL";
        
        DataCollectorConnection connection = new SOAPConnection(template, server, "yyyy-MM-dd'T'HH:mm:ss'Z'", "0", 2l, 60l, null);
        
        String lastReadoutText = "01112014000000";
        DateTimeFormatter forPattern = DateTimeFormat.forPattern("ddMMyyyyHHmmss");
        DateTime lastReadout = forPattern.parseDateTime(lastReadoutText);
        DataPoint datapoint = new DataPoint(template, null, null, null, lastReadout, true);
        List<DataPoint> datapoints = new ArrayList<DataPoint>();
        datapoints.add(datapoint);

        Request request = RequestGenerator.createConnectionRequest(connection, datapoints);

        DataCollector collector = new DataCollector(request);
        collector.run();
        Document doc = ((SOAPMessageInputHandler) collector.getInputHandler().get(0)).getDocuments().get(0);
//        DOMSource domSource = new DOMSource(doc);
//        StringWriter writer = new StringWriter();
//        StreamResult result = new StreamResult(writer);
//        TransformerFactory tf = TransformerFactory.newInstance();
//        Transformer transformer = tf.newTransformer();
//        transformer.transform(domSource, result);
//        System.out.println(writer.toString());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(new File("linx.xml"));
        Source input = new DOMSource(doc);
        transformer.transform(input, output);
    }
}
