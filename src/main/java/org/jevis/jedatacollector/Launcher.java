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
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.connection.HTTP.HTTPConnection;
import org.jevis.jedatacollector.data.Data;
import org.jevis.jedatacollector.data.Equipment;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.commons.JEVisTypes;
import org.jevis.jedatacollector.connection.ConnectionFactory;
import org.jevis.jedatacollector.connection.ConnectionHelper;
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
            requestJobs = launcher.fetchJEVisDataJobs();
        }

        //hier müssen verschiedene Modi an und abgestellt werden können
//        boolean cliJob = false;

        launcher.excecuteRequsts(requestJobs);
        Logger.getLogger(Launcher.class.getName()).log(Level.INFO, "########## Finish JEDataCollector #########");

    }

    private static List<JEVisObject> getDatapoints(JEVisObject dpDir) {
        List<JEVisObject> datapoints = new ArrayList<JEVisObject>();
        try {
            JEVisClass parser = _client.getJEVisClass("Data Point");
            datapoints.addAll(dpDir.getChildren(parser, true));

        } catch (JEVisException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return datapoints;
    }

    private void excecuteRequsts(List<Request> requestJobs) {
        for (Request request : requestJobs) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "-----Execute Request------");
            if (request.getEquipment() != null) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ID; " + request.getEquipment().getID());
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
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "fetch JEVis Data Jobs");
        List<JEVisObject> equipments;
        List<Data> dataList = new ArrayList<Data>();
        try {
            JEVisClass jeVisClass = _client.getJEVisClass(JEVisTypes.Equipment.NAME);
//            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "----------class,"+jeVisClass.getName());
            equipments = _client.getObjects(jeVisClass, true);
            JEVisClass parser = _client.getJEVisClass(JEVisTypes.Parser.CSVParser.NAME);
            JEVisClass connectionType = _client.getJEVisClass(JEVisTypes.Connection.HTTP.Name);
            //workaround for inherit bug, normally only with jevic class parser and connection
            JEVisClass ftpConnection = _client.getJEVisClass(JEVisTypes.Connection.FTP.Name);
            JEVisClass sftpConnection = _client.getJEVisClass(JEVisTypes.Connection.sFTP.Name);
            JEVisClass datapoints = _client.getJEVisClass(JEVisTypes.DataPointDirectory.NAME);
            for (JEVisObject equip : equipments) {
                try {
                    List<JEVisObject> parserObject = equip.getChildren(parser, true);
                    List<JEVisObject> connectionObject = equip.getChildren(connectionType, true);

                    if (parserObject.size() != 1) {
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Number of Parsing Objects != 1 under: " + equip.getID());
                        for (JEVisObject tmp : parserObject) {
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ID" + tmp.getID());
                        }
                        continue;
                    }
                    if (connectionObject.size() != 1) {
                        for (JEVisObject tmp : connectionObject) {
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ID" + tmp.getID());
                        }
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Number of Connection Objects != 1 under: " + equip.getID());
                        //same workaround as above
                        connectionObject = equip.getChildren(ftpConnection, true);
                        if (connectionObject.size() != 1) {
                            connectionObject = equip.getChildren(sftpConnection, true);
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ftp Connection");
                            if (connectionObject.size() != 1) {
                                Logger.getLogger(this.getClass().getName()).log(Level.WARN, "no connection");
                                continue;
                            }
                        }
                    }

                    List<JEVisObject> datapointsDir = equip.getChildren(datapoints, true);
                    if (datapointsDir.size() != 1) {
                        continue;
                    }
                    //hier kommt der Connection/Parsing class loader rein? connection direkt übergeben
                    List<JEVisObject> datapointsJEVis = getDatapoints(datapointsDir.get(0));
                    DatacollectorConnection connection = ConnectionFactory.getConnection(connectionObject.get(0));
                    connection.initialize(equip);
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "path," + connection.getWholeFilePath());
                    boolean needMultiConnection = ConnectionHelper.containsToken(connection.getWholeFilePath());
                    Equipment equipment = new Equipment(equip);
                    if (needMultiConnection) {
                        for (JEVisObject dps : datapointsJEVis) {
                            List<JEVisObject> tmpList = new ArrayList<JEVisObject>();
                            tmpList.add(dps);
                            Data data = new Data(parserObject.get(0), connectionObject.get(0), equip, tmpList);
                            dataList.add(data);
                        }
                    } else {
                        Data data = new Data(parserObject.get(0), connectionObject.get(0), equip, datapointsJEVis);
                        dataList.add(data);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Launcher.class.getName()).log(Level.ERROR, "Problems with equip with id: " + equip.getID(), ex);
                }
            }
        } catch (JEVisException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.ERROR, ex.getMessage());
        }
        //create Requests
        List<Request> requests = new ArrayList<Request>();
        for (Data data : dataList) {
            Request request = RequestGenerator.createJEVisRequest(data);
            requests.add(request);
        }

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



        DatacollectorConnection connection = ConnectionFactory.getConnection(con);
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

    private List<Request> requestedJobs() {
        List<Request> requests = new ArrayList<Request>();
        JEVisCommandLine cmd = JEVisCommandLine.getInstance();
        boolean cliJob = cmd.isUsed();
        if (cliJob) {
            Logger.getLogger(Launcher.class.getName()).log(Level.INFO, "Start CLI Job");
            requests = fetchCLIJob();
        } else {
            Logger.getLogger(Launcher.class.getName()).log(Level.INFO, "Start Jevis Job");
//            List<Data> dataList = getAllJEvisData();
            requests = fetchJEVisDataJobs();
        }
        return requests;
    }
}
