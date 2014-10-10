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
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorService;

import java.util.List;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class IndicatorGroupSetImporter
    extends AbstractImporter<IndicatorGroupSet> implements Importer<IndicatorGroupSet>
{
    protected IndicatorService indicatorService;

    public IndicatorGroupSetImporter()
    {
    }

    public IndicatorGroupSetImporter( BatchHandler<IndicatorGroupSet> batchHandler, IndicatorService indicatorService )
    {
        this.batchHandler = batchHandler;
        this.indicatorService = indicatorService;
    }

    @Override
    public void importObject( IndicatorGroupSet object, ImportParams params )
    {
        NameMappingUtil.addIndicatorGroupSetMapping( object.getId(), object.getName() );

        read( object, GroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( IndicatorGroupSet object )
    {
        indicatorService.addIndicatorGroupSet( object );
    }

    @Override
    protected void importMatching( IndicatorGroupSet object, IndicatorGroupSet match )
    {
        match.setName( object.getName() );

        indicatorService.updateIndicatorGroupSet( match );
    }

    @Override
    protected IndicatorGroupSet getMatching( IndicatorGroupSet object )
    {
        List<IndicatorGroupSet> indicatorGroupSetByName = indicatorService.getIndicatorGroupSetByName( object.getName() );
        return indicatorGroupSetByName.isEmpty() ? null : indicatorGroupSetByName.get( 0 );
    }

    @Override
    protected boolean isIdentical( IndicatorGroupSet object, IndicatorGroupSet existing )
    {
        return object.getName().equals( existing.getName() );
    }
}
