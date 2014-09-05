/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.util.ArrayList;
import java.util.List;
import org.jevis.commons.parsing.GenericParser;
import org.jevis.commons.parsing.ParsingRequest;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.data.Data;
import org.jevis.jedatacollector.data.Equipment;
import org.jevis.jedatacollector.data.DataPoint;
import org.jevis.jedatacollector.service.ParsingService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author bf
 */
public class DefaultRequest implements Request {

    private boolean _needConnection = false;
    private DatacollectorConnection _connection;
    private GenericParser _parser;
    private DataPoint _datapoint;
    private Equipment _equipment;
    private Data _data;
    private boolean _needImport;
    private ParsingService _parsingService;
    private boolean _needParsing;
    private DateTime _from;
    private DateTime _until;
    private InputHandler _inputHandler;
    private List<DataPoint> _dataPoints = new ArrayList<DataPoint>();
    private DateTimeZone _dateTimeZone;
    private String _importType;
    private String _outputPath;
    private ParsingRequest _parsingRequest;
    
    

    @Override
    public boolean needConnection() {
        return _needConnection;
    }

    @Override
    public void setNeedConnection(boolean b) {
        _needConnection = b;
    }

    @Override
    public DatacollectorConnection getConnectionData() {
        return _connection;
    }

    public DataPoint getSpecificDatapoint() {
        return _datapoint;
    }

    @Override
    public GenericParser getParser() {
        return _parser;
    }

    @Override
    public void setConnection(DatacollectorConnection connection) {
        _connection = connection;
    }

    @Override
    public void setParser(GenericParser parsing) {
        _parser = parsing;
    }

    @Override
    public void setEquipment(Equipment equipment) {
        _equipment = equipment;
    }

    @Override
    public Equipment getEquipment() {
        return _equipment;
    }

    @Override
    public void setData(Data data) {
        _data = data;
    }

    @Override
    public Data getData() {
        return _data;
    }

    @Override
    public boolean needImport() {
        return _needImport;
    }

    @Override
    public void setNeedImport(boolean needImport) {
        _needImport = needImport;
    }

    @Override
    public void setParsingService(ParsingService ps) {
        _parsingService = ps;
    }

    @Override
    public boolean needParsing() {
        return _needParsing;
    }

    @Override
    public void setNeedParsing(boolean needParsing) {
        _needParsing = needParsing;
    }

    @Override
    public void setFrom(DateTime from) {
        _from = from;
    }

    @Override
    public DateTime getFrom() {
        return _from;
    }

    @Override
    public void setUntil(DateTime until) {
        _until = until;
    }

    @Override
    public DateTime getUntil() {
        return _until;
    }

    @Override
    public void setInputHandler(InputHandler input) {
        _inputHandler = input;
    }

    @Override
    public InputHandler getInputHandler() {
        return _inputHandler;
    }

    @Override
    public void setDataPoints(List<DataPoint> dataPoints) {
        _dataPoints = dataPoints;
    }

    @Override
    public List<DataPoint> getDataPoints() {
        return _dataPoints;
    }

    @Override
    public DateTimeZone getTimezone() {
        return _dateTimeZone;
    }

    @Override
    public void setTimeZone(DateTimeZone timeZone) {
        _dateTimeZone = timeZone;
    }

    @Override
    public void setOutputType(String importType) {
        _importType = importType;
    }

    @Override
    public String getOutputType() {
        return _importType;
    }

    @Override
    public void setFileOutputPath(String outputPath) {
        _outputPath = outputPath;
    }

    @Override
    public String getFileOutputPath() {
        return _outputPath;
    }

    @Override
    public ParsingRequest getParsingRequest() {
        return _parsingRequest;
    }

    @Override
    public void setParsingRequest(ParsingRequest preq) {
        _parsingRequest = preq;
    }
}
