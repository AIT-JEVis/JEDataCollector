///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.jevis.jedatacollector.ConnectionTests;
//
///**
// *
// * @author bf
// */
//public class ILONSOAPConnectionTest {
//
//    public void test_iLONConnect() throws Exception {
////        String template = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?> <SOAP-ENV:Envelope      SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Body><DataLogger_Read xmlns=\"http://wsdl.echelon.com/web_services_ns/ilon100/v3.0/message/\"><DATA><Log><UCPTpointName>*DATAPOINT*</UCPTpointName><UCPTindex>*DATALOGGER*</UCPTindex><UCPTstart>*DATE_FROM*</UCPTstart><UCPTstop>*DATE_TO*</UCPTstop><UCPTcount>50</UCPTcount></Log></DATA></DataLogger_Read></SOAP-ENV:Body> </SOAP-ENV:Envelope>";
////        DatacollectorConnection connection = new SOAPConnection(template, "http://192.168.2.209/WSDL/iLON100.WSDL", "yyyy-MM-dd'T'HH:mm:ss'Z'", "0", 2l, 60l, null);
////
////        DateTime until = new DateTime();
////        DateTime from = until.minusDays(10);
////        NewDataPoint datapoint = new NewDataPoint("NVE_VIVA24nvoSteamTemp", "0", null);
////
////        Request request = RequestGenerator.createConnectionRequestWithTimeperiod(connection, datapoint, from, until);
////
////        DataCollector collector = new DataCollector(request);
////        collector.run();
////
////        Document doc = ((SOAPMessageInputHandler) collector.getInputHandler()).getDocuments().get(0);
////
////        Transformer transformer = TransformerFactory.newInstance().newTransformer();
////        Result output = new StreamResult(new File("ilon.xml"));
////        Source input = new DOMSource(doc);
////
////        transformer.transform(input, output);
////
////        NodeList nodeNames = doc.getElementsByTagName("UCPTpointName");
////        NodeList nodeDates = doc.getElementsByTagName("UCPTlogTime");
////        NodeList nodeValues = doc.getElementsByTagName("UCPTvalue");
////        Assert.assertTrue(nodeNames.getLength() > 0);
////        Assert.assertTrue(nodeDates.getLength() > 0);
////        Assert.assertTrue(nodeValues.getLength() > 0);
////        Assert.assertTrue(nodeNames.getLength() == nodeDates.getLength());
////        Assert.assertTrue(nodeValues.getLength() == nodeDates.getLength());
//    }
//}
