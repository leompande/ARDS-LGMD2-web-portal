package org.hisp.dhis.ll.action.llagg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;

import com.opensymphony.xwork2.Action;

public class GetAggDataElementsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

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

    private Integer degId;

    public void setDegId( Integer degId )
    {
        this.degId = degId;
    }

    private List<DataElement> dataElementList;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        optionComboNames = new ArrayList<String>();

        optionComboIds = new ArrayList<String>();

        dataElementList = new ArrayList<DataElement>( dataElementService.getDataElementGroup( degId ).getMembers() );

        Iterator<DataElement> deIterator = dataElementList.iterator();

        while ( deIterator.hasNext() )
        {
            DataElement de = deIterator.next();

            if ( !de.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
            {
                deIterator.remove();
            }
        }

        if ( dataElementList != null && !dataElementList.isEmpty() )
        {
            deIterator = dataElementList.iterator();

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
        }

        return SUCCESS;
    }

}
