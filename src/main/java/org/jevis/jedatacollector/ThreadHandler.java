/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.util.ArrayList;
import java.util.List;
import org.jevis.api.JEVisObject;

/**
 *
 * @author bf
 */
public class ThreadHandler {

    private List<JEVisObject> _requests;
    private static List<JEVisObject> _activeRequests = new ArrayList<JEVisObject>();

    public ThreadHandler(List<JEVisObject> requests) {
        _requests = requests;
    }

    synchronized public boolean hasRequest() {
        if (_requests.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    synchronized public JEVisObject getNextDataSource() {
        for (JEVisObject currentReq : _requests) {
            _activeRequests.add(currentReq);
            _requests.remove(currentReq);
            return currentReq;
        }
        return null;
    }

    synchronized public int getNumberActiveRequests() {
        return _activeRequests.size();
    }
}
