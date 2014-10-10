package org.hisp.dhis.hr.action.dataentryform;

/*
 * Copyright (c) 2004-2009, University of Oslo
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.customvalue.CustomValue;
import org.hisp.dhis.customvalue.CustomValueService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;

import com.opensymphony.xwork2.Action;

/**
 * @author Latifov Murodillo Abdusamadovich
 * 
 * @version $Id$
 */
public class GetCustomValuesAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

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

    private CustomValueService customValueService;

    public void setCustomValueService( CustomValueService customValueService )
    {
        this.customValueService = customValueService;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private int customValueId;

    public int getCustomValueId()
    {
        return customValueId;
    }

    public void setCustomValueId( int customValueId )
    {
        this.customValueId = customValueId;
    }

    private int dataElementId;

    public void setDataElementId( int dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    private int dataSetId;

    public void setDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    public int getDataSetId()
    {
        return dataSetId;
    }

    private int categoryOptionComboId;

    public int getCategoryOptionComboId()
    {
        return categoryOptionComboId;
    }

    public void setCategoryOptionComboId( int categoryOptionComboId )
    {
        this.categoryOptionComboId = categoryOptionComboId;
    }

    private List<String> customValueIds;

    public List<String> getCustomValueIds()
    {
        return customValueIds;
    }

    private List<String> customValueNames;

    public List<String> getCustomValueNames()
    {
        return customValueNames;
    }

    private String operation;

    public String getOperation()
    {
        return operation;
    }

    public void setOperation( String operation )
    {
        this.operation = operation;
    }

    private String value;

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        customValueIds = new ArrayList<String>();
        customValueNames = new ArrayList<String>();

        DataSet dataSet = dataSetService.getDataSet( dataSetId );
        DataElement dataElement = dataElementService.getDataElement( dataElementId );
        DataElementCategoryOptionCombo dataElementCategoryOptionCombo = dataElementCategoryService
            .getDataElementCategoryOptionCombo( categoryOptionComboId );

        CustomValue customValue = new CustomValue();
        customValue.setDataSet( dataSet );
        customValue.setDataElement( dataElement );
        customValue.setOptionCombo( dataElementCategoryOptionCombo );

        if ( operation.equalsIgnoreCase( "add" ) )
        {
            customValue.setCustomValue( value );

            customValueService.addCustomValue( customValue );
        }

        if ( operation.equalsIgnoreCase( "delete" ) )
        {
            customValue = customValueService.getCustomValue( customValueId );
            customValueService.deleteCustomValue( customValue );
        }

        List<CustomValue> customValues = null;

        if ( operation.equalsIgnoreCase( "find" ) )
        {
            customValues = new ArrayList<CustomValue>( customValueService.findCustomValues( value ) );
        }
        else
        {
            customValues = new ArrayList<CustomValue>( customValueService.getCustomValues( dataSet, dataElement,
                dataElementCategoryOptionCombo ) );
        }

        Iterator<CustomValue> customValueIterator = customValues.iterator();

        if ( operation.equalsIgnoreCase( "find" ) )
        {
            while ( customValueIterator.hasNext() )
            {
                CustomValue customVal = customValueIterator.next();
                
                if ( !customValueNames.contains( customVal.getCustomValue() ) )
                {
                    customValueIds.add( String.valueOf( customVal.getId() ) );
                    customValueNames.add( customVal.getCustomValue() );
                }
            }
        }
        else
        {
            while ( customValueIterator.hasNext() )
            {
                CustomValue customVal = customValueIterator.next();

                customValueIds.add( String.valueOf( customVal.getId() ) );
                customValueNames.add( customVal.getCustomValue() );
            }
        }
        if ( operation.equalsIgnoreCase( "delete" ) )
        {
            customValueIds.remove( String.valueOf( customValue.getId() ) );
            customValueNames.remove( customValue.getCustomValue() );
        }

        if ( operation.equalsIgnoreCase( "add" ) )
        {
            if ( !customValueNames.contains( customValue.getCustomValue() ) )
            {
                customValueIds.remove( String.valueOf( customValue.getId() ) );
                customValueNames.remove( customValue.getCustomValue() );
            }
        }

        return SUCCESS;
    }
}
