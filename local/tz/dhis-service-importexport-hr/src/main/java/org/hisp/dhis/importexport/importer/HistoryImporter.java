package org.hisp.dhis.importexport.importer;

/*
 * Copyright (c) 2004-2012, University of Oslo, University Of Dar es salaam
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

import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.hr.History;
import org.hisp.dhis.hr.HistoryService;
import org.hisp.dhis.importexport.HrGroupMemberType;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.mapping.HrNameMappingUtil;

/**
 * @author John Francis Mukulu<john.f.mukulu@gmail.com>
 * @version $Id$
 */
public class HistoryImporter
    extends AbstractHrImporter<History> implements Importer<History>
{
    protected HistoryService historyService;

    public HistoryImporter()
    {
    }
    
    public HistoryImporter( BatchHandler<History> batchHandler, HistoryService historyService )
    {
        this.batchHandler = batchHandler;
        this.historyService = historyService;
    }
    
    @Override
    public void importObject( History object, ImportParams params )
    {
    	HrNameMappingUtil.addHistoryMapping(object.getId(), object.getHistory());
        
        read( object, HrGroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( History object )
    {
        batchHandler.addObject( object );        
    }

    @Override
    protected void importMatching( History object, History match )
    {
    	match.setId( object.getId() );
        match.setHistory( object.getHistory() );
        match.setPerson(object.getPerson());
        match.setReason(object.getReason());
        match.setAttribute(object.getAttribute());
        match.setStartDate(object.getStartDate());
        
        historyService.updateHistory(match);
    }

    @Override
    protected History getMatching( History object )
    {
        return historyService.getHistory(object.getId());
    }

    @Override
    protected boolean isIdentical( History object, History existing )
    {
        return object.getHistory().equals( existing.getHistory() );
    }
}
