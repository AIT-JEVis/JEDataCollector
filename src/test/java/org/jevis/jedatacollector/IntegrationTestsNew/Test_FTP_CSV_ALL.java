/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.IntegrationTestsNew;

import java.util.ArrayList;
import java.util.List;
import org.jevis.commons.parsing.DataCollectorParser;
import org.jevis.commons.parsing.csvParsing.CSVParsing;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.jedatacollector.DataCollector;
import org.jevis.jedatacollector.Request;
import org.jevis.jedatacollector.RequestGenerator;
import org.jevis.jedatacollector.connection.DataCollectorConnection;
import org.jevis.jedatacollector.data.DataPoint;
import org.junit.Test;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

/**
 *
 * @author bf
 */
public class Test_FTP_CSV_ALL {

    @Test
    public void test_all() throws Exception {
        //hier der neue konstruktor
        DataCollectorConnection ftpConnection = new FakeFTPConnection("/Jahr_${D:yyyy}/Monat_${D:MM}/Tagesdaten_${D:dd HH:mm:ss}.csv", false);

        //hier neuer konstruktor
        DataPoint datapoint = new DataPoint(null, null, null);

        //unix and windowsfile system possible
        FileSystem fileSystem = setupFilesystem();

        FakeFTPClient fakeClient = new FakeFTPClient();
        fakeClient.setFileSystem(fileSystem);
        ((FakeFTPConnection) ftpConnection).setClient(fakeClient);


        DataCollectorParser parser = new CSVParsing();

        //create JEVis Request
        Request request = null;

        DataCollector dataCollector = new DataCollector(request);
        dataCollector.run();
        //check the results here
        InputHandler inputHandler = dataCollector.getInputHandler();
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    private FileSystem setupFilesystem() {
        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/Jahr_2013"));
        fileSystem.add(new DirectoryEntry("/Jahr_2014"));
        fileSystem.add(new DirectoryEntry("/Jahr_2015"));


        //setup month folder
        String jan = "/Jahr_2014/Monat_01";
        String feb = "/Jahr_2014/Monat_02";
        String march = "/Jahr_2014/Monat_03";
        String april = "/Jahr_2014/Monat_04";
        String mai = "/Jahr_2014/Monat_05";
        String june = "/Jahr_2014/Monat_06";
        String juli = "/Jahr_2014/Monat_07";
        String aug = "/Jahr_2014/Monat_08";
        String sep = "/Jahr_2014/Monat_09";
        String oct = "/Jahr_2014/Monat_10";
        String nov = "/Jahr_2014/Monat_11";
        String dec = "/Jahr_2014/Monat_12";

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
        fileSystem.add(new DirectoryEntry(dec));

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
        monthList_31.add(dec);

        for (String month : monthList_28) {
            for (int i = 0; i <= 28; i++) {
                String dd = String.valueOf(i);
                if (dd.length() == 1) {
                    dd = "0" + String.valueOf(i);
                }
                fileSystem.add(new FileEntry(month + "/tagesdaten_" + dd + " 00:00:00"));
            }
        }

        for (String month : monthList_30) {
            for (int i = 0; i <= 30; i++) {
                String dd = String.valueOf(i);
                if (dd.length() == 1) {
                    dd = "0" + String.valueOf(i);
                }
                fileSystem.add(new FileEntry(month + "/tagesdaten_" + dd + " 00:00:00"));
            }
        }

        for (String month : monthList_31) {
            for (int i = 0; i <= 31; i++) {
                String dd = String.valueOf(i);
                if (dd.length() == 1) {
                    dd = "0" + String.valueOf(i);
                }
                fileSystem.add(new FileEntry(month + "/tagesdaten_" + dd + " 00:00:00"));
            }
        }
        return fileSystem;
    }
}
