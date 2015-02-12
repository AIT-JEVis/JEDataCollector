/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.commons.JEVisTypes;
import org.jevis.commons.cli.JEVisCommandLine;
import org.jevis.commons.parsing.DataCollectorParser;
import org.jevis.commons.parsing.ParsingFactory;
import org.jevis.jedatacollector.Launcher;
import org.jevis.jedatacollector.connection.ConnectionFactory;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.jedatacollector.data.DataPointDir;

/**
 *
 * @author bf
 */
public class JevisJobHandler {

    public List<Request> createJobs() {
        //getJEVIS Data
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "---------- fetch JEVis Data Jobs ----------");
        List<JEVisObject> dataSources;
        List<Request> requests = new ArrayList<Request>();
        try {
            JEVisClass jeVisClass = Launcher.getClient().getJEVisClass(JEVisTypes.DataServer.NAME);
            dataSources = Launcher.getClient().getObjects(jeVisClass, true);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, dataSources.size() + " Equipments found");

            JEVisClass datapointDirClass = Launcher.getClient().getJEVisClass(JEVisTypes.DataPointDirectory.NAME);
            JEVisClass datapointDirCompressClass = Launcher.getClient().getJEVisClass(JEVisTypes.DataPointDirectory.DataPointDirectoryCompressed.NAME);

            //for each found datasource a equipment object has to be generated
            for (JEVisObject dataSource : dataSources) {
                try {

                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "------------------------------------------------");
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Current Equipment (ID,Name): (" + dataSource.getID() + "," + dataSource.getName() + ")");
                    DataCollectorConnection connection = ConnectionFactory.getConnection(dataSource);
                    connection.initialize(dataSource);
                    Boolean enabled = connection.isEnabled();
                    //get parser //hier kommt der Connection/Parsing class loader rein? connection direkt übergeben //unten muss es neu erstellt werden
                    //perhaps the class could be loaded into the factory?
                    if (!enabled) { //build a connection and check if the connection is enabled
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Equipment is disabled");
                        continue;
                    }

                    List<JEVisObject> jevisDataPointDirInit = dataSource.getChildren(datapointDirCompressClass, false);
                    jevisDataPointDirInit.addAll(dataSource.getChildren(datapointDirClass, false));
                    List<DataPointDir> datapointsDir = initializeDatapointDir(jevisDataPointDirInit);

                    //for each datapoint directory.. normally there should only be one data point directory
                    for (DataPointDir dir : datapointsDir) {
                        //get the complete path under this datapointdir
                        List<DataPoint> datapoints = getDatapoints(dir.getJevisObject());
                        for (DataPoint dp : datapoints) {
                            dp.setDirectory(dir);
                        }

                        //check if the connection has to be splittet into several connections (if the path to the data source
                        //is different for the data points
                        boolean needMultiConnections = false;
                        String previousPath = null;
                        for (DataPoint dp : datapoints) {
                            if (previousPath == null) {
                                previousPath = dp.getFileName();
                            }
                            if (dp.getFileName() != null && !dp.getFileName().equals(previousPath)) {
                                needMultiConnections = true;
                                break;
                            }
                            previousPath = dp.getFileName();
                        }

                        Logger.getRootLogger().setLevel(Level.ERROR);

                        if (needMultiConnections) { //if it is a multiconnection, all dps have to be splitted into different requests
                            Logger.getLogger(this.getClass().getName()).log(Level.ALL, "Multi Connection JEVisJob");
                            for (DataPoint dp : datapoints) {
                                DataCollectorParser newParser = ParsingFactory.getParsing(dataSource);
                                newParser.initialize(dataSource);
                                DataCollectorConnection newConnection = ConnectionFactory.getConnection(dataSource);
                                newConnection.initialize(dataSource);
                                List<DataPoint> tmpList = new ArrayList<DataPoint>();
                                tmpList.add(dp);
                                Request request = RequestGenerator.createJEVisRequest(newParser, newConnection, tmpList);
                                requests.add(request);
                            }
                        } else {
                            Logger.getLogger(this.getClass().getName()).log(Level.ALL, "Single Connection JEVisJob");
                            DataCollectorParser parser = ParsingFactory.getParsing(dataSource);
                            parser.initialize(dataSource);
                            Request request = RequestGenerator.createJEVisRequest(parser, connection, datapoints);
                            requests.add(request);
                        }
                        Logger.getRootLogger().setLevel(JEVisCommandLine.getInstance().getDebugLevel());

                    }
                } catch (Exception ex) {
                    Logger.getLogger(Launcher.class.getName()).log(Level.ERROR, "Problems with equip with id: " + dataSource.getID(), ex);
                }
            }
        } catch (JEVisException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.ERROR, ex.getMessage());
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "------------------------------------------------");
        return requests;
    }

    public List<DataPointDir> initializeDatapointDir(List<JEVisObject> children) {
        List<DataPointDir> datapointDirLeaf = new ArrayList<DataPointDir>();
        List<DataPointDir> datapointDirParents = new ArrayList<DataPointDir>();
        try {
            datapointDirLeaf = getChildrenDirectories(children, datapointDirParents, datapointDirLeaf);
        } catch (JEVisException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.ERROR, ex.getMessage());
        }
        return datapointDirLeaf;
    }

    private List<DataPointDir> getChildrenDirectories(List<JEVisObject> children, List<DataPointDir> datapointDirParents, List<DataPointDir> datapointDirLeaf) throws JEVisException {
        JEVisClass datapointDirClass = Launcher.getClient().getJEVisClass(JEVisTypes.DataPointDirectory.NAME);
        JEVisClass datapointDirClassCompress = Launcher.getClient().getJEVisClass(JEVisTypes.DataPointDirectory.DataPointDirectoryCompressed.NAME);
        for (JEVisObject child : children) {
            DataPointDir datapointDir = new DataPointDir();
            datapointDir.initialize(child);
            datapointDir.setPreviousDirs(datapointDirParents);
            if (child.getChildren(datapointDirClass, false).isEmpty() && child.getChildren(datapointDirClassCompress, false).isEmpty()) {
                datapointDirLeaf.add(datapointDir);
            } else {
                datapointDirParents.add(datapointDir);
                List<JEVisObject> tmpChildren = child.getChildren(datapointDirClassCompress, false);
                tmpChildren.addAll(child.getChildren(datapointDirClass, false));
                getChildrenDirectories(tmpChildren, datapointDirParents, datapointDirLeaf);
                datapointDirParents.remove(datapointDir);
            }
        }
        return datapointDirLeaf;
    }

    private static List<DataPoint> getDatapoints(JEVisObject dpDir) {
        List<DataPoint> datapoints = new ArrayList<DataPoint>();
        try {
            JEVisClass parser = Launcher.getClient().getJEVisClass(JEVisTypes.DataPoint.NAME);
            for (JEVisObject dps : dpDir.getChildren(parser, true)) {
                datapoints.add(new DataPoint(dps));
            }

        } catch (JEVisException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return datapoints;
    }
}
