/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jedatacollector.parsing.CSV;

/**
 *
 * @author max
 */
public class SinglePointCSV extends CSVSingleLineEntry
{
//    private String _channelName;
//    private String _loggerName;
//    
//    public SinglePointCSV(String channel, String logger)
//    {
//        _channelName = channel;
//        _loggerName = logger;
//    }
    public SinglePointCSV(){}

    public SinglePointCSV(String dateFormat, String timeFormat, int indexDate,
            Integer indexTime, int indexValue, String seperatorColumn, String sepetatorDecimal, String seperatorThousand, String enclosedBy, int ignoreFirstNLines)
    {
        _dateFormat = dateFormat;
        _timeFormat = timeFormat;
        _indexDate = indexDate;
        _indexTime = indexTime;
        _indexValue = indexValue;
        _seperatorColumn = seperatorColumn;
        _seperatorDecimal = sepetatorDecimal;
        _seperatorThousand = seperatorThousand;
        _enclosedBy = enclosedBy;
        _ignoreFirstNLines = ignoreFirstNLines;
    }

//    @Override
//    protected String getChannel(String line)
//    {
//        return dp.getChannelID();
//    }
//
//    @Override
//    protected String getLogger(String line)
//    {
//        return dp.getDataLoggerName();
//    }
//    public static void main(String[] args)
//    {
//        JevLoginHandler.createDirectLogin("Andrey Temichev", "kuli", "http://alpha.openjevis.org/axis2/services/JEWebService");
//        
//        IDataNode node = NodeManager.getInstance().getRegistryNode(12140L);
//        
//        TimeSet timeset = new TimeSet(node.getMinTS(), node.getMaxTS()); 
//         System.out.println("Node has Sample from: " + node.getMinTS());
//         System.out.println("Node has Sample until: " + node.getMaxTS());
//
//        JevDataMap<JevSample> samples = null;
//        try
//        {
//            samples = NodeManager.getInstance().registryDataRequest((IDataNode) node,timeset);
//
//            for(JevSample sample:samples.getListOfSamples())
//            {
//                System.out.println("TS: "+sample.getCal()+" Value: "+sample.getVal());
//            }
//
//        }
//        catch(Exception ex)
//        {
//            ex.printStackTrace();
//            System.out.println("Error: " + ex.getMessage());
//        }
//    }
}
