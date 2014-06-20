/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service.outputHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.jevis.jedatacollector.Request;
import org.jevis.jedatacollector.parsingNew.Result;

/**
 *
 * @author bf
 */
public class CSVFileOutputHandler extends OutputHandler {

    @Override
    public void writeOutput(Request request, List<Result> results) {
        try {
            String outputPath = request.getFileOutputPath();
            if(outputPath == null){
                outputPath = "output.csv";
            }
            File output = new File(outputPath);

            BufferedWriter write = new BufferedWriter(new FileWriter(output));
            for (Result r : results) {
                write.write(r.getValue() + "," + r.getOnlineID() + "," + r.getDate()+"\n");
            }
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
