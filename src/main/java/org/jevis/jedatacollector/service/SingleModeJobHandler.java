/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jevis.commons.cli.JEVisCommandLine;
import org.jevis.commons.parsing.DataCollectorParser;
import org.jevis.commons.parsing.csvParsing.CSVParsing;
import org.jevis.jedatacollector.CLIProperties.ConnectionCLIParser;
import org.jevis.jedatacollector.CLIProperties.ParsingCLIParser;
import org.jevis.jedatacollector.Helper;
import org.jevis.jedatacollector.connection.ConnectionFactory;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author bf
 */
public class SingleModeJobHandler {

    public List<Request> createJobs() {
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
        DataCollectorParser fileParser = new CSVParsing(par.getQuote(), par.getDelim(), par.getHeaderlines());

        Long outputOnlineID = null;
        String outputFile = null;
        if (cmd.getValue(Helper.OUTPUT_ONLINE) != null) {
            outputOnlineID = Long.parseLong(cmd.getValue(Helper.OUTPUT_ONLINE));
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Output Online ID: " + outputOnlineID);
        } else if (cmd.getValue(Helper.OUTPUT_FILE) != null) {
            outputFile = cmd.getValue(Helper.OUTPUT_FILE);
        }
//        GeneralMappingParser datapointParser = new CSVDatapointParser(false, outputOnlineID);
//        GeneralDateParser dateParser = new DateCSVParser(par.getTimeformat(), par.getTimeIndex(), par.getDateformat(), par.getDateIndex());
//        GeneralValueParser valueParser = new ValueCSVParser(par.getValueIndex(), par.getDecimalSep(), par.getThousandSep());

//        SampleParserContainer sampleContainer = new SampleParserContainer(datapointParser, dateParser, valueParser);
//        fileParser.addSampleContainer(sampleContainer);

//        String datapointID = "16"; //TODO this should come from a file
//        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Datapoint id: " + datapointID);
//        DataPoint datapoint = new DataPoint(datapointID, JEVisTypes.Equipment.NAME, outputOnlineID);
//
//        DateTimeZone timeZone = DateTimeZone.getDefault(); //TODO this should come from a file
//
        List<Request> requests = new ArrayList<Request>();
//        Request request = null;
//        if (cmd.getValue(Helper.OUTPUT_ONLINE) != null) {
//        } else if (cmd.getValue(Helper.OUTPUT_FILE) != null) {
//            outputFile = cmd.getValue(Helper.OUTPUT_FILE);
//            request = RequestGenerator.createCLIRequestWithFileOutput(connection, fileParser, datapoint, from, until, timeZone, outputFile);
//        }
//
//        requests.add(request);
        return requests;
    }
}
