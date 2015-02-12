/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import org.jevis.jedatacollector.service.Request;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.jevis.commons.cli.JEVisCommandLine;
import org.jevis.jedatacollector.CLIProperties.JEVisServerConnectionCLI;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.jedatacollector.service.JevisJobHandler;

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

        //ToDo starts a new launcher --> with Factory?
        if (cliJob) {
            JevisJobHandler handler = new JevisJobHandler();
            requestJobs = handler.createJobs();
        } else {
            launcher.establishConnection();
            JevisJobHandler handler = new JevisJobHandler();
            requestJobs = handler.createJobs();
        }

        //hier müssen verschiedene Modi an und abgestellt werden können
//        boolean cliJob = false;

        launcher.excecuteRequsts(requestJobs);
        Logger.getLogger(Launcher.class.getName()).log(Level.INFO, "########## Finish JEDataCollector #########");
        System.out.println("Fertig");
    }

    private void excecuteRequsts(List<Request> requestJobs) {
        Logger.getLogger(
                this.getClass().getName()).log(Level.INFO, "Number of Requests: " + requestJobs.size());
        //ToDo each request should be executed in a thread
        for (Request request : requestJobs) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "----------------Execute Request-----------------");
            if (request.getDataSource() != null) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Data Source (ID,Name): (" + request.getDataSource().getID() + "," + request.getDataSource().getName() + ")");
//                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Datapoints:");
                for (DataPoint p : request.getDataPoints()) {
                    Logger.getLogger(this.getClass().getName()).log(Level.ALL, "Datapoint ID: " + p.getDatapointId());
                }
            }
            DataCollector datalogger = new DataCollector(request);
            try {
                datalogger.run();

            } catch (Exception ex) {
                Logger.getLogger(Launcher.class.getName()).log(Level.ERROR, ex.getMessage());
            }
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "------------------------------------------------");
        }
    }

    private boolean establishConnection() {
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "Connection start");
        JEVisCommandLine cmd = JEVisCommandLine.getInstance();
        String configFile = cmd.getConfigPath();
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "ConfigFile: " + configFile);
        JEVisServerConnectionCLI con = new JEVisServerConnectionCLI(configFile);
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, con.getDb());
        try {
//            _client = new JEVisDataSourceSQL("192.168.2.55", "3306", "jevis", "jevis", "jevistest", "Sys Admin", "jevis");
//            _client.connect("Sys Admin", "jevis");
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
}
