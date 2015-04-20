//package org.jevis.jedatacollector.connection.FTP;
//
///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.net.UnknownHostException;
//
//import org.apache.commons.net.PrintCommandListener;
//import org.apache.commons.net.ftp.FTPClient;
//import org.apache.commons.net.ftp.FTPConnectionClosedException;
//import org.apache.commons.net.ftp.FTPFile;
//
///**
// * This is an example program demonstrating how to use the FTPClient class. This
// * program connects to an FTP server and retrieves the specified file. If the -s
// * flag is used, it stores the local file at the FTP server. Just so you can see
// * what's happening, all reply strings are printed. If the -b flag is used, a
// * binary transfer is assumed (default is ASCII). See below for further options.
// */
//public final class FTPClientExample
//{
//
//    public static void main(String[] args) throws UnknownHostException
//    {
//        final FTPClient ftp;
//        ftp = new FTPClient();
//        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
//
//        try
//        {
//            ftp.connect("192.168.2.204");
//            ftp.login("tes", "kee6Soop");
////            ftp.enterLocalPassiveMode();
//            ftp.setUseEPSVwithIPv4(false);
//
//            for (FTPFile f : ftp.listFiles("/"))
//            {
//                System.out.println(f.getRawListing());
//                System.out.println(f.toFormattedString());
//            }
//        } catch (FTPConnectionClosedException e)
//        {
//            System.err.println("Server closed connection.");
//            e.printStackTrace();
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        } finally
//        {
//            if (ftp.isConnected())
//            {
//                try
//                {
//                    ftp.disconnect();
//                } catch (IOException f)
//                {
//                    // do nothing
//                }
//            }
//        }
//
//    } // end main
//}
