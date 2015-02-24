/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import org.jevis.jedatacollector.service.Request;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PatternLayout;
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
import org.jevis.jedatacollector.service.ConnectionService;
import org.jevis.jedatacollector.service.ParsingService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 *
 * @author broder
 */
public class DataCollector implements Runnable {

    private ParsingService _parsingService;
    private ConnectionService _connectionService;
    private List<InputHandler> _inputHandler;
    private Request _request;
    private DateTime _lastDateInUTC;
    private Logger logger = Logger.getRootLogger();
    private FileAppender appender = null;

    public DataCollector() {
    }

    public DataCollector(Request req) {
        _request = req;

        if (req.getInputHandlers() != null) {
            _inputHandler = req.getInputHandlers();
        }
    }
    //TODO validate EACH Step.. for example "Exist all information for the conenction, parsing...?

    @Override
    public void run() {
        if (_request.needConnection()) {
            Logger.getLogger(DataCollector.class.getName()).log(Level.ALL, "Start Connection");
            connect();
            getInput();
        }
        System.out.println("Need Parsing:" + _request.needParsing());
        if (_request.needParsing()) {
            Logger.getLogger(DataCollector.class.getName()).log(Level.ALL, "Start Parsing");
            parse();
        }
        if (_request.needImport()) {
            Logger.getLogger(DataCollector.class.getName()).log(Level.ALL, "Import Data");
            importData();
        }
        if (_request.getParsingRequest() != null && _request.getParsingRequest().getOutputType().equals(OutputHandler.JEVIS_OUTPUT)) {
            for (DataPoint dp : _request.getDataPoints()) {
                String currentReadout = null;
                try {
                    JEVisSample latestSample = Launcher.getClient().getObject(Long.parseLong(dp.getTarget())).getAttribute("Value").getLatestSample();
                    if (!dp.getPeriodicallySampling()) {

                        currentReadout = dp.getCurrentReadoutString();
                    } else if (latestSample != null) {
                        currentReadout = latestSample.getTimestamp().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
                    }
                    JEVisSample buildSample = dp.getJEVisDatapoint().getAttribute(JEVisTypes.DataPoint.LAST_READOUT).buildSample(new DateTime(), currentReadout);
                    List<JEVisSample> sampleList = new ArrayList<JEVisSample>();
                    sampleList.add(buildSample);
                    dp.getJEVisDatapoint().getAttribute(JEVisTypes.DataPoint.LAST_READOUT).addSamples(sampleList);
                } catch (JEVisException ex) {
                    Logger.getLogger(DataCollector.class.getName()).log(Level.WARN, "Problems while calculating the Last Readout Attribute");
                    Logger.getLogger(DataCollector.class.getName()).log(Level.WARN, "Periodical Sampling: " + dp.getPeriodicallySampling());
                    Logger.getLogger(DataCollector.class.getName()).log(Level.WARN, "Calculated Readout: " + currentReadout);
                }
            }
        }
        try {
            Thread.sleep(50000);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public void parse() {
        _parsingService = new ParsingService(_request.getParser());
        DataCollectorParser fileParser = _parsingService.getFileParser();
        for (DataPoint dp : _request.getDataPoints()) {
            fileParser.addDataPointParser(dp.getDatapointId(), dp.getTarget(), dp.getMappingIdentifier(), dp.getValueIdentifier());
//            fileParser.addDataPoints(_request.getDataPoints());
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

    public List<Result> getResults() {
        return _parsingService.getFileParser().getResults();
    }

    private void connect() {
//        _connectionService = new ConnectionService();
        _request.getDataSource().connect();
    }

    private void getInput() {

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
                Logger.getLogger(DataCollector.class.getName()).log(Level.ERROR, ex.getMessage());
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

    private void initNewAppender(String NameForAppender, String Name4LogFile) {
//        logger = Logger.getLogger(NameForAppender); //NOT DEFAULT BY "logger = Logger.getLogger(TestJob.class);"

        appender = new FileAppender();
        appender.setLayout(new PatternLayout("%d{yyyy-MM-dd/HH:mm:ss.SSS/zzz} %-5p %c{1}:%L - %m%n"));
        appender.setFile(Name4LogFile);
        appender.setAppend(true);
        appender.setImmediateFlush(true);
        appender.activateOptions();
        appender.setName(NameForAppender);
        logger.setAdditivity(false);    //<--do not use default root logger
        logger.addAppender(appender);
    }
}
