/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.util.List;
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.data.Data;
import org.jevis.jedatacollector.data.Equipment;
import org.jevis.jedatacollector.data.NewDataPoint;
import org.jevis.jedatacollector.parsingNew.DataCollectorParser;
import org.jevis.jedatacollector.service.ParsingService;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author bf
 */
public class DefaultRequest implements Request {

    private boolean _needConnection = false;
    private DatacollectorConnection _connection;
    private DataCollectorParser _parser;
    private NewDataPoint _datapoint;
    private Equipment _equipment;
    private Data _data;
    private boolean _needImport;
    private ParsingService _parsingService;
    private boolean _needParsing;
    private DateTime _from;
    private DateTime _until;
    private InputHandler _inputHandler;
    private List<NewDataPoint> _dataPoints;
    private DateTimeZone _dateTimeZone;
    private String _importType;
    private String _outputPath;

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

    public NewDataPoint getSpecificDatapoint() {
        return _datapoint;
    }

    @Override
    public DataCollectorParser getParser() {
        return _parser;
    }

    @Override
    public void setConnection(DatacollectorConnection connection) {
        _connection = connection;
    }

    @Override
    public void setParser(DataCollectorParser parsing) {
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
    public void setDataPoints(List<NewDataPoint> dataPoints) {
        _dataPoints = dataPoints;
    }

    @Override
    public List<NewDataPoint> getDataPoints() {
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
}
