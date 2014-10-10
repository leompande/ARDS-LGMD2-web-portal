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
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import java.util.List;

import static org.apache.commons.lang.StringUtils.defaultIfEmpty;

/**
 * @author Lars Helge Overland
 * @version $Id: AbstractOrganisationUnitConverter.java 6251 2008-11-10 14:37:05Z larshelg $
 */
public class OrganisationUnitImporter
    extends AbstractImporter<OrganisationUnit> implements Importer<OrganisationUnit>
{
    protected OrganisationUnitService organisationUnitService;

    public OrganisationUnitImporter()
    {
    }

    public OrganisationUnitImporter( BatchHandler<OrganisationUnit> batchHandler, OrganisationUnitService organisationUnitService )
    {
        this.batchHandler = batchHandler;
        this.organisationUnitService = organisationUnitService;
    }

    @Override
    public void importObject( OrganisationUnit object, ImportParams params )
    {
        NameMappingUtil.addOrganisationUnitMapping( object.getId(), object.getName() );

        read( object, GroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( OrganisationUnit object )
    {
        batchHandler.addObject( object );
    }

    @Override
    protected void importMatching( OrganisationUnit object, OrganisationUnit match )
    {
        match.setName( object.getName() );
        match.setShortName( defaultIfEmpty( object.getShortName(), match.getShortName() ) );
        match.setCode( defaultIfEmpty( object.getCode(), match.getCode() ) );
        match.setOpeningDate( object.getOpeningDate() );
        match.setClosedDate( object.getClosedDate() );
        match.setActive( object.isActive() );
        match.setComment( defaultIfEmpty( object.getComment(), match.getComment() ) );
        match.setGeoCode( defaultIfEmpty( object.getGeoCode(), match.getGeoCode() ) );
        match.setFeatureType( defaultIfEmpty( object.getFeatureType(), match.getFeatureType() ) );
        match.setCoordinates( defaultIfEmpty( object.getCoordinates(), match.getCoordinates() ) );
        match.setLastUpdated( object.getLastUpdated() );

        organisationUnitService.updateOrganisationUnit( match );
    }

    @Override
    protected OrganisationUnit getMatching( OrganisationUnit object )
    {
        List<OrganisationUnit> organisationUnitByName = organisationUnitService.getOrganisationUnitByName( object.getName() );
        OrganisationUnit match = organisationUnitByName.isEmpty() ? null : organisationUnitByName.get( 0 );

        if ( match == null )
        {
            match = organisationUnitService.getOrganisationUnitByCode( object.getCode() );
        }

        return match;
    }

    @Override
    protected boolean isIdentical( OrganisationUnit object, OrganisationUnit existing )
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
        if ( !isSimiliar( object.getOpeningDate(), existing.getOpeningDate() ) || (isNotNull( object.getOpeningDate(), existing.getOpeningDate() ) && !object.getOpeningDate().equals( existing.getOpeningDate() )) )
        {
            return false;
        }
        if ( !isSimiliar( object.getClosedDate(), existing.getClosedDate() ) || (isNotNull( object.getClosedDate(), existing.getClosedDate() ) && !object.getClosedDate().equals( existing.getClosedDate() )) )
        {
            return false;
        }
        if ( object.isActive() != existing.isActive() )
        {
            return false;
        }
        if ( !isSimiliar( object.getComment(), existing.getComment() ) || (isNotNull( object.getComment(), existing.getComment() ) && !object.getComment().equals( existing.getComment() )) )
        {
            return false;
        }
        if ( !isSimiliar( object.getGeoCode(), existing.getGeoCode() ) || (isNotNull( object.getGeoCode(), existing.getGeoCode() ) && !object.getGeoCode().equals( existing.getGeoCode() )) )
        {
            return false;
        }
        if ( !isSimiliar( object.getFeatureType(), existing.getFeatureType() ) || (isNotNull( object.getFeatureType(), existing.getFeatureType() ) && !object.getFeatureType().equals( existing.getFeatureType() )) )
        {
            return false;
        }
        if ( !isSimiliar( object.getCoordinates(), existing.getCoordinates() ) || (isNotNull( object.getCoordinates(), existing.getCoordinates() ) && !object.getCoordinates().equals( existing.getCoordinates() )) )
        {
            return false;
        }

        return true;
    }
}
