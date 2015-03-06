/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.LoytecTests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jevis.commons.parsing.DataCollectorParser;
import org.jevis.commons.parsing.inputHandler.SOAPMessageInputHandler;
import org.jevis.commons.parsing.xmlParsing.XMLParsing;
import org.jevis.jedatacollector.DataCollector;
import org.jevis.jedatacollector.service.Request;
import org.jevis.jedatacollector.service.RequestGenerator;
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
        String templateLoytec = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "    <SOAP-ENV:Header/>\n"
                + "    <SOAP-ENV:Body>\n"
                + "        <LogRead xmlns=\"http://www.loytec.com/wsdl/XMLDL/1.0/\" NumItems=\"500\" ReturnCompleteSet=\"false\" StartDateTime=\"2015-02-23T22:15:00\">\n"
                + "            <ReqBase clientItemHandle=\"360003409\" logHandle=\"00/var/lib/dpal//trend-15E3.bin\"/>\n"
                + "        </LogRead>\n"
                + "    </SOAP-ENV:Body>\n"
                + "</SOAP-ENV:Envelope>";
        String templateEnvidatec = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "    <SOAP-ENV:Header/>\n"
                + "    <SOAP-ENV:Body>\n"
                + "        <LogRead xmlns=\"http://www.loytec.com/wsdl/XMLDL/1.0/\" NumItems=\"500\" ReturnCompleteSet=\"false\" StartDateTime=\"2015-02-23T22:15:00\">\n"
                + "            <ReqBase clientItemHandle=\"360003409\" logHandle=\"00/var/lib/dpal/trend-10C3.bin\"/>\n"
                + "        </LogRead>\n"
                + "    </SOAP-ENV:Body>\n"
                + "</SOAP-ENV:Envelope>";
        String template2 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "    <SOAP-ENV:Header/>\n"
                + "    <SOAP-ENV:Body>\n"
                + "        <LogRead xmlns=\"http://www.loytec.com/wsdl/XMLDL/1.0/\" NumItems=\"500\" ReturnCompleteSet=\"false\" StartDateTime=\"${DF:yyyy-MM-ddTHH:mm:ss}\">\n"
                + "            <ReqBase clientItemHandle=\"360003409\" logHandle=\"00/var/lib//dpal/trend-15ED.bin\"/>\n"
                + "        </LogRead>\n"
                + "    </SOAP-ENV:Body>\n"
                + "</SOAP-ENV:Envelope>";
        String template3 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "    <SOAP-ENV:Header/>\n"
                + "    <SOAP-ENV:Body>\n"
                + "        <LogRead xmlns=\"http://www.loytec.com/wsdl/XMLDL/1.0/\" NumItems=\"500\" ReturnCompleteSet=\"false\" StartDateTime=\"2014-10-15T22:15:00\">\n"
                + "            <ReqBase logHandle=\"00/var/lib//dpal/trend-15E3.bin\"/>\n"
                + "        </LogRead>\n"
                + "    </SOAP-ENV:Body>\n"
                + "</SOAP-ENV:Envelope>";
        String server = "http://admin:JjwwidHg!@212.17.98.149:80";
//        String server = "http://admin:envidatec4u@195.186.2.254:80";

        DataCollectorConnection connection = new SOAPConnection(null, false, "192.168.2.254", 80, 200, 500, "admin", "envidatec4u", "Europe/Berlin");
        DataCollectorConnection connection1 = new SOAPConnection(null, false, "10.97.5.14", 80, 200, 500, "admin", "JjwwidHg!", "Europe/Berlin");
        DataCollectorParser parser = new XMLParsing(server, template2, Boolean.FALSE);

        String lastReadoutText = "20112014000000";
        DateTimeFormatter forPattern = DateTimeFormat.forPattern("ddMMyyyyHHmmss");
        DateTime lastReadout = forPattern.parseDateTime(lastReadoutText);
        DataPoint datapoint = new DataPoint(templateLoytec, null, null, null, lastReadout, true);
        List<DataPoint> datapoints = new ArrayList<DataPoint>();
        datapoints.add(datapoint);

        Request request = RequestGenerator.createConnectionRequest(connection1, datapoints);

        DataCollector collector = new DataCollector(request);
        collector.run();
        Document doc = ((SOAPMessageInputHandler) collector.getInputHandler().get(0)).getDocuments().get(0);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(new File("linx.xml"));
        Source input = new DOMSource(doc);
        transformer.transform(input, output);
    }
}
