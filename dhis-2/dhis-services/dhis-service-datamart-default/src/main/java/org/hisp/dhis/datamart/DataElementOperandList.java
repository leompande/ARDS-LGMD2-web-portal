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

import java.util.Arrays;
import java.util.List;

import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;

/**
 * Wrapper class for a list of DataElementOperands which encapsulates logic for
 * initializing, adding values and checking the state of the list. 
 * 
 * @author Lars Helge Overland
 */
public class DataElementOperandList
{
    private List<DataElementOperand> operands;
    
    private Object[] valueList;
    
    private boolean hasValues;
    
    private int offset;
    
    public DataElementOperandList( List<DataElementOperand> operands )
    {
        this.operands = operands;
    }
    
    public void init( Period period, OrganisationUnit unit, OrganisationUnitGroup group )
    {
        this.hasValues = false;
        this.offset = group != null ? 3 : 2;
        
        if ( valid() )
        {
            this.valueList = new Object[operands.size() + offset];
            this.valueList[0] = period.getId();
            this.valueList[1] = unit.getId();
            
            if ( group != null && group.getId() != 0 )
            {
                this.valueList[2] = group.getId();
            }
        }
    }
    
    public void addValue( DataElementOperand operand, Double value )
    {
        if ( valid() )
        {
            final int index = operands.indexOf( operand );
            
            if ( index != -1 && value != null )
            {                
                this.valueList[index + offset] = value;
                this.hasValues = true;
            }
        }
    }
    
    public List<Object> getList()
    {
        return valid() ? Arrays.asList( this.valueList ) : null;
    }
    
    public boolean valid()
    {
        return operands != null && operands.size() > 0;
    }
    
    public boolean hasValues()
    {
        return hasValues;
    }
}
