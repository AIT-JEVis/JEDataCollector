/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.csvParsing;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.jeapi.JEVisClass;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jeapi.JEVisType;
import org.jevis.jedatacollector.data.JevisAttributes;
import org.jevis.jedatacollector.parsingNew.DataCollectorParser;
import org.jevis.jedatacollector.parsingNew.Result;
import org.jevis.jedatacollector.parsingNew.GeneralDatapointParser;
import org.jevis.jedatacollector.parsingNew.GeneralDateParser;
import org.jevis.jedatacollector.parsingNew.GeneralValueParser;
import org.jevis.jedatacollector.parsingNew.SampleParserContainer;
import org.jevis.jedatacollector.service.ParsingService;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;
import org.joda.time.DateTime;

/**
 *
 * @author broder
 */
public class CSVParsing extends DataCollectorParser {

    private String _quote;
    private String _delim;
    private int _headerLines;

    public CSVParsing(String quote, String delim, int headerlines) {
        _quote = quote;
        _delim = delim;
        _headerLines = headerlines;
    }

    public CSVParsing() {
    }

    //this should be easier.. perhaps with a JEObject?
    public void setQuote(String q) {
        _quote = q;
    }

    public void setDelim(String d) {
        _delim = d;
    }

    public void setHeaderLines(int h) {
        _headerLines = h;
    }

    @Override
    public void parse(InputHandler ic) {
        System.out.println("File Parsing starts");
        String[] stringArrayInput = ic.getStringArrayInput().clone();
        System.out.println("Sampleparserlist " + _sampleParsers.size());
        for (int i = _headerLines; i < stringArrayInput.length; i++) {
            String line[] = stringArrayInput[i].split(String.valueOf(_delim),-1);
            if (_quote != null) {
                line = removeQuotes(line);
            }

            //line noch setzen im InputConverter als temp oder so
            ic.setTmpInput(line);

            DateTime dateTime;
            Double value;
            Long datapoint;
            for (SampleParserContainer parser : _sampleParsers) {
                GeneralDateParser dateParser = parser.getDateParser();
                dateParser.parse(ic);
                dateTime = dateParser.getDateTime();
                GeneralValueParser valueParser = parser.getValueParser();
                valueParser.parse(ic);
                value = valueParser.getValue();
                GeneralDatapointParser dpParser = parser.getDpParser();
                dpParser.parse(ic);
                datapoint = dpParser.getDatapoint();
              
                
                if(((ValueCSVParser)valueParser).outOfBounce()){
                    System.out.println("Date "+ dateTime);
                    System.out.println("Value "+ value);
                }
                boolean valueIsValid = ParsingService.checkValue(parser);
                if(!valueIsValid){
                    continue;
                }
                _results.add(new Result(datapoint, value, dateTime));
            }
        }
    }

    private String[] removeQuotes(String[] line) {
        String[] removed = new String[line.length];
        for (int i = 0; i < line.length; i++) {
            removed[i] = line[i].replace(_quote, "");
        }
        return removed;
    }

    @Override
    public void initialize(JEVisObject pn) {
        _jevisParser = pn;
        try {
            JEVisClass jeClass = pn.getJEVisClass();
            JEVisType seperatorColumn = jeClass.getType(JevisAttributes.CSV_DELIM);
            JEVisType enclosedBy = jeClass.getType(JevisAttributes.CSV_QUOTE);
            JEVisType ignoreFirstNLines = jeClass.getType(JevisAttributes.CSV_HEADERLINES);

            _delim = (String) pn.getAttribute(seperatorColumn).getLatestSample().getValue();

            if (pn.getAttribute(enclosedBy).getLatestSample() != null) {
                _quote = (String) pn.getAttribute(enclosedBy).getLatestSample().getValue();
            }

            if (pn.getAttribute(ignoreFirstNLines).getLatestSample() != null) {
                _headerLines = Integer.parseInt((String) pn.getAttribute(ignoreFirstNLines).getLatestSample().getValue());
            }
        } catch (JEVisException ex) {
            Logger.getLogger(CSVParsing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SampleParserContainer extractSampleContainer(JEVisObject mapping, JEVisObject dateObject, JEVisObject valueObject) {
        SampleParserContainer container = null;
        try {
            //DateObject 
            DateCSVParser dateParser = null;
            JEVisClass dateClass = dateObject.getJEVisClass();
            System.out.println("Dateobjectid " + dateObject.getID());

            JEVisType dateFormat = dateClass.getType(JevisAttributes.DATE_DATEFORMAT);
            JEVisType timeFormat = dateClass.getType(JevisAttributes.DATE_TIMEFORMAT);
            JEVisType indexDate = dateClass.getType(JevisAttributes.DATE_CSV_DATEINDEX);
            JEVisType indexTime = dateClass.getType(JevisAttributes.DATE_CSV_TIMEINDEX);

            String date = dateObject.getAttribute(dateFormat).getLatestSample().getValueAsString();
            System.out.println("Date" + date);
            String time = dateObject.getAttribute(timeFormat).getLatestSample().getValueAsString();
            System.out.println("Time" + time);
            int dateIndex = -1;
            if (dateObject.getAttribute(indexDate) != null) {
                dateIndex = (int) (long) dateObject.getAttribute(indexDate).getLatestSample().getValueAsLong();
            }
            System.out.println("Dateindex" + dateIndex);

            int timeIndex = -1;
            if (dateObject.getAttribute(indexTime) != null) {
                timeIndex = (int) (long) dateObject.getAttribute(indexTime).getLatestSample().getValueAsLong();
            }
            System.out.println("Timeindex" + timeIndex);

            dateParser = new DateCSVParser(time, timeIndex, date, dateIndex);


            //Mappingclass
            GeneralDatapointParser datapointParser = null;
            JEVisClass mappingClass = mapping.getJEVisClass();
            JEVisType indexValueType = mappingClass.getType(JevisAttributes.MAPPING_VALUE_SPECIFICATION);
//            JEVisType indexDatapointType = mappingClass.getType("Index Datapoint");
//            JEVisType datapointInFileType = mappingClass.getType("infile");
            JEVisType datapointType = mappingClass.getType(JevisAttributes.MAPPING_ONLINEID);

            int indexValue = -1;
            if (mapping.getAttribute(indexValueType) != null) {
                indexValue = (int) (long) mapping.getAttribute(indexValueType).getLatestSample().getValueAsLong();
            }
            System.out.println("IndexValue" + indexValue);
//            int indexDatapoint = 0;
//            if (mapping.getAttribute(indexDatapointType) != null) {
//                indexDatapoint = Integer.parseInt((String) mapping.getAttribute(indexDatapointType).getLatestSample().getValue());
//            }
            long datapoint = -1;
            if (mapping.getAttribute(datapointType) != null) {
                datapoint = mapping.getAttribute(datapointType).getLatestSample().getValueAsLong();
            }
            System.out.println("Datapoint" + datapoint);
//            boolean inFile = false;
//            if (mapping.getAttribute(datapointInFileType) != null) {
//                inFile = Boolean.parseBoolean((String) mapping.getAttribute(datapointInFileType).getLatestSample().getValue());
//            }
            //entweder den einen oder den anderen Parser!!!!!
            datapointParser = new DatapointFixCSVParser(false, datapoint);

            //ValueObject
            GeneralValueParser valueParser = null;
            JEVisClass valueClass = valueObject.getJEVisClass();
            JEVisType seperatorDecimalType = valueClass.getType(JevisAttributes.VALUE_DECIMSEPERATOR);
            JEVisType seperatorThousandType = valueClass.getType(JevisAttributes.VALUE_THOUSANDSEPERATOR);

            String seperatorDecimal = valueObject.getAttribute(seperatorDecimalType).getLatestSample().getValueAsString();
            System.out.println("sepDecimal" + seperatorDecimal);
            String seperatorThousand = valueObject.getAttribute(seperatorThousandType).getLatestSample().getValueAsString();
            System.out.println("sepThousand" + seperatorThousand);
            valueParser = new ValueCSVParser(indexValue, seperatorDecimal, seperatorThousand);

            container = new SampleParserContainer(datapointParser, dateParser, valueParser);
        } catch (JEVisException ex) {
            Logger.getLogger(DataCollectorParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return container;
    }
}
