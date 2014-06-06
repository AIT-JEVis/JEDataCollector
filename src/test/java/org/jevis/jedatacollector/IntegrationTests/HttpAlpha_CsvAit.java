/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.IntegrationTests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.jevis.jedatacollector.DataCollector;

import org.jevis.jedatacollector.Request;
import org.jevis.jedatacollector.RequestGenerator;
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.connection.HTTP.HTTPConnection;
import org.jevis.jedatacollector.parsingNew.DataCollectorParser;
import org.jevis.jedatacollector.parsingNew.Result;
import org.jevis.jedatacollector.parsingNew.csvParsing.CSVParsing;
import org.jevis.jedatacollector.parsingNew.csvParsing.MappingFixCSVParser;
import org.jevis.jedatacollector.parsingNew.csvParsing.DateCSVParser;
import org.jevis.jedatacollector.parsingNew.csvParsing.ValueCSVParser;
import org.jevis.jedatacollector.parsingNew.GeneralMappingParser;
import org.jevis.jedatacollector.parsingNew.GeneralDateParser;
import org.jevis.jedatacollector.parsingNew.GeneralValueParser;
import org.jevis.jedatacollector.parsingNew.SampleParserContainer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Broder
 */
public class HttpAlpha_CsvAit {

    File _file;
    Request _request;

    public HttpAlpha_CsvAit() {
    }

    public void setUp() {
    }

    public void tearDown() {
    }

    /**
     * Testcases:
     *
     */
    public void test_alphaConnect() throws Exception {
        
        DatacollectorConnection connection = new HTTPConnection("192.168.2.55", "/Parsing/data-all.csv", 80, 300, 30);
        DataCollectorParser fileParser = new CSVParsing("\"", ";", 1);

        GeneralDateParser dateParser = new DateCSVParser(null, null, "dd-MM-yyyy HH:mm.ss", 1,DateTimeZone.UTC);
        GeneralMappingParser datapointParser2 = new MappingFixCSVParser(false, 2);
        GeneralValueParser valueParser2 = new ValueCSVParser(2, ",", null);
        GeneralMappingParser datapointParser3 = new MappingFixCSVParser(false, 3);
        GeneralValueParser valueParser3 = new ValueCSVParser(3, ",", null);
        GeneralMappingParser datapointParser4 = new MappingFixCSVParser(false, 4);
        GeneralValueParser valueParser4 = new ValueCSVParser(4, ",", null);
        GeneralMappingParser datapointParser5 = new MappingFixCSVParser(false, 5);
        GeneralValueParser valueParser5= new ValueCSVParser(5, ",", null);
        GeneralMappingParser datapointParser6 = new MappingFixCSVParser(false, 6);
        GeneralValueParser valueParser6 = new ValueCSVParser(6, ",", null);
        GeneralMappingParser datapointParser22 = new MappingFixCSVParser(false, 22);
        GeneralValueParser valueParser22 = new ValueCSVParser(22, ",", null);


        SampleParserContainer sampleContainer = new SampleParserContainer(datapointParser2, dateParser, valueParser2);
        fileParser.addSampleContainer(sampleContainer);
        sampleContainer = new SampleParserContainer(datapointParser3, dateParser, valueParser3);
        fileParser.addSampleContainer(sampleContainer);
        sampleContainer = new SampleParserContainer(datapointParser4, dateParser, valueParser4);
        fileParser.addSampleContainer(sampleContainer);
        sampleContainer = new SampleParserContainer(datapointParser5, dateParser, valueParser5);
        fileParser.addSampleContainer(sampleContainer);
        sampleContainer = new SampleParserContainer(datapointParser6, dateParser, valueParser6);
        fileParser.addSampleContainer(sampleContainer);
        sampleContainer = new SampleParserContainer(datapointParser22, dateParser, valueParser22);
        fileParser.addSampleContainer(sampleContainer);
        Request request = RequestGenerator.createConnectionParsingRequest(connection, fileParser);

        DataCollector collector = new DataCollector(request);
        collector.run();

        List<Result> resultList = collector.getResults();
        List<DateTime> realDateTimes = new ArrayList<DateTime>();
        List<Double> realValues2 = new ArrayList<Double>();
        List<Double> realValues3 = new ArrayList<Double>();
        List<Double> realValues4 = new ArrayList<Double>();
        List<Double> realValues5 = new ArrayList<Double>();
        List<Double> realValues6 = new ArrayList<Double>();
        List<Double> realValues22 = new ArrayList<Double>();
        for (Result r : resultList) {
            realDateTimes.add(r.getDate());
            if (r.getOnlineID() == 2) {
                realValues2.add(r.getValue());
            }
            if (r.getOnlineID() == 3) {
                realValues3.add(r.getValue());
            }
            if (r.getOnlineID() == 4) {
                realValues4.add(r.getValue());
            }
            if (r.getOnlineID() == 5) {
                realValues5.add(r.getValue());
            }
            if (r.getOnlineID() == 6) {
                realValues6.add(r.getValue());
            }
            if (r.getOnlineID() == 22) {
                realValues22.add(r.getValue());
            }
        }

        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm.ss");
        List<DateTime> expectedDateTimes = new ArrayList<DateTime>();
        expectedDateTimes.add(dtf.parseDateTime("01-06-2009 00:00.00").toDateTime(DateTimeZone.UTC));
        expectedDateTimes.add(dtf.parseDateTime("20-10-2012 01:48.00").toDateTime(DateTimeZone.UTC));

        List expectedValues2 = new ArrayList<Double>();
        expectedValues2.add(13.8);
        expectedValues2.add(9.6);


        Assert.assertEquals(expectedValues2.get(0), realValues2.get(0));
        Assert.assertEquals(expectedValues2.get(1), realValues2.get(realValues2.size() - 1));

    }
}
