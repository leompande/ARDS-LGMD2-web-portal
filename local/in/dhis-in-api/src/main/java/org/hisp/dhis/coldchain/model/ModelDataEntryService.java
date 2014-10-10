package org.hisp.dhis.coldchain.model;

import java.util.Collection;
import java.util.regex.Pattern;

import org.hisp.dhis.i18n.I18n;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version ModelDataEntryService.java Jun 7, 2012 5:12:27 PM	
 */
public interface ModelDataEntryService
{
    final Pattern INPUT_PATTERN = Pattern.compile( "(<input.*?)[/]?>", Pattern.DOTALL );
    
    //final Pattern IDENTIFIER_PATTERN_FIELD = Pattern.compile( "id=\"(\\d+)-(\\d+)-val\"" );
    
    final Pattern IDENTIFIER_PATTERN_FIELD = Pattern.compile( "id=\"attr(\\d+)\"" );
    
    //--------------------------------------------------------------------------
    // ProgramDataEntryService
    //--------------------------------------------------------------------------
    
    String prepareDataEntryFormForModel( String htmlCode, Collection<ModelAttributeValue> dataValues, String disabled,
        I18n i18n, ModelType modelType );
    
    String prepareDataEntryFormForEdit( String htmlCode );
}
