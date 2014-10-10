package org.hisp.dhis.de.action;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.LocalDataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.reportsheet.AttributeValueGroupOrder;
import org.hisp.dhis.reportsheet.AttributeValueGroupOrderService;
import org.hisp.dhis.util.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class LoadICDFormAction
    implements Action
{
    protected static final String KEY_ICD_FORM_RESULT = "icdFormResults";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private LocalDataElementService localDataElementService;

    @Autowired
    private AttributeValueGroupOrderService attributeValueGroupOrderService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private Integer chapterId;

    public void setChapterId( Integer chapterId )
    {
        this.chapterId = chapterId;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Map<String, Collection<DataElement>> orderedDiseaseDataElements = new HashMap<String, Collection<DataElement>>();

    public Map<String, Collection<DataElement>> getOrderedDiseaseDataElements()
    {
        return orderedDiseaseDataElements;
    }

    private List<String> values = new ArrayList<String>();

    public List<String> getValues()
    {
        return values;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        DataSet dataSet = dataSetService.getDataSet( dataSetId, true, false, false );

        AttributeValueGroupOrder group = attributeValueGroupOrderService.getAttributeValueGroupOrder( chapterId );

        if ( group != null )
        {
            values = group.getAttributeValues();
        }

        Collections.sort( values );

        Map<String, List<Integer>> orderedDiseaseDEIdentifiers = localDataElementService.get( dataSet, values );
        
        Collection<DataElement> dataElementList = new HashSet<DataElement>();

        List<DataElement> dataElements = null;
        
        for ( String value : values )
        {
            dataElements = new ArrayList<DataElement>( dataElementService.getDataElements( orderedDiseaseDEIdentifiers.get( value ) ) );
            
            dataElementList.addAll( dataElements );

            orderedDiseaseDataElements.put( value, dataElements );
        }

        group = null;
        dataSet = null;
        dataElements = null;
        orderedDiseaseDEIdentifiers = null;

        SessionUtils.setSessionVar( KEY_ICD_FORM_RESULT, dataElementList );

        return DataSet.TYPE_DEFAULT;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
}
