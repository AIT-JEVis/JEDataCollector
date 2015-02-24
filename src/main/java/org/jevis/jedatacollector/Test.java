package org.jevis.jedatacollector;


/**
 * Copyright (C) 2015 Envidatec GmbH <info@envidatec.com>
 *
 * This file is part of Tester.
 *
 * Tester is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation in version 3.
 *
 * Tester is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Tester. If not, see <http://www.gnu.org/licenses/>.
 *
 * Tester is part of the OpenJEVis project, further project information are
 * published at <http://www.OpenJEVis.org/>.
 */
import java.util.logging.Level;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.slf4j.MDC;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class Test {

    public static int _val = 1;

    public static void main(String[] args) {

        final Logger logger = Logger.getRootLogger();

//  tag all child threads with this process-id so we can separate out log output
        for (int i = 0; i < 5; i++) {
            String processID = "" + i;

            if (logger.getAppender(processID) == null) {

                try {
                    String pattern = "%d{yy/MM/dd HH:mm:ss} %p %c{2}: %m%n";
                    String logfile = processID + ".log";

                    FileAppender fileAppender = new FileAppender(
                            new PatternLayout(pattern), logfile, true);
                    fileAppender.setName(processID);
                    fileAppender.addFilter(new ThreadFilter(processID));

                    // add a filter so we can ignore any logs from other threads
//                    fileAppender.addFilter(new ProcessIDFilter(processID));
                    logger.addAppender(fileAppender);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            MDC.put("process-id", processID);
            Thread test = new Thread() {
                @Override
                public void run() {
                    int currentVal = (int) (Math.random()*100d);
                    while (true) {
                        try {
                            sleep(200);
                        } catch (InterruptedException ex) {
                            java.util.logging.Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                        }
                        logger.info(currentVal + " -- " + Math.random());
                    }
                }
            };
            test.start();
            MDC.remove(processID);
            logger.info("About to end the job");

        }

    }
}