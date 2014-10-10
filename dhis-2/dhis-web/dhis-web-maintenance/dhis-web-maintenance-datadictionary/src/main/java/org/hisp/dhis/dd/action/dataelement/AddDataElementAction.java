package org.hisp.dhis.dd.action.dataelement;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
import java.util.HashSet;
import java.util.List;

import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.system.util.AttributeUtils;
import org.hisp.dhis.system.util.ConversionUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Torgeir Lorange Ostby
 */
public class AddDataElementAction
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

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    private OptionService optionService;

    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
    }
    
    private MappingService mappingService;

    public void setMappingService( MappingService mappingService )
    {
        this.mappingService = mappingService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String alternativeName;

    public void setAlternativeName( String alternativeName )
    {
        this.alternativeName = alternativeName;
    }

    private String shortName;

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    private String code;

    public void setCode( String code )
    {
        this.code = code;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private String formName;

    public void setFormName( String formName )
    {
        this.formName = formName;
    }

    private String domainType;

    public void setDomainType( String domainType )
    {
        this.domainType = domainType;
    }

    private String numberType;

    public void setNumberType( String numberType )
    {
        this.numberType = numberType;
    }

    private String textType;

    public void setTextType( String textType )
    {
        this.textType = textType;
    }

    private String valueType;

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }

    private String aggregationOperator;

    public void setAggregationOperator( String aggregationOperator )
    {
        this.aggregationOperator = aggregationOperator;
    }

    private String url;

    public void setUrl( String url )
    {
        this.url = url;
    }

    private Collection<String> aggregationLevels;

    public void setAggregationLevels( Collection<String> aggregationLevels )
    {
        this.aggregationLevels = aggregationLevels;
    }

    private Integer selectedCategoryComboId;

    public void setSelectedCategoryComboId( Integer selectedCategoryComboId )
    {
        this.selectedCategoryComboId = selectedCategoryComboId;
    }

    private boolean zeroIsSignificant;

    public void setZeroIsSignificant( Boolean zeroIsSignificant )
    {
        this.zeroIsSignificant = zeroIsSignificant;
    }

    private List<String> jsonAttributeValues;

    public void setJsonAttributeValues( List<String> jsonAttributeValues )
    {
        this.jsonAttributeValues = jsonAttributeValues;
    }

    private Collection<String> selectedGroups = new HashSet<String>();

    public void setSelectedGroups( Collection<String> selectedGroups )
    {
        this.selectedGroups = selectedGroups;
    }

    private Integer selectedOptionSetId;

    public void setSelectedOptionSetId( Integer selectedOptionSetId )
    {
        this.selectedOptionSetId = selectedOptionSetId;
    }
    
    private Integer selectedLegendSetId;

    public void setSelectedLegendSetId( Integer selectedLegendSetId )
    {
        this.selectedLegendSetId = selectedLegendSetId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        // ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------

        if ( alternativeName != null && alternativeName.trim().length() == 0 )
        {
            alternativeName = null;
        }

        if ( code != null && code.trim().length() == 0 )
        {
            code = null;
        }

        if ( description != null && description.trim().length() == 0 )
        {
            description = null;
        }

        // ---------------------------------------------------------------------
        // Create data element
        // ---------------------------------------------------------------------

        DataElement dataElement = new DataElement();

        DataElementCategoryCombo categoryCombo = dataElementCategoryService
            .getDataElementCategoryCombo( selectedCategoryComboId );

        OptionSet optionSet = optionService.getOptionSet( selectedOptionSetId );
        MapLegendSet legendSet = mappingService.getMapLegendSet( selectedLegendSetId );

        dataElement.setName( name );
        dataElement.setShortName( shortName );
        dataElement.setCode( code );
        dataElement.setDescription( description );
        dataElement.setFormName( formName );
        dataElement.setActive( true );
        dataElement.setDomainType( domainType );
        dataElement.setType( valueType );
        
        if ( DataElement.VALUE_TYPE_STRING.equalsIgnoreCase( valueType ) )
        {
            dataElement.setTextType( textType );
        }
        else
        {
            dataElement.setNumberType( numberType );
        }
        
        dataElement.setAggregationOperator( aggregationOperator );
        dataElement.setUrl( url );
        dataElement.setZeroIsSignificant( zeroIsSignificant );
        dataElement.setCategoryCombo( categoryCombo );
        dataElement.setAggregationLevels( new ArrayList<Integer>( ConversionUtils.getIntegerCollection( aggregationLevels ) ) );
        dataElement.setOptionSet( optionSet );
        dataElement.setLegendSet( legendSet );

        if ( jsonAttributeValues != null )
        {
            AttributeUtils.updateAttributeValuesFromJson( dataElement.getAttributeValues(), jsonAttributeValues,
                attributeService );
        }

        dataElementService.addDataElement( dataElement );

        for ( String id : selectedGroups )
        {
            DataElementGroup group = dataElementService.getDataElementGroup( Integer.parseInt( id ) );

            if ( group != null )
            {
                group.addDataElement( dataElement );
                dataElementService.updateDataElementGroup( group );
            }
        }

        dataElementService.updateDataElement( dataElement );

        return SUCCESS;
    }
}
