/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.commons.parsing.Result;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.commons.parsing.inputHandler.InputHandlerFactory;
import org.jevis.commons.parsing.outputHandler.OutputHandler;
import org.jevis.commons.parsing.outputHandler.OutputHandlerFactory;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.service.ConnectionService;
import org.jevis.jedatacollector.service.ParsingService;
import org.joda.time.DateTime;

/**
 *
 * @author broder
 */
public class DataCollector {

    private ParsingService _parsingService;
    private ConnectionService _connectionService;
    private InputHandler _inputHandler;
    private Request _request;
    private DateTime _lastDateInUTC;

    public DataCollector() {
    }

    public DataCollector(Request req) {
        _request = req;

        if (req.getInputHandler() != null) {
            _inputHandler = req.getInputHandler();
        }
    }
    //TODO validate EACH Step.. for example "Exist all information for the conenction, parsing...?

    public void run() throws FetchingException {
        if (_request.needConnection()) {
            Logger.getLogger(DataCollector.class.getName()).log(Level.INFO, "Start Connection");
            connect();
            getInput();
        }
        System.out.println("Need Parsing:"+_request.needParsing());
        if (_request.needParsing()) {
            Logger.getLogger(DataCollector.class.getName()).log(Level.INFO, "Start Parsing");
            parse();
        }
        if (_request.needImport()) {
            Logger.getLogger(DataCollector.class.getName()).log(Level.INFO, "Import Data");
            importData();
        }
    }

    public void parse() throws FetchingException {
//        if (_parsingService == null) {
        _parsingService = new ParsingService(_request.getParser(),_request.getOutputType(),_request.getData());
//        }
        _parsingService.parseData(_inputHandler);
    }

    public InputHandler getInputHandler() {
        return _inputHandler;
    }

    public void importData() {
        OutputHandler outputHandler = OutputHandlerFactory.getOutputHandler(_request.getOutputType());
        System.out.println("Outputtype: "+_request.getOutputType());
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
        _connectionService = new ConnectionService(_request.getConnectionData());
        _connectionService.connect();
    }

    private void getInput() throws FetchingException {
        DateTime from = _request.getFrom();
        DateTime until = _request.getUntil();
        //get oldest timestamp from all online data rows

        if (from == null) {
            DateTime currentFrom;
            for (DataPoint dp : _request.getDataPoints()) {
                currentFrom = getFrom(dp);
                if (from == null) {
                    from = currentFrom;
                } else if (from.isAfter(currentFrom)) {
                    from = currentFrom;
                }
            }
        }
        if (from == null && _request.getEquipment() != null) {
            from = _request.getEquipment().getStartCollectingTime();
        }
        if (from == null) {
            from = new DateTime(0);
        }
        if (until == null) {
            until = new DateTime();
        }
        DataPoint dp = null;
        if (!_request.getDataPoints().isEmpty()) {
            dp = _request.getDataPoints().get(0);
        }
       _inputHandler = _connectionService.sendSamplesRequest(from, until, dp);
       _inputHandler.convertInput();
    }

    public DateTime getFrom(DataPoint dp) {
        if (dp != null) {
            try {
                JEVisObject onlineData = Launcher.getClient().getObject(dp.getOnlineID());
                JEVisAttribute attribute = onlineData.getAttribute("Raw Data"); //TODO iwo auslagern
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
