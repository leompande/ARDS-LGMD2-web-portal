package org.hisp.dhis.ccem.transferfacilitydata.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version TraferFacilityDataResultAction.javaJan 21, 2013 3:01:43 PM	
 */

public class TraferFacilityDataResultAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    /*
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    */
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
    private DataSet dataSet;
    
    public DataSet getDataSet()
    {
        return dataSet;
    }
    
    private int currentYear;
    
    public int getCurrentYear()
    {
        return currentYear;
    }

    private int previousYear;
    
    public int getPreviousYear()
    {
        return previousYear;
    }
    
    private List<OrganisationUnit> orgUnitList;
    
    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }
    
    private List<DataElement> dataElementList;
    
    public List<DataElement> getDataElementList()
    {
        return dataElementList;
    }
    
    private Period currentPeriod;
    
    private Period previousPeriod;
    
    private String resultStatus;

    public String getResultStatus()
    {
        return resultStatus;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
 

    public String execute() throws Exception
    {
        
        dataElementList = new ArrayList<DataElement>();
        orgUnitList = new ArrayList<OrganisationUnit>();
        
        currentPeriod = new Period();
        previousPeriod = new Period();
        
        resultStatus = " ";
        
        // OrganisationUnit Information
        List<OrganisationUnitGroup> ouGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getOrganisationUnitGroupByName( EquipmentAttributeValue.HEALTHFACILITY ) ); 
        OrganisationUnitGroup ouGroup = ouGroups.get( 0 );
		//organisationUnitGroupService.getOrganisationUnitGroupByName( EquipmentAttributeValue.HEALTHFACILITY );
        
        if ( ouGroup != null )
        {
            orgUnitList.addAll( ouGroup.getMembers() );
        }
        
        //System.out.println(  " -- OrgUnit Size " + orgUnitList.size() );
        
        // OrganisationUnit and its Attribute Information
        //OrganisationUnit organisationUnit1 = organisationUnitService.getOrganisationUnit( 305 );
        
        //orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( organisationUnit1.getId() ) );
        
        // Data set and sections Information
		List<DataSet> dataSets = new ArrayList<DataSet>( dataSetService.getDataSetByShortName( "FMD" ) );
        dataSet = dataSets.get( 0 );
        
        orgUnitList.retainAll( dataSet.getSources() );
        
       // System.out.println(  " -- OrgUnit Size assign Facility data set " + orgUnitList.size() );
        
        // DataElement Information
        dataElementList = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        //System.out.println(  " -- Data Element List size " + dataElementList.size() );
        
        
        String storedBy = currentUserService.getCurrentUsername();
        
        //Date timestamp = new Date();
        
        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }
        
        Date now = new Date();
        
        
        //Period Information
        
        PeriodType periodType = dataSet.getPeriodType();
        
        Calendar calender = Calendar.getInstance();
        
        if ( periodType.getName().equals( "Yearly" ) )
        {
            currentYear = calender.get( Calendar.YEAR );
            
            previousYear = currentYear - 1;
        }
        
        // previous year information
        
        calender.set( previousYear, Calendar.JANUARY, 1 );
        
        Date firstDayPreviousYear = new Date( calender.getTimeInMillis() );
        
        calender.set( previousYear, Calendar.DECEMBER, 31 );
        
        Date lastDaypreviousYear = new Date( calender.getTimeInMillis() );
        
        previousPeriod = periodService.getPeriod( firstDayPreviousYear, lastDaypreviousYear, periodType );
        
        //String createPreviousYearlyPeroid = "Yearly" + "_" + previousYear + "-01-01";
        
        String createPreviousYearlyPeroid = ""+previousYear;
        
        if( previousPeriod == null )
        {
            //previousPeriod = PeriodType.createPeriodExternalId( createPreviousYearlyPeroid );
            previousPeriod = PeriodType.getPeriodFromIsoString( createPreviousYearlyPeroid );
        }
        
        
        // Current  year information
        calender.set( currentYear, Calendar.JANUARY, 1 );
        
        Date firstDayCurrentYear = new Date( calender.getTimeInMillis() );
        
        calender.set( currentYear, Calendar.DECEMBER, 31 );
        
        Date lastDayCurrentYear = new Date( calender.getTimeInMillis() );
        
        currentPeriod = periodService.getPeriod( firstDayCurrentYear, lastDayCurrentYear, periodType );
        
        //String createCurrentYearlyPeroid = "Yearly" + "_" + currentYear + "-01-01";
        String createCurrentYearlyPeroid = ""+currentYear;
        
        if( currentPeriod == null )
        {
            //currentPeriod = PeriodType.createPeriodExternalId( createCurrentYearlyPeroid );
            currentPeriod = PeriodType.getPeriodFromIsoString( createCurrentYearlyPeroid );
        }
        
        System.out.println( "Transfering Facility Data Start Time is : \t" + new Date() );
        
        int flag = 1;
        //int orgUnitCount = 1;
        for( OrganisationUnit organisationUnit : orgUnitList )
        {
            for( DataElement dataElement : dataElementList )
            {
                DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
                
                DataValue previousDataValue = new DataValue();
                
                String previousValue = "";
                String currentValue = "";
                
                previousDataValue = dataValueService.getDataValue( dataElement, previousPeriod, organisationUnit, decoc );
                
                if ( previousDataValue != null )
                {
                    previousValue = previousDataValue.getValue();
                    
                    DataValue currentDataValue = dataValueService.getDataValue( dataElement, currentPeriod, organisationUnit, decoc );
                    
                    if ( currentDataValue == null )
                    {
                        if ( previousValue != null )
                        {
                            flag = 2;
                            currentDataValue = new DataValue( dataElement, currentPeriod, organisationUnit, decoc, null, previousValue, storedBy, now, null  ); 
                            dataValueService.addDataValue( currentDataValue );
                        }
                    }
                    else
                    {
                        flag = 2;
                        currentValue = currentDataValue.getValue();
                        currentDataValue.setValue( currentValue );
                        currentDataValue.setTimestamp( now );
                        currentDataValue.setStoredBy( storedBy );

                        dataValueService.updateDataValue( currentDataValue );
                    }
                }
            }
            //System.out.println(  "-SL No- " + orgUnitCount  + " --Data Transfer for facility- " + " : "+ organisationUnit.getId() +  " : " + organisationUnit.getName() + " --currentPeriod : " + currentPeriod.getId() + " --previousPeriod : " + previousPeriod.getId() );
            //orgUnitCount++;
        }
        
        if(flag != 1)
        {
            resultStatus = "<font color=red><strong>Facility data is successfully copied from : " + previousYear + " to : "  + currentYear + " <br> for all health facilities</font></strong>";
        }
        
        /*
        System.out.println( currentYear  + " -- " + firstDayCurrentYear + " -- " + lastDayCurrentYear );
        System.out.println( previousYear  + " -- " + firstDayPreviousYear + " -- " + lastDaypreviousYear );
        
        System.out.println( currentPeriod  + " -- " + previousPeriod );
        */
        
        System.out.println( "Transfering Facility Data End Time is : \t" + new Date() );
        
        return SUCCESS;
    }
}

