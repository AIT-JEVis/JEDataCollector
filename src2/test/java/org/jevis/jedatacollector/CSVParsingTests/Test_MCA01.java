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
import org.jevis.commons.parsing.Result;
import org.jevis.commons.parsing.csvParsing.CSVParsing;
import org.jevis.commons.parsing.inputHandler.FileInputHandler;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.jedatacollector.DataCollector;
import org.jevis.jedatacollector.service.Request;
import org.jevis.jedatacollector.service.RequestGenerator;
import org.jevis.jedatacollector.data.DataPoint;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

/**
 *
 * @author bf
 */
public class Test_MCA01 {

    @Test
    public void test_MCA01() throws Exception {
        File file = new File("src/test/java/org/jevis/jedatacollector/CSVParsingTests/MCA01.csv");

        InputHandler inputHandler = new FileInputHandler(file);
        inputHandler.convertInput();

        DataCollectorParser fileParser = new CSVParsing(null, ",", 0, 1, null, 2, "dd.MM.yyyy HH:mm:ss", null, null, null);
        DataPoint datapoint1 = new DataPoint(null, "3", null, "3", null, null);
//        DataPoint datapoint2 = new DataPoint(null, "4", null, "3", null, true);
//        DataPoint datapoint3 = new DataPoint(null, "5", null, "3", null, true);

        List<InputHandler> inputHandlers = new ArrayList<InputHandler>();
        inputHandlers.add(inputHandler);
        List<DataPoint> datapoints = new ArrayList<DataPoint>();
        datapoints.add(datapoint1);
//        datapoints.add(datapoint2);
//        datapoints.add(datapoint3);
        Request request = RequestGenerator.createOnlyParsingRequest(fileParser, datapoints, inputHandlers);

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
        expectedValues.add(265266d);
//        expectedValues.add(3030d);
//        expectedValues.add(55d);
        expectedValues.add(1312d);
//        expectedValues.add(23d);
//        expectedValues.add(192d);

//        List expectedDatapoints = new ArrayList<String>();
//        expectedDatapoints.add(3333l);
//        expectedDatapoints.add(4444l);
//        expectedDatapoints.add(5555l);
//        expectedDatapoints.add(3333l);
//        expectedDatapoints.add(4444l);
//        expectedDatapoints.add(5555l);

        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss");

        List<DateTime> expectedDateTimes = new ArrayList<DateTime>();
        expectedDateTimes.add(dtf.parseDateTime("29.06.2009 14:45:00"));
//        expectedDateTimes.add(dtf.parseDateTime("29.06.2009 14:45:00"));
//        expectedDateTimes.add(dtf.parseDateTime("29.06.2009 14:45:00"));
        expectedDateTimes.add(dtf.parseDateTime("30.06.2009 15:45:00"));
//        expectedDateTimes.add(dtf.parseDateTime("30.06.2009 15:45:00"));
//        expectedDateTimes.add(dtf.parseDateTime("30.06.2009 15:45:00"));

        Assert.assertEquals(expectedValues.size(), realValues.size());
        Assert.assertEquals(expectedDateTimes.size(), realDateTimes.size());
//        Assert.assertEquals(expectedDatapoints.size(), realDatapoints.size());
        for (int i = 0; i < expectedDateTimes.size(); i++) {
            Assert.assertEquals(expectedValues.get(i), realValues.get(i));
            Assert.assertEquals(expectedDateTimes.get(i), realDateTimes.get(i));
//            Assert.assertEquals(expectedDatapoints.get(i), realDatapoints.get(i));
        }
    }
}
