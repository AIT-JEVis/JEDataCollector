/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.JEVisDataSourceSQL;

/**
 *
 * @author bf
 */
class Test {

    public Test() {
//        Logger.getLogger(this.getClass()).log(Level.INFO, "INFO TEST");
//        Logger.getLogger(this.getClass()).log(Level.WARN, "WARN TEST");
//        Logger.getLogger(this.getClass()).log(Level.ALL, "ALL TEST");
    }

    public static void main(String[] args) {
        try {
            JEVisDataSourceSQL client = new JEVisDataSourceSQL("192.168.2.55", "3306", "jevis", "jevis", "jevistest", "Sys Admin", "jevis");
            client.connect("Sys Admin", "jevis");
            //            JEVisClass jeVisClass = client.getJEVisClass(JEVisTypes.Equipment.NAME);
            //            JEVisClass parser = client.getJEVisClass(JEVisTypes.Parser.CSVParser.NAME);
            //            JEVisClass connection = client.getJEVisClass("HTTPCon");
            //            connection.setName(JEVisTypes.Connection.HTTP.Name);
            JEVisObject object = client.getObject(390l);
            object.getAttribute("Raw Data").deleteAllSample();
            object = client.getObject(391l);
            object.getAttribute("Raw Data").deleteAllSample();
            object = client.getObject(392l);
            object.getAttribute("Raw Data").deleteAllSample();
            object = client.getObject(393l);
            object.getAttribute("Raw Data").deleteAllSample();
            object = client.getObject(394l);
            object.getAttribute("Raw Data").deleteAllSample();
            object = client.getObject(395l);
            object.getAttribute("Raw Data").deleteAllSample();
            object = client.getObject(396l);
            object.getAttribute("Raw Data").deleteAllSample();
            object = client.getObject(397l);
            object.getAttribute("Raw Data").deleteAllSample();
            object = client.getObject(398l);
            object.getAttribute("Raw Data").deleteAllSample();
            object = client.getObject(399l);
            object.getAttribute("Raw Data").deleteAllSample();

        } catch (JEVisException ex) {
            java.util.logging.Logger.getLogger(Test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
//        System.out.println(Boolean.parseBoolean("0"));
//    String token = "/data/trend/Trend_L1_1_${DATAPOINT}.csv";
//        System.out.println(ConnectionHelper.containsToken(token));
//        System.out.println(ConnectionHelper.replaceDatapoint(token,new DataPoint("hallo", "logger", 35l)));
    }
    

}
