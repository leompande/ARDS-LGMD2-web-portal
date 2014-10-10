package org.hisp.dhis.hr.action.dataentryform;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;

import com.opensymphony.xwork2.Action;

/**
 * @author Bharath Kumar
 * @version $Id$
 */
public class SaveDataEntryFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------


    private HrDataSetService dataSetService;

    public void setHrDataSetService( HrDataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------
    
    private int dataSetIdField;

    public void setDataSetIdField( int dataSetIdField )
    {
        this.dataSetIdField = dataSetIdField;
    }

    private String nameField;

    public void setNameField( String nameField )
    {
        this.nameField = nameField;
    }

    private String designTextarea;

    public void setDesignTextarea( String designTextarea )
    {
        this.designTextarea = designTextarea;
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------
    
    public String execute()
        throws Exception
    {
    	HrDataSet dataset = dataSetService.getHrDataSet( dataSetIdField );

    	String dataEntryForm = dataset.getHypertext();

        if ( dataEntryForm == null )
        {
            setDesignTextarea( designTextarea );
            dataset.setHypertext( designTextarea  );
            dataset.setName( nameField );
            dataSetService.updateHrDataSet( dataset );
        }
        else
        {
            setDesignTextarea( designTextarea );
            dataset.setHypertext( prepareDataEntryFormInputs(designTextarea ));
            dataset.setName( nameField );
            dataSetService.updateHrDataSet( dataset );
        }       

        return SUCCESS;
    }

    private String prepareDataEntryFormCode( String dataEntryFormCode )
    {
        String preparedCode = dataEntryFormCode;

        preparedCode = prepareDataEntryFormInputs( preparedCode );
        
        return preparedCode;
    }

    private String prepareDataEntryFormInputs( String preparedCode )
    {
        // ---------------------------------------------------------------------
        // Buffer to contain the final result.
        // ---------------------------------------------------------------------
        
        StringBuffer sb = new StringBuffer();
        
        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code.
        // ---------------------------------------------------------------------
 
        Pattern patDataElement = Pattern.compile( "(<input.*?)[/]?>" );
        Matcher matDataElement = patDataElement.matcher( preparedCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields.
        // ---------------------------------------------------------------------
        
        boolean result = matDataElement.find();
        
        while ( result )
        {
            // -----------------------------------------------------------------
            // Get input HTML code (HTML input field code).
            // -----------------------------------------------------------------
            
            String dataElementCode = matDataElement.group( 1 );
            
            // -----------------------------------------------------------------
            // Pattern to extract data element name from data element field
            // -----------------------------------------------------------------
            
            Pattern patDataElementName = Pattern.compile( "value=\"\\[ (.*) \\]\"" );
            Matcher matDataElementName = patDataElementName.matcher( dataElementCode );

            Pattern patTitle = Pattern.compile( "title=\"-- (.*) --\"" );
            Matcher matTitle = patTitle.matcher( dataElementCode );
       
            if ( matDataElementName.find() && matDataElementName.groupCount() > 0 )
            {
                String temp = "[ " + matDataElementName.group( 1 ) + " ]";
                dataElementCode = dataElementCode.replace( temp, "" );

                if ( matTitle.find() && matTitle.groupCount() > 0 )
                {
                    temp = "-- " + matTitle.group( 1 ) + " --";
                    dataElementCode = dataElementCode.replace( temp, "" );
                }

                // -------------------------------------------------------------
                // Appends dataElementCode
                // -------------------------------------------------------------
       
                String appendCode = dataElementCode;
                appendCode += "/>";
                matDataElement.appendReplacement( sb, appendCode );
            }

            // -----------------------------------------------------------------
            // Go to next data entry field
            // -----------------------------------------------------------------
   
            result = matDataElement.find();
        }

        // -----------------------------------------------------------------
        // Add remaining code (after the last match), and return formatted code.
        // -----------------------------------------------------------------

        matDataElement.appendTail( sb );
        
        return sb.toString();
    }    
}
