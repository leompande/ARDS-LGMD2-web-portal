package org.hisp.dhis.datamart;

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

/**
 * @author Lars Helge Overland
 */
public class OrgUnitOperand
{
    private int periodId;

    private int periodTypeId;
    
    private int orgUnitId;
    
    private int orgUnitGroupId;
    
    private double value;
    
    public OrgUnitOperand()
    {
    }
    
    public OrgUnitOperand( int periodId, int periodTypeId, int orgUnitId, int orgUnitGroupId, double value )
    {
        this.periodId = periodId;
        this.periodTypeId = periodTypeId;
        this.orgUnitId = orgUnitId;
        this.orgUnitGroupId = orgUnitGroupId;
        this.value = value;
    }

    public int getPeriodId()
    {
        return periodId;
    }

    public void setPeriodId( int periodId )
    {
        this.periodId = periodId;
    }

    public int getPeriodTypeId()
    {
        return periodTypeId;
    }

    public void setPeriodTypeId( int periodTypeId )
    {
        this.periodTypeId = periodTypeId;
    }

    public int getOrgUnitId()
    {
        return orgUnitId;
    }

    public int getOrgUnitGroupId()
    {
        return orgUnitGroupId;
    }

    public double getValue()
    {
        return value;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + orgUnitId;
        result = prime * result + orgUnitGroupId;
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        
        if ( obj == null )
        {
            return false;
        }
        
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        
        final OrgUnitOperand other = (OrgUnitOperand) obj;
        
        return orgUnitId == other.orgUnitId && orgUnitGroupId == other.orgUnitGroupId;
    }
    
    @Override
    public String toString()
    {
        return "[period: " + periodId + ", org unit: " + orgUnitId + ", group: " + orgUnitGroupId + ", value: " + value + "]";
    }
}
