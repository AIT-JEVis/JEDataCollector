/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.util.List;
import java.util.TimeZone;
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.data.Data;
import org.jevis.jedatacollector.data.Equipment;
import org.jevis.jedatacollector.data.NewDataPoint;
import org.jevis.jedatacollector.parsingNew.DataCollectorParser;
import org.jevis.jedatacollector.service.ParsingService;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;
import org.joda.time.DateTime;

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
    public InputHandler _inputHandler;

    public boolean needConnection() {
        return _needConnection;
    }

    public void setNeedConnection(boolean b) {
        _needConnection = b;
    }

    public DatacollectorConnection getConnectionData() {
        return _connection;
    }

    public NewDataPoint getSpecificDatapoint() {
        return _datapoint;
    }

    public DataCollectorParser getParser() {
        return _parser;
    }

    public void setConnection(DatacollectorConnection connection) {
        _connection = connection;
    }

    public void setParser(DataCollectorParser parsing) {
        _parser = parsing;
    }

    public void setEquipment(Equipment equipment) {
        _equipment = equipment;
    }

    public Equipment getEquipment() {
        return _equipment;
    }

    public void setSpecificDatapoint(NewDataPoint tmpdatapoints) {
        _datapoint = tmpdatapoints;
    }

    public void setData(Data data) {
        _data = data;
    }

    public Data getData() {
        return _data;
    }

    public boolean needImport() {
        return _needImport;
    }

    public void setNeedImport(boolean needImport) {
        _needImport = needImport;
    }

    public void setParsingService(ParsingService ps) {
        _parsingService = ps;
    }

    public boolean needParsing() {
        return _needParsing;
    }

    public void setNeedParsing(boolean needParsing) {
        _needParsing = needParsing;
    }

    public void setFrom(DateTime from) {
        _from = from;
    }

    public DateTime getFrom() {
        return _from;
    }

    public void setUntil(DateTime until) {
        _until = until;
    }

    public DateTime getUntil() {
        return _until;
    }

    public void setInputHandler(InputHandler input) {
        _inputHandler = input;
    }

    public InputHandler getInputHandler() {
        return _inputHandler;
    }
}
