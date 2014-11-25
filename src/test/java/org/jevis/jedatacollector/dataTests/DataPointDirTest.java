/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.dataTests;

import java.util.ArrayList;
import java.util.List;
import org.jevis.api.JEVisObject;
import org.jevis.jedatacollector.Launcher;
import org.jevis.jedatacollector.data.DataPointDir;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author bf
 */
public class DataPointDirTest {

    @Test
    public void test_data_point_dir_initialization() {
        Launcher launch = new Launcher();
        launch.setJevisClient(new JEVisFakeClient());
        List<JEVisObject> children = new ArrayList<JEVisObject>();
        JEVisObject root = new JEVisFakeObject("/root/");
        JEVisObject child1 = new JEVisFakeObject("child1/");
        JEVisObject child2 = new JEVisFakeObject("child2/");
        JEVisObject leaf1 = new JEVisFakeObject("leaf1");
        JEVisObject leaf2 = new JEVisFakeObject("leaf2");
        JEVisObject leaf3 = new JEVisFakeObject("leaf3");
        JEVisObject leaf4 = new JEVisFakeObject("leaf4");
        JEVisObject child3 = new JEVisFakeObject("child3/");
        JEVisObject leaf5 = new JEVisFakeObject("leaf5");
        JEVisObject leaf6 = new JEVisFakeObject("leaf6");

        ((JEVisFakeObject) root).addChild(child1);
        ((JEVisFakeObject) root).addChild(leaf1);
        ((JEVisFakeObject) root).addChild(child2);

        ((JEVisFakeObject) child1).addChild(child3);
        ((JEVisFakeObject) child1).addChild(leaf2);

        ((JEVisFakeObject) child2).addChild(leaf3);
        ((JEVisFakeObject) child2).addChild(leaf4);

        ((JEVisFakeObject) child3).addChild(leaf5);
        ((JEVisFakeObject) child3).addChild(leaf6);

        children.add(root);
        List<DataPointDir> initializeDatapointDir = launch.initializeDatapointDir(children);
        List<String> pathes = new ArrayList<String>();
        for (DataPointDir dir : initializeDatapointDir) {
            List<DataPointDir> parentDirs = dir.getParentDirs();
            String path = "";
            for (DataPointDir parentDir : parentDirs) {
                path += parentDir.getJevisObject().getName();
            }
            path += dir.getJevisObject().getName();
            pathes.add(path);
        }

        List<String> targetPathes = new ArrayList<String>();
        targetPathes.add("/root/child1/child3/leaf5");
        targetPathes.add("/root/child1/child3/leaf6");
        targetPathes.add("/root/child1/leaf2");
        targetPathes.add("/root/leaf1");
        targetPathes.add("/root/child2/leaf3");
        targetPathes.add("/root/child2/leaf4");

        for (String path : pathes) {
            boolean found = false;
            for (String targetPath : targetPathes) {
                if (path.equals(targetPath)) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(found);
        }
    }
}
