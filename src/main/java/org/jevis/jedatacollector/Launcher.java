/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PatternLayout;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisType;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.jevis.commons.DatabaseHelper;
import org.jevis.commons.cli.JEVisCommandLine;
import org.jevis.commons.driver.ConverterFactory;
import org.jevis.commons.driver.DataSourceFactory;
import org.jevis.commons.driver.ImporterFactory;
import org.jevis.commons.driver.DataCollectorTypes;
import org.jevis.commons.driver.ParserFactory;
import org.jevis.commons.driver.DataSource;
import org.jevis.jedatacollector.CLIProperties.JEVisServerConnectionCLI;

/**
 *
 * @author broder
 */
public class Launcher {

    public static String KEY = "process-id";
    private static JEVisDataSource _client;
    private Logger logger = Logger.getRootLogger();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Appender appender = Logger.getRootLogger().getAppender("FILE");
        appender.addFilter(new ThreadFilter("-1"));
        MDC.put(Launcher.KEY, "-1");
        Logger.getLogger(Launcher.class.getName()).log(Level.INFO, "-------Start JEDataCollector r37-------");
        Helper.initializeCommandLine(args);
        Helper.initializeLogger(JEVisCommandLine.getInstance().getDebugLevel());

        JEVisCommandLine cmd = JEVisCommandLine.getInstance();

        Launcher launcher = new Launcher();

        launcher.run();

    }

    private void run() {
        //connect to the JEVis System
        establishConnection();

        //load all the possible drivers from the JEVis System. They are specified under 
        //the JEvis Service Node with the names DataSourceDriver,ParserDriver,ConverterDriver,ImportDriver and the Attributes File, Mainmethod and Objectname.
        ParserFactory.initializeParser(_client);
        DataSourceFactory.initializeDataSource(_client);
        ConverterFactory.initializeConverter(_client);
        ImporterFactory.initializeImporter(_client);

        //get all data sources from the system, which are enabled
        List<JEVisObject> dataSources = getEnabledDataSources(_client);

        //execute all data sources
        excecuteDataSources(dataSources);
    }

    /**
     * Run all datasources in Threads. The maximum number of threads is defined
     * in the JEVis System.
     *
     * PROBLEM: with the new structure. Probably not all data sources are able
     * to handle multiple queries. Any solutions? Maybe without threads?
     *
     * @param dataSources
     */
    private void excecuteDataSources(List<JEVisObject> dataSources) {
        Logger.getLogger(
                this.getClass().getName()).log(Level.INFO, "Number of Requests: " + dataSources.size());

        long maxNumberThreads = getNumberOfMaxThreads();
        int threadID = 1;
        ThreadHandler threadReqHandler = new ThreadHandler(dataSources);
        MDC.remove(Launcher.KEY);
        while (threadReqHandler.hasRequest()) {
            int activeCount = threadReqHandler.getNumberActiveRequests();
            if (activeCount < maxNumberThreads) {
                JEVisObject currentDataSourceJevis = threadReqHandler.getNextDataSource();
                initNewAppender("" + threadID, currentDataSourceJevis.getName().replace(" ", "_") + "_ID(" + currentDataSourceJevis.getID() + ").log");
                MDC.put(Launcher.KEY, "" + threadID);

                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "----------------Execute DataSource-----------------");
//                DataCollector dataCollector = new DataCollector(currentDataSourceJevis);
                DataSource dataSource = DataSourceFactory.getDataSource(currentDataSourceJevis);
                dataSource.initialize(currentDataSourceJevis);
                Thread dataCollectionThread = new Thread(dataSource, currentDataSourceJevis.getName());
                try {
                    //start the data source in a new thread
                    dataCollectionThread.start();
                } catch (Exception ex) {
                    Logger.getLogger(Launcher.class.getName()).log(Level.ERROR, ex.getMessage());
                }
                threadID++;
                MDC.remove(Launcher.KEY);
            } else {
                try {
                    Thread.sleep(10000);
                    System.out.println("thread sleeps");
                } catch (InterruptedException ie) {
                    System.out.println(ie);
                }
            }
        }
    }

    private Long getNumberOfMaxThreads() {
        try {
            JEVisClass collectorClass = getClient().getJEVisClass(DataCollectorTypes.JEDataCollector.NAME);
            JEVisType numberThreadsType = collectorClass.getType(DataCollectorTypes.JEDataCollector.MAX_NUMBER_THREADS);
            List<JEVisObject> dataCollector = getClient().getObjects(collectorClass, false);
            if (dataCollector.size() == 1) {
                return dataCollector.get(0).getAttribute(numberThreadsType).getLatestSample().getValueAsLong();
            }
        } catch (JEVisException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return 2l;
    }

    private boolean establishConnection() {
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "Connection start");
        JEVisCommandLine cmd = JEVisCommandLine.getInstance();
        String configFile = cmd.getConfigPath();
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "ConfigFile: " + configFile);
        JEVisServerConnectionCLI con = new JEVisServerConnectionCLI(configFile);
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, con.getDb());
        try {
            _client = new JEVisDataSourceSQL(con.getDb(), con.getPort(), con.getSchema(), con.getUser(), con.getPw());
            _client.connect(con.getJevisUser(), con.getJevisPW());
        } catch (JEVisException ex) {
            Logger.getLogger(Launcher.class.getName()).log(Level.ERROR, ex.getMessage());
            return false;
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Connection established");
        return true;
    }

    public static JEVisDataSource getClient() {
        return _client;
    }

    private void initNewAppender(String NameForAppender, String Name4LogFile) {
        FileAppender appender = new FileAppender();
        appender.setLayout(new PatternLayout("[%d{dd MMM yyyy HH:mm:ss}][%c{2}]: %-10m%n"));
        appender.setFile(Name4LogFile);
        appender.setAppend(true);
        appender.setImmediateFlush(true);
        appender.activateOptions();
        appender.setName(NameForAppender);
        appender.addFilter(new ThreadFilter(NameForAppender));
        logger.setAdditivity(false);    //<--do not use default root logger
        logger.addAppender(appender);
    }

    private List<JEVisObject> getEnabledDataSources(JEVisDataSource client) {
        List<JEVisObject> enabledDataSources = new ArrayList<JEVisObject>();
        try {
            JEVisClass dataSourceClass = client.getJEVisClass(DataCollectorTypes.DataSource.NAME);
            JEVisType enabledType = dataSourceClass.getType(DataCollectorTypes.DataSource.ENABLE);
            List<JEVisObject> allDataSources = client.getObjects(dataSourceClass, true);
            for (JEVisObject dataSource : allDataSources) {
                Boolean enabled = DatabaseHelper.getObjectAsBoolean(dataSource, enabledType);
                if (enabled) {
                    enabledDataSources.add(dataSource);
                }
            }
        } catch (JEVisException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return enabledDataSources;
    }

}
