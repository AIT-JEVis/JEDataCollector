/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jeapi.*;
import org.jevis.jeapi.sql.JEVisDataSourceSQL;
import org.jevis.jedatacollector.data.Data;

/**
 *
 * @author broder
 */
public class TestMain {

    private JEVisDataSource _client;
//    private final Logger _logger = Logger.getLogger(getClass());
    private static final String EQUIPMENT = "Datalogger";
    private static final String PARSER = "Parser";
    private static final String CONNECTION = "Connection";
    private static final String DATAPOINT = "Datapoint";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
            System.out.println("###starte DataLogger###");
            
            //Parse the parameter from commandline
            DataLoggerCommandLine cmd = DataLoggerCommandLine.getInstance();
            cmd.parse(args);
     
            PropertyConfigurator.configure("log4j.properties");
            
            Logger.getRootLogger().info("Info");
            Logger.getRootLogger().error("Warning");
            
            if(cmd.needHelp()){
                cmd.showHelp();
                System.exit(0);
            }

            TestMain adf = new TestMain(true);
    //        adf.getDataSamples();
    //        adf.createNewSample();
            JEVisObject equip = adf.getEquipment();


            adf.fetch(equip);

    }

    public TestMain(boolean connect) {
//        JevLoginHandler.createDirectLogin(user, pass, host);
        if (connect) {
            System.out.println("Verbinden zum Config");
            try {
                _client = new JEVisDataSourceSQL("192.168.2.55", "3306", "jevis", "jevis", "jevistest", "Sys Admin", "jevis");
                _client.connect("Sys Admin", "jevis");
            } catch (JEVisException ex) {
                Logger.getLogger(TestMain.class.getName()).log(Level.ERROR, null, ex);
            }
            System.out.println("Verbinden zum Config erfolgreich");
        }
    }

    //TODO vllt diesen job nur für jevis und nen anderen für andere....
    //TODO Threads pro Equipment oder pro Anfrage (bei VIDA geht es nicht)
    public void fetch(JEVisObject equip) {
        Data data = getJEVisData(equip);

        List<Request> requests = RequestGenerator.createJEVisRequests(data);

        for (Request request : requests) {
            DataCollector datalogger = new DataCollector(request);
            try {
                datalogger.run();

            } catch (Throwable t) {
                if (t instanceof FetchingException) {
                    FetchingException fe = (FetchingException) t;

                    if (fe.createAlarm()) {
                        System.out.println(fe.getMsg());
//                    setStatusFailed(n);
//                    setAlarm(n, fe);
                    } else {
//                    JevHandler.printDebug(fe.getMsg(), 2);
                    }

                } else {
                    Logger.getLogger(TestMain.class.getName()).log(Level.ERROR, null, t);
                }
            }
        }
    }

//    public Datalogger getDatalogger(Object equip) {
//        Datalogger datalogger = null;
//        //mit Factory!!
//        if (equip instanceof JEVisObject) {
//            datalogger = new JEVisDataLogger((JEVisObject) equip);
//        }
////        if (equip instanceof File) {
////            datalogger = new UnconnectedDataLogger((File) equip);
////        }
//
//        return datalogger;
//    }
//    private List<Data> getAllJobs() {
//        JEObjectType parserType = _client.getObjectType(PARSER);
//        JEObjectType connectionType = _client.getObjectType(CONNECTION);
//        JEObjectType datapointType = _client.getObjectType(DATAPOINT);
//
////        List<JEObject> equipments = _client.getObjectsFromType(equipmentType);
//        JEObjectType equipmentType = _client.getObjectType(EQUIPMENT);
//        List<JEObject> equipments = new ArrayList<JEObject>();
//        for (JEObject o : _client.getRootObjects()) {
//            equipments.addAll(o.getChildrenByType(equipmentType));
//        }
//        List<Data> jobCollector = new ArrayList<Data>();
//
//        for (JEObject e : equipments) {
//            List<JEObject> datapoints = e.getChildrenByType(datapointType);
//            JEObject parser = e.getChildrenByType(parserType).get(0);
//            JEObject connection = e.getChildrenByType(connectionType).get(0);
//            for (JEObject dp : datapoints) {
//                jobCollector.add(new Data(connection, parser, dp, e));
//            }
//        }
//        return jobCollector;
//
//    }
    
    private JEVisObject getEquipment() {
        //hier muss er sich eigentl alle möglichen holen!!
        JEVisObject logger = null;
        try {
            logger = _client.getObject(54l);
        } catch (JEVisException ex) {
            Logger.getLogger(TestMain.class.getName()).log(Level.ERROR, null, ex);
        }
        return logger;
    }

    public static Data getJEVisData(JEVisObject equipment) {
        Data data = null;
        try {
            JEVisDataSource client = equipment.getDataSource();
//            JEVisObject valueMappings = client.getObject(108l);
            JEVisObject parser = client.getObject(56l);
            JEVisObject connection = client.getObject(55l);
            List<JEVisObject> datapoints = new ArrayList<JEVisObject>();
            datapoints.add(client.getObject(60l)); //TODO stimmt so nicht
            data = new Data(parser, connection, equipment, datapoints);
        } catch (JEVisException ex) {
            Logger.getLogger(DataCollector.class.getName()).log(Level.ERROR, null, ex);
        }
        return data;
    }
//    private void createNewSample() {
//        JEObjectType onlineNode = _client.getObjectType("Online");
//        JEAttributeType attributeType = onlineNode.getAttributeType("Raw");
//        JEObject online = _client.getObject(98l);
//        JEAttribute attribute = online.getAttribute(attributeType);
//        DateTime dateTime = new DateTime();
//        DateTime minusDays = dateTime.minusDays(1);
//        attribute.addSample(new JEDefaultSample(attribute, minusDays, "18"));
//        _client.commitObject(online);
//    }
//
//    private void getDataSamples() {
//        JEObjectType onlineNode = _client.getObjectType("Online");
//        JEAttributeType attributeType = onlineNode.getAttributeType("Raw");
//        JEObject online = _client.getObject(98l);
//        JEAttribute attribute = online.getAttribute(attributeType);
//        List<JESample> allSamples = attribute.getAllSamples();
////        attribute.deleteAllSample();
////        _client.commitObject(online);
//
//    }
}
