package org.hisp.dhis.ll.action.aggmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;

import com.opensymphony.xwork2.Action;

public class SanctionedPostMappingForm implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LineListService lineListService;
    
    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<LineListGroup> departments;
    
    public List<LineListGroup> getDepartments()
    {
        return departments;
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
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        optionComboIds = new ArrayList<String>();
        optionComboNames = new ArrayList<String>();

        departments = new ArrayList<LineListGroup>( lineListService.getAllLineListGroups() );
        
        List<DataElement> dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        
        Iterator<DataElement> deIterator = dataElements.iterator();
        while ( deIterator.hasNext() )
        {
            DataElement de1 = deIterator.next();
            if ( !de1.getType().equals( DataElement.VALUE_TYPE_INT ) || !de1.getDomainType().equals( DataElement.DOMAIN_TYPE_AGGREGATE ) )    
            {
                deIterator.remove();
            }
        }

        deIterator = dataElements.iterator();
        while ( deIterator.hasNext() )
        {
            DataElement de = deIterator.next();

            DataElementCategoryCombo dataElementCategoryCombo = de.getCategoryCombo();
            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                dataElementCategoryCombo.getOptionCombos() );

            Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
            while ( optionComboIterator.hasNext() )
            {
                DataElementCategoryOptionCombo decoc = optionComboIterator.next();
                optionComboIds.add( de.getId() + ":" + decoc.getId() );
                optionComboNames.add( de.getName() + ":" + decoc.getName() );
            }

        }

        return SUCCESS;
    }

}
