/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author bf
 */
class Test {

    public Test() {
        Logger.getLogger(this.getClass()).log(Level.INFO, "INFO TEST");
        Logger.getLogger(this.getClass()).log(Level.WARN, "WARN TEST");
        Logger.getLogger(this.getClass()).log(Level.ALL, "ALL TEST");
    }
}
