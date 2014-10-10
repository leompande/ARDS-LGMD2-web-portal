package org.hisp.dhis.dataset;

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

import java.util.Iterator;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.system.deletion.DeletionHandler;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataSetDeletionHandler
    extends DeletionHandler
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // DeletionHandler implementation
    // -------------------------------------------------------------------------

    @Override
    public String getClassName()
    {
        return DataSet.class.getSimpleName();
    }

    @Override
    public void deleteDataElement( DataElement dataElement )
    {
        Iterator<DataSet> iterator = dataElement.getDataSets().iterator();
        
        while ( iterator.hasNext() )
        {
            DataSet dataSet = iterator.next();
            dataSet.removeDataElement( dataElement );
            dataSetService.updateDataSet( dataSet );
        }
        
        for ( DataSet dataSet : dataSetService.getAllDataSets() )
        {
            boolean update = false;
            
            Iterator<DataElementOperand> operands = dataSet.getCompulsoryDataElementOperands().iterator();
            
            while ( operands.hasNext() )
            {
                DataElementOperand operand = operands.next();
                
                if ( operand.getDataElement().equals( dataElement ) )
                {
                    operands.remove();
                    update = true;
                }
            }
            
            if ( update )
            {
                dataSetService.updateDataSet( dataSet );
            }
        }
    }
    
    @Override
    public void deleteIndicator( Indicator indicator )
    {
        Iterator<DataSet> iterator = indicator.getDataSets().iterator();
        
        while ( iterator.hasNext() )
        {
            DataSet dataSet = iterator.next();
            dataSet.getIndicators().remove( indicator );
            dataSetService.updateDataSet( dataSet );
        }
    }
    
    @Override
    public void deleteSection( Section section )
    {
        DataSet dataSet = section.getDataSet();
        
        if ( dataSet != null )
        {
            dataSet.getSections().remove( section );
            dataSetService.updateDataSet( dataSet );
        }
    }

    @Override
    public void deleteOrganisationUnit( OrganisationUnit unit )
    {
        Iterator<DataSet> iterator = unit.getDataSets().iterator();
        
        while ( iterator.hasNext() )
        {
            DataSet dataSet = iterator.next();
            dataSet.getSources().remove( unit );
            dataSetService.updateDataSet( dataSet );
        }
    }
    
    @Override
    public void deleteOrganisationUnitGroup( OrganisationUnitGroup group )
    {
        Iterator<DataSet> iterator = group.getDataSets().iterator();
        
        while ( iterator.hasNext() )
        {
            DataSet dataSet = iterator.next();
            dataSet.getOrganisationUnitGroups().remove( group );
            dataSetService.updateDataSet( dataSet );
        }
    }
}
