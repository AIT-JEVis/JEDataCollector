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
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jeapi.JEVisSample;
import org.jevis.jedatacollector.data.NewDataPoint;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.service.ConnectionService;
import org.jevis.jedatacollector.service.ParsingService;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;
import org.jevis.jedatacollector.parsingNew.Result;

/**
 *
 * @author broder
 */
public class DataCollector {

    protected ParsingService _parsingService;
    protected ConnectionService _connection;
    protected InputHandler _inputHandler;
    protected Request _request;

    public DataCollector(Request req) {
        _request = req;
    }
    //TODO validate EACH Step.. for example "Exist all information for the conenction, parsing...?

    public void run() throws FetchingException {
        if (_request.needConnection()) {
            connect();
            getInput();
        }
        System.out.println("Beginne parsen");
        parse();
        System.out.println("Parsen fertig");
        if (_request.needImport()) {
            importData();
        }
        System.out.println("Alles importiert");
    }

    public void parse() throws FetchingException {
        if (_parsingService == null) {
            _parsingService = new ParsingService(_request);
        }
        _parsingService.parseData(_inputHandler);
    }

    public void importData() {
        try {
            List<Result> results = getResults();
            System.out.println("Resultlistsize: " + results.size());
            Map<JEVisObject, List<JEVisSample>> onlineToSampleMap = new HashMap<JEVisObject, List<JEVisSample>>();

            //extract all online nodes and save them in a map
            for (NewDataPoint dp : _request.getData().getDatapoints()) {
                JEVisObject onlineData = dp.getJEVisOnlineData();
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
                long datapoint = s.getDatapoint();
                NewDataPoint dataPoint = _request.getData().getDataPointPerOnlineID(datapoint);
                JEVisObject onlineData = dataPoint.getJEVisOnlineData();
                List<JEVisSample> samples = onlineToSampleMap.get(dataPoint.getJEVisOnlineData());
                JEVisSample sample = onlineData.getAttribute("Raw Data").buildSample(s.getDate(), s.getValue());
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

    public List<Result> getResults() {
        return _parsingService.getFileParser().getResults();
    }

    private void connect() throws FetchingException {
        _connection = new ConnectionService(_request.getConnectionData());
        _connection.connect();
    }

    private void getInput() throws FetchingException {
        _inputHandler = _connection.sendSamplesRequest(_request.getData().getFrom(_request.getSpecificDatapoint()), _request.getSpecificDatapoint());
//        _inputHandler.convertInput();
    }

    public void setParsingService(ParsingService ps) {
        _parsingService = ps;
    }

    public void setInputConverter(InputHandler handler) {
        _inputHandler = handler;
    }
}
