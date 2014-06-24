/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.util.List;
import org.jevis.commons.parsing.DataCollectorParser;
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
public interface Request {

    public boolean needConnection();

    public void setNeedConnection(boolean b);

    public DatacollectorConnection getConnectionData();

    public DataCollectorParser getParser();

    public void setConnection(DatacollectorConnection connection);

    public void setParser(DataCollectorParser parsing);

    public void setEquipment(Equipment equipment);

//    public void setSpecificDatapoint(NewDataPoint datapoint);
//
//    public NewDataPoint getSpecificDatapoint();
    public Equipment getEquipment();

    public void setData(Data data);

    public Data getData();

    public boolean needImport();

    public void setNeedImport(boolean needImport);

    public void setParsingService(ParsingService ps);

    public boolean needParsing();

    public void setNeedParsing(boolean needParsing);

    public void setFrom(DateTime from);

    public DateTime getFrom();

    public void setUntil(DateTime until);

    public DateTime getUntil();

    public void setInputHandler(InputHandler input);

    public InputHandler getInputHandler();

    public void setDataPoints(List<DataPoint> dataPoints);

    public List<DataPoint> getDataPoints();

    public DateTimeZone getTimezone();

    public void setTimeZone(DateTimeZone timeZone);

    public void setOutputType(String importType);

    public String getOutputType();
    
    public void setFileOutputPath(String outputPath);
    
    public String getFileOutputPath();
    
    public ParsingRequest getParsingRequest();
    
    public void setParsingRequest(ParsingRequest preq);
}
