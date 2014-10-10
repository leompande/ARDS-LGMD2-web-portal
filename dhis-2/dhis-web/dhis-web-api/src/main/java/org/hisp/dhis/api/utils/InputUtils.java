package org.hisp.dhis.api.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class InputUtils
{
    @Autowired
    private DataElementCategoryService categoryService;
    
    /**
     * Validates and retrieves the attribute option combo. 409 conflict as status
     * code along with a textual message will be set on the response in case of
     * invalid input.
     * 
     * @param response the servlet response.
     * @param cc the category combo identifier.
     * @param cp the category and option query string.
     * @return the attribute option combo identified from the given input, or null
     *         if the input was invalid.
     */
    public DataElementCategoryOptionCombo getAttributeOptionCombo( HttpServletResponse response, String cc, String cp )
    {
        List<String> opts = ContextUtils.getQueryParamValues( cp );

        // ---------------------------------------------------------------------
        // Attribute category combo validation
        // ---------------------------------------------------------------------

        if ( ( cc == null && opts != null || ( cc != null && opts == null ) ) )
        {
            ContextUtils.conflictResponse( response, "Both or none of category combination and category options must be present" );
            return null;
        }

        DataElementCategoryCombo categoryCombo = null;
        
        if ( cc != null && ( categoryCombo = categoryService.getDataElementCategoryCombo( cc ) ) == null )
        {
            ContextUtils.conflictResponse( response, "Illegal category combo identifier: " + cc );
            return null;
        }

        // ---------------------------------------------------------------------
        // Attribute category options validation
        // ---------------------------------------------------------------------

        DataElementCategoryOptionCombo attributeOptionCombo = null;

        if ( opts != null )
        {
            Set<DataElementCategoryOption> categoryOptions = new HashSet<DataElementCategoryOption>();

            for ( String id : opts )
            {
                DataElementCategoryOption categoryOption = categoryService.getDataElementCategoryOption( id );
                
                if ( categoryOption == null )
                {
                    ContextUtils.conflictResponse( response, "Illegal category option identifier: " + id );
                    return null;
                }
                
                categoryOptions.add( categoryOption );
            }
            
            attributeOptionCombo = categoryService.getDataElementCategoryOptionCombo( categoryCombo, categoryOptions );
            
            if ( attributeOptionCombo == null )
            {
                ContextUtils.conflictResponse( response, "Attribute option combo does not exist for given category combo and category options" );
                return null;
            }
        }

        if ( attributeOptionCombo == null )
        {
            attributeOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        }
        
        if ( attributeOptionCombo == null )
        {
            ContextUtils.conflictResponse( response, "Default attribute option combo does not exist" );
            return null;
        }
        
        return attributeOptionCombo;
    }
}
