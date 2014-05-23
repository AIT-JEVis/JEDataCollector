/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jedatacollector.data.JevisAttributes;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;

/**
 *
 * @author broder
 */
public abstract class DataCollectorParser {

    protected List<SampleParserContainer> _sampleParsers;
    protected List<Result> _results;
    protected JEVisObject _jevisParser;

    public DataCollectorParser() {
        _results = new ArrayList<Result>();
        _sampleParsers = new ArrayList<SampleParserContainer>();
    }

//    public void initializeValueParser() {
//        //erstmal einfach
//        _valueParser.add(new ValueCSVEasyParser());
//    }
//
//    public void initializeDatapointParser() {
//        //erstmal einfach
//        _dpParser.add(new DatapointCSVEasyParser());
//    }
//
//    public void initializeDateParser() {
//        //erstmal einfach
//        _dateParser.add(new DateCSVEasyParser());
//    }
    public void addSampleContainer(SampleParserContainer parser) {
        //erstmal einfach
        _sampleParsers.add(parser);
    }

    public List<SampleParserContainer> getSampleParserContianers() {
        return _sampleParsers;
    }

    public JEVisObject getJEVisParser() {
        return _jevisParser;
    }

    public List<Result> getResults() {
        return _results;
    }

    public abstract void parse(InputHandler ic);

    abstract public void initialize(JEVisObject parser);

    /**
     * Creates the SampleContainer for a parsing. All Parsings have one
     * DateObject, one ValueObject and 1-n Mappingobjects
     *
     * @param parser
     */
    public void createSampleContainers(JEVisObject parser, List<JEVisObject> datapoints) {
        try {
            List<JEVisObject> mappingObjects = datapoints;
            List<JEVisObject> dateObjects = parser.getChildren(parser.getDataSource().getJEVisClass(JevisAttributes.PARSER_DATE), true);
            List<JEVisObject> valueObjects = parser.getChildren(parser.getDataSource().getJEVisClass(JevisAttributes.PARSER_VALUE), true);

            JEVisObject dateObject = null;
            JEVisObject valueObject = null;
            if (dateObjects.size() == 1 && valueObjects.size() == 1) {
                dateObject = dateObjects.get(0);
                valueObject = valueObjects.get(0);
            } else {
                System.out.println("more than one dateobject or valueobject"); //should be an exception
            }

            GeneralDateParser dateParser = null;
            GeneralValueParser valueParser = null;
            GeneralMappingParser datapointParser = null;
            for (JEVisObject o : mappingObjects) {
                datapointParser = initializeDatapointParser(dateObject, valueObject, o);
                dateParser = initializeDateParser(dateObject, valueObject, o);
                valueParser = initializeValueParser(dateObject, valueObject, o);
                _sampleParsers.add(new SampleParserContainer(datapointParser, dateParser, valueParser));
            }
        } catch (JEVisException ex) {
            Logger.getLogger(DataCollectorParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    abstract public SampleParserContainer extractSampleContainer(JEVisObject mapping, JEVisObject dateObject, JEVisObject valueObject);
    abstract public GeneralDateParser initializeDateParser(JEVisObject dateObject, JEVisObject valueObject, JEVisObject datapointObject);

    abstract public GeneralMappingParser initializeDatapointParser(JEVisObject dateObject, JEVisObject valueObject, JEVisObject datapointObject);

    abstract public GeneralValueParser initializeValueParser(JEVisObject dateObject, JEVisObject valueObject, JEVisObject datapointObject);
}
