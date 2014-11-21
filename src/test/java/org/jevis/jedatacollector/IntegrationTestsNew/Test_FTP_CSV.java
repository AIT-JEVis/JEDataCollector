/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.IntegrationTestsNew;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class Test_FTP_CSV {

    @Test
    public void test_send_sample_request_and_parse() throws Exception {
//        //hier der neue konstruktor
//        DataCollectorConnection ftpConnection = new FakeFTPConnection("/Jahresdaten_${D:yyyy}/Monatsdaten_${D:MM}/Tagesdaten_${D:dd HH:mm:ss}.csv", false);
//
//        //hier neuer konstruktor
//        DataPoint datapoint = new DataPoint(null, null, null);

        //unix and windowsfile system possible
        FileSystem fileSystem = setupFilesystem();

//        FakeFTPClient fakeClient = new FakeFTPClient();
//        fakeClient.setFileSystem(fileSystem);
//        ((FakeFTPConnection) ftpConnection).setClient(fakeClient);
//
//        DataCollectorParser parser = new CSVParsing();
//
//        Request request = RequestGenerator.createConnectionParsingRequest(ftpConnection, null);
//
//        DataCollector dataCollector = new DataCollector(request);
//        dataCollector.run();
//        //check the results here
//        InputHandler inputHandler = dataCollector.getInputHandler();
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
//        monthList_31.add(dec);

        
        //setup day folder with data
        for (String month : monthList_28) {
            for (int i = 1; i <= 28; i++) {
                String dd = String.valueOf(i);
                if (dd.length() == 1) {
                    dd = "0" + String.valueOf(i);
                }
                String filePath = month + "/Tagesdaten_" + dd + "_00:00:00.csv";
                String fileInputText = null;
                try {
                    BufferedReader br = new BufferedReader(new FileReader(filePath.substring(1, filePath.length())));
                    try {
                        StringBuilder sb = new StringBuilder();
                        String line = br.readLine();

                        while (line != null) {
                            sb.append(line);
                            sb.append(System.lineSeparator());
                            line = br.readLine();
                        }
                        fileInputText = sb.toString();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Test_FTP_CSV.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Test_FTP_CSV.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Test_FTP_CSV.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(fileInputText);
                fileSystem.add(new FileEntry(filePath, fileInputText));
            }
        }

        for (String month : monthList_30) {
            for (int i = 1; i <= 30; i++) {
                String dd = String.valueOf(i);
                if (dd.length() == 1) {
                    dd = "0" + String.valueOf(i);
                }
                String filePath = month + "/Tagesdaten_" + dd + "_00:00:00.csv";
                String fileInputText = null;
                try {
                    BufferedReader br = new BufferedReader(new FileReader(filePath.substring(1, filePath.length())));
                    try {
                        StringBuilder sb = new StringBuilder();
                        String line = br.readLine();

                        while (line != null) {
                            sb.append(line);
                            sb.append(System.lineSeparator());
                            line = br.readLine();
                        }
                        fileInputText = sb.toString();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Test_FTP_CSV.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Test_FTP_CSV.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Test_FTP_CSV.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(fileInputText);
                fileSystem.add(new FileEntry(filePath, fileInputText));
            }
        }

        for (String month : monthList_31) {
            for (int i = 1; i <= 31; i++) {
                String dd = String.valueOf(i);
                if (dd.length() == 1) {
                    dd = "0" + String.valueOf(i);
                }
                String filePath = month + "/Tagesdaten_" + dd + "_00:00:00.csv";
                String fileInputText = null;
                try {
                    BufferedReader br = new BufferedReader(new FileReader(filePath.substring(1, filePath.length())));
                    try {
                        StringBuilder sb = new StringBuilder();
                        String line = br.readLine();

                        while (line != null) {
                            sb.append(line);
                            sb.append(System.lineSeparator());
                            line = br.readLine();
                        }
                        fileInputText = sb.toString();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Test_FTP_CSV.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Test_FTP_CSV.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Test_FTP_CSV.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(fileInputText);
                fileSystem.add(new FileEntry(filePath, fileInputText));
            }
        }
        return fileSystem;
    }
}
