package org.hisp.dhis.dbmanager;

import java.util.List;
import java.util.Map;

import org.hisp.dhis.linelisting.LineListDataValue;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;


public interface DataBaseManagerInterface
{
    public boolean createTable( String tableName, List<String> columnNames, List<String> autoIncrement, List<String> dataTypes,
        List<Integer> sizeOfColumns );
    
    public void dropTable( String tableName);

    public boolean checkDataFromTable( String tableName, LineListElement lineListElement);

    public boolean updateTable( String tableName, List<LineListElement> removeList, List<LineListElement> addList );

    public boolean insertLLValueIntoDb( List<LineListDataValue> llDataValuesList, String tableName );
    
    public boolean insertSingleLLValueIntoDb( LineListDataValue llDataValue, String tableName );

    //public List<Map<String, String>> getFromLLTable( String tableName, int source, int  period );
    public List<LineListDataValue> getFromLLTable( String tableName, OrganisationUnit source, Period  period );

    public boolean removeLLRecord( int recordId , String tableName );

    public LineListDataValue getLLValuesByRecordNumber( String tableName, int recordId );

    public Period getPeriodByRecordNumber( String tableName, int recordId );

    public boolean updateLLValue( List<LineListDataValue> llDataValuesList, String tableName );
    
    public boolean updateSingleLLValue( LineListDataValue llDataValue, String tableName );
    
    public int getLLValueCountByLLElements( String tablename, Map<String,String> llElementValueMap, OrganisationUnit source );
    
    public List<LineListDataValue> getLLValuesFilterByLLElements( String tableName, Map<String,String> llElementValueMap, OrganisationUnit source, Period  period );
    
    public List<LineListDataValue> getLLValuesFilterByLLElements( String tableName, Map<String,String> llElementValueMap, OrganisationUnit source );

    public List<LineListDataValue> getLLValuesByLLElementValue( String tableName, String llElementName, String llElementValue, OrganisationUnit source, Period  period );

    public Period getRecentPeriodForOnChangeData( String tableName, String llElementName, String llElementValue, OrganisationUnit source );
    
    public List<LineListDataValue> getLLValuesSortBy( String tableName, String sortBy, OrganisationUnit source, Period  period );
    
    public int rowCount( String tableName );
    
    public int getMaxRecordNumber( String tableName );
    
}
