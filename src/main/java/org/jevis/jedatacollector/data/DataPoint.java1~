/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.data;

import org.jevis.commons.JEVisTypes;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisType;
import org.jevis.commons.DatabaseHelper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Broder
 */
public class DataPoint {

//    private String _channelID;
//    private Long _onlineID;
//    private String _valueSpec;
//    private String _dataLoggerName;
    private JEVisObject _jevisDatapoint;
    private Long _datapointID;
    private JEVisObject _onlineData;
    private String _fileName;
    private String _mappingIdentifier;
    private String _target;
    private String _valueIdentifier;
    private String _dateFormat;
    private DateTime _lastReadout;
    private String _currentReadoutString;
    private Boolean _periodicallySampling;
    private DataPointDir _directory;

    public DateTime getLastReadout() {
        return _lastReadout;
    }

    public Boolean getPeriodicallySampling() {
        return _periodicallySampling;
    }

    public DataPoint(String channelID, String dataLoggerName, Long onlineID) {
//        _channelID = channelID;
//        _dataLoggerName = dataLoggerName;
//        _onlineID = onlineID;
    }

    public DataPoint(String filePath, String mappingIdentifier, String target, String valueIdentifier, DateTime lastReadout, Boolean periodicallySampling) {
        _fileName = filePath;
        _mappingIdentifier = mappingIdentifier;
        _target = target;
        _valueIdentifier = valueIdentifier;

        DateTimeFormatter fmt = DateTimeFormat.forPattern("ddMMyyyyHHmmss");

        _lastReadout = lastReadout;
        _currentReadoutString = new DateTime().toString(fmt);
        _periodicallySampling = periodicallySampling;
    }

    public DataPoint(JEVisObject dp) {
        _jevisDatapoint = dp;
        _datapointID = dp.getID();

        try {
            JEVisClass type = dp.getJEVisClass();
//            JEVisType channelIDType = type.getType(JEVisTypes.DataPoint.CHANNEL_ID);
//            JEVisType dataLoggerNameType = type.getType(JEVisTypes.DataPoint.DATA_LOGGER_NAME);
//            JEVisType onlineIDType = type.getType(JEVisTypes.DataPoint.ONLINE_ID);
//            JEVisType valueSpecType = type.getType(JEVisTypes.DataPoint.VALUE_SPEC);

            JEVisType filePathType = type.getType(JEVisTypes.DataPoint.SOURCE);
            JEVisType mappingIdentifierType = type.getType(JEVisTypes.DataPoint.MAPPING_IDENTIFIER);
            JEVisType targetType = type.getType(JEVisTypes.DataPoint.TARGET);
            JEVisType valueIdentifierType = type.getType(JEVisTypes.DataPoint.VALUE_IDENTIFIER);
            JEVisType dateFormatType = type.getType(JEVisTypes.DataPoint.DATE_FORMAT);
            JEVisType lastReadoutType = type.getType(JEVisTypes.DataPoint.LAST_READOUT);
            JEVisType periodicallySampling = type.getType(JEVisTypes.DataPoint.PERIODICALLY_SAMPLING);

            _fileName = DatabaseHelper.getObjectAsString(dp, filePathType);
            _mappingIdentifier = DatabaseHelper.getObjectAsString(dp, mappingIdentifierType);
            _target = DatabaseHelper.getObjectAsString(dp, targetType);
            _valueIdentifier = DatabaseHelper.getObjectAsString(dp, valueIdentifierType);
            _dateFormat = DatabaseHelper.getObjectAsString(dp, dateFormatType);

            DateTimeFormatter fmt = DateTimeFormat.forPattern("ddMMyyyyHHmmss");
            DateTimeFormatter fmtNew = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            String dateString = DatabaseHelper.getObjectAsString(dp, lastReadoutType);
            if (dateString == null) {
            dateString = "2000-01-01 00:00:00";
            }
            DateTime tmpDateNew = null;
            try {
                tmpDateNew = fmtNew.parseDateTime(dateString);
            }
            catch (Exception ex){
                tmpDateNew = fmt.parseDateTime(dateString);
            }
            _lastReadout = tmpDateNew;
            _currentReadoutString = new DateTime().toString(fmt);
            _periodicallySampling = DatabaseHelper.getObjectAsBoolean(dp, periodicallySampling);

//            _channelID = DatabaseHelper.getObjectAsString(dp, channelIDType);
//            _dataLoggerName = DatabaseHelper.getObjectAsString(dp, dataLoggerNameType);
//            _onlineID = DatabaseHelper.getObjectAsLong(dp, onlineIDType);
//            _valueSpec = DatabaseHelper.getObjectAsString(dp, valueSpecType);
//            _onlineData = dp.getDataSource().getObject(_onlineID);
        } catch (JEVisException ex) {
            Logger.getLogger(DataPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getCurrentReadoutString() {
        return _currentReadoutString;
    }

    public String getFileName() {
        return _fileName;
    }

    public String getMappingIdentifier() {
        return _mappingIdentifier;
    }

    public String getTarget() {
        return _target;
    }

    public String getValueIdentifier() {
        return _valueIdentifier;
    }

    public String getDateFormat() {
        return _dateFormat;
    }

//    public String getChannelID() {
//        return _channelID;
//    }
    public Long getDatapointId() {
        return _datapointID;
    }

    public JEVisObject getJEVisDatapoint() {
        return _jevisDatapoint;
    }

    public JEVisObject getJEVisOnlineData() {
        return _onlineData;
    }

//    public String getDataLoggerName() {
//        return _dataLoggerName;
//    }
//
//    public long getOnlineID() {
//        return _onlineID;
//    }
//
//    public String getValueSpec() {
//        return _valueSpec;
//    }
    public String getJEVisID() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setDirectory(DataPointDir dir) {
        _directory = dir;
    }

    public DataPointDir getDirectory() {
        return _directory;
    }

    public String getFilePath() {
        String filePath = _directory.getFolderPath();
        if (!_directory.containsCompressedFolder()) {
            filePath += _fileName;
        }
        return filePath;
    }
}
