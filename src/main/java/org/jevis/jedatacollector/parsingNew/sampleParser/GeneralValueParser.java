/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsingNew.sampleParser;

/**
 *
 * @author broder
 */
public interface GeneralValueParser extends GeneralParser{

    public double getValue();

    public String getThousandSeperator();

    public String getDecimalSeperator();
}
