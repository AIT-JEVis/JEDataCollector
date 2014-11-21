/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.IntegrationTestsNew;

import org.apache.commons.net.ftp.FTPFile;

/**
 *
 * @author bf
 */
public class FakeFTPFile extends FTPFile {

    private String _name;
    
    public FakeFTPFile(String name){
        _name = name;
    }
    
    @Override
    public String getName() {
        return _name;
    }
}
