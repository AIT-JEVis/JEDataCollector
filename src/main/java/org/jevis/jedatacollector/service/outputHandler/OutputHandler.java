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
public abstract class OutputHandler {

    public static String FILE_OUTPUT = "csv_file";
    public static String JEVIS_OUTPUT = "jevis";

    abstract public void writeOutput(Request request, List<Result> results);
}
