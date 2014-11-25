/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.jevis.commons.cli.JEVisCommandLine;
import org.jevis.commons.parsing.GenericParser;
import org.jevis.commons.parsing.GeneralDateParser;
import org.jevis.commons.parsing.GeneralMappingParser;
import org.jevis.commons.parsing.GeneralValueParser;
import org.jevis.commons.parsing.SampleParserContainer;
import org.jevis.commons.parsing.csvParsing.CSVParsing;
import org.jevis.commons.parsing.csvParsing.DateCSVParser;
import org.jevis.commons.parsing.csvParsing.MappingFixCSVParser;
import org.jevis.commons.parsing.csvParsing.ValueCSVParser;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.CLIProperties.ConnectionCLIParser;
import org.jevis.jedatacollector.CLIProperties.JEVisServerConnectionCLI;
import org.jevis.jedatacollector.CLIProperties.ParsingCLIParser;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.commons.JEVisTypes;
import org.jevis.commons.parsing.DataCollectorParser;
import org.jevis.commons.parsing.ParsingFactory;
import org.jevis.jedatacollector.connection.ConnectionFactory;
import org.jevis.jedatacollector.data.DataPointDir;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author broder
 */
public class Launcher {

    private static JEVisDataSource _client;
    private Logger _logger;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Logger.getLogger(Launcher.class.getName()).log(Level.INFO, "-------Start JEDataCollector r35-------");
        Helper.initializeCommandLine(args);
        Helper.initializeLogger(JEVisCommandLine.getInstance().getDebugLevel());

        JEVisCommandLine cmd = JEVisCommandLine.getInstance();
        boolean cliJob = cmd.isUsed();

        Launcher launcher = new Launcher();
        List<Request> requestJobs;

        //starts a new launcher
        if (cliJob) {
            requestJobs = launcher.fetchCLIJob();
        } else {
            launcher.establishConnection();
//            launcher.generateUseCases();
            requestJobs = launcher.fetchJEVisDataJobs();
        }

        //hier müssen verschiedene Modi an und abgestellt werden können
//        boolean cliJob = false;

        launcher.excecuteRequsts(requestJobs);
        Logger.getLogger(Launcher.class.getName()).log(Level.INFO, "########## Finish JEDataCollector #########");

    }

    private static List<DataPoint> getDatapoints(JEVisObject dpDir) {
        List<DataPoint> datapoints = new ArrayList<DataPoint>();
        try {
            JEVisClass parser = _client.getJEVisClass(JEVisTypes.DataPoint.NAME);
            for (JEVisObject dps : dpDir.getChildren(parser, true)) {
                datapoints.add(new DataPoint(dps));
            }

        } catch (JEVisException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return datapoints;
    }

    private void excecuteRequsts(List<Request> requestJobs) {
        for (Request request : requestJobs) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "-----Execute Request------");
            if (request.getDataSource() != null) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ID; " + request.getDataSource().getName());
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Datapoints:");
                for (DataPoint p : request.getDataPoints()) {
                    Logger.getLogger(this.getClass().getName()).log(Level.ALL, p.getDatapointId());
                }
            }
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
                    Logger.getLogger(Launcher.class.getName()).log(Level.ERROR, null, t);
                }
            }
        }
    }

    private boolean establishConnection() {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Connect to JEConfig");
        JEVisCommandLine cmd = JEVisCommandLine.getInstance();
        String configFile = cmd.getConfigPath();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ConfigFile: " + configFile);
        JEVisServerConnectionCLI con = new JEVisServerConnectionCLI(configFile);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, con.getDb());
        try {
//            _client = new JEVisDataSourceSQL("192.168.2.55", "3306", "jevis", "jevis", "jevistest", "Sys Admin", "jevis");
//            _client.connect("Sys Admin", "jevis");
            _client = new JEVisDataSourceSQL(con.getDb(), con.getPort(), con.getSchema(), con.getUser(), con.getPw(), con.getJevisUser(), con.getJevisPW());
            _client.connect(con.getJevisUser(), con.getJevisPW());
        } catch (JEVisException ex) {
            Logger.getLogger(Launcher.class.getName()).log(Level.ERROR, null, ex);
            return false;
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Connect to JEConfig established");
        return true;
    }

    public static JEVisDataSource getClient() {
        return _client;
    }

    private List<Request> fetchJEVisDataJobs() {
        //getJEVIS Data
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "---------- fetch JEVis Data Jobs ----------");
        List<JEVisObject> dataSources;
        List<Request> requests = new ArrayList<Request>();
        try {
            JEVisClass jeVisClass = _client.getJEVisClass(JEVisTypes.DataServer.FTP.NAME);
//            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "----------class,"+jeVisClass.getName());
            dataSources = _client.getObjects(jeVisClass, true);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, dataSources.size() + " Equipments found");

//            JEVisClass connectionType = _client.getJEVisClass(JEVisTypes.Connection.HTTP.Name);
//            //workaround for inherit bug, normally only with jevic class parser and connection
//            JEVisClass ftpConnection = _client.getJEVisClass(JEVisTypes.Connection.FTP.Name);
//            JEVisClass sftpConnection = _client.getJEVisClass(JEVisTypes.Connection.sFTP.Name);
            JEVisClass datapointDirClass = _client.getJEVisClass(JEVisTypes.DataPointDirectory.NAME);
            JEVisClass datapointDirCompressClass = _client.getJEVisClass(JEVisTypes.DataPointDirectory.DataPointDirectoryCompressed.NAME);
            for (JEVisObject dataSource : dataSources) {
                try {

                    DataCollectorConnection connection = ConnectionFactory.getConnection(dataSource);
                    connection.initialize(dataSource);
                    Boolean enabled = connection.isEnabled();
                    if (!enabled) {
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Equipment is disabled : (" + dataSource.getID() + "," + dataSource.getName() + ")");
                        continue;
                    }
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "------------------------------------------------");
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Current Equipment (ID,Name): (" + dataSource.getID() + "," + dataSource.getName() + ")");
                    //get parser //hier kommt der Connection/Parsing class loader rein? connection direkt übergeben //unten muss es neu erstellt werden
                    List<JEVisObject> jevisDataPointDirInit = dataSource.getChildren(datapointDirCompressClass, false);
                    jevisDataPointDirInit.addAll(dataSource.getChildren(datapointDirClass, false));
                    List<DataPointDir> datapointsDir = initializeDatapointDir(jevisDataPointDirInit);

                    for (DataPointDir dir : datapointsDir) {
                        List<DataPoint> datapoints = getDatapoints(dir.getJevisObject());
                        for (DataPoint dp: datapoints){
                            dp.addDirectory(dir);
                        }

                        //need improvement
                        boolean needMultiConnections = false;
                        String previousPath = null;
                        for (DataPoint dp : datapoints) {
                            if (dp.getFilePath().equals(previousPath)) {
                                needMultiConnections = true;
                                break;
                            }
                            previousPath = dp.getFilePath();
                        }

                        DataCollectorParser parser = ParsingFactory.getParsing(dataSource);
                        parser.initialize(dataSource);

                        if (needMultiConnections) {
                            for (DataPoint dp : datapoints) {
                                DataCollectorParser newParser = ParsingFactory.getParsing(dataSource);
                                newParser.initialize(dataSource);
                                DataCollectorConnection newConnection = ConnectionFactory.getConnection(dataSource);
                                newConnection.initialize(dataSource);
                                List<DataPoint> tmpList = new ArrayList<DataPoint>();
                                tmpList.add(dp);
                                Request request = RequestGenerator.createJEVisRequest(parser, connection, datapoints);
                                requests.add(request);
                            }
                        } else {
                            Request request = RequestGenerator.createJEVisRequest(parser, connection, datapoints);
                            requests.add(request);
                        }

                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Create Multi File JEVisJob");
                    }


//                    Data data = new Data(parser, connection, datapointsJEVis);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Logger.getLogger(Launcher.class.getName()).log(Level.ERROR, "Problems with equip with id: " + dataSource.getID(), ex);
                }
            }
        } catch (JEVisException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.ERROR, ex.getMessage());
        }
        //create Requests
//        List<Request> requests = new ArrayList<Request>();
//
//        for (Data data : dataList) {
//            Request request = RequestGenerator.createJEVisRequest(data);
//            requests.add(request);
//        }

        Logger.getLogger(
                this.getClass().getName()).log(Level.INFO, "Number of Requests: " + requests.size());
        return requests;
    }

    private List<Request> fetchCLIJob() {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "fetch CLI Job");
        JEVisCommandLine cmd = JEVisCommandLine.getInstance();

        //Define the date format and from/until date
        String dateFormat = "ddMMyyyyHHmmss"; //TODO this should come from a parameter
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "DateFormat: " + dateFormat);
        DateTimeFormatter dtf = DateTimeFormat.forPattern(dateFormat);
        String fromString = null;
        if (cmd.getValue(Helper.FROM) != null) {
            fromString = cmd.getValue(Helper.FROM);
        } else {
            fromString = "01012000000000";
        }
        DateTime from = dtf.parseDateTime(fromString);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Timestamp from: " + fromString);
        DateTime until = null;
        if (cmd.getValue(Helper.UNTIL) != null) {
            String untilString = cmd.getValue(Helper.UNTIL);
            until = dtf.parseDateTime(untilString);
        } else {
            until = new DateTime();
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Timestamp until: " + until.toString());

        //Define the file for the connection (e.g. http)
        String connectionFile = cmd.getValue(Helper.CONNETION_FILE);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ConnectionFile: " + connectionFile);
        ConnectionCLIParser con = new ConnectionCLIParser(connectionFile);
        //Define the file for the parsing (e.g. csv)
        String parsingFile = cmd.getValue(Helper.PARSING_FILE);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ParsingFile: " + parsingFile);
        ParsingCLIParser par = new ParsingCLIParser(parsingFile);
        //the output online id from the jevis system



        DataCollectorConnection connection = ConnectionFactory.getConnection(con);
        GenericParser fileParser = new CSVParsing(par.getQuote(), par.getDelim(), par.getHeaderlines());

        Long outputOnlineID = null;
        String outputFile = null;
        if (cmd.getValue(Helper.OUTPUT_ONLINE) != null) {
            outputOnlineID = Long.parseLong(cmd.getValue(Helper.OUTPUT_ONLINE));
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Output Online ID: " + outputOnlineID);
        } else if (cmd.getValue(Helper.OUTPUT_FILE) != null) {
            outputFile = cmd.getValue(Helper.OUTPUT_FILE);
        }
        GeneralMappingParser datapointParser = new MappingFixCSVParser(false, outputOnlineID);
        GeneralDateParser dateParser = new DateCSVParser(par.getTimeformat(), par.getTimeIndex(), par.getDateformat(), par.getDateIndex());
        GeneralValueParser valueParser = new ValueCSVParser(par.getValueIndex(), par.getDecimalSep(), par.getThousandSep());

        SampleParserContainer sampleContainer = new SampleParserContainer(datapointParser, dateParser, valueParser);
        fileParser.addSampleContainer(sampleContainer);

        String datapointID = "16"; //TODO this should come from a file
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Datapoint id: " + datapointID);
        DataPoint datapoint = new DataPoint(datapointID, JEVisTypes.Equipment.NAME, outputOnlineID);

        DateTimeZone timeZone = DateTimeZone.getDefault(); //TODO this should come from a file

        List<Request> requests = new ArrayList<Request>();
        Request request = null;
        if (cmd.getValue(Helper.OUTPUT_ONLINE) != null) {
        } else if (cmd.getValue(Helper.OUTPUT_FILE) != null) {
            outputFile = cmd.getValue(Helper.OUTPUT_FILE);
            request = RequestGenerator.createCLIRequestWithFileOutput(connection, fileParser, datapoint, from, until, timeZone, outputFile);
        }

        requests.add(request);
        return requests;
    }

    public void setJevisClient(JEVisDataSource client) {
        _client = client;
    }

//    private List<Request> requestedJobs() {
//        List<Request> requests = new ArrayList<Request>();
//        JEVisCommandLine cmd = JEVisCommandLine.getInstance();
//        boolean cliJob = cmd.isUsed();
//        if (cliJob) {
//            Logger.getLogger(Launcher.class.getName()).log(Level.INFO, "Start CLI Job");
//            requests = fetchCLIJob();
//        } else {
//            Logger.getLogger(Launcher.class.getName()).log(Level.INFO, "Start Jevis Job");
////            List<Data> dataList = getAllJEvisData();
//            requests = fetchJEVisDataJobs();
//        }
//        return requests;
//    }
//
//    private void generateUseCases() {
//        try {
////            JEVisClass jeVisClass = _client.getJEVisClass(JEVisTypes.DataServer.FTP.NAME);
////            JEVisObject orga = _client.getObject(476l);
////            JEVisClass monitoredObject = _client.getJEVisClass("Monitored Object Directory");
////            orga.buildObject("Monitored Objects", monitoredObject);
////            orga.commit();
//            JEVisObject jevisObject = _client.getObject(478l);
//            JEVisClass jevisClass = _client.getJEVisClass("Data Directory");
//            jevisObject.buildObject("Data Directory", jevisClass);
//        } catch (JEVisException ex) {
//            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//    }
    public List<DataPointDir> initializeDatapointDir(List<JEVisObject> children) {
        List<DataPointDir> datapointDirLeaf = new ArrayList<DataPointDir>();
        List<DataPointDir> datapointDirParents = new ArrayList<DataPointDir>();
        try {
            datapointDirLeaf = getChildrenDirectories(children, datapointDirParents, datapointDirLeaf);
        } catch (JEVisException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        return datapointDirLeaf;
    }

    private List<DataPointDir> getChildrenDirectories(List<JEVisObject> children, List<DataPointDir> datapointDirParents, List<DataPointDir> datapointDirLeaf) throws JEVisException {
        JEVisClass datapointDirClass = _client.getJEVisClass(JEVisTypes.DataPointDirectory.NAME);
        JEVisClass datapointDirClassCompress = _client.getJEVisClass(JEVisTypes.DataPointDirectory.DataPointDirectoryCompressed.NAME);
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
}
