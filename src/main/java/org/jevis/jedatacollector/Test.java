/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.jevis.commons.JEVisTypes;

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
        try {
            JEVisDataSourceSQL client = new JEVisDataSourceSQL("192.168.2.55", "3306", "jevis", "jevis", "jevistest", "Sys Admin", "jevis");
            client.connect("Sys Admin", "jevis");
            JEVisClass jeVisClass = client.getJEVisClass(JEVisTypes.Equipment.NAME);
            JEVisClass parser = client.getJEVisClass(JEVisTypes.Parser.CSVParser.NAME);
            JEVisClass connection = client.getJEVisClass("HTTPCon");
            connection.setName(JEVisTypes.Connection.HTTP.Name);
        } catch (JEVisException ex) {
            java.util.logging.Logger.getLogger(Test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
}
