/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service.outputHandler;

import java.util.List;
import org.jevis.jedatacollector.Request;
import org.jevis.jedatacollector.parsingNew.Result;

/**
 *
 * @author bf
 */
public class FileOutputHandler extends OutputHandler{

    @Override
    public void writeOutput(Request request, List<Result> results) {
        for(Result r: results){
            
        }
    }
    
}
