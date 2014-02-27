package org.jevis.jedatacollector.CSVParsingTests;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
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
import org.jevis.jedatacollector.parsingNew.sampleParser.CSV.DatapointFixCSVParser;
import org.jevis.jedatacollector.parsingNew.sampleParser.CSV.DateCSVParser;
import org.jevis.jedatacollector.parsingNew.sampleParser.CSV.ValueCSVParser;
import org.jevis.jedatacollector.parsingNew.sampleParser.GeneralDatapointParser;
import org.jevis.jedatacollector.parsingNew.sampleParser.GeneralDateParser;
import org.jevis.jedatacollector.parsingNew.sampleParser.GeneralValueParser;
import org.jevis.jedatacollector.parsingNew.sampleParser.SampleParserContainer;
import org.jevis.jedatacollector.service.ConnectionService;
import org.jevis.jedatacollector.service.ParsingService;
import org.jevis.jedatacollector.service.inputHandler.FileInputHandler;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Broder
 */
public class TestCSVParsing {

    File _file;
    Request _request;

    public TestCSVParsing() {
    }

    public void setUp() {
        _file = new File("test/ParsingTests/ARA01.csv");
        _request = RequestGenerator.createOnlyParsingRequest();
    }

    public void tearDown() {
    }

    /**
     * Testcases: ARA01 Format Date and Time: seperated Value: thousand sep = ,
     * and dec sep = .
     *
     */
    public void test_ARA01() throws Exception {
        System.out.println("parse");
        _file = new File("src/test/java/org/jevis/jedatacollector/CSVParsingTests/ARA01.csv");
        _request = RequestGenerator.createOnlyParsingRequest();


        DataCollector instance = new DataCollector(_request);
        InputHandler inputHandler = new FileInputHandler(_file);
        instance.setInputConverter(inputHandler);
        DataCollectorParser fileParser = new CSVParsing(null, ";", 2);

        GeneralDatapointParser datapointParser = new DatapointFixCSVParser(false, 22);
        GeneralDateParser dateParser = new DateCSVParser("HH:mm:ss", 2, "dd.MM.yyyy", 3);
        GeneralValueParser valueParser = new ValueCSVParser(1, ".", ",");

        SampleParserContainer sampleContainer = new SampleParserContainer(datapointParser, dateParser, valueParser);
        fileParser.addSampleContainer(sampleContainer);

        ParsingService ps = new ParsingService();
        ps.setFileParser(fileParser);
//        IParsing parser = new SinglePointCSV("dd.MM.yyyy", "HH:mm:ss", 3, 2, 1, ";", ",", ".", null, 2); //der Parser
        instance.setParsingService(ps);
        instance.run();

        List<Result> resultList = instance.getResults();
        List<Double> realValues = new ArrayList<Double>();
        List<DateTime> realDateTimes = new ArrayList<DateTime>();
        List<Long> realDatapoints = new ArrayList<Long>();
        for (Result r : resultList) {
            realValues.add(r.getValue());
            realDateTimes.add(r.getDate());
            realDatapoints.add(r.getDatapoint());
        }

        List expectedValues = new ArrayList<Double>();
        expectedValues.add(117.55);
        expectedValues.add(110375d);
        expectedValues.add(11775d);
        expectedValues.add(130125d);
        expectedValues.add(12775d);
        expectedValues.add(12775d);
        expectedValues.add(124875.12);
        expectedValues.add(1235d);

        List expectedDatapoints = new ArrayList<String>();
        expectedDatapoints.add(22l);
        expectedDatapoints.add(22l);
        expectedDatapoints.add(22l);
        expectedDatapoints.add(22l);
        expectedDatapoints.add(22l);
        expectedDatapoints.add(22l);
        expectedDatapoints.add(22l);
        expectedDatapoints.add(22l);

        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        List<DateTime> expectedDateTimes = new ArrayList<DateTime>();
        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 12:20:23").toDateTime(DateTimeZone.UTC));
        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 12:35:23").toDateTime(DateTimeZone.UTC));
        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 12:50:23").toDateTime(DateTimeZone.UTC));
        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 13:05:23").toDateTime(DateTimeZone.UTC));
        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 13:20:27").toDateTime(DateTimeZone.UTC));
        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 13:35:27").toDateTime(DateTimeZone.UTC));
        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 13:50:27").toDateTime(DateTimeZone.UTC));
        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 14:05:27").toDateTime(DateTimeZone.UTC));

        Assert.assertEquals(expectedValues.size(), realValues.size());
        Assert.assertEquals(expectedDateTimes.size(), realDateTimes.size());
        Assert.assertEquals(expectedDatapoints.size(), realDatapoints.size());
        for (int i = 0; i < expectedDateTimes.size(); i++) {
            Assert.assertEquals(expectedValues.get(i), realValues.get(i));
            Assert.assertEquals(expectedDateTimes.get(i), realDateTimes.get(i));
            Assert.assertEquals(expectedDatapoints.get(i), realDatapoints.get(i));
        }
    }

    /**
     * Testcases: JEvisDefault Format Date and Time: seperated Value: thousand
     * sep = , and dec sep = .
     *
     */
    public void test_JEVisDefault() throws Exception {
        System.out.println("parse");
        _file = new File("src/test/java/org/jevis/jedatacollector/CSVParsingTests/JEVis_DEFAULT_example.csv");
        _request = RequestGenerator.createOnlyParsingRequest();

        InputHandler inputHandler = new FileInputHandler(_file);

        DataCollector instance = new DataCollector(_request);
        instance.setInputConverter(inputHandler);
        DataCollectorParser fileParser = new CSVParsing("\"", ";", 1);

        GeneralDatapointParser datapointParser = new DatapointFixCSVParser(false, 22);
        GeneralDateParser dateParser = new DateCSVParser(null, null, "dd-MM-yyyy HH:mm:ss", 1);
        GeneralValueParser valueParser = new ValueCSVParser(2, ".", null);

        SampleParserContainer sampleContainer = new SampleParserContainer(datapointParser, dateParser, valueParser);
        fileParser.addSampleContainer(sampleContainer);

        ParsingService ps = new ParsingService();
        ps.setFileParser(fileParser);
//        IParsing parser = new SinglePointCSV("dd.MM.yyyy", "HH:mm:ss", 3, 2, 1, ";", ",", ".", null, 2); //der Parser
        instance.setParsingService(ps);
        instance.run();

        List<Result> resultList = instance.getResults();
        List<Double> realValues = new ArrayList<Double>();
        List<DateTime> realDateTimes = new ArrayList<DateTime>();
        List<Long> realDatapoints = new ArrayList<Long>();
        for (Result r : resultList) {
            realValues.add(r.getValue());
            realDateTimes.add(r.getDate());
            realDatapoints.add(r.getDatapoint());
        }

        List expectedValues = new ArrayList<Double>();
        expectedValues.add(129599.99999999852);
        expectedValues.add(144000.00000000332);
        expectedValues.add(129599.99999999852);
        expectedValues.add(143999.99999999694);

        List expectedDatapoints = new ArrayList<String>();
        expectedDatapoints.add(22l);
        expectedDatapoints.add(22l);
        expectedDatapoints.add(22l);
        expectedDatapoints.add(22l);

        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss");

        List<DateTime> expectedDateTimes = new ArrayList<DateTime>();
        expectedDateTimes.add(dtf.parseDateTime("13-07-2012 00:00:00").toDateTime(DateTimeZone.UTC));
        expectedDateTimes.add(dtf.parseDateTime("13-07-2012 00:15:00").toDateTime(DateTimeZone.UTC));
        expectedDateTimes.add(dtf.parseDateTime("13-07-2012 00:30:00").toDateTime(DateTimeZone.UTC));
        expectedDateTimes.add(dtf.parseDateTime("13-07-2012 00:45:00").toDateTime(DateTimeZone.UTC));

        Assert.assertEquals(expectedValues.size(), realValues.size());
        Assert.assertEquals(expectedDateTimes.size(), realDateTimes.size());
        Assert.assertEquals(expectedDatapoints.size(), realDatapoints.size());
        for (int i = 0; i < expectedDateTimes.size(); i++) {
            Assert.assertEquals(expectedValues.get(i), realValues.get(i));
            Assert.assertEquals(expectedDateTimes.get(i), realDateTimes.get(i));
            Assert.assertEquals(expectedDatapoints.get(i), realDatapoints.get(i));
        }
    }
}
