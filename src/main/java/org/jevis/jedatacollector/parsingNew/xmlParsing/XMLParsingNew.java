/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.xmlParsing;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.jeapi.JEVisClass;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jeapi.JEVisType;
import org.jevis.jedatacollector.data.JevisAttributes;
import org.jevis.jedatacollector.parsingNew.DataCollectorParser;
import org.jevis.jedatacollector.parsingNew.GeneralMappingParser;
import org.jevis.jedatacollector.parsingNew.GeneralDateParser;
import org.jevis.jedatacollector.parsingNew.GeneralValueParser;
import org.jevis.jedatacollector.parsingNew.Result;
import org.jevis.jedatacollector.parsingNew.SampleParserContainer;
import org.jevis.jedatacollector.parsingNew.csvParsing.CSVParsing;
import org.jevis.jedatacollector.service.ParsingService;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author bf
 */
public class XMLParsingNew extends DataCollectorParser {

    private String _generalTag;
    private String _specificationTag;
    private Boolean _specificationInAttribute;

    public XMLParsingNew(String generalTag, String specificationTag, Boolean specificationInAttribute) {
        _generalTag = generalTag;
        _specificationTag = specificationTag;
        _specificationInAttribute = specificationInAttribute;
    }

    @Override
    public void parse(InputHandler ic) {
        System.out.println("XMl File Parsing starts");
        System.out.println("Sampleparserlist " + _sampleParsers.size());
        List<Document> documents = ic.getDocuments();
        for (Document d : documents) {
            NodeList elementsByTagName = d.getElementsByTagName(_generalTag);

            //iterate over all nodes with the element name
            for (int i = 0; i < elementsByTagName.getLength(); i++) {
                Node currentNode = elementsByTagName.item(i);
                ic.setXMLInput(currentNode);

                //single parsing
                boolean isCorrectNode = true; //eigentl false
                DateTime dateTime;
                Double value;
                Long datapoint;
                if (_specificationTag == null) {
                    //should only be one sample parser
                    for (SampleParserContainer parser : _sampleParsers) {
                        GeneralDateParser dateParser = parser.getDateParser();
                        dateParser.parse(ic);
                        dateTime = dateParser.getDateTime();
                        GeneralValueParser valueParser = parser.getValueParser();
                        valueParser.parse(ic);
                        value = valueParser.getValue();
                        GeneralMappingParser dpParser = parser.getDpParser();
                        dpParser.parse(ic);
                        datapoint = dpParser.getDatapoint();

                        //should be in an extra method
                        boolean valueIsValid = ParsingService.checkValue(parser);
                        if (!valueIsValid) {
                            continue;
                        }

                        boolean datapointIsValid = ParsingService.checkDatapoint(parser);
                        if (!datapointIsValid) {
                            continue;
                        }
                        _results.add(new Result(datapoint, value, dateTime));
                    }
                } else {
                    //multi parsing
                    if (_specificationInAttribute) {
                        NamedNodeMap attributes = currentNode.getAttributes();
                        for (int j = 0; j < attributes.getLength(); j++) {
                        }
                    } else {
                    }
                }

                //parse the correct node
                if (isCorrectNode) {
                }
            }
        }
    }

    @Override
    public void initialize(JEVisObject parser) {
        _jevisParser = parser;
        try {
            JEVisClass jeClass = parser.getJEVisClass();
            JEVisType generalTag = jeClass.getType(JevisAttributes.XML_GENERAL_TAG);
            JEVisType specificationTag = jeClass.getType(JevisAttributes.XML_SPECIFICATION_TAG);
            JEVisType specificationIsAttribute = jeClass.getType(JevisAttributes.XML_SPECIFICATION_ATTRIBUTE);

            _generalTag = parser.getAttribute(generalTag).getLatestSample().getValueAsString();

            if (parser.getAttribute(specificationTag).getLatestSample() != null) {
                _specificationTag = (String) parser.getAttribute(specificationTag).getLatestSample().getValue();
            }

            if (parser.getAttribute(specificationIsAttribute).getLatestSample() != null) {
                _specificationInAttribute = parser.getAttribute(specificationIsAttribute).getLatestSample().getValueAsBoolean();
            }
        } catch (JEVisException ex) {
            Logger.getLogger(CSVParsing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public GeneralDateParser initializeDateParser(JEVisObject dateObject, JEVisObject valueObject, JEVisObject mapping) {
        DateXMLParsing dateParser = null;
        try {
            JEVisClass dateClass = dateObject.getJEVisClass();
            System.out.println("Dateobjectid " + dateObject.getID());

            JEVisType dateFormatType = dateClass.getType(JevisAttributes.DATE_DATEFORMAT);
            JEVisType dateTagType = dateClass.getType(JevisAttributes.XML_DATE_TAG);
            JEVisType dateInAttributeType = dateClass.getType(JevisAttributes.XML_DATE_ATTRIBUTE);
            JEVisType timeFormatType = dateClass.getType(JevisAttributes.DATE_TIMEFORMAT);
            JEVisType timeTagType = dateClass.getType(JevisAttributes.XML_TIME_TAG);
            JEVisType timeInAttributeType = dateClass.getType(JevisAttributes.XML_TIME_ATTRIBUTE);

            String dateFormat = dateObject.getAttribute(dateFormatType).getLatestSample().getValueAsString();
            System.out.println("Dateformat" + dateFormat);
            String dateTag = dateObject.getAttribute(dateTagType).getLatestSample().getValueAsString();
            System.out.println("DateTag" + dateTag);
            boolean dateInAttribute = dateObject.getAttribute(dateInAttributeType).getLatestSample().getValueAsBoolean();
            System.out.println("DateInAttribute" + dateInAttribute);


            String timeFormat = dateObject.getAttribute(timeFormatType).getLatestSample().getValueAsString();
            System.out.println("Time" + timeFormat);
            String timeTag = dateObject.getAttribute(timeTagType).getLatestSample().getValueAsString();
            System.out.println("DateTag" + timeTag);
            boolean timeInAttribute = dateObject.getAttribute(timeInAttributeType).getLatestSample().getValueAsBoolean();
            System.out.println("DateInAttribute" + timeInAttribute);


            dateParser = new DateXMLParsing(dateFormat, dateTag, dateInAttribute, timeFormat, timeTag, timeInAttribute);
        } catch (JEVisException ex) {
            Logger.getLogger(CSVParsing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dateParser;
    }

    @Override
    public GeneralMappingParser initializeDatapointParser(JEVisObject date, JEVisObject value, JEVisObject mapping) {
        GeneralMappingParser datapointParser = null;
        try {
            //Mappingclass
            JEVisClass mappingClass = mapping.getJEVisClass();
            JEVisType indexValueType = mappingClass.getType(JevisAttributes.MAPPING_VALUE_SPECIFICATION);
            //            JEVisType indexDatapointType = mappingClass.getType("Index Datapoint");
            //            JEVisType datapointInFileType = mappingClass.getType("infile");
            JEVisType datapointType = mappingClass.getType(JevisAttributes.MAPPING_ONLINEID);

            String valueSpecification = null;
            if (mapping.getAttribute(indexValueType) != null) {
                valueSpecification = mapping.getAttribute(indexValueType).getLatestSample().getValueAsString();
            }
            System.out.println("ValueSpecification" + valueSpecification);
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
            datapointParser = new DatapointFixXMLParsing(false, datapoint);
        } catch (JEVisException ex) {
            Logger.getLogger(CSVParsing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return datapointParser;
    }

    @Override
    public GeneralValueParser initializeValueParser(JEVisObject dateObject, JEVisObject valueObject, JEVisObject mapping) {
        GeneralValueParser valueParser = null;
        try {
            //get the index from the mapping object
            JEVisClass mappingClass = mapping.getJEVisClass();
            JEVisType indexValueType = mappingClass.getType(JevisAttributes.MAPPING_VALUE_SPECIFICATION);
            //            JEVisType indexDatapointType = mappingClass.getType("Index Datapoint");
            //            JEVisType datapointInFileType = mappingClass.getType("infile");
            String valueSpecification = null;
            if (mapping.getAttribute(indexValueType) != null) {
                valueSpecification = mapping.getAttribute(indexValueType).getLatestSample().getValueAsString();
            }
            JEVisType valueInAttributeType = mappingClass.getType(JevisAttributes.XML_VALUE_ATTRIBUTE);
            //            JEVisType indexDatapointType = mappingClass.getType("Index Datapoint");
            //            JEVisType datapointInFileType = mappingClass.getType("infile");
            boolean valueInAttribute = false;
            if (mapping.getAttribute(indexValueType) != null) {
                valueInAttribute = mapping.getAttribute(valueInAttributeType).getLatestSample().getValueAsBoolean();
            }

            //ValueObject
            JEVisClass valueClass = valueObject.getJEVisClass();
            JEVisType seperatorDecimalType = valueClass.getType(JevisAttributes.VALUE_DECIMSEPERATOR);
            JEVisType seperatorThousandType = valueClass.getType(JevisAttributes.VALUE_THOUSANDSEPERATOR);

            String seperatorDecimal = valueObject.getAttribute(seperatorDecimalType).getLatestSample().getValueAsString();
            System.out.println("sepDecimal" + seperatorDecimal);
            String seperatorThousand = valueObject.getAttribute(seperatorThousandType).getLatestSample().getValueAsString();
            System.out.println("sepThousand " + seperatorThousand);
            valueParser = new ValueXMLParsing(valueSpecification, valueInAttribute, seperatorDecimal, seperatorThousand);
        } catch (JEVisException ex) {
            Logger.getLogger(CSVParsing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valueParser;
    }
}
