/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.XMLParsingTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.jevis.commons.parsing.GenericParser;
import org.jevis.commons.parsing.GeneralDateParser;
import org.jevis.commons.parsing.GeneralMappingParser;
import org.jevis.commons.parsing.GeneralValueParser;
import org.jevis.commons.parsing.Result;
import org.jevis.commons.parsing.SampleParserContainer;
import org.jevis.commons.parsing.inputHandler.FileInputHandler;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.commons.parsing.xmlParsing.DatapointFixXMLParsing;
import org.jevis.commons.parsing.xmlParsing.DateXMLParsing;
import org.jevis.commons.parsing.xmlParsing.ValueXMLParsing;
import org.jevis.commons.parsing.xmlParsing.XMLParsing;
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
public class Test_Single_XML {

    /**
     * Testcases: ARA01 Format Date and Time: seperated Value: thousand sep = ,
     * and dec sep = .
     *
     */
    public void test_ilon_envidatec() throws Exception {
        System.out.println("parse");
        System.out.println(DateTimeFormat.longDateTime().print(546216445l));
        System.out.println(DateTimeFormat.mediumDateTime());
        File file = new File("src/test/java/org/jevis/jedatacollector/XMLParsingTest/XML_single_normal.xml");
        InputHandler inputHandler = new FileInputHandler(file);
        inputHandler.convertInput();

        GenericParser fileParser = new XMLParsing("Element", null, null);
        GeneralMappingParser datapointParser = new DatapointFixXMLParsing(false, 1234l);
        GeneralDateParser dateParser = new DateXMLParsing("yyyy-MM-dd HH:mm:ss.SSSZ", "UCPTlogTime", false, null, null, false);
        GeneralValueParser valueParser = new ValueXMLParsing("UCPTvalue", false, ".", ",");

        SampleParserContainer sampleContainer = new SampleParserContainer(datapointParser, dateParser, valueParser);
        fileParser.addSampleContainer(sampleContainer);

        Request request = RequestGenerator.createOnlyParsingRequest(fileParser, inputHandler);

        DataCollector instance = new DataCollector(request);

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
        expectedValues.add(44.0);
        expectedValues.add(44.0);
        expectedValues.add(44.0);
        expectedValues.add(45.0);
        expectedValues.add(44.0);

        List expectedDatapoints = new ArrayList<String>();
        expectedDatapoints.add(1234l);
        expectedDatapoints.add(1234l);
        expectedDatapoints.add(1234l);
        expectedDatapoints.add(1234l);
        expectedDatapoints.add(1234l);

        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
//        DateTimeFormatter dtf = DateTimeFormat.fullDate();

        List<DateTime> expectedDateTimes = new ArrayList<DateTime>();
        expectedDateTimes.add(dtf.parseDateTime("2012-12-14 01:03:00.391"));
        expectedDateTimes.add(dtf.parseDateTime("2012-12-14 01:04:00.391"));
        expectedDateTimes.add(dtf.parseDateTime("2012-12-14 01:05:00.441"));
        expectedDateTimes.add(dtf.parseDateTime("2012-12-14 01:06:00.401"));
        expectedDateTimes.add(dtf.parseDateTime("2012-12-14 01:07:00.501"));

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
