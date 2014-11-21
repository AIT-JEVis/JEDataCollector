/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.IntegrationTestsNew;

import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.jedatacollector.data.DataPoint;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

/**
 * This file should test the JEDataCollector with a ftp device
 *
 * @author bf
 */
public class Test_FTP_sample_Request {

    @Test
    public void test_send_sample_request() throws Exception {
        FakeFTPConnection ftpConnection = new FakeFTPConnection(false);
        String lastReadoutText = "01112014000000";
        DateTimeFormatter forPattern = DateTimeFormat.forPattern("ddMMyyyyHHmmss");
        DateTime lastReadout = forPattern.parseDateTime(lastReadoutText);
        DataPoint datapoint = new DataPoint("/Daten/Jahresdaten_${D:yyyy}/Monatsdaten_${D:MM}/Tagesdaten_${D:dd_HH:mm:ss}.csv", null, null, "1", lastReadout, true);


        //unix and windowsfile system possible
        FileSystem fileSystem = setupFilesystem();

        FakeFTPClient fakeClient = new FakeFTPClient();
        fakeClient.setFileSystem(fileSystem);
        ftpConnection.setClient(fakeClient);
        ftpConnection.connect();
        //check the input of the sampleRequest
        List<InputHandler> inputHandlers = ftpConnection.sendSampleRequest(datapoint, null, null);
        //should contain all files from 2.-30. November
        for (InputHandler input : inputHandlers) {
            System.out.println(input.getFilePath());
        }
        Assert.assertTrue(inputHandlers.size() == 29);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    private FileSystem setupFilesystem() {
        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/Daten/Jahresdaten_2012"));
        fileSystem.add(new DirectoryEntry("/Daten/Jahresdaten_2013"));
        fileSystem.add(new DirectoryEntry("/Daten/Jahresdaten_2014"));

        //setup month folder
        String jan = "/Daten/Jahresdaten_2014/Monatsdaten_01";
        String feb = "/Daten/Jahresdaten_2014/Monatsdaten_02";
        String march = "/Daten/Jahresdaten_2014/Monatsdaten_03";
        String april = "/Daten/Jahresdaten_2014/Monatsdaten_04";
        String mai = "/Daten/Jahresdaten_2014/Monatsdaten_05";
        String june = "/Daten/Jahresdaten_2014/Monatsdaten_06";
        String juli = "/Daten/Jahresdaten_2014/Monatsdaten_07";
        String aug = "/Daten/Jahresdaten_2014/Monatsdaten_08";
        String sep = "/Daten/Jahresdaten_2014/Monatsdaten_09";
        String oct = "/Daten/Jahresdaten_2014/Monatsdaten_10";
        String nov = "/Daten/Jahresdaten_2014/Monatsdaten_11";
        String dec = "/Daten/Jahresdaten_2014/Monatsdaten_12";

        fileSystem.add(new DirectoryEntry(jan));
        fileSystem.add(new DirectoryEntry(feb));
        fileSystem.add(new DirectoryEntry(march));
        fileSystem.add(new DirectoryEntry(april));
        fileSystem.add(new DirectoryEntry(mai));
        fileSystem.add(new DirectoryEntry(june));
        fileSystem.add(new DirectoryEntry(juli));
        fileSystem.add(new DirectoryEntry(aug));
        fileSystem.add(new DirectoryEntry(sep));
        fileSystem.add(new DirectoryEntry(oct));
        fileSystem.add(new DirectoryEntry(nov));
//        fileSystem.add(new DirectoryEntry(dec));

        List<String> monthList_28 = new ArrayList<String>();
        List<String> monthList_30 = new ArrayList<String>();
        List<String> monthList_31 = new ArrayList<String>();
        monthList_31.add(jan);
        monthList_28.add(feb);
        monthList_31.add(march);
        monthList_30.add(april);
        monthList_31.add(mai);
        monthList_30.add(june);
        monthList_31.add(juli);
        monthList_31.add(aug);
        monthList_30.add(sep);
        monthList_31.add(oct);
        monthList_30.add(nov);
//        monthList_31.add(dec);


        //setup day files
        for (String month : monthList_28) {
            for (int i = 1; i <= 28; i++) {
                String dd = String.valueOf(i);
                if (dd.length() == 1) {
                    dd = "0" + String.valueOf(i);
                }
                fileSystem.add(new FileEntry(month + "/Tagesdaten_" + dd + "_00:00:00.csv"));
            }
        }

        for (String month : monthList_30) {
            for (int i = 1; i <= 30; i++) {
                String dd = String.valueOf(i);
                if (dd.length() == 1) {
                    dd = "0" + String.valueOf(i);
                }
                fileSystem.add(new FileEntry(month + "/Tagesdaten_" + dd + "_00:00:00.csv"));
            }
        }

        for (String month : monthList_31) {
            for (int i = 1; i <= 31; i++) {
                String dd = String.valueOf(i);
                if (dd.length() == 1) {
                    dd = "0" + String.valueOf(i);
                }
                fileSystem.add(new FileEntry(month + "/Tagesdaten_" + dd + "_00:00:00.csv"));
            }
        }
        return fileSystem;
    }
}
