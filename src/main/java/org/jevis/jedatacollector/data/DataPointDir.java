/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.data;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisType;
import org.jevis.commons.DatabaseHelper;
import org.jevis.commons.JEVisTypes;
import org.jevis.jedatacollector.Launcher;

/**
 *
 * @author bf
 */
public class DataPointDir {

    private String _folderName;
    private Boolean _compressed = false;
    private String _compressFormat;
    private List<DataPointDir> _parentDirs;
    private JEVisObject _jevisDir;

    public DataPointDir() {
        _parentDirs = new ArrayList<DataPointDir>();
    }

    public List<DataPointDir> getParentDirs() {
        return _parentDirs;
    }

    public void initialize(JEVisObject datapointDir) {
        try {
            _jevisDir = datapointDir;

            JEVisClass dirType = Launcher.getClient().getJEVisClass(JEVisTypes.DataPointDirectory.NAME);
            JEVisClass dirCompressType = Launcher.getClient().getJEVisClass(JEVisTypes.DataPointDirectory.DataPointDirectoryCompressed.NAME);
            
            if (_jevisDir.getJEVisClass().compareTo(dirCompressType)==0){
                _compressed = true;
                JEVisType archiveType = dirCompressType.getType(JEVisTypes.DataPointDirectory.DataPointDirectoryCompressed.ARCHIVE_FORMAT);
                _compressFormat = DatabaseHelper.getObjectAsString(datapointDir, archiveType);
            }
            
            JEVisType folderType = dirType.getType(JEVisTypes.DataPointDirectory.FOLDER);
           _folderName = DatabaseHelper.getObjectAsString(datapointDir, folderType);
        } catch (JEVisException ex) {
            Logger.getLogger(DataPointDir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getFolderName() {
        return _folderName;
    }

    public Boolean getCompressed() {
        return _compressed;
    }

    public String getCompressFormat() {
        return _compressFormat;
    }

    public void setPreviousDirs(List<DataPointDir> datapointDirParents) {
        for (DataPointDir dir : datapointDirParents) {
            _parentDirs.add(dir);
        }
    }

    public JEVisObject getJevisObject() {
        return _jevisDir;
    }
}
