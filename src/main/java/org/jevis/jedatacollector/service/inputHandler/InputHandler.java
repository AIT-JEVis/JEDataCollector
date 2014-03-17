/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service.inputHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Broder
 */
public abstract class InputHandler implements Iterable<Object> {

    protected Object _rawInput;
    protected List<InputStream> _inputStream;
    protected String[] _tmpInput;

    public InputHandler(Object rawInput) {
        _inputStream = new ArrayList<InputStream>();
        _rawInput = rawInput;
    }

    public void setInput(Object input) {
        _rawInput = input;
    }

    public abstract void convertInput();

    @Override
    public Iterator iterator() {
        return _inputStream.iterator();
    }

    public String[] getStringArrayInput() {
        List<String> stringInput = new ArrayList<String>();
        for (InputStream s : _inputStream) {
            try {
                //            String inputStreamString = new Scanner(s, "UTF-8").useDelimiter("\\A").next();
                String inputStreamString = IOUtils.toString(s, "UTF-8");
                stringInput.add(inputStreamString);
            } catch (IOException ex) {
                Logger.getLogger(InputHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        String[] inputArray = new String[stringInput.size()];
        return stringInput.toArray(inputArray);
    }

    public void setTmpInput(String[] input) {
        _tmpInput = input;
    }

    public String[] getTmpInput() {
        return _tmpInput;
    }

    public void setInputStream(List<InputStream> input) {
        _inputStream = input;
    }

    public String getStringInput() {
        StringBuilder buffer = new StringBuilder();
        for (InputStream s : _inputStream) {
            String inputStreamString = new Scanner(s, "UTF-8").useDelimiter("\\A").next();
            buffer.append(inputStreamString);
        }
        return buffer.toString();
    }
}
