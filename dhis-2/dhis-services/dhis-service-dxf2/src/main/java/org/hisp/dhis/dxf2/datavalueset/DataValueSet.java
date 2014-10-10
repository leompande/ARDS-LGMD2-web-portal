package org.hisp.dhis.dxf2.datavalueset;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dxf2.datavalue.DataValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@JacksonXmlRootElement(localName = "dataValueSet", namespace = DxfNamespaces.DXF_2_0)
public class DataValueSet
{
    //--------------------------------------------------------------------------
    // Options
    //--------------------------------------------------------------------------

    protected String dataElementIdScheme;

    protected String orgUnitIdScheme;

    protected Boolean dryRun;

    protected String strategy;

    //--------------------------------------------------------------------------
    // Properties
    //--------------------------------------------------------------------------

    protected String dataSet;

    protected String completeDate;

    protected String period;

    protected String orgUnit;

    protected List<DataValue> dataValues = new ArrayList<DataValue>();

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    public DataValueSet()
    {
    }

    //--------------------------------------------------------------------------
    // Getters and setters
    //--------------------------------------------------------------------------

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getDataElementIdScheme()
    {
        return dataElementIdScheme;
    }

    public void setDataElementIdScheme( String dataElementIdScheme )
    {
        this.dataElementIdScheme = dataElementIdScheme;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getOrgUnitIdScheme()
    {
        return orgUnitIdScheme;
    }

    public void setOrgUnitIdScheme( String orgUnitIdScheme )
    {
        this.orgUnitIdScheme = orgUnitIdScheme;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getDryRun()
    {
        return dryRun;
    }

    public void setDryRun( Boolean dryRun )
    {
        this.dryRun = dryRun;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getStrategy()
    {
        return strategy;
    }

    public void setStrategy( String strategy )
    {
        this.strategy = strategy;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getDataSet()
    {
        return dataSet;
    }

    public void setDataSet( String dataSet )
    {
        this.dataSet = dataSet;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getCompleteDate()
    {
        return completeDate;
    }

    public void setCompleteDate( String completeDate )
    {
        this.completeDate = completeDate;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getPeriod()
    {
        return period;
    }

    public void setPeriod( String period )
    {
        this.period = period;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getOrgUnit()
    {
        return orgUnit;
    }

    public void setOrgUnit( String orgUnit )
    {
        this.orgUnit = orgUnit;
    }

    @JsonProperty(value = "dataValues")
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlElementWrapper(localName = "dataValues", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "dataValue", namespace = DxfNamespaces.DXF_2_0)
    public List<DataValue> getDataValues()
    {
        return dataValues;
    }

    public void setDataValues( List<DataValue> dataValues )
    {
        this.dataValues = dataValues;
    }

    //--------------------------------------------------------------------------
    // Logic
    //--------------------------------------------------------------------------

    private Iterator<DataValue> dataValueIterator;

    public void refreshDataValueIterator()
    {
        dataValueIterator = dataValues.iterator();
    }

    public boolean hasNextDataValue()
    {
        if ( dataValueIterator == null )
        {
            refreshDataValueIterator();
        }

        return dataValueIterator.hasNext();
    }

    public DataValue getNextDataValue()
    {
        if ( dataValueIterator == null )
        {
            refreshDataValueIterator();
        }

        return dataValueIterator.next();
    }

    public DataValue getDataValueInstance()
    {
        return new DataValue();
    }

    public void close()
    {
    }

    @Override
    public String toString()
    {
        return "[" + dataSet + ", " + completeDate + ", " + period + ", " + orgUnit + ", " + dataValues.size() + "]";
    }
}
