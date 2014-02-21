/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.data.Data;
import org.jevis.jedatacollector.data.Equipment;
import org.jevis.jedatacollector.data.NewDataPoint;
import org.jevis.jedatacollector.parsingNew.DataCollectorParser;
import org.jevis.jedatacollector.service.ParsingService;

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

    public void setSpecificDatapoint(NewDataPoint datapoint);

    public NewDataPoint getSpecificDatapoint();

    public Equipment getEquipment();

    public void setData(Data data);

    public Data getData();

    public boolean needImport();
    
    public void setNeedImport(boolean needImport);
    
    public void setParsingService(ParsingService ps);
}
