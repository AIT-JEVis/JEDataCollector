/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.CSVParsingTests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.jevis.commons.parsing.DataCollectorParser;
import org.jevis.commons.parsing.GeneralDateParser;
import org.jevis.commons.parsing.GeneralMappingParser;
import org.jevis.commons.parsing.GeneralValueParser;
import org.jevis.commons.parsing.Result;
import org.jevis.commons.parsing.SampleParserContainer;
import org.jevis.commons.parsing.csvParsing.CSVParsing;
import org.jevis.commons.parsing.csvParsing.DateCSVParser;
import org.jevis.commons.parsing.csvParsing.MappingCSVParser;
import org.jevis.commons.parsing.csvParsing.ValueCSVParser;
import org.jevis.commons.parsing.inputHandler.FileInputHandler;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.jedatacollector.DataCollector;
import org.jevis.jedatacollector.Request;
import org.jevis.jedatacollector.RequestGenerator;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author bf
 */
public class Test_MCA01 {

    public void test_MCA01() throws Exception {
        File file = new File("src/test/java/org/jevis/jedatacollector/CSVParsingTests/MCA01.csv");


        InputHandler inputHandler = new FileInputHandler(file);
        inputHandler.convertInput();

        DataCollectorParser fileParser = new CSVParsing(null, ",", 0);

        GeneralMappingParser datapointParser1 = new MappingCSVParser(true, 3333l, "3", 2);
        GeneralMappingParser datapointParser2 = new MappingCSVParser(true, 4444l, "4", 2);
        GeneralMappingParser datapointParser3 = new MappingCSVParser(true, 5555l, "5", 2);
        GeneralDateParser dateParser = new DateCSVParser(null, null, "dd.MM.yyyy HH:mm:ss", 1);
        GeneralValueParser valueParser = new ValueCSVParser(3, ".", null);

        SampleParserContainer sampleContainer1 = new SampleParserContainer(datapointParser1, dateParser, valueParser);
        SampleParserContainer sampleContainer2 = new SampleParserContainer(datapointParser2, dateParser, valueParser);
        SampleParserContainer sampleContainer3 = new SampleParserContainer(datapointParser3, dateParser, valueParser);
        fileParser.addSampleContainer(sampleContainer1);
        fileParser.addSampleContainer(sampleContainer2);
        fileParser.addSampleContainer(sampleContainer3);

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
            realDatapoints.add(r.getOnlineID());
        }

        List expectedValues = new ArrayList<Double>();
        expectedValues.add(265266.000);
        expectedValues.add(3030.420);
        expectedValues.add(55.0);
        expectedValues.add(1312d);
        expectedValues.add(23.0);
        expectedValues.add(192.23);

        List expectedDatapoints = new ArrayList<String>();
        expectedDatapoints.add(3333l);
        expectedDatapoints.add(4444l);
        expectedDatapoints.add(5555l);
        expectedDatapoints.add(3333l);
        expectedDatapoints.add(4444l);
        expectedDatapoints.add(5555l);

        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss");

        List<DateTime> expectedDateTimes = new ArrayList<DateTime>();
        expectedDateTimes.add(dtf.parseDateTime("29.06.2009 14:45:00"));
        expectedDateTimes.add(dtf.parseDateTime("29.06.2009 14:45:00"));
        expectedDateTimes.add(dtf.parseDateTime("29.06.2009 14:45:00"));
        expectedDateTimes.add(dtf.parseDateTime("30.06.2009 15:45:00"));
        expectedDateTimes.add(dtf.parseDateTime("30.06.2009 15:45:00"));
        expectedDateTimes.add(dtf.parseDateTime("30.06.2009 15:45:00"));

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
