/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsing.CSV;

import java.util.TimeZone;
import org.jevis.jedatacollector.exception.FetchingException;
import org.joda.time.DateTime;

/**
 *
 * @author max
 */
public class MultipleDataCompact extends CSVParsing
{
    
    @Override
    protected void parseLine(String line, TimeZone tz) throws FetchingException
    {
        String[] columns = line.split(_seperatorColumn, -1);
        int channelIndex;
        
        String dateVal = columns[_indexDate-1];
        String timeVal = null;
        
        if(_indexTime != null)
        {
            timeVal = columns[_indexTime-1];
        }
        
        DateTime cal = getCal(timeVal, dateVal, tz);
        
//        for(DataPoint dp : pd.getDataPoints())
//        {
//            channelIndex = Integer.valueOf(dp.getChannelID());
//            pd.addSample(dp.getDataLoggerName(), dp.getChannelID(), new JevSampleImpl<Object>(cal, columns[channelIndex-1]));
//        }
    }
}
