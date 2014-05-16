/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.CSVParsingTests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.jevis.jedatacollector.DataCollector;
import org.jevis.jedatacollector.Request;
import org.jevis.jedatacollector.RequestGenerator;
import org.jevis.jedatacollector.parsingNew.DataCollectorParser;
import org.jevis.jedatacollector.parsingNew.GeneralDatapointParser;
import org.jevis.jedatacollector.parsingNew.GeneralDateParser;
import org.jevis.jedatacollector.parsingNew.GeneralValueParser;
import org.jevis.jedatacollector.parsingNew.Result;
import org.jevis.jedatacollector.parsingNew.SampleParserContainer;
import org.jevis.jedatacollector.parsingNew.csvParsing.CSVParsing;
import org.jevis.jedatacollector.parsingNew.csvParsing.DatapointFixCSVParser;
import org.jevis.jedatacollector.parsingNew.csvParsing.DateCSVParser;
import org.jevis.jedatacollector.parsingNew.csvParsing.ValueCSVParser;
import org.jevis.jedatacollector.service.inputHandler.FileInputHandler;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author bf
 */
public class Test_ARA01 {

    public void test_ARA01() throws Exception {
        File file = new File("src/test/java/org/jevis/jedatacollector/CSVParsingTests/ARA01.csv");


        InputHandler inputHandler = new FileInputHandler(file);
        inputHandler.convertInput();

        DataCollectorParser fileParser = new CSVParsing(null, ";", 2);

        GeneralDatapointParser datapointParser = new DatapointFixCSVParser(false, 22);
        GeneralDateParser dateParser = new DateCSVParser("HH:mm:ss", 2, "dd.MM.yyyy", 3);
        GeneralValueParser valueParser = new ValueCSVParser(1, ".", ",");

        SampleParserContainer sampleContainer = new SampleParserContainer(datapointParser, dateParser, valueParser);
        fileParser.addSampleContainer(sampleContainer);

        Request request = RequestGenerator.createOnlyParsingRequest(fileParser, inputHandler);
//        ParsingService ps = new ParsingService(_request);
//        IParsing parser = new SinglePointCSV("dd.MM.yyyy", "HH:mm:ss", 3, 2, 1, ";", ",", ".", null, 2); //der Parser
        DataCollector instance = new DataCollector(request);
//        instance.setParsingService(ps);
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
        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 12:20:23"));
        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 12:35:23"));
        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 12:50:23"));
        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 13:05:23"));
        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 13:20:27"));
        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 13:35:27"));
        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 13:50:27"));
        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 14:05:27"));

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
