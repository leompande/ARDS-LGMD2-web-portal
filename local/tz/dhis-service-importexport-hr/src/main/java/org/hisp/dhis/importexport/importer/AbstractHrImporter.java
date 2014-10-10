package org.hisp.dhis.importexport.importer;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static org.hisp.dhis.importexport.ImportObjectStatus.MATCH;
import static org.hisp.dhis.importexport.ImportObjectStatus.NEW;
import static org.hisp.dhis.importexport.ImportObjectStatus.UPDATE;
import static org.hisp.dhis.importexport.ImportStrategy.NEW_AND_UPDATES;

import org.amplecode.quick.BatchHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.ImportableObject;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.importexport.HrGroupMemberType;
import org.hisp.dhis.importexport.ImportDataValue;
import org.hisp.dhis.importexport.ImportHrObjectService;
import org.hisp.dhis.importexport.ImportObjectStatus;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;

/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id: AbstractConverter.java 6298 2008-11-17 17:31:14Z larshelg $
 */
public abstract class AbstractHrImporter<T extends ImportableObject>
{
    protected static final Log log = LogFactory.getLog( AbstractHrImporter.class );
    
    protected static final String EMPTY = "";
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    protected ImportHrObjectService importObjectService;
    
    protected BatchHandler<T> batchHandler;
    
    protected BatchHandler<ImportDataValue> importDataValueBatchHandler;
    
    protected ImportAnalyser importAnalyser;

    // -------------------------------------------------------------------------
    // AbstractReadXMLConverter
    // -------------------------------------------------------------------------

    protected void read( T object, HrGroupMemberType groupMemberType, ImportParams params )
    {
        if ( params.isAnalysis() ) 
        {
            if ( importAnalyser != null ) // Analyser instantiatet only for relevant objects
            {
                importAnalyser.addObject( object );
            }
        }
        else
        {
            T match = getMatching( object );
            
            if ( match == null ) // No similar object exists
            {
                if ( !params.isPreview() ) // Import object
                {
                    importUnique( object );
                }
                else if ( params.isPreview() ) // Preview object 
                {
                    if ( object.getClass().equals( DataValue.class ) ) // Using BatchHandler to improve performance
                    {
                        ImportDataValue value = new ImportDataValue();
                        
                        value.setDataValue( (DataValue) object, NEW );
                        
                        importDataValueBatchHandler.addObject( value );
                    }
                    else // Using ImportObjectService
                    {
                        importObjectService.addImportObject( NEW, groupMemberType, object );
                    }
                }
            }
            else if ( params.getImportStrategy() == NEW_AND_UPDATES ) // Similar object exists
            {
                if ( !params.isPreview() ) // Import object
                {
                    if ( !isIdentical( object, match ) && !ignore( object, match ) ) // Skip if identical or ignore-able
                    {
                        importMatching( object, match );
                    }
                }
                else if ( params.isPreview() ) // Preview object. DataValue cannot be match in preview.
                {
                    ImportObjectStatus status = !isIdentical( object, match ) && !ignore( object, match ) ? UPDATE : MATCH;
                        
                    importObjectService.addImportObject( status, groupMemberType, object, match ); // Set to match if existing, update otherwise
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    protected abstract void importUnique( T object );
    
    protected abstract void importMatching( T object, T match );
    
    protected abstract T getMatching( T object );
    
    protected abstract boolean isIdentical( T object, T match );

    // -------------------------------------------------------------------------
    // Override-able methods
    // -------------------------------------------------------------------------

    protected boolean ignore( T object, T match )
    {
        return false;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    protected static boolean isNotNull( Object arg1, Object arg2 )
    {
        return arg1 != null && arg2 != null;
    }
    
    protected static boolean isSimiliar( Object arg1, Object arg2 )
    {
        return ( arg1 == null && arg2 == null ) || ( arg1 != null && arg2 != null );
    }
    
    protected static String valueOf( Integer integer )
    {
        if ( integer == null )
        {
            return "";
        }
        
        return String.valueOf( integer );
    }
    
    protected static Integer parseInteger( String string )
    {
        if ( string != null )
        {
            return Integer.parseInt( string );
        }
        
        return null;
    }
}
