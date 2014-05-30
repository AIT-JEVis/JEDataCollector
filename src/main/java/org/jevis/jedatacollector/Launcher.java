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

    private static void configureLogger(Level debugLevel) {
        PropertyConfigurator.configure("log4j.properties");
        Logger.getRootLogger().setLevel(debugLevel);
    }
    private JEVisDataSource _client;
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

    private static void createCommandLine(String[] args) {
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("###starte DataLogger###");

        createCommandLine(args);

        configureLogger(JEVisCommandLine.getInstance().getDebugLevel());


        Launcher launcher = new Launcher(true);
        //        adf.getDataSamples();
        //        adf.createNewSample();
        JEVisObject equip = launcher.getEquipment();

        //hier müssen verschiedene Modi an und abgestellt werden können
        boolean single = true;
        if (single) {
            launcher.fetchCLIJob();
        } else {
            launcher.fetchEquipmentJob(equip);
        }

    }

    public Launcher(boolean connect) {
//        JevLoginHandler.createDirectLogin(user, pass, host);

        if (connect) {
            System.out.println("Verbinden zum Config");
            try {
                _client = new JEVisDataSourceSQL("192.168.2.55", "3306", "jevis", "jevis", "jevistest", "Sys Admin", "jevis");
                _client.connect("Sys Admin", "jevis");
            } catch (JEVisException ex) {
                Logger.getLogger(Launcher.class.getName()).log(Level.ERROR, null, ex);
            }
            System.out.println("Verbinden zum Config erfolgreich");
        }
    }

    private void fetchCLIJob() {
        JEVisCommandLine cmd = JEVisCommandLine.getInstance();
        String connectionFile = cmd.getValue(CONNETION_FILE);
        ConnectionCLIParser con = new ConnectionCLIParser(connectionFile);
        String parsingFile = cmd.getValue(PARSING_FILE);
        ParsingCLIParser par = new ParsingCLIParser(parsingFile);
        long outputOnlineID = Long.parseLong(cmd.getValue(OUTPUT_ONLINE));

        DateTimeFormatter dtf = DateTimeFormat.forPattern("ddMMyyyyHHmmss");
        String fromString = cmd.getValue(FROM);
        DateTime from = dtf.parseDateTime(fromString);
        String untilString = cmd.getValue(UNTIL);
        DateTime until = dtf.parseDateTime(untilString);

        DatacollectorConnection connection = new HTTPConnection(con.getIP(), con.getPath(), con.getPort(), con.getConnectionTimeout(), con.getReadTimeout(), con.getDateFormat());
        DataCollectorParser fileParser = new CSVParsing(par.getQuote(), par.getDelim(), par.getHeaderlines());

        GeneralMappingParser datapointParser = new MappingFixCSVParser(false, outputOnlineID);
        GeneralDateParser dateParser = new DateCSVParser(null, null, par.getDateformat(), par.getDateIndex(), DateTimeZone.UTC);
        GeneralValueParser valueParser = new ValueCSVParser(par.getValueIndex(), par.getDecimalSep(), par.getThousandSep());

        SampleParserContainer sampleContainer = new SampleParserContainer(datapointParser, dateParser, valueParser);
        fileParser.addSampleContainer(sampleContainer);

        NewDataPoint datapoint = new NewDataPoint("16", null);
        Request request = RequestGenerator.createCLIRequest(connection, fileParser, datapoint, from, until);
        executeRequest(request);
    }
    //TODO vllt diesen job nur für jevis und nen anderen für andere....
    //TODO Threads pro Equipment oder pro Anfrage (bei VIDA geht es nicht)

    private void fetchEquipmentJob(JEVisObject equip) {
        Logger.getLogger(this.getClass()).log(Level.INFO, "INFO LAUNCHER");
        Logger.getLogger(this.getClass()).log(Level.WARN, "WARN LAUNCHER");
        Logger.getLogger(this.getClass()).log(Level.ALL, "ALL LAUNCHER");
        Test test = new Test();
        Data data = getJEVisData(equip);

        List<Request> requests = RequestGenerator.createJEVisRequests(data);

        for (Request request : requests) {
            executeRequest(request);
        }
    }

    private void executeRequest(Request request) {
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
            Logger.getLogger(Launcher.class.getName()).log(Level.ERROR, null, ex);
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
