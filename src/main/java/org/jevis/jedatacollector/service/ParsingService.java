/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service;

import java.util.ArrayList;
import java.util.List;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jedatacollector.Request;
import org.jevis.jedatacollector.data.NewDataPoint;
import org.jevis.jedatacollector.parsingNew.DataCollectorParser;
import org.jevis.jedatacollector.parsingNew.sampleParser.SampleParserContainer;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;

/**
 *
 * @author broder
 */
public class ParsingService {

    public static boolean checkValue(SampleParserContainer parser) {
        boolean validValue = parser.getValueParser().isValueValid();
        return validValue;
    }
//    private IParsing _parser;
    private DataCollectorParser _fileParser;

    public ParsingService(Request request) {
        _fileParser = request.getParser();
        if (_fileParser.getSampleParserContianers().isEmpty()) {
            List<JEVisObject> datapoints = new ArrayList<JEVisObject>();
            JEVisObject dp = request.getSpecificDatapoint().getJEVisDatapoint();
            if (dp != null) {
                datapoints.add(dp);
            } else {
                for (NewDataPoint ndp : request.getData().getDatapoints()) {
                    datapoints.add(ndp.getJEVisDatapoint());
                }
            }
            _fileParser.createSampleContainers(_fileParser.getJEVisParser(), datapoints);
        }
        //initilize of the FileParser.. in the future this should be from a factory and not with the IParsingObject
    }

    public ParsingService() {
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
    public void setFileParser(DataCollectorParser fileParser) {
        _fileParser = fileParser;
    }

    public void parseData(InputHandler ic) {
        _fileParser.parse(ic);
    }

    public DataCollectorParser getFileParser() {
        return _fileParser;
    }
}
