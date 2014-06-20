/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

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

    public static void main(String[] args) {
        DateTime tmpFrom = new DateTime();//testcase 
        DateTime from = new DateTime(tmpFrom.getMillis() - 2000000000);
        DateTime until = new DateTime(from.getMillis() + 10000000);
        
        System.out.println(tmpFrom);
        System.out.println(from);
        System.out.println(until);
    }
}
