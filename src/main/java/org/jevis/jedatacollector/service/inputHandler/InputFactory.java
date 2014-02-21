/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.service.inputHandler;

import java.io.File;
import java.util.List;

/**
 *
 * @author Broder
 */
public class InputFactory {

    public static InputHandler getInputConverter(Object input) {
        if (input instanceof List) {
            List tmp = (List) input;
            if (tmp.isEmpty()) {
                return null;
            }
            Object o = tmp.get(0);
            if (o instanceof List) {
                return new StringInputHandler((List) input);
            }
            if (o instanceof Object[]) {
                return new ArrayInputHandler((Object[]) input);
            }
            return null;
        }
        if (input instanceof File) {
            return new FileInputHandler((File)input);
        }
        return null;


    }
}
