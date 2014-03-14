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
import org.jevis.jedatacollector.parsingNew.csvParsing.DatapointFixCSVParser;
import org.jevis.jedatacollector.parsingNew.csvParsing.DateCSVParser;
import org.jevis.jedatacollector.parsingNew.csvParsing.ValueCSVParser;
import org.jevis.jedatacollector.parsingNew.GeneralDatapointParser;
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
public class HttpAlpha_CsvVida {

    File _file;
    Request _request;

    public HttpAlpha_CsvVida() {
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
        DatacollectorConnection connection = new HTTPConnection("192.168.2.55", "/Parsing/JEVis_DEFAULT_example.csv", 80, 300, 30);
        DataCollectorParser fileParser = new CSVParsing("\"", ";", 1);

        GeneralDatapointParser datapointParser = new DatapointFixCSVParser(false, 22);
        GeneralDateParser dateParser = new DateCSVParser(null, null, "dd-MM-yyyy HH:mm:ss", 1);
        GeneralValueParser valueParser = new ValueCSVParser(2, ".", null);

        SampleParserContainer sampleContainer = new SampleParserContainer(datapointParser, dateParser, valueParser);
        fileParser.addSampleContainer(sampleContainer);

        Request request = RequestGenerator.createConnectionParsingRequest(connection, fileParser);

        DataCollector collector = new DataCollector(request);
        collector.run();

        List<Result> resultList = collector.getResults();
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
