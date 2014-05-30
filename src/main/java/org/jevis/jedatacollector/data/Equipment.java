/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.data;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisType;
import org.joda.time.DateTimeZone;

/**
 *
 * @author Broder
 */
public class Equipment {

//    public static String NAME = "Datenlogger Name";
//    public static String INSTALLATION_DATE = "Installation Date";
//    public static String LAST_FETCH = "Last Data Fetch";
//    public static String LAST_SERVICE = "Last Service";
    public static String SINGLE_CONNECTION = "Single Reader";
    public static String TIMEZONE = "Timezone";
//    private String _name;
//    private String _installationDate;
//    private String _lastFetch;
//    private String _lastService;
    private boolean _singleReader;
    private DateTimeZone _timezone;

//    public String getInstallationDate() {
//        return _installationDate;
//    }
//
//    public String getLastFetch() {
//        return _lastFetch;
//    }
//
//    public String getLastService() {
//        return _lastService;
//    }
//
//    public String getName() {
//        return _name;
//    }
    public boolean isSingleConnection() {
        return _singleReader;
    }

    public DateTimeZone getTimezone() {
        return _timezone;
    }

    public Equipment(JEVisObject equipment) {
        try {
            JEVisClass type = equipment.getJEVisClass();
//            JEVisType nameType = type.getType(NAME);
//            JEVisType installationType = type.getType(INSTALLATION_DATE);
//            JEVisType lastFetchType = type.getType(LAST_FETCH);
//            JEVisType lastServiceType = type.getType(LAST_SERVICE);
            JEVisType singleReaderType = type.getType(SINGLE_CONNECTION);
            JEVisType timezoneType = type.getType(TIMEZONE);

//            if (equipment.getAttribute(nameType).getLatestSample() != null) {
//                _name = (String) equipment.getAttribute(nameType).getLatestSample().getValue();
//            }
//            if (equipment.getAttribute(installationType).getLatestSample() != null) {
//                _installationDate = (String) equipment.getAttribute(installationType).getLatestSample().getValue();
//            }
//            if (equipment.getAttribute(lastFetchType).getLatestSample() != null) {
//                _lastFetch = (String) equipment.getAttribute(lastFetchType).getLatestSample().getValue();
//            }
//            if (equipment.getAttribute(lastServiceType).getLatestSample() != null) {
//                _lastService = (String) equipment.getAttribute(lastServiceType).getLatestSample().getValue();
//            }
            if (equipment.getAttribute(singleReaderType).getLatestSample() != null) {
                _singleReader = Boolean.parseBoolean((String) equipment.getAttribute(singleReaderType).getLatestSample().getValue());
            }
            if (equipment.getAttribute(timezoneType).getLatestSample() != null) {
                String timezoneName = (String) equipment.getAttribute(timezoneType).getLatestSample().getValue();
                _timezone = DateTimeZone.forID(timezoneName);
            }
        } catch (JEVisException ex) {
            Logger.getLogger(Equipment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
