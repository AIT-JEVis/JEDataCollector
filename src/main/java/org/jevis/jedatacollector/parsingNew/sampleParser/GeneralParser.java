/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.sampleParser;

import org.jevis.jedatacollector.service.inputHandler.InputHandler;

/**
 * Interface for parsing a line in a csv File
 * 
 * @author broder
 */
interface GeneralParser {

    public void parse(InputHandler ic);
}
