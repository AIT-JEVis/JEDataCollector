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
    private boolean _containsCompressedFolder;
    private String _folderPathToComp;
    private String _folderPathFromComp;
    private String _folderPath = null;

    public DataPointDir() {
        _parentDirs = new ArrayList<DataPointDir>();
    }

    public DataPointDir(String folderName, boolean compressed, String compressFormat, List<DataPointDir> parentDirs, JEVisObject jevisDir) {
        _folderName = folderName;
        _compressed = compressed;
        _compressFormat = compressFormat;
        _parentDirs = parentDirs;
        _jevisDir = jevisDir;
        initPathes();
    }

    public List<DataPointDir> getParentDirs() {
        return _parentDirs;
    }

    public void initialize(JEVisObject datapointDir) {
        try {
            _jevisDir = datapointDir;

            JEVisClass dirType = Launcher.getClient().getJEVisClass(JEVisTypes.DataPointDirectory.NAME);
            JEVisClass dirCompressType = Launcher.getClient().getJEVisClass(JEVisTypes.DataPointDirectory.DataPointDirectoryCompressed.NAME);

            if (_jevisDir.getJEVisClass().equals(dirCompressType)) {
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
    
    public void setFolderName(String folderName){
        _folderName = folderName;
    }

    public Boolean isCompressed() {
        return _compressed;
    }

    public String getCompressFormat() {
        return _compressFormat;
    }

    public void setPreviousDirs(List<DataPointDir> datapointDirParents) {
        for (DataPointDir dir : datapointDirParents) {
            _parentDirs.add(dir);
        }
        initPathes();
    }

    public String getFolderPath() {
        return _folderPath;
    }

    public String getFolderPathToComp() {
        return _folderPathToComp;
    }

    public String getFolderPathFromComp() {
        if (_folderPathFromComp!=null){
            return _folderPathFromComp;
        }
        return "";
    }

    public Boolean containsCompressedFolder() {
        return _containsCompressedFolder;
    }

    public JEVisObject getJevisObject() {
        return _jevisDir;
    }

    private void initPathes() {
        String folderPathFromComp = null;
        String folderPath = "";
        String currentDir = null;
        _containsCompressedFolder = false;
        List<DataPointDir> allDirs = new ArrayList<DataPointDir>();
        if (_parentDirs != null) {
            allDirs.addAll(_parentDirs);
        }
        allDirs.add(this);

        for (DataPointDir dir : allDirs) {
            currentDir = dir.getFolderName();
            if (currentDir == null){
                continue;
            }
            if (folderPath.equals("")) {
                folderPath += currentDir;
            } else {
                if (folderPath.endsWith("/") && currentDir.startsWith("/")) {
                    folderPath += currentDir.substring(1, currentDir.length());
                } else if (!folderPath.endsWith("/") && !currentDir.startsWith("/")) {
                    folderPath += "/" + currentDir;
                }
            }
            if (_containsCompressedFolder) {
                if (folderPathFromComp == null) {
                    folderPathFromComp = currentDir;
                } else {
                    if (folderPathFromComp.endsWith("/") && currentDir.startsWith("/")) {
                        folderPathFromComp += currentDir.substring(1, currentDir.length());
                    } else if (!folderPathFromComp.endsWith("/") && !currentDir.startsWith("/")) {
                        folderPathFromComp += "/" + currentDir;
                    }
                }
            }
            if (dir.isCompressed() && _folderPath == null) {
                _containsCompressedFolder = true;
                _folderPath = folderPath;
            }
        }
        if (_folderPath == null && !_containsCompressedFolder) {
            _folderPath = folderPath;
//            if (!_folderPath.endsWith("/")){
//                _folderPath += "/";
//            }
        }
//        if (folderPathFromComp != null && !folderPathFromComp.endsWith("/")) {
//            folderPathFromComp += "/";
//        }
        if (folderPathFromComp != null && folderPathFromComp.startsWith("/")) {
            folderPathFromComp = folderPathFromComp.substring(1, folderPathFromComp.length());
        }
        _folderPathFromComp = folderPathFromComp;
    }
}
