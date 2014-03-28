/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.XMLParsingTest;

/**
 *
 * @author bf
 */
public class XMLParsingTest {

    /**
     * Testcases: ARA01 Format Date and Time: seperated Value: thousand sep = ,
     * and dec sep = .
     *
     */
    public void test_ilon_envidatec() throws Exception {
//        System.out.println("parse");
//        File file = new File("src/test/java/org/jevis/jedatacollector/XMLParsingTest/ilon_envidatec.xml");
//        Request request = RequestGenerator.createOnlyParsingRequest();
//
//        DataCollector instance = new DataCollector(request);
//        InputHandler inputHandler = new FileInputHandler(file);
//        instance.setInputConverter(inputHandler);
//        DataCollectorParser fileParser = new XMLParsingNew();
//
//        GeneralDatapointParser datapointParser = new DatapointXMLParsing("NVE_VIVA24nvoSteamTemp");
//        GeneralDateParser dateParser = new DateXMLParsing("UCPTlogTime");
//        GeneralValueParser valueParser = new ValueXMLParsing("UCPTvalue");
//
//        SampleParserContainer sampleContainer = new SampleParserContainer(datapointParser, dateParser, valueParser);
//        fileParser.addSampleContainer(sampleContainer);
//
//        ParsingService ps = new ParsingService();
//        ps.setFileParser(fileParser);
////        IParsing parser = new SinglePointCSV("dd.MM.yyyy", "HH:mm:ss", 3, 2, 1, ";", ",", ".", null, 2); //der Parser
//        instance.setParsingService(ps);
//        instance.run();
//
//        List<Result> resultList = instance.getResults();
//        List<Double> realValues = new ArrayList<Double>();
//        List<DateTime> realDateTimes = new ArrayList<DateTime>();
//        List<Long> realDatapoints = new ArrayList<Long>();
//        for (Result r : resultList) {
//            realValues.add(r.getValue());
//            realDateTimes.add(r.getDate());
//            realDatapoints.add(r.getDatapoint());
//        }
//
//        List expectedValues = new ArrayList<Double>();
//        expectedValues.add(117.55);
//        expectedValues.add(110375d);
//        expectedValues.add(11775d);
//        expectedValues.add(130125d);
//        expectedValues.add(12775d);
//        expectedValues.add(12775d);
//        expectedValues.add(124875.12);
//        expectedValues.add(1235d);
//
//        List expectedDatapoints = new ArrayList<String>();
//        expectedDatapoints.add(22l);
//        expectedDatapoints.add(22l);
//        expectedDatapoints.add(22l);
//        expectedDatapoints.add(22l);
//        expectedDatapoints.add(22l);
//        expectedDatapoints.add(22l);
//        expectedDatapoints.add(22l);
//        expectedDatapoints.add(22l);
//
//        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
//
//        List<DateTime> expectedDateTimes = new ArrayList<DateTime>();
//        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 12:20:23").toDateTime(DateTimeZone.UTC));
//        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 12:35:23").toDateTime(DateTimeZone.UTC));
//        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 12:50:23").toDateTime(DateTimeZone.UTC));
//        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 13:05:23").toDateTime(DateTimeZone.UTC));
//        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 13:20:27").toDateTime(DateTimeZone.UTC));
//        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 13:35:27").toDateTime(DateTimeZone.UTC));
//        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 13:50:27").toDateTime(DateTimeZone.UTC));
//        expectedDateTimes.add(dtf.parseDateTime("2002-05-15 14:05:27").toDateTime(DateTimeZone.UTC));
//
//        Assert.assertEquals(expectedValues.size(), realValues.size());
//        Assert.assertEquals(expectedDateTimes.size(), realDateTimes.size());
//        Assert.assertEquals(expectedDatapoints.size(), realDatapoints.size());
//        for (int i = 0; i < expectedDateTimes.size(); i++) {
//            Assert.assertEquals(expectedValues.get(i), realValues.get(i));
//            Assert.assertEquals(expectedDateTimes.get(i), realDateTimes.get(i));
//            Assert.assertEquals(expectedDatapoints.get(i), realDatapoints.get(i));
//        }
    }
}
