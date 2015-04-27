/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.util.ArrayList;
import org.jevis.jedatacollector.service.Request;
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
import org.jevis.commons.JEVisTypes;
import org.jevis.commons.cli.JEVisCommandLine;
import org.jevis.jedatacollector.CLIProperties.JEVisServerConnectionCLI;
import org.jevis.jedatacollector.connection.ConnectionFactory;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.commons.parsing.Driver;
import org.jevis.jedatacollector.service.JevisJobHandler;
import org.jevis.commons.parsing.LoadingDriver;
import org.jevis.commons.parsing.ParsingFactory;

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
        boolean cliJob = cmd.isUsed();

        Launcher launcher = new Launcher();
        List<Request> requestJobs;

        //ToDo starts a new launcher --> with Factory?
        if (cliJob) {
            JevisJobHandler handler = new JevisJobHandler();
            requestJobs = handler.createJobs();
        } else {
            launcher.establishConnection();
            List<Driver> driverList = getDriverList();
            ConnectionFactory.setDrivers(driverList);
            ParsingFactory.setDrivers(driverList);
            JevisJobHandler handler = new JevisJobHandler();
            requestJobs = handler.createJobs();
        }

        //hier müssen verschiedene Modi an und abgestellt werden können
//        boolean cliJob = false;
        launcher.excecuteRequstsWithThreadsExtend(requestJobs);
        MDC.put(Launcher.KEY, "-1");
        Logger.getLogger(Launcher.class.getName()).log(Level.INFO, "########## Finish JEDataCollector #########");
        System.out.println("Fertig");
    }

    private void excecuteRequstsWithThreadsExtend(List<Request> requestJobs) {
        Logger.getLogger(
                this.getClass().getName()).log(Level.INFO, "Number of Requests: " + requestJobs.size());

        long maxNumberThreads = getNumberOfMaxThreads();
        int threadID = 1;
        ThreadRequestHandler threadReqHandler = new ThreadRequestHandler(requestJobs);
        MDC.remove(Launcher.KEY);
        while (threadReqHandler.hasRequest()) {
//            int activeCount = Thread.activeCount();
            int activeCount = threadReqHandler.getNumberActiveRequests();
            if (activeCount < maxNumberThreads && threadReqHandler.hasValidRequest()) {
                Request currentReq = threadReqHandler.getNextRequest();
                DataCollectorConnection dataSource = currentReq.getDataSource();
                initNewAppender("" + threadID, dataSource.getName() + "_ID(" + dataSource.getID() + ").log");
//                    initNewAppender("" + threadID, "/home/jedc/bin/Thread_" + threadID + ".log");
//                    Logger.getLogger("Thread_" + currentReq.getDataSource().getID()).log(Level.INFO, "----------------Execute Request-----------------");
                MDC.put(Launcher.KEY, "" + threadID);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "----------------Execute Request-----------------");
                if (currentReq.getDataSource() != null) {
//                        Logger.getLogger("Thread_" + currentReq.getDataSource().getID()).log(Level.INFO, "Data Source (ID,Name): (" + currentReq.getDataSource().getID() + "," + currentReq.getDataSource().getName() + ")");
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Data Source (ID,Name): (" + currentReq.getDataSource().getID() + "," + currentReq.getDataSource().getName() + ")");
//                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Datapoints:");
                    for (DataPoint p : currentReq.getDataPoints()) {
//                            Logger.getLogger("Thread_" + currentReq.getDataSource().getID()).log(Level.ALL, "Datapoint ID: " + p.getDatapointId());
                        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "Datapoint ID: " + p.getDatapointId());
                    }
                }
                DataCollector dataCollector = new DataCollector(currentReq);
                Thread dataCollectionThread = new Thread(dataCollector, currentReq.getDataSource().getName());
                try {
                    dataCollectionThread.start();

                } catch (Exception ex) {
                    Logger.getLogger(Launcher.class.getName()).log(Level.ERROR, ex.getMessage());
                }
//                finally {

                threadID++;
                MDC.remove(Launcher.KEY);
//                    requestJobs.remove(0);
//                }
            } else {
                try {
                    Thread.sleep(10000);
                    System.out.println("thread sleeps");
                    for (Request req : threadReqHandler.getActiveRequests()) {
                        System.out.println(req.getDataSource().getName());
                    }
                } catch (InterruptedException ie) {
                    System.out.println(ie);
                }
            }
        }
    }

    private Long getNumberOfMaxThreads() {
        try {
            JEVisClass collectorClass = getClient().getJEVisClass(JEVisTypes.JEDataCollector.NAME);
            JEVisType numberThreadsType = collectorClass.getType(JEVisTypes.JEDataCollector.MAX_NUMBER_THREADS);
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

    public void setJevisClient(JEVisDataSource client) {
        _client = client;
    }

    private void initNewAppender(String NameForAppender, String Name4LogFile) {
//        logger = Logger.getLogger(NameForAppender); //NOT DEFAULT BY "logger = Logger.getLogger(TestJob.class);"

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

    private static List<Driver> getDriverList() {
        List<Driver> drivers = new ArrayList<Driver>();
        try {
            JEVisClass collectorClass = getClient().getJEVisClass(JEVisTypes.JEDataCollector.NAME);
            JEVisClass driverDirClass = getClient().getJEVisClass(JEVisTypes.DriverDirectory.NAME);
            JEVisClass driverClass = getClient().getJEVisClass(JEVisTypes.Driver.NAME);
            List<JEVisObject> dataCollector = getClient().getObjects(collectorClass, false);
            if (dataCollector.size() == 1) {
                List<JEVisObject> children = dataCollector.get(0).getChildren();
                JEVisObject driverDirectory = null;
                for (JEVisObject child : children) {
                    if (child.getJEVisClass().equals(driverDirClass)) {
                        driverDirectory = child;
                        break;
                    }
                }
                if (driverDirectory == null) {
                    return drivers;
                }
                for (JEVisObject child : driverDirectory.getChildren()) {
                    if (child.getJEVisClass().equals(driverClass)) {
                        Driver driver = new Driver();
                        driver.init(child);
                        drivers.add(driver);
                    }
                }
            }
        } catch (JEVisException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return drivers;
    }
}
