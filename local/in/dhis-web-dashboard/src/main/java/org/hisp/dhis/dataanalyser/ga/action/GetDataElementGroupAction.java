package org.hisp.dhis.dataanalyser.ga.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.SectionService;
import org.hisp.dhis.dataset.comparator.SectionOrderComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GetDataElementGroupAction.java Jul 2, 2012 12:05:34 PM	
 */
public class GetDataElementGroupAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private SectionService sectionService;

    public void setSectionService( SectionService sectionService )
    {
        this.sectionService = sectionService;
    }
    
    
    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------
   
    private String checkValue;

    public void setCheckValue( String checkValue )
    {
        this.checkValue = checkValue;
    }

    public String getCheckValue()
    {
        return checkValue;
    }

    private List<DataElementGroup> dataElementGroups;

    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    private List<Section> sections;

    public Collection<Section> getSections()
    {
        return sections;
    }

    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        if ( checkValue.equals( "true" ) )
        {
            dataElementGroups = new ArrayList<DataElementGroup>();
            
            //System.out.println( "Value is False" );
            dataElementGroups = new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() );
            Collections.sort( dataElementGroups, new IdentifiableObjectNameComparator() );
        }
        if ( checkValue.equals( "false" ) )
        {
            sections = new ArrayList<Section>();
            sections = new ArrayList<Section>( sectionService.getAllSections() );
            Collections.sort( sections, new SectionOrderComparator() );
        }
        
        
        return SUCCESS;
    }
}
