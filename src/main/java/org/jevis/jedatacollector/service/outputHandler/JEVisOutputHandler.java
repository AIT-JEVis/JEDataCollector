/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service.outputHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javolution.testing.TimeContext;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisSample;
import org.jevis.jedatacollector.DataCollector;
import org.jevis.jedatacollector.Launcher;
import org.jevis.jedatacollector.Request;
import org.jevis.jedatacollector.data.NewDataPoint;
import org.jevis.jedatacollector.parsingNew.Result;
import org.jevis.jedatacollector.service.TimeConverter;
import org.joda.time.DateTime;

/**
 *
 * @author bf
 */
public class JEVisOutputHandler extends OutputHandler {

    @Override
    public void writeOutput(Request request, List<Result> results) {
        try {
            Logger.getLogger(DataCollector.class.getName()).log(Level.ALL, "Number of results: " + results.size());
            Map<JEVisObject, List<JEVisSample>> onlineToSampleMap = new HashMap<JEVisObject, List<JEVisSample>>();

            //extract all online nodes and save them in a map
            for (NewDataPoint dp : request.getDataPoints()) {
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
                DateTime convertedDate = TimeConverter.convertTime(request.getTimezone(), s.getDate());
                JEVisSample sample = onlineData.getAttribute("Raw Data").buildSample(convertedDate, s.getValue());
                samples.add(sample);
            }

            for (JEVisObject o : onlineToSampleMap.keySet()) {
                List<JEVisSample> samples = onlineToSampleMap.get(o);
                Logger.getLogger(DataCollector.class.getName()).log(Level.INFO, "ID: " + o.getID());
                Logger.getLogger(DataCollector.class.getName()).log(Level.INFO, "Number of imported Samples: " + samples.size());
                o.getAttribute("Raw Data").addSamples(samples);
            }
        } catch (JEVisException ex) {
            Logger.getLogger(DataCollector.class.getName()).log(Level.ERROR, null, ex);
        }
    }
}
