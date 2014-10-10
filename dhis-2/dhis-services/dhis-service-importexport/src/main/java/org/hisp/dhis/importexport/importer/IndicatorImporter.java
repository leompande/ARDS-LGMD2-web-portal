package org.hisp.dhis.importexport.importer;

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

import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;

import java.util.List;

/**
 * @author Lars Helge Overland
 * @version $Id: AbstractIndicatorConverter.java 4753 2008-03-14 12:48:50Z larshelg $
 */
public class IndicatorImporter
    extends AbstractImporter<Indicator> implements Importer<Indicator>
{
    protected IndicatorService indicatorService;

    public IndicatorImporter()
    {
    }

    public IndicatorImporter( BatchHandler<Indicator> batchHandler, IndicatorService indicatorService )
    {
        this.batchHandler = batchHandler;
        this.indicatorService = indicatorService;
    }

    @Override
    public void importObject( Indicator object, ImportParams params )
    {
        NameMappingUtil.addIndicatorMapping( object.getId(), object.getName() );

        read( object, GroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( Indicator object )
    {
        batchHandler.addObject( object );
    }

    @Override
    protected void importMatching( Indicator object, Indicator match )
    {
        match.setName( object.getName() );
        match.setShortName( object.getShortName() );
        match.setCode( object.getCode() );
        match.setDescription( object.getDescription() );
        match.setIndicatorType( object.getIndicatorType() );
        match.setNumerator( object.getNumerator() );
        match.setNumeratorDescription( object.getNumeratorDescription() );
        match.setDenominator( object.getDenominator() );
        match.setDenominatorDescription( object.getDenominatorDescription() );
        match.setLastUpdated( object.getLastUpdated() );

        indicatorService.updateIndicator( match );
    }

    @Override
    protected Indicator getMatching( Indicator object )
    {
        List<Indicator> indicatorByName = indicatorService.getIndicatorByName( object.getName() );
        Indicator match = indicatorByName.isEmpty() ? null : indicatorByName.get( 0 );

        if ( match == null )
        {
            List<Indicator> indicatorByShortName = indicatorService.getIndicatorByShortName( object.getShortName() );
            match = indicatorByShortName.isEmpty() ? null : indicatorByShortName.get( 0 );
        }
        if ( match == null )
        {
            match = indicatorService.getIndicatorByCode( object.getCode() );
        }

        return match;
    }

    @Override
    protected boolean isIdentical( Indicator object, Indicator existing )
    {
        if ( !object.getName().equals( existing.getName() ) )
        {
            return false;
        }
        if ( !object.getShortName().equals( existing.getShortName() ) )
        {
            return false;
        }
        if ( !isSimiliar( object.getCode(), existing.getCode() ) || (isNotNull( object.getCode(), existing.getCode() ) && !object.getCode().equals( existing.getCode() )) )
        {
            return false;
        }
        if ( !isSimiliar( object.getDescription(), existing.getDescription() ) || (isNotNull( object.getDescription(), existing.getDescription() ) && !object.getDescription().equals( existing.getDescription() )) )
        {
            return false;
        }
        if ( !isSimiliar( object.getNumeratorDescription(), existing.getNumeratorDescription() ) || (isNotNull( object.getNumeratorDescription(), existing.getNumeratorDescription() ) && !object.getNumeratorDescription().equals( existing.getNumeratorDescription() )) )
        {
            return false;
        }
        if ( !isSimiliar( object.getDenominatorDescription(), existing.getDenominatorDescription() ) || (isNotNull( object.getDenominatorDescription(), existing.getDenominatorDescription() ) && !object.getDenominatorDescription().equals( existing.getDenominatorDescription() )) )
        {
            return false;
        }

        return true;
    }
}
