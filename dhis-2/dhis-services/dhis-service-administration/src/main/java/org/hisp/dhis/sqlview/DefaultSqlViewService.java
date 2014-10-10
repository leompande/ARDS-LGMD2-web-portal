package org.hisp.dhis.sqlview;

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

import java.util.Collection;
import java.util.Map;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.system.grid.ListGrid;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Dang Duy Hieu
 * @version $Id DefaultSqlViewService.java July 06, 2010$
 */
@Transactional
public class DefaultSqlViewService
    implements SqlViewService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<SqlView> sqlViewStore;

    public void setSqlViewStore( GenericIdentifiableObjectStore<SqlView> sqlViewStore )
    {
        this.sqlViewStore = sqlViewStore;
    }

    private SqlViewExpandStore sqlViewExpandStore;

    public void setSqlViewExpandStore( SqlViewExpandStore sqlViewExpandStore )
    {
        this.sqlViewExpandStore = sqlViewExpandStore;
    }

    // -------------------------------------------------------------------------
    // Implement methods
    // -------------------------------------------------------------------------

    @Override
    public void deleteSqlView( SqlView sqlViewObject )
    {
        dropViewTable( sqlViewObject.getViewName() );
        
        sqlViewStore.delete( sqlViewObject );
    }

    @Override
    public Collection<SqlView> getAllSqlViews()
    {
        return sqlViewStore.getAll();
    }

    @Override
    public SqlView getSqlView( int viewId )
    {
        return sqlViewStore.get( viewId );
    }

    @Override
    public SqlView getSqlViewByUid( String uid )
    {
        return sqlViewStore.getByUid( uid );
    }

    @Override
    public SqlView getSqlView( String viewName )
    {
        return sqlViewStore.getByName( viewName );
    }

    @Override
    public int saveSqlView( SqlView sqlViewObject )
    {
        return sqlViewStore.save( sqlViewObject );
    }

    @Override
    public void updateSqlView( SqlView sqlViewObject )
    {
        sqlViewStore.update( sqlViewObject );
    }

    @Override
    public int getSqlViewCount()
    {
        return sqlViewStore.getCount();
    }

    @Override
    public Collection<SqlView> getSqlViewsBetween( int first, int max )
    {
        return sqlViewStore.getAllOrderedName( first, max );
    }

    @Override
    public Collection<SqlView> getSqlViewsBetweenByName( String name, int first, int max )
    {
        return sqlViewStore.getAllLikeNameOrderedName( name, first, max );
    }

    @Override
    public String makeUpForQueryStatement( String query )
    {
        return query.replaceAll( "\\s*;\\s+", ";" ).replaceAll( ";+", ";" ).replaceAll( "\\s+", " " ).trim();
    }

    // -------------------------------------------------------------------------
    // SqlView expanded
    // -------------------------------------------------------------------------

    @Override
    public boolean viewTableExists( String viewTableName )
    {
        return sqlViewExpandStore.viewTableExists( viewTableName );
    }

    @Override
    public String createViewTable( SqlView sqlViewInstance )
    {
        return sqlViewExpandStore.createViewTable( sqlViewInstance );
    }

    @Override
    public Grid getSqlViewGrid( SqlView sqlView, Map<String, String> criteria )
    {
        Grid sqlViewGrid = new ListGrid();

        sqlViewExpandStore.setUpDataSqlViewTable( sqlViewGrid, sqlView.getViewName(), criteria );

        return sqlViewGrid;
    }

    @Override
    public String testSqlGrammar( String sql )
    {
        return sqlViewExpandStore.testSqlGrammar( sql );
    }

    @Override
    public void dropViewTable( String sqlViewTableName )
    {
        sqlViewExpandStore.dropViewTable( sqlViewTableName );
    }
}