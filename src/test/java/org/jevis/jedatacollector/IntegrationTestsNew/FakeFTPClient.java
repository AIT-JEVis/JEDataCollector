/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.IntegrationTestsNew;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;

/**
 *
 * @author bf
 */
public class FakeFTPClient extends FTPClient {

    FileSystem _fileSystem;

    @Override
    public void connect(String hostname, int port) throws SocketException, IOException {
        System.out.println("connect to fake client");
    }

    @Override
    public boolean retrieveFile(String remote, OutputStream local) throws IOException {
        FileEntry entry = (FileEntry) _fileSystem.getEntry(remote);
        InputStream createInputStream = entry.createInputStream();
        try {
            String theString = IOUtils.toString(createInputStream, "UTF-8");
            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = createInputStream.read(data, 0, data.length)) != -1) {
                local.write(data, 0, nRead);
            }

            local.flush();
            System.out.println(theString);
        } catch (IOException ex) {
            Logger.getLogger(Test_FTP_sample_Request.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public void setFileSystem(FileSystem fileSystem) {
        _fileSystem = fileSystem;
    }

    @Override
    public FTPFile[] listFiles(String pathname) throws IOException {
        List<String> listFiles = _fileSystem.listFiles(pathname);
        FTPFile[] fakeFiles = new FTPFile[listFiles.size()];
        for (int i = 0; i < listFiles.size(); i++) {
            fakeFiles[i] = new FakeFTPFile(listFiles.get(i));
        }
        return fakeFiles;
    }
}
