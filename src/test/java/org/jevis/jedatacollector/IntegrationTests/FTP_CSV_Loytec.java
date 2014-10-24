/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.IntegrationTests;

import java.util.List;
import junit.framework.Assert;
import org.jevis.commons.parsing.GenericParser;
import org.jevis.commons.parsing.GeneralDateParser;
import org.jevis.commons.parsing.GeneralMappingParser;
import org.jevis.commons.parsing.GeneralValueParser;
import org.jevis.commons.parsing.Result;
import org.jevis.commons.parsing.SampleParserContainer;
import org.jevis.commons.parsing.csvParsing.CSVParsing;
import org.jevis.commons.parsing.csvParsing.DateCSVParser;
import org.jevis.commons.parsing.csvParsing.MappingFixCSVParser;
import org.jevis.commons.parsing.csvParsing.ValueCSVParser;
import org.jevis.jedatacollector.DataCollector;
import org.jevis.jedatacollector.Request;
import org.jevis.jedatacollector.RequestGenerator;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.jevis.jedatacollector.connection.FTP.FTPConnection;
import org.jevis.jedatacollector.exception.FetchingException;

/**
 *
 * @author bf
 */
public class FTP_CSV_Loytec {

    public void test_loytec_ftp_csv() throws FetchingException {
//        DatacollectorConnection connection = new FTPConnection("", "/data/", "Trend_L1_1_UI1_Input_10C6.csv", "192.168.2.254", "admin", "envidatec4u", 200l, 2l);
//        DataCollectorParser fileParser = new CSVParsing(null, ",", 7);
//
//        GeneralMappingParser datapointParser = new MappingFixCSVParser(false, 22);
//        GeneralDateParser dateParser = new DateCSVParser(null, null, "yyyy-MM-dd HH:mm:ss", 5);
//        GeneralValueParser valueParser = new ValueCSVParser(6, ".", null);
//
//        SampleParserContainer sampleContainer = new SampleParserContainer(datapointParser, dateParser, valueParser);
//        fileParser.addSampleContainer(sampleContainer);
//
//
//        Request request = RequestGenerator.createConnectionParsingRequest(connection, fileParser);
//        DataCollector collector = new DataCollector(request);
//        collector.run();
//
//        System.out.println(collector.getInputHandler().getStringArrayInput()[0]);
//        System.out.println(collector.getInputHandler().getStringArrayInput()[1]);
//        System.out.println(collector.getInputHandler().getStringArrayInput()[2]);
//        System.out.println(collector.getInputHandler().getStringArrayInput()[3]);
//        System.out.println(collector.getInputHandler().getStringArrayInput()[4]);
//
//        System.out.println("size " + collector.getResults().size());
//        List<Result> results = collector.getResults();
//        Assert.assertEquals(0.06846, results.get(0).getValue());
//        Assert.assertEquals(0.06846, results.get(1).getValue());
//        Assert.assertEquals(0.06846, results.get(2).getValue());
//        Assert.assertEquals(0.06863, results.get(results.size() - 3).getValue());
//        Assert.assertEquals(0.06863, results.get(results.size() - 2).getValue());
//        Assert.assertEquals(0.06863, results.get(results.size() - 1).getValue());
        //        Document doc = ((SOAPMessageInputHandler) collector.getInputHandler()).getDocument().get(0);
        //        NodeList nodeNames = doc.getElementsByTagName("UCPTpointName");
        //        NodeList nodeDates = doc.getElementsByTagName("UCPTlogTime");
        //        NodeList nodeValues = doc.getElementsByTagName("UCPTvalue");
        //        Assert.assertTrue(nodeNames.getLength() > 0);
        //        Assert.assertTrue(nodeDates.getLength() > 0);
        //        Assert.assertTrue(nodeValues.getLength() > 0);
        //        Assert.assertTrue(nodeNames.getLength() == nodeDates.getLength());
        //        Assert.assertTrue(nodeValues.getLength() == nodeDates.getLength());
    }
}
