/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service.outputHandler;

/**
 *
 * @author bf
 */
public class OutputHandlerFactory {

    public static OutputHandler getOutputHandler(String outputType) {
        if (outputType.equals(OutputHandler.FILE_OUTPUT)) {
            return new FileOutputHandler();
        }
        if (outputType.equals(OutputHandler.JEVIS_OUTPUT)) {
            return new JEVisOutputHandler();
        }
        return null;


    }
}
