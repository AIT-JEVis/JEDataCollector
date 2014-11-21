/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisSample;
import org.jevis.commons.JEVisTypes;
import org.jevis.commons.parsing.DataCollectorParser;
import org.jevis.commons.parsing.Result;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.commons.parsing.outputHandler.OutputHandler;
import org.jevis.commons.parsing.outputHandler.OutputHandlerFactory;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.service.ConnectionService;
import org.jevis.jedatacollector.service.ParsingService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author broder
 */
public class DataCollector {

    private ParsingService _parsingService;
    private ConnectionService _connectionService;
    private List<InputHandler> _inputHandler;
    private Request _request;
    private DateTime _lastDateInUTC;

    public DataCollector() {
    }

    public DataCollector(Request req) {
        _request = req;

        if (req.getInputHandler() != null) {
            _inputHandler = new ArrayList<InputHandler>();
            _inputHandler.add(req.getInputHandler());
        }
    }
    //TODO validate EACH Step.. for example "Exist all information for the conenction, parsing...?

    public void run() throws FetchingException {
        if (_request.needConnection()) {
            Logger.getLogger(DataCollector.class.getName()).log(Level.INFO, "Start Connection");
            connect();
            getInput();
        }
        System.out.println("Need Parsing:" + _request.needParsing());
        if (_request.needParsing()) {
            Logger.getLogger(DataCollector.class.getName()).log(Level.INFO, "Start Parsing");
            parse();
        }
        if (_request.needImport()) {
            Logger.getLogger(DataCollector.class.getName()).log(Level.INFO, "Import Data");
            importData();
        }
        if (_request.getParsingRequest() != null && _request.getParsingRequest().getOutputType().equals(OutputHandler.JEVIS_OUTPUT)) {
            for (DataPoint dp : _request.getDataPoints()) {
                try {
                    String currentReadout = null;
                    JEVisSample latestSample = Launcher.getClient().getObject(Long.parseLong(dp.getTarget())).getAttribute("Value").getLatestSample();
                    if (dp.getPeriodicallySampling()) {
                        currentReadout = dp.getCurrentReadoutString();
                    } else if (latestSample != null) {
                        currentReadout = latestSample.getTimestamp().toString(DateTimeFormat.forPattern("ddMMyyyyHHmmss"));
                    }
                    JEVisSample buildSample = dp.getJEVisDatapoint().getAttribute(JEVisTypes.DataPoint.LAST_READOUT).buildSample(new DateTime(), currentReadout);
                    List<JEVisSample> sampleList = new ArrayList<JEVisSample>();
                    sampleList.add(buildSample);
                    dp.getJEVisDatapoint().getAttribute(JEVisTypes.DataPoint.LAST_READOUT).addSamples(sampleList);
                } catch (JEVisException ex) {
                    java.util.logging.Logger.getLogger(DataCollector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void parse() throws FetchingException {
//        if (_parsingService == null) {
        _parsingService = new ParsingService(_request.getParser(), _request.getOutputType(), _request.getData());
        //        }
        DataCollectorParser fileParser = _parsingService.getFileParser();
        for (DataPoint dp : _request.getDataPoints()) {
            fileParser.addDataPointParser(dp.getDatapointId(), dp.getTarget(), dp.getMappingIdentifier(), dp.getValueIdentifier());
        }
        for (InputHandler inputHandler : _inputHandler) {
            _parsingService.parseData(inputHandler);
        }
    }
    public List<InputHandler> getInputHandler() {
        return _inputHandler;
    }

    public void importData() {
        OutputHandler outputHandler = OutputHandlerFactory.getOutputHandler(_request.getParsingRequest().getOutputType());
        System.out.println("Outputtype: " + _request.getParsingRequest().getOutputType());
        outputHandler.writeOutput(_request.getParsingRequest(), getResults());
    }

//    public DateTime convertTimeInTransitionRange(DateTimeZone from, DateTime currentDate, DateTime lastDate) {
//
//        long timeBetween = currentDate.getMillis() - lastDate.getMillis();
//        if (timeBetween != 240000) {
//            System.out.println("###############################NICHT GLEICH!!!!!!!!!");
//            System.out.println(timeBetween);
//            System.out.println("c " + currentDate);
//            System.out.println("l " + lastDate);
//        }
//        long timeBetween = 240000;
//        return lastDate.plusMillis((int) timeBetween);
//    }
    public List<Result> getResults() {
        return _parsingService.getFileParser().getResults();
    }

    private void connect() throws FetchingException {
//        _connectionService = new ConnectionService();
        _request.getDataSource().connect();
    }

    private void getInput() throws FetchingException {

        for (DataPoint dp : _request.getDataPoints()) {
            DateTime from = _request.getFrom();
            if (from == null) {
                from = dp.getLastReadout();
            }
            DateTime until = _request.getUntil();
            if (until == null) {
                until = new DateTime();
            }
            _inputHandler = _request.getDataSource().sendSampleRequest(dp, from, until);
            break;
        }
        for (InputHandler inputHandler : _inputHandler) {
            inputHandler.convertInput();
        }
    }
    public DateTime getFrom(DataPoint dp) {
        if (dp != null) {
            try {
                JEVisObject onlineData = Launcher.getClient().getObject(Long.parseLong(dp.getTarget()));
                JEVisAttribute attribute = onlineData.getAttribute("Value"); //TODO iwo auslagern
                if (attribute.getLatestSample() != null) {
                    return attribute.getTimestampFromLastSample();
                } else {
                    //TODO iwas überlegen
                }
            } catch (JEVisException ex) {
                Logger.getLogger(DataCollector.class.getName()).log(Level.ERROR, null, ex);
            }
        } else {
            //TODO wie oben das gleiche!
        }
        return null;
    }
//    public void setParsingService(ParsingService ps) {
//        _parsingService = ps;
//    }
//
//    public void setInputConverter(InputHandler handler) {
//        _inputHandler = handler;
//    }
//    private void initializeInputConverter(List<Object> rawResult) {
//        Logger.getLogger(DataCollector.class.getName()).log(Level.INFO, "Initialize Input Converter");
//        _inputHandler = InputHandlerFactory.getInputConverter(rawResult);
//        _inputHandler.convertInput(); //this should happn in the converter
//    }
}
