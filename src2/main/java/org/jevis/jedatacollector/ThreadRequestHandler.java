/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.util.ArrayList;
import java.util.List;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.jevis.jedatacollector.service.Request;

/**
 *
 * @author bf
 */
public class ThreadRequestHandler {

    private List<Request> _requests;
    private static List<Request> _activeRequests = new ArrayList<Request>();

    public ThreadRequestHandler(List<Request> requests) {
        _requests = requests;
    }

    synchronized public boolean hasRequest() {
        if (_requests.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    synchronized public boolean hasValidRequest() {
        for (Request currentReq : _requests) {

            DataCollectorConnection dataSource = currentReq.getDataSource();
            boolean contains = false;
            int currentPort = dataSource.getPort();
            String currentHost = dataSource.getHost();
            for (Request currentActiveReq : _activeRequests) {
                DataCollectorConnection connection = currentActiveReq.getDataSource();
                if (currentPort == connection.getPort() && currentHost.equals(dataSource.getHost())) {
                    contains = true;
                    break;
                }
            }
            if (contains) {
                continue;
            } else {
                return true;
            }
        }
        return false;
    }

    synchronized public Request getNextRequest() {
        for (Request currentReq : _requests) {

            DataCollectorConnection dataSource = currentReq.getDataSource();
            boolean contains = false;
            int currentPort = dataSource.getPort();
            String currentHost = dataSource.getHost();
            for (Request currentActiveReq : _activeRequests) {
                DataCollectorConnection connection = currentActiveReq.getDataSource();
                if (currentPort == connection.getPort() && currentHost.equals(dataSource.getHost())) {
                    contains = true;
                    break;
                }
            }
            if (contains) {
                continue;
            } else {
                _activeRequests.add(currentReq);
                _requests.remove(currentReq);
                return currentReq;
            }
        }

        return null;
    }

    synchronized public int getRequestSize() {
        return _requests.size();
    }

    synchronized public static void removeActiveRequest(Request request) {
        _activeRequests.remove(request);
    }

    synchronized public int getNumberActiveRequests() {
        return _activeRequests.size();
    }

    synchronized public List<Request> getActiveRequests() {
        return _activeRequests;
    }
}
