/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author bf
 */
public class DataLoggerCommandLine {

    private static DataLoggerCommandLine _instance = null;
    private Options _options;
    private CommandLineParser _parser;
    private CommandLine _cmd;

    private DataLoggerCommandLine() {
        _options = new Options();
        _parser = new GnuParser();
        setOptions();
    }

    public static DataLoggerCommandLine getInstance() {
        if (_instance == null) {
            _instance = new DataLoggerCommandLine();
        }
        return _instance;
    }

    private void setOptions() {
        _options.addOption("server", true, "the server for the connection");
        _options.addOption("user", true, "the user for the connection");
        _options.addOption("password", true, "the password for the connection");
        _options.addOption("datapoint", true, "laod a specific datapoin");
        _options.addOption("help", false, "show the help list");
        _options.addOption("debug",true, "sets the debug level (INFO, WARNING, ALL, OFF)");
    }

    public void parse(String[] args) {
        try {
            _cmd = _parser.parse(_options, args);
        } catch (ParseException ex) {
            Logger.getLogger(DataLoggerCommandLine.class.getName()).log(Level.SEVERE, null, ex);
            showHelp();
            System.exit(0);
        }
    }

    public String getServer() {
        return _cmd.getOptionValue("server");
    }

    public void showHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("DataLogger", _options);
    }
    
    public boolean needHelp(){
        return _cmd.hasOption("help");
    }
    
    public Level getDebugLevel(){
        return Level.parse(_cmd.getOptionValue("debug").toUpperCase());
    }
}
