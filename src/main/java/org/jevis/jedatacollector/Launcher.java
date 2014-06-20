/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.Option;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.jevis.commons.cli.JEVisCommandLine;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.CLIProperties.ConnectionCLIParser;
import org.jevis.jedatacollector.CLIProperties.ParsingCLIParser;
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.connection.HTTP.HTTPConnection;
import org.jevis.jedatacollector.data.Data;
import org.jevis.jedatacollector.data.Equipment;
import org.jevis.jedatacollector.data.NewDataPoint;
import org.jevis.jedatacollector.parsingNew.DataCollectorParser;
import org.jevis.jedatacollector.parsingNew.GeneralDateParser;
import org.jevis.jedatacollector.parsingNew.GeneralMappingParser;
import org.jevis.jedatacollector.parsingNew.GeneralValueParser;
import org.jevis.jedatacollector.parsingNew.SampleParserContainer;
import org.jevis.jedatacollector.parsingNew.csvParsing.CSVParsing;
import org.jevis.jedatacollector.parsingNew.csvParsing.DateCSVParser;
import org.jevis.jedatacollector.parsingNew.csvParsing.MappingFixCSVParser;
import org.jevis.jedatacollector.parsingNew.csvParsing.ValueCSVParser;
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
    //Command line Parameter
    public static String SINGLE = "single";
    public static String DRY = "dry";
    public static String OUTPUT = "output";
    public static String QUERY_SERVER = "query-server";
    public static String QUERY_USER = "query-user";
    public static String QUERY_PASS = "query-pass";
    public static String DATA_SOURCE = "data-source";
    public static String DATA_POINT = "data-point";
    public static String EQUIPMENT = "equipment";
    public static String FROM = "from";
    public static String UNTIL = "until";
    public static String PROTOCOL = "protocol";
    public static String CSV = "csv";
    public static String CONNETION_FILE = "connection";
    public static String PARSING_FILE = "parsing";
    public static String OUTPUT_FILE = "path";
    public static String OUTPUT_ONLINE = "dp";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Logger.getLogger(Launcher.class.getName()).log(Level.INFO, "-------Start JEDataCollector r22-------");
        initializeCommandLine(args);
        initializeLogger(JEVisCommandLine.getInstance().getDebugLevel());

        //starts a new Logger

        Launcher launcher = new Launcher();
        launcher.connectAlphaServer();


        //hier müssen verschiedene Modi an und abgestellt werden können
        List<Request> requestJobs = new ArrayList<Request>();
        boolean cliJob = false;
        if (cliJob) {
            Logger.getLogger(Launcher.class.getName()).log(Level.INFO, "Start CLI Job");
            requestJobs = launcher.fetchCLIJob();
        } else {
            Logger.getLogger(Launcher.class.getName()).log(Level.INFO, "Start Jevis Job");
//            List<Data> dataList = getAllJEvisData();
            requestJobs = launcher.fetchJEVisDataJobs();
        }
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
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Execute Request");
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

    private static void initializeCommandLine(String[] args) {
        JEVisCommandLine cmd = JEVisCommandLine.getInstance();
        //Execution Control
        cmd.addOption(new Option(SINGLE, false, "Invokes Single Mode, usefull for debug mode"));
        cmd.addOption(new Option(DRY, false, "starts a dry run -> fetching data without DB import"));
        cmd.addOption(new Option(OUTPUT, true, "the outputfile is saved under this path"));
        //Fetch Job Parameters
        cmd.addOption(new Option("qs", QUERY_SERVER, true, "Defines the server url for the device request"));
        cmd.addOption(new Option("qu", QUERY_USER, true, "Defines a user for authentication"));
        cmd.addOption(new Option("qp", QUERY_PASS, true, "Defines a password for authentication"));
        cmd.addOption(new Option("ds", DATA_SOURCE, true, "Forces a specific data source"));
        cmd.addOption(new Option("dp", DATA_POINT, true, "Forces a specific data point"));
        cmd.addOption(new Option("e", EQUIPMENT, true, "Forces a specific equipment"));
        cmd.addOption(new Option(FROM, true, "Forces the \"from\" timestamp in UTC format"));
        cmd.addOption(new Option(UNTIL, true, "Forces the \"until\" timestamp in UTC format"));
        cmd.addOption(new Option(PROTOCOL, true, "Forces the protocol type"));
        cmd.addOption(new Option(CSV, true, "Forces the CSV format"));

        cmd.addOption(new Option(OUTPUT_FILE, true, "Saves the output under the given path"));
        cmd.addOption(new Option(OUTPUT_ONLINE, true, "Saves the output under the given online node in the jevis system"));
        cmd.addOption(new Option(CONNETION_FILE, true, "Path of the connection file"));
        cmd.addOption(new Option(PARSING_FILE, true, "Path of the parsing file"));

        //Create Options

        cmd.parse(args);
    }

    private static void initializeLogger(Level debugLevel) {
//        PropertyConfigurator.configure("log4j.properties");
        Logger.getRootLogger().setLevel(debugLevel);
    }

    private boolean connectAlphaServer() {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Connect to JEConfig");
        try {
            _client = new JEVisDataSourceSQL("192.168.2.55", "3306", "jevis", "jevis", "jevistest", "Sys Admin", "jevis");
            _client.connect("Sys Admin", "jevis");
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
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "fetch JEVis Data Jobs");
        List<JEVisObject> equipments;
        List<Data> dataList = new ArrayList<Data>();
        try {
            JEVisClass jeVisClass = _client.getJEVisClass("VIDA350");
            equipments = _client.getObjects(jeVisClass, true);

            JEVisClass parser = _client.getJEVisClass("CSV");
            JEVisClass connection = _client.getJEVisClass("HTTPCon");
            JEVisClass datapoints = _client.getJEVisClass("Data Point Directory");
            for (JEVisObject equip : equipments) {
                try {
                    List<JEVisObject> parserObject = equip.getChildren(parser, true);
                    List<JEVisObject> connectionObject = equip.getChildren(connection, true);
                    if (parserObject.size() != 1) {
                        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "Number of Parsing Objects != 1");
                        continue;
                    }
                    if (connectionObject.size() != 1) {
                        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "Number of Connection Objects != 1");
                        continue;
                    }

                    List<JEVisObject> datapointsDir = equip.getChildren(datapoints, true);
                    if (datapointsDir.size() != 1) {
                        continue;
                    }
                    List<JEVisObject> datapointsJEVis = getDatapoints(datapointsDir.get(0));
                    Equipment equipment = new Equipment(equip);
                    if (equipment.isSingleConnection()) {
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
                this.getClass().getName()).log(Level.ALL, "Number of Requests: " + requests.size());
        return requests;
    }

    private List<Request> fetchCLIJob() {
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "fetch CLI Job");
        JEVisCommandLine cmd = JEVisCommandLine.getInstance();
        //Define the file for the connection (e.g. http)
        String connectionFile = cmd.getValue(CONNETION_FILE);
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "ConnectionFile: " + connectionFile);
        ConnectionCLIParser con = new ConnectionCLIParser(connectionFile);
        //Define the file for the parsing (e.g. csv)
        String parsingFile = cmd.getValue(PARSING_FILE);
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "ParsingFile: " + parsingFile);
        ParsingCLIParser par = new ParsingCLIParser(parsingFile);
        //the output online id from the jevis system
        long outputOnlineID = Long.parseLong(cmd.getValue(OUTPUT_ONLINE));
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "Output Online ID: " + outputOnlineID);
        //Define the date format and from/until date
        String dateFormat = "ddMMyyyyHHmmss"; //TODO this should come from a parameter
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "DateFormat: " + dateFormat);
        DateTimeFormatter dtf = DateTimeFormat.forPattern(dateFormat);
        String fromString = cmd.getValue(FROM);
        DateTime from = dtf.parseDateTime(fromString);
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "Timestamp from: " + fromString);
        String untilString = cmd.getValue(UNTIL);
        DateTime until = dtf.parseDateTime(untilString);
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "Timestamp until: " + untilString);

        DatacollectorConnection connection = new HTTPConnection(con.getIP(), con.getPath(), con.getPort(), con.getConnectionTimeout(), con.getReadTimeout(), con.getDateFormat());
        DataCollectorParser fileParser = new CSVParsing(par.getQuote(), par.getDelim(), par.getHeaderlines());

        GeneralMappingParser datapointParser = new MappingFixCSVParser(false, outputOnlineID);
        GeneralDateParser dateParser = new DateCSVParser(par.getTimeformat(), par.getTimeIndex(), par.getDateformat(), par.getDateIndex());
        GeneralValueParser valueParser = new ValueCSVParser(par.getValueIndex(), par.getDecimalSep(), par.getThousandSep());

        SampleParserContainer sampleContainer = new SampleParserContainer(datapointParser, dateParser, valueParser);
        fileParser.addSampleContainer(sampleContainer);

        String datapointID = "16"; //TODO this should come from a file
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "Datapoint id: " + datapointID);
        NewDataPoint datapoint = new NewDataPoint(datapointID, "VIDA350", outputOnlineID);

        DateTimeZone timeZone = DateTimeZone.getDefault(); //TODO this should come from a file

        List<Request> requests = new ArrayList<Request>();
        Request request = RequestGenerator.createCLIRequest(connection, fileParser, datapoint, from, until, timeZone);
        requests.add(request);
        return requests;
    }
}
