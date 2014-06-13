/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisSample;
import org.jevis.jedatacollector.data.Data;
import org.jevis.jedatacollector.data.NewDataPoint;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.service.ConnectionService;
import org.jevis.jedatacollector.service.ParsingService;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;
import org.jevis.jedatacollector.parsingNew.Result;
import org.jevis.jedatacollector.service.inputHandler.InputFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author broder
 */
public class DataCollector {

    protected ParsingService _parsingService;
    protected ConnectionService _connection;
    protected InputHandler _inputHandler;
    protected Request _request;
    protected DateTime _lastDateInUTC;

    public DataCollector() {
    }

    public DataCollector(Request req) {
        _request = req;

        if (req.getInputHandler() != null) {
            _inputHandler = req.getInputHandler();
        }
        System.out.println("Default Timezone " + DateTimeZone.getDefault());
    }
    //TODO validate EACH Step.. for example "Exist all information for the conenction, parsing...?

    public void run() throws FetchingException {
        if (_request.needConnection()) {
            connect();
            getInput();
        }
        if (_request.needParsing()) {
            System.out.println("Beginne parsen");
            parse();
            System.out.println("Parsen fertig");
        }
        if (_request.needImport()) {
            importData();
            System.out.println("Alles importiert");
        }
    }

    public void parse() throws FetchingException {
        if (_parsingService == null) {
            _parsingService = new ParsingService(_request);
        }
        _parsingService.parseData(_inputHandler);
    }

    public InputHandler getInputHandler() {
        return _inputHandler;
    }

    public void importData() {
        try {
            List<Result> results = getResults();
            System.out.println("Resultlistsize: " + results.size());
            Map<JEVisObject, List<JEVisSample>> onlineToSampleMap = new HashMap<JEVisObject, List<JEVisSample>>();

            //extract all online nodes and save them in a map
            for (NewDataPoint dp : _request.getDataPoints()) {
                Long onlineID = dp.getOnlineID();
                JEVisObject onlineData = Launcher.getClient().getObject(onlineID);
                onlineToSampleMap.put(onlineData, new ArrayList<JEVisSample>());
            }

//            JEVisClass onlineNode = online.getDataSource().getJEVisClass(OnlineData.ONLINE_DATAROW);
//            JEVisType rawAttributeType = onlineNode.getType(OnlineData.SAMPLE_ATTRIBUTE);
//            JEVisAttribute attribute = online.getAttribute(rawAttributeType);
//            List<JEVisSample> sampleList = new ArrayList<JEVisSample>();


            //look into all results and map the sample to the online node
            for (Result s : results) {
                //                DateTime time = s.getCal();
                //                System.out.println("value " + s.getVal());
                //                System.out.println("cal " + s.getCal());
                //                sampleList.add(attribute.buildSample(time, s.getVal()));
                long onlineID = s.getOnlineID();
                JEVisObject onlineData = Launcher.getClient().getObject(onlineID);
                List<JEVisSample> samples = onlineToSampleMap.get(onlineData);
                DateTime convertedDate = convertTime(_request.getTimezone(), s.getDate());
                JEVisSample sample = onlineData.getAttribute("Raw Data").buildSample(convertedDate, s.getValue());
                samples.add(sample);
            }

            for (JEVisObject o : onlineToSampleMap.keySet()) {
                List<JEVisSample> samples = onlineToSampleMap.get(o);
                System.out.println("ID: " + o.getID());
                System.out.println("Size: " + samples.size());
                o.getAttribute("Raw Data").addSamples(samples);
            }
        } catch (JEVisException ex) {
            Logger.getLogger(DataCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public DateTime convertTime(DateTimeZone from, DateTime time) {
        long timeInMillis = time.getMillis();
        DateTime dateTime = new DateTime(timeInMillis, from);
        long nextTransition = from.nextTransition(timeInMillis) - timeInMillis;
        long currentOffset = from.getOffset(timeInMillis);
//        from.getStandardOffset(timeInMillis);
        DateTime tmpTime = dateTime;
        if (_lastDateInUTC != null) {
            tmpTime = convertTimeInTransitionRange(from, dateTime, _lastDateInUTC);
        }
        dateTime = tmpTime.toDateTime(DateTimeZone.UTC);
        _lastDateInUTC = dateTime;
        return dateTime;
    }

    public DateTime convertTimeInTransitionRange(DateTimeZone from, DateTime currentDate, DateTime lastDate) {

        long timeBetween = currentDate.getMillis() - lastDate.getMillis();
        if (timeBetween != 240000) {
            System.out.println("###############################NICHT GLEICH!!!!!!!!!");
            System.out.println(timeBetween);
            System.out.println("c " + currentDate);
            System.out.println("l " + lastDate);
        }
//        long timeBetween = 240000;
        return lastDate.plusMillis((int) timeBetween);
    }

    public List<Result> getResults() {
        return _parsingService.getFileParser().getResults();
    }

    private void connect() throws FetchingException {
        _connection = new ConnectionService(_request.getConnectionData());
        _connection.connect();
    }

    private void getInput() throws FetchingException {
        DateTime from = _request.getFrom();
        DateTime until = _request.getUntil();
        //get oldest timestamp from all online data rows

        if (from == null) {
            DateTime currentFrom;
            for (NewDataPoint dp : _request.getDataPoints()) {
                currentFrom = getFrom(dp);
                if (from == null) {
                    from = currentFrom;
                } else if (from.isAfter(currentFrom)) {
                    from = currentFrom;
                }
            }
        }
        if (from == null) {
            from = new DateTime(0);
        }
//        if (until == null) { //TODO nur zu testzwecken so
            until = from.plusMinutes(45);
//        }
        List<Object> rawResult = _connection.sendSamplesRequest(from, until, _request.getDataPoints().get(0));

        initializeInputConverter(rawResult);
    }

    public DateTime getFrom(NewDataPoint dp) {
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
                Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
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

    private void initializeInputConverter(List<Object> rawResult) {
        System.out.println("###Initialisiere InputConverter###");
        _inputHandler = InputFactory.getInputConverter(rawResult);
        _inputHandler.convertInput(); //this should happn in the converter
    }
}
