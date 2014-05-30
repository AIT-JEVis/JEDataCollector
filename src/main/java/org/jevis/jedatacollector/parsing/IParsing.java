/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsing;

import java.util.TimeZone;
import org.jevis.api.JEVisObject;
import org.jevis.jedatacollector.exception.FetchingException;
import org.jevis.jedatacollector.service.ParsedData;
import org.jevis.jedatacollector.service.inputHandler.InputHandler;

/**
 *
 * @author max
 */
public interface IParsing 
{
    /**
     * Parses the answer to a list of samples
     *
     * @param pd Here will the parsed data be put into
     * @param o The answer of the previous request
     * @param tz The time zone the requested data is expected to be in
     * @return A list of samples
     */
    void parseData(InputHandler ic, TimeZone tz) throws FetchingException;
    
    void initialize(JEVisObject object) throws FetchingException;
    
    ParsedData getData();
}
