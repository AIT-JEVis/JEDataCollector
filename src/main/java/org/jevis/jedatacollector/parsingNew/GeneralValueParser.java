/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew;

/**
 *
 * @author broder
 */
public interface GeneralValueParser extends GeneralParser {

    public Double getValue();

    public String getThousandSeperator();

    public String getDecimalSeperator();

    public boolean isValueValid();
}
