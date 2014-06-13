/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsing;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.parsingNew.DataCollectorParser;
import org.jevis.jedatacollector.parsingNew.csvParsing.CSVParsing;

/**
 *
 * @author broder
 */
public class ParsingFactory {

    private static final String CSV_PARSER = ("CSV");
    private static final String CSV_MULTI_TIME_PARSER = ("CSV Multi Time");
    private static final String CSV_MULTI_DATA_PARSER = ("CSV Multi Data");
    private static final String MULTI_DATA_PARSER = ("Multi Data");
    private static final String XML_SINGLE_PARSER = ("XML Single");
    private static final String XML_MULTI_PARSER = ("XML Multi");
    private static final String SQL_PARSER = ("SQL");

    public static DataCollectorParser getParsing(Object parsingData) throws FetchingException {
//        if (parsingData == null) {
//            throw new FetchingException(parsingData.getID(), FetchingExceptionType.PARSING_DRIVER_ERROR);
//        }

        DataCollectorParser parsing = null;
        String identifier = null;
        if (parsingData instanceof JEVisObject) {
            try {
                identifier = ((JEVisObject) parsingData).getJEVisClass().getName();
            } catch (JEVisException ex) {
                Logger.getLogger(ParsingFactory.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (identifier.equals(CSV_PARSER)) {
                parsing = new CSVParsing();
            } else if (identifier.equals(CSV_MULTI_TIME_PARSER)) {
//                parsing = new MultipleDataCoherentTimeCSV();
            } else if (identifier.equals(CSV_MULTI_DATA_PARSER)) {
//                parsing = new MultipleDataCoherentDataCSV();
            } else if (identifier.equals(MULTI_DATA_PARSER)) {
//                parsing = new MultipleDataCompact();
            } else if (identifier.equals(XML_SINGLE_PARSER)) {
//                parsing = new XMLParsingSingleData();
            } else if (identifier.equals(XML_MULTI_PARSER)) {
//                parsing = new XMLParsingMultipleData();
            } else if (identifier.equals(SQL_PARSER)) {
//                parsing = new SQLParsing();
            }

            parsing.initialize((JEVisObject) parsingData); //TODO neue Fetch8ingexception????
        }

//        if (parsingData instanceof String) {
//            identifier = (String) parsingData;
//
//            if (identifier.equals(CSV_PARSER)) {
//                parsing = new SinglePointCSV(identifier, identifier, indexDate, Integer.MIN_VALUE, indexValue, identifier, identifier, identifier, identifier);
//            } else if (identifier.equals(CSV_MULTI_TIME_PARSER)) {
//                parsing = new MultipleDataCoherentTimeCSV();
//            } else if (identifier.equals(CSV_MULTI_DATA_PARSER)) {
//                parsing = new MultipleDataCoherentDataCSV();
//            } else if (identifier.equals(MULTI_DATA_PARSER)) {
//                parsing = new MultipleDataCompact();
//            } else if (identifier.equals(XML_SINGLE_PARSER)) {
//                parsing = new XMLParsingSingleData();
//            } else if (identifier.equals(XML_MULTI_PARSER)) {
//                parsing = new XMLParsingMultipleData();
//            } else if (identifier.equals(SQL_PARSER)) {
//                parsing = new SQLParsing();
//            }
//        }



        return parsing;
    }
}
