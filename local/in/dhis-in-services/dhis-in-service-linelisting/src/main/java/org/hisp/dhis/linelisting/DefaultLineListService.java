package org.hisp.dhis.linelisting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultLineListService
    implements LineListService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LineListStore lineListStore;

    public void setLineListStore( LineListStore lineListStore )
    {
        this.lineListStore = lineListStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // Line List group
    // -------------------------------------------------------------------------

    public int addLineListGroup( LineListGroup lineListGroup )
    {
        int id = lineListStore.addLineListGroup( lineListGroup );

        //i18nService.addObject( lineListGroup );
       

        return id;
    }

    public void deleteLineListGroup( LineListGroup lineListGroup )
    {
        i18nService.removeObject( lineListGroup );

        lineListStore.deleteLineListGroup( lineListGroup );

    }

    public Collection<LineListGroup> getAllLineListGroups()
    {
        return lineListStore.getAllLineListGroups();
    }

    public LineListGroup getLineListGroup( int id )
    {
        return lineListStore.getLineListGroup( id );
    }

    public LineListGroup getLineListGroupByName( String name )
    {
        return lineListStore.getLineListGroupByName( name );
    }

    public LineListGroup getLineListGroupByShortName( String shortName )
    {
        return lineListStore.getLineListGroupByShortName( shortName );
    }

    public void updateLineListGroup( LineListGroup lineListGroup )
    {
        lineListStore.updateLineListGroup( lineListGroup );
    }

    public Collection<LineListGroup> getLineListGroups( Collection<Integer> identifiers )
    {
        if ( identifiers == null )
        {
            return getAllLineListGroups();
        }

        Collection<LineListGroup> objects = new ArrayList<LineListGroup>();

        for ( Integer id : identifiers )
        {
            objects.add( getLineListGroup( id ) );
        }

        return objects;
    }

    public Collection<LineListGroup> getLineListGroupsBySource(OrganisationUnit source)
    {
        return lineListStore.getLineListGroupsBySource( source );
    }

    public Collection<LineListGroup> getLineListGroupsByElement(LineListElement lineListElement)
    {
        return lineListStore.getLineListGroupsByElement( lineListElement );
    }

    public Collection<LineListGroup> getLineListGroupsBySources( Collection<OrganisationUnit> sources )
    {
        Set<LineListGroup> lineListGroups = new HashSet<LineListGroup>();

        for (OrganisationUnit source : sources)
        {
            lineListGroups.addAll( lineListStore.getLineListGroupsBySource( source ) );
        }

        return lineListGroups;
    }

    public int getSourcesAssociatedWithLineListGroup( LineListGroup lineListGroup, Collection<OrganisationUnit> sources )
    {
        int count = 0;

        for ( OrganisationUnit source : sources )
        {
            if ( lineListGroup.getSources().contains( source ) )
            {
                count++;
            }
        }

        return count;
    }

    // -------------------------------------------------------------------------
    // Line List Element
    // -------------------------------------------------------------------------

    public int addLineListElement( LineListElement lineListElement )
    {

        int id = lineListStore.addLineListElement( lineListElement );

        //i18nService.addObject( lineListElement );

        return id;

    }

    public void deleteLineListElement( LineListElement lineListElement )
    {
        i18nService.removeObject( lineListElement );

        lineListStore.deleteLineListElement( lineListElement );

    }

    public Collection<LineListElement> getAllLineListElements()
    {
        return lineListStore.getAllLineListElements();
    }

    public LineListElement getLineListElement( int id )
    {
        return lineListStore.getLineListElement( id );
    }

    public LineListElement getLineListElementByName( String name )
    {
        return lineListStore.getLineListElementByName( name );
    }

    public LineListElement getLineListElementByShortName( String shortName )
    {
        return lineListStore.getLineListElementByShortName( shortName );
    }

    public Collection<LineListElement> getLineListElementsByOption (LineListOption lineListOption)
    {
        return lineListStore.getLineListElementsByOption( lineListOption );
    }

    public Collection<LineListElement> getLineListElementsBySortOrder (LineListGroup lineListGroup)
    {
        return lineListStore.getLineListElementsBySortOrder( lineListGroup );
    }

    public void updateLineListElement( LineListElement lineListElement )
    {
        lineListStore.updateLineListElement( lineListElement );
    }

    // -------------------------------------------------------------------------
    // Line List Option
    // -------------------------------------------------------------------------

    public int addLineListOption( LineListOption lineListOption )
    {
        int id = lineListStore.addLineListOption( lineListOption );

        //i18nService.addObject( lineListOption );

        return id;
    }

    public void deleteLineListOption( LineListOption lineListOption )
    {
        i18nService.removeObject( lineListOption );

        lineListStore.deleteLineListOption( lineListOption );
    }

    public Collection<LineListOption> getAllLineListOptions()
    {
        return lineListStore.getAllLineListOptions();
    }

    public LineListOption getLineListOption( int id )
    {
        return lineListStore.getLineListOption( id );
    }

    public LineListOption getLineListOptionByName( String name )
    {
        return lineListStore.getLineListOptionByName( name );
    }

    public LineListOption getLineListOptionByShortName( String shortName )
    {
        return lineListStore.getLineListOptionByShortName( shortName );
    }

    public void updateLineListOption( LineListOption lineListOption )
    {
        lineListStore.updateLineListOption( lineListOption );
    }
    
    // -------------------------------------------------------------------------
    // LinelistElemnet - Dataelement Mapping
    // -------------------------------------------------------------------------

    public void addLinelistDataelementMapping( LineListDataElementMap lineListDataElementMap )
    {
        lineListStore.addLinelistDataelementMapping( lineListDataElementMap );
    }
    
    public void updateLinelistDataelementMapping( LineListDataElementMap lineListDataElementMap )
    {
        lineListStore.updateLinelistDataelementMapping( lineListDataElementMap );
    }
    
    public void deleteLinelistDataelementMapping( LineListDataElementMap lineListDataElementMap )
    {
        lineListStore.deleteLinelistDataelementMapping( lineListDataElementMap );
    }

    public List<LineListDataElementMap> getLinelistDataelementMappings( LineListElement linelistElement, LineListOption linelistOption )
    {
        return lineListStore.getLinelistDataelementMappings( linelistElement, linelistOption );
    }
    
    public LineListDataElementMap getLinelistDataelementMapping( LineListElement linelistElement, LineListOption linelistOption, DataElement dataElement, DataElementCategoryOptionCombo deCOC )
    {
        return lineListStore.getLinelistDataelementMapping( linelistElement, linelistOption, dataElement, deCOC );
    }

    public int getLineListGroupCount()
    {
        return lineListStore.getLineListGroupCount();
    }
    
    public Collection<LineListGroup> getLineListGroupsBetween( int first, int max )
    {
        return lineListStore.getLineListGroupsBetween( first, max );
    }

    public int getLineListElementCount()
    {
        return lineListStore.getLineListElementCount();
    }
    
    public Collection<LineListElement> getLineListElementsBetween( int first, int max )
    {
        return lineListStore.getLineListElementsBetween( first, max );
    }

    public int getLineListOptionCount()
    {
        return lineListStore.getLineListOptionCount();
    }
    
    @SuppressWarnings("unchecked")
    public Collection<LineListOption> getLineListOptionsBetween( int first, int max )
    {
        return lineListStore.getLineListOptionsBetween( first, max );
    }

}
