/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service.inputHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Broder
 */
public abstract class InputHandler implements Iterable<Object> {

    protected Object _rawInput;
    protected List<InputStream> _inputStream;
    protected String[] _csvInput;
    private String[] _stringArrayOutput;
    private boolean _stringArrayOutputParsed;
    private String _stringOutput;
    private boolean _stringOutputParsed;
    private Node _xmlInput;
    protected List<Document> _document;

    public InputHandler(Object rawInput) {
        _inputStream = new ArrayList<InputStream>();
        _rawInput = rawInput;
        _document = new ArrayList<Document>();
    }

    public void setInput(Object input) {
        _rawInput = input;
    }

    public abstract void convertInput();

    @Override
    public Iterator iterator() {
        return _inputStream.iterator();
    }

    public Object getRawInput() {
        return _rawInput;
    }

    public String[] getStringArrayInput() {
        if (!_stringArrayOutputParsed) {
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
            _stringArrayOutput = stringInput.toArray(inputArray);
            _stringArrayOutputParsed = true;
        }
        return _stringArrayOutput;
    }

    public void setCSVInput(String[] input) {
        _csvInput = input;
    }

    public String[] getCSVInput() {
        return _csvInput;
    }

    public void setXMLInput(Node input) {
        _xmlInput = input;
    }

    public Node getXMLInput() {
        return _xmlInput;
    }

    public void setInputStream(List<InputStream> input) {
        _inputStream = input;
    }

    public String getStringInput() {
        if (!_stringOutputParsed) {

            StringBuilder buffer = new StringBuilder();
            for (InputStream s : _inputStream) {
                String inputStreamString = new Scanner(s, "UTF-8").useDelimiter("\\A").next();
                buffer.append(inputStreamString);
            }
            _stringOutput = buffer.toString();
            _stringOutputParsed = true;
        }
        return _stringOutput;
    }

    public List<Document> getDocuments() {
        if (_document.isEmpty()) {
            try {
                String stringInput = getStringInput();
                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                domFactory.setNamespaceAware(true); // never forget this!
                DocumentBuilder builder = domFactory.newDocumentBuilder();
                _document.add(builder.parse(new InputSource(new StringReader(stringInput))));
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(InputHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(InputHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(InputHandler.class.getName()).log(Level.SEVERE, null, ex);
            }


        }
        return _document;
    }
}
