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
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;

import java.util.List;

/**
 * @author Lars Helge Overland
 * @version $Id: AbstractIndicatorGroupConverter.java 4646 2008-02-26 14:54:29Z larshelg $
 */
public class IndicatorGroupImporter
    extends AbstractImporter<IndicatorGroup> implements Importer<IndicatorGroup>
{
    protected IndicatorService indicatorService;

    public IndicatorGroupImporter()
    {
    }

    public IndicatorGroupImporter( BatchHandler<IndicatorGroup> batchHandler, IndicatorService indicatorService )
    {
        this.batchHandler = batchHandler;
        this.indicatorService = indicatorService;
    }

    @Override
    public void importObject( IndicatorGroup object, ImportParams params )
    {
        NameMappingUtil.addIndicatorGroupMapping( object.getId(), object.getName() );

        read( object, GroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( IndicatorGroup object )
    {
        batchHandler.addObject( object );
    }

    @Override
    protected void importMatching( IndicatorGroup object, IndicatorGroup match )
    {
        match.setName( object.getName() );

        indicatorService.updateIndicatorGroup( object );
    }

    @Override
    protected IndicatorGroup getMatching( IndicatorGroup object )
    {
        List<IndicatorGroup> indicatorGroupByName = indicatorService.getIndicatorGroupByName( object.getName() );
        return indicatorGroupByName.isEmpty() ? null : indicatorGroupByName.get( 0 );
    }

    @Override
    protected boolean isIdentical( IndicatorGroup object, IndicatorGroup existing )
    {
        return object.getName().equals( existing.getName() );
    }
}
