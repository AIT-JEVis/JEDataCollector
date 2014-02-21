/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.sampleParser;

import org.jevis.jedatacollector.service.inputHandler.InputHandler;

/**
 *
 * @author broder
 */
public interface GeneralDatapointParser extends GeneralParser{

    public boolean isInFile();

    public long getDatapoint();
    
 
}
