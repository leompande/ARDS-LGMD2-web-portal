package org.hisp.dhis.jdbc.batchhandler;

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

import org.amplecode.quick.JdbcConfiguration;
import org.amplecode.quick.batchhandler.AbstractBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Lars Helge Overland
 * @version $Id: OrganisationUnitBatchHandler.java 5062 2008-05-01 18:10:35Z larshelg $
 */
public class OrganisationUnitBatchHandler
    extends AbstractBatchHandler<OrganisationUnit>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public OrganisationUnitBatchHandler( JdbcConfiguration config )
    {
        super( config, false, false );
    }

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "organisationunit" );
    }

    @Override
    protected void setAutoIncrementColumn()
    {
        statementBuilder.setAutoIncrementColumn( "organisationunitid" );
    }
    
    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "organisationunitid" );
    }
    
    @Override
    protected void setIdentifierValues( OrganisationUnit unit )
    {        
        statementBuilder.setIdentifierValue( unit.getId() );
    }
    
    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "name" );
        statementBuilder.setUniqueColumn( "shortname" );
        statementBuilder.setUniqueColumn( "code" );
    }
    
    protected void setUniqueValues( OrganisationUnit unit )
    {        
        statementBuilder.setUniqueValue( unit.getName() );        
        statementBuilder.setUniqueValue( unit.getShortName() );
        statementBuilder.setUniqueValue( unit.getCode() );
    }
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "uid" );
        statementBuilder.setColumn( "name" );
        statementBuilder.setColumn( "parentid" );
        statementBuilder.setColumn( "shortname" );
        statementBuilder.setColumn( "code" );
        statementBuilder.setColumn( "description" );
        statementBuilder.setColumn( "openingdate" );
        statementBuilder.setColumn( "closeddate" );
        statementBuilder.setColumn( "active" );
        statementBuilder.setColumn( "comment" );
        statementBuilder.setColumn( "geocode" );
        statementBuilder.setColumn( "featuretype" );
        statementBuilder.setColumn( "coordinates" );
    }
    
    protected void setValues( OrganisationUnit unit )
    {
        statementBuilder.setValue( unit.getUid() );        
        statementBuilder.setValue( unit.getName() );        
        statementBuilder.setValue( unit.getParent() != null ? unit.getParent().getId() : null );
        statementBuilder.setValue( unit.getShortName() );
        statementBuilder.setValue( unit.getCode() );
        statementBuilder.setValue( unit.getDescription() );
        statementBuilder.setValue( unit.getOpeningDate() );
        statementBuilder.setValue( unit.getClosedDate() );
        statementBuilder.setValue( unit.isActive() );
        statementBuilder.setValue( unit.getComment() );
        statementBuilder.setValue( unit.getGeoCode() );
        statementBuilder.setValue( unit.getFeatureType() );
        statementBuilder.setValue( unit.getCoordinates() );
    }
}
