/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.exception;

/**
 *
 * @author max
 */
public class FetchingException extends Exception
{

    private long _typeID;
    private boolean _todo;
    protected String _msg;
    private boolean _createAlarm;

    public FetchingException(Long ID, FetchingExceptionType fet)
    {
        if (ID != null)
        {
            _msg = fet.getMessage().replaceAll("ID", ID.toString());
        } else
        {
            _msg = fet.getMessage();
        }

        _todo = fet.hasTodo();
        _typeID = fet.getTypeID();
        _createAlarm = fet.createAlarm();
    }

    public String getMsg()
    {
        return _msg;
    }

    public boolean hasTodo()
    {
        return _todo;
    }

    public long getTypeID()
    {
        return _typeID;
    }
    
    public boolean createAlarm()
    {
        return _createAlarm;
    }
}
