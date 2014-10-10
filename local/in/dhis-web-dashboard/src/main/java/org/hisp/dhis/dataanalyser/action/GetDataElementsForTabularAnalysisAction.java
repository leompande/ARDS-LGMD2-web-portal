package org.hisp.dhis.dataanalyser.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.SectionService;

import com.opensymphony.xwork2.Action;

public class GetDataElementsForTabularAnalysisAction implements Action
{

    private final static int ALL = 0;

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
    
    private SectionService sectionService;

    public void setSectionService( SectionService sectionService )
    {
        this.sectionService = sectionService;
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
    /*
    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }
    */
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

    private String chkValue;

    public void setChkValue( String chkValue )
    {
        this.chkValue = chkValue;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        optionComboIds = new ArrayList<String>();
        optionComboNames = new ArrayList<String>();

        if ( id == null || id == ALL )
        {
            System.out.println("The id is null");
            dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
            System.out.println( " DataElements size = "+ dataElements.size() );
        } 
        else
        {
            if ( chkValue.equals( "true" ) )
            {
                DataElementGroup dataElementGroup = dataElementService.getDataElementGroup( id );
                if ( dataElementGroup != null )
                {
                    dataElements = new ArrayList<DataElement>( dataElementGroup.getMembers() );
                    //System.out.println( "dataElementGroup id = " + id + " dataElements size = " + dataElements.size() );
                }
                else
                {
                    dataElements = new ArrayList<DataElement>();
                }
            }
            if ( chkValue.equals( "false" ) )
            {
                Section section = sectionService.getSection( id );
                if ( section != null )
                {
                    dataElements = new ArrayList<DataElement>( section.getDataElements() );
                    //System.out.println( "section id = " + id + " dataElements size = " + dataElements.size() );
                }
                else
                {
                    dataElements = new ArrayList<DataElement>();
                }
            }
        }
        //System.out.println( " dataElements size = " + dataElements.size() );
        Iterator<DataElement> alldeIterator = dataElements.iterator();
        while ( alldeIterator.hasNext() )
        {
            DataElement de1 = alldeIterator.next();
            if ( !de1.getType().equals( DataElement.VALUE_TYPE_INT ) ||!de1.getDomainType().equals( DataElement.DOMAIN_TYPE_AGGREGATE ) )
           // if ( de1.getType().equals( DataElement.VALUE_TYPE_BOOL ) )
            {
                alldeIterator.remove();
            }
        }
        
        Collections.sort( dataElements, dataElementComparator );

        //displayPropertyHandler.handle( dataElements );

        if ( deOptionValue != null )
        {
            if ( deOptionValue.equalsIgnoreCase( "optioncombo" ) )
            {
                Iterator<DataElement> deIterator = dataElements.iterator();
                while ( deIterator.hasNext() )
                {
                    DataElement de = deIterator.next();

                    DataElementCategoryCombo dataElementCategoryCombo = de.getCategoryCombo();
                    List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( dataElementCategoryCombo.getOptionCombos() );

                    Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                    while ( optionComboIterator.hasNext() )
                    {
                        DataElementCategoryOptionCombo decoc = optionComboIterator.next();
                        optionComboIds.add( de.getId() + ":" + decoc.getId() );
                        optionComboNames.add( de.getName() + ":" + dataElementCategoryService.getDataElementCategoryOptionCombo( decoc ).getName() );
                    }

                }
            }
        }


        return SUCCESS;
    }
}