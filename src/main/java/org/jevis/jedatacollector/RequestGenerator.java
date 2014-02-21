/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector;

import java.util.ArrayList;
import java.util.List;
import org.jevis.jedatacollector.connection.DatacollectorConnection;
import org.jevis.jedatacollector.data.Data;
import org.jevis.jedatacollector.data.Equipment;
import org.jevis.jedatacollector.data.NewDataPoint;
import org.jevis.jedatacollector.parsingNew.DataCollectorParser;

/**
 *
 * @author bf
 */
public class RequestGenerator {

    public static List<Request> createJEVisRequests(Data data) {
        List<Request> requests = new ArrayList<Request>();

        DatacollectorConnection connection = data.getConnection();
        DataCollectorParser parsing = data.getParsing();
        Equipment equipment = data.getEquipment();
        List<NewDataPoint> datapoints = data.getDatapoints();

        if (equipment.isSingleConnection()) {
            for (NewDataPoint dp : datapoints) {
                Request request = new DefaultRequest();
                request.setNeedConnection(true);
                request.setConnection(connection);
                request.setParser(parsing);
                request.setEquipment(equipment);
                request.setSpecificDatapoint(dp);
                request.setData(data);
                request.setNeedImport(true);
                requests.add(request);
            }
        } else {
            Request request = new DefaultRequest();
            request.setNeedConnection(true);
            request.setConnection(connection);
            request.setParser(parsing);
            request.setEquipment(equipment);
            request.setSpecificDatapoint(null);
            request.setNeedImport(true);
            requests.add(request);
        }

        return requests;
    }

    public static Request createOnlyParsingRequest() {
        Request req = new DefaultRequest();
        req.setNeedConnection(false);
        req.setNeedImport(false);
        req.setParser(null);
        return req;
    }

    public static Request createConnectionParsingRequest(DatacollectorConnection connection, DataCollectorParser parsing) {
        Request request = new DefaultRequest();
        request.setNeedConnection(true);
        request.setConnection(connection);
        request.setParser(parsing);
        request.setNeedImport(true);
        return request;
    }
}
