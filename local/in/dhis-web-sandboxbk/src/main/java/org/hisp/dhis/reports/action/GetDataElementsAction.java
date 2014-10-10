package org.hisp.dhis.reports.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.options.displayproperty.DefaultDisplayPropertyHandler;

import com.opensymphony.xwork2.ActionSupport;

public class GetDataElementsAction extends ActionSupport
{

    private final int ALL = 0;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private DataElementCategoryService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
    		DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }

    // -------------------------------------------------------------------------
    // DisplayPropertyHandler
    // -------------------------------------------------------------------------

    private DefaultDisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DefaultDisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String deOptionValue;

    public void setDeOptionValue( String deOptionValue )
    {
        this.deOptionValue = deOptionValue;
    }

    public String getDeOptionValue()
    {
        return deOptionValue;
    }

    private List<DataElement> dataElements;

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }
    
    private List<String> optionComboNames;

    public List<String> getOptionComboNames()
    {
        return optionComboNames;
    }

    private List<String> optionComboIds;
    
    public List<String> getOptionComboIds()
    {
        return optionComboIds;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        optionComboIds = new ArrayList<String>();
        optionComboNames = new ArrayList<String>();
        
        if ( id == ALL )
        {
            dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        }
        else
        {
            DataElementGroup dataElementGroup = dataElementService.getDataElementGroup( id );

            if ( dataElementGroup != null )
            {
                dataElements = new ArrayList<DataElement>( dataElementGroup.getMembers() );
            }
            else
            {
                dataElements = new ArrayList<DataElement>();
            }
        }

        Collections.sort( dataElements, dataElementComparator );

        displayPropertyHandler.handle( dataElements );
        
        /*
        if ( deOptionValue != null )
        {
                if( deOptionValue.equalsIgnoreCase( "optioncombo" ))
            {
                Iterator<DataElement> deIterator = dataElements.iterator();
                while(deIterator.hasNext())
                {
                    DataElement de = (DataElement) deIterator.next();
                    DataElementCategoryCombo dataElementCategoryCombo = de.getCategoryCombo();
                    List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                        dataElementCategoryCombo.getOptionCombos() );

                    Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                    while ( optionComboIterator.hasNext() )
                    {
                        DataElementCategoryOptionCombo decoc = (DataElementCategoryOptionCombo) optionComboIterator
                            .next();
                        optionComboIds.add( de.getId()+":"+decoc.getId());
                        optionComboNames.add( de.getName()+":"+dataElementCategoryOptionComboService.getOptionNames( decoc ));
                        
                    }   
                }
            }           
        }
        */
                       
        return SUCCESS;
    }

}
