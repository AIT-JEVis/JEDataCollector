/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsing.SQL;

import java.util.TimeZone;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jedatacollector.parsing.IParsing;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;

/**
 *
 * @author max
 */
public class SQLParsing implements IParsing {

    @Override
    public void parseData(InputHandler ic, TimeZone tz) throws FetchingException {
//        List<Object> ol = (List<Object>)o;
//        
//        for(Object os : ol)
//        {
//            pd.addSample((JevSample)o);
//        }
    }

    @Override
    public void initialize(JEVisObject node) throws FetchingException {
    }

    @Override
    public org.jevis.jedatacollector.service.ParsedData getData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
