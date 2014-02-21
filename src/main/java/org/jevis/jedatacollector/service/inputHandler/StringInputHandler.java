/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service.inputHandler;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 *
 * @author Broder
 */
public class StringInputHandler extends InputHandler {

    public StringInputHandler(List input) {
        super();
        System.out.println("--new Stringinputconvterter--");
        _rawInput = input;
        System.out.println("--RawInputsize--" + input.size());
        this.convertInput();
    }
    //input is List<List<String>>

    @Override
    public void convertInput() {
        System.out.println("--convertiere String input--");
        for (Object o : (List) _rawInput) {
            List tmp = (List) o;
            for (Object m : tmp) {
                String s = (String) m;
//            System.out.println("Value beim convert "+m);
                _inputStream.add(new ByteArrayInputStream(s.getBytes()));
            }
        }
        System.out.println("Inputstreamsize " + _inputStream.size());
    }
}
