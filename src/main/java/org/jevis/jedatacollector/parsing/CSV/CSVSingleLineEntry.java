/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsing.CSV;

import java.util.Arrays;
import java.util.TimeZone;
import org.jevis.jedatacollector.exception.FetchingException;
import org.joda.time.DateTime;

/**
 *
 * @author max
 */
public abstract class CSVSingleLineEntry extends CSVParsing {

    @Override
    protected void parseLine(String line, TimeZone tz) throws
            FetchingException {
        System.out.println("parse Line");
        System.out.println("Line: "+line);
        DateTime cal;
        String[] columns;
        String sVal, sTime, sDate = null;
//                sPoint = null, sLogger = null;

//        try
//        {
//            sPoint = getChannel(line);
//            sLogger = getLogger(line);
//        } catch (Exception e)
//        {
//        }

        columns = line.split(_seperatorColumn, -1);

        sVal = getValue(columns);
        sVal = removeEnclosing(sVal);
        sVal = sVal.replaceAll("\\" + _seperatorThousand, "");
        sVal = sVal.replaceAll("\\" + _seperatorDecimal, ".");

        sTime = null;
        if (_indexTime != null) {
            try {
                sTime = columns[_indexTime - 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(line);
                System.out.println(_seperatorColumn);
                System.out.println(Arrays.toString(columns));
                throw new RuntimeException();
            }
            sTime = removeEnclosing(sTime);
        }
        sDate = columns[_indexDate - 1];
        sDate = removeEnclosing(sDate);
        cal = getCal(sTime, sDate, tz);

        System.out.println("Adde geparste Daten");
        _parsedData.add(sVal, cal); // pd.addSample(sLogger, sPoint, JevSampleImpl.createJevSample(sVal, cal));
    }

    protected String getValue(String[] columns) {
        return columns[_indexValue - 1];
    }
//    protected abstract String getChannel(String line);
//
//    protected abstract String getLogger(String line);
}
