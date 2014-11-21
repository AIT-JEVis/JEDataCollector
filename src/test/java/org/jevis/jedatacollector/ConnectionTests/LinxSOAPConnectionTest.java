/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.ConnectionTests;

import java.io.File;
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
import org.w3c.dom.Document;

/**
 *
 * @author bf
 */
public class LinxSOAPConnectionTest {

    public void test_alphaConnect() throws Exception {
        String template = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "    <SOAP-ENV:Header/>\n"
                + "    <SOAP-ENV:Body>\n"
                + "        <LogRead xmlns=\"http://www.loytec.com/wsdl/XMLDL/1.0/\" NumItems=\"500\" ReturnCompleteSet=\"false\" StartDateTime=\"2014-04-15T10:30:00\">\n"
                + "            <ReqBase logHandle=\"00/var/lib/dpal/trend-10DF.bin.1\"/>\n"
                + "        </LogRead>\n"
                + "    </SOAP-ENV:Body>\n"
                + "</SOAP-ENV:Envelope>";
        DataCollectorConnection connection = new SOAPConnection(template, "http://admin:envidatec4u@192.168.2.254/DL", "yyyy-MM-dd'T'HH:mm:ss'Z'", "0", 2l, 60l, null);

        DateTime until = new DateTime();
        DateTime from = until.minusDays(15);
        System.out.println("from "+from);
        System.out.println("until "+until);
        DataPoint datapoint = new DataPoint("NVE_VIVA24nvoSteamTemp", "0",null);

        Request request = RequestGenerator.createConnectionRequestWithTimeperiod(connection, datapoint, from, until);

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
