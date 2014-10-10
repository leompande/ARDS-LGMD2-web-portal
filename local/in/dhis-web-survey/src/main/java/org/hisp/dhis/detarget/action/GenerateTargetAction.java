package org.hisp.dhis.detarget.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;

import com.opensymphony.xwork2.Action;

public class GenerateTargetAction
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

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    @SuppressWarnings("unused")
	private Comparator<DataElementGroup> dataElementGroupComparator;

    public void setDataElementGroupComparator( Comparator<DataElementGroup> dataElementGroupComparator )
    {
        this.dataElementGroupComparator = dataElementGroupComparator;
    }

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------
    private List<DataElementGroup> dataElementGroups;

    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    public void setDataElementGroups( List<DataElementGroup> dataElementGroups )
    {
        this.dataElementGroups = dataElementGroups;
    }

    public void setDataElements( List<DataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    private List<DataElement> dataElements;

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private DataElementCategoryOptionCombo defaultoptioncombo;

    public DataElementCategoryOptionCombo getDefaultoptioncombo()
    {
        return defaultoptioncombo;
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

    public String execute()
        throws Exception
    {
        optionComboIds = new ArrayList<String>();
        optionComboNames = new ArrayList<String>();

        /* DataElements and Groups */
        dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        dataElementGroups = new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() );

        defaultoptioncombo = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();

        Collections.sort( dataElements, new IdentifiableObjectNameComparator() );
        Collections.sort( dataElementGroups, new IdentifiableObjectNameComparator() );

        Iterator<DataElement> deIterator = dataElements.iterator();
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

                optionComboNames
                    .add( de.getName() + ":" + dataElementCategoryService.getDataElementCategoryOptionCombo( decoc ).getName() );

            }
        }

        return SUCCESS;
    }
}