/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jevis.api.JEVisObject;
import org.jevis.commons.parsing.DataCollectorParser;
import org.jevis.commons.parsing.GenericParser;
import org.jevis.commons.parsing.SampleParserContainer;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.jedatacollector.data.Data;
import org.jevis.jedatacollector.data.DataPoint;

/**
 *
 * @author broder
 */
public class ParsingService {

    public static boolean checkValue(SampleParserContainer parser) {
        boolean validValue = parser.getValueParser().isValueValid();
        return validValue;
    }

    public static boolean checkDatapoint(SampleParserContainer parser) {
        return parser.getDpParser().isMappingSuccessfull();
    }
    private DataCollectorParser _fileParser;

    public ParsingService(DataCollectorParser parser) {
        _fileParser = parser;
//        if (_fileParser.getSampleParserContianers().isEmpty()) {
////            List<JEVisObject> datapoints = new ArrayList<JEVisObject>();
////            JEVisObject dp = request.getSpecificDatapoint().getJEVisDatapoint();
////            if (dp != null) {
////                datapoints.add(dp);
////            } else {
////                for (NewDataPoint ndp : request.getData().getDatapoints()) {
////                    datapoints.add(ndp.getJEVisDatapoint());
////                }
////            }
//            List<JEVisObject> datapoints = new ArrayList<JEVisObject>();
//            for (DataPoint ndp : request.getDataPoints()) {
//                datapoints.add(ndp.getJEVisDatapoint());
//            }
//            _fileParser.createSampleContainers(_fileParser.getJEVisParser(),datapoints);
//        }
    }

    public ParsingService(DataCollectorParser parser, String outputType, Data data) {
        _fileParser = parser;
        if (data != null && _fileParser.getSampleParserContianers().isEmpty()) {
            List<JEVisObject> jevisDatapoints = new ArrayList<JEVisObject>();
            for (DataPoint dp : data.getDatapoints()) {
                jevisDatapoints.add(dp.getJEVisDatapoint());
            }
            _fileParser.createSampleContainers(data.getParsing().getJEVisParser(), jevisDatapoints);
        }
    }

//    /**
//     * Parses the given answer to a list of samples
//     *
//     * @param answer the received answer from the request
//     * @return a list of samples
//     */
//    public void parseData(InputHandler inputConverter, TimeZone tz) throws FetchingException {
//        _parser.parseData(inputConverter, tz);
//    }
//    
//    public ParsedData getParsedData(){
//        return _parser.getData();
//    }
    public void setFileParser(GenericParser fileParser) {
        _fileParser = fileParser;
    }

    public void parseData(InputHandler ic) {
        _fileParser.parse(ic);
    }

    public DataCollectorParser getFileParser() {
        return _fileParser;
    }
}
