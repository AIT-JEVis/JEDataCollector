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
import org.jevis.jedatacollector.service.inputHandler.InputHandler;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author bf
 */
public class RequestGenerator {

//    public static List<Request> createJEVisRequests(Data data) {
//        List<Request> requests = new ArrayList<Request>();
//
//        DatacollectorConnection connection = data.getConnection();
//        DataCollectorParser parsing = data.getParsing();
//        Equipment equipment = data.getEquipment();
//        List<NewDataPoint> datapoints = data.getDatapoints();
//
//        if (equipment.isSingleConnection()) {
//            for (NewDataPoint dp : datapoints) {
//                Request request = new DefaultRequest();
//                request.setNeedConnection(true);
//                request.setConnection(connection);
//                request.setParser(parsing);
//                request.setEquipment(equipment);
//                request.setSpecificDatapoint(dp);
////                request.setData(data);
//                request.setNeedImport(true);
//                request.setNeedParsing(true);
//                requests.add(request);
//            }
//        } else {
//            Request request = new DefaultRequest();
//            request.setNeedConnection(true);
//            request.setConnection(connection);
//            request.setParser(parsing);
//            request.setEquipment(equipment);
//            request.setSpecificDatapoint(null);
//            request.setNeedImport(true);
//            request.setNeedParsing(true);
//            requests.add(request);
//        }
//
//        return requests;
//    }
    public static Request createOnlyParsingRequest(DataCollectorParser fileParser, InputHandler input) {
        Request req = new DefaultRequest();
        req.setNeedConnection(false);
        req.setNeedImport(false);
        req.setNeedParsing(true);
        req.setParser(null);
        req.setParser(fileParser);
        req.setInputHandler(input);
        return req;
    }

    public static Request createConnectionParsingRequest(DatacollectorConnection connection, DataCollectorParser parsing) {
        Request request = new DefaultRequest();
        request.setNeedConnection(true);
        request.setConnection(connection);
        request.setParser(parsing);
        request.setNeedImport(false);
        request.setNeedParsing(true);
        return request;
    }

    public static Request createCLIRequest(DatacollectorConnection connection, DataCollectorParser parsing, NewDataPoint dataPoint, DateTime from, DateTime until, DateTimeZone timeZone) {
        Request request = new DefaultRequest();
        request.setNeedConnection(true);
        request.setConnection(connection);
        request.setFrom(from);
        request.setUntil(until);
        request.setParser(parsing);
        request.setNeedImport(true);
//        request.setSpecificDatapoint(dataPoint);
        List<NewDataPoint> dataPoints = new ArrayList<NewDataPoint>();
        dataPoints.add(dataPoint);
        request.setDataPoints(dataPoints);
        request.setNeedParsing(true);
        request.setTimeZone(timeZone);
        return request;
    }

    public static Request createConnectionRequestWithTimeperiod(DatacollectorConnection connection, NewDataPoint datapoint, DateTime from, DateTime until) {
        Request request = new DefaultRequest();
        request.setNeedConnection(true);
        request.setConnection(connection);
        request.setNeedImport(false);
        request.setNeedParsing(false);
        request.setFrom(from);
        request.setUntil(until);
        ArrayList<NewDataPoint> datapoints = new ArrayList<NewDataPoint>();
        datapoints.add(datapoint);
        request.setDataPoints(datapoints);
//        request.setSpecificDatapoint(datapoint);
        return request;
    }

    static Request createJEVisRequest(Data data) {
        DatacollectorConnection connection = data.getConnection();
        DataCollectorParser parsing = data.getParsing();
        Equipment equipment = data.getEquipment();
        List<NewDataPoint> datapoints = data.getDatapoints();

        Request request = new DefaultRequest();
        request.setNeedConnection(true);
        request.setConnection(connection);
        request.setParser(parsing);
        request.setEquipment(equipment);
        request.setDataPoints(datapoints);
//            request.setSpecificDatapoint(dp);
//                request.setData(data);
        request.setNeedImport(true);
        request.setNeedParsing(true);
        request.setTimeZone(equipment.getTimezone());
        return request;
    }
}
