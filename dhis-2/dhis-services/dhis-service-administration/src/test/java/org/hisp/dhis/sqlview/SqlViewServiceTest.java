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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.hisp.dhis.DhisTest;
import org.junit.Test;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class SqlViewServiceTest
    extends DhisTest
{
    private SqlViewService sqlViewService;

    protected static final String SQL1 = "SELECT   *  FROM     _categorystructure;;  ; ;;;  ;; ; ";

    protected static final String SQL2 = "SELECT COUNT(_ous.*) AS so_dem FROM _orgunitstructure AS _ous";

    protected static final String SQL3 = "SELECT COUNT(_cocn.*) AS so_dem, _icgss.indicatorid AS in_id"
        + "FROM _indicatorgroupsetstructure AS _icgss, _categoryoptioncomboname AS _cocn "
        + "GROUP BY _icgss.indicatorid;";

    protected static final String SQL4 = "SELECT de.name, dv.sourceid, dv.value, p.startdate "
        + "FROM dataelement AS de, datavalue AS dv, period AS p " + "WHERE de.dataelementid=dv.dataelementid "
        + "AND dv.periodid=p.periodid LIMIT 10";

    @Override
    public void setUpTest()
    {
        sqlViewService = (SqlViewService) getBean( SqlViewService.ID );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void assertEq( char uniqueCharacter, SqlView sqlView, String sql )
    {
        assertEquals( "SqlView" + uniqueCharacter, sqlView.getName() );
        assertEquals( "Description" + uniqueCharacter, sqlView.getDescription() );
        assertEquals( sql, sqlView.getSqlQuery() );
    }

    // -------------------------------------------------------------------------
    // SqlView
    // -------------------------------------------------------------------------

    @Test
    public void testAddSqlView()
    {
        SqlView sqlViewA = createSqlView( 'A', SQL1 );
        SqlView sqlViewB = createSqlView( 'B', SQL2 );

        int idA = sqlViewService.saveSqlView( sqlViewA );
        int idB = sqlViewService.saveSqlView( sqlViewB );

        sqlViewA = sqlViewService.getSqlView( idA );
        sqlViewB = sqlViewService.getSqlView( idB );

        assertEquals( idA, sqlViewA.getId() );
        assertEq( 'A', sqlViewA, SQL1 );

        assertEquals( idB, sqlViewB.getId() );
        assertEq( 'B', sqlViewB, SQL2 );

        sqlViewService.deleteSqlView( sqlViewA );
        sqlViewService.deleteSqlView( sqlViewB );
    }

    @Test
    public void testUpdateSqlView()
    {
        SqlView sqlView = createSqlView( 'A', SQL1 );

        int id = sqlViewService.saveSqlView( sqlView );

        sqlView = sqlViewService.getSqlView( id );

        assertEq( 'A', sqlView, SQL1 );

        sqlView.setName( "SqlViewC" );

        sqlViewService.updateSqlView( sqlView );

        sqlView = sqlViewService.getSqlView( id );

        assertEquals( sqlView.getName(), "SqlViewC" );

        sqlViewService.deleteSqlView( sqlView );
    }

    @Test
    public void testDeleteAndGetSqlView()
    {
        SqlView sqlViewA = createSqlView( 'A', SQL3 );
        SqlView sqlViewB = createSqlView( 'B', SQL4 );

        int idA = sqlViewService.saveSqlView( sqlViewA );
        int idB = sqlViewService.saveSqlView( sqlViewB );

        assertNotNull( sqlViewService.getSqlView( idA ) );
        assertNotNull( sqlViewService.getSqlView( idB ) );

        sqlViewService.deleteSqlView( sqlViewService.getSqlView( idA ) );

        assertNull( sqlViewService.getSqlView( idA ) );
        assertNotNull( sqlViewService.getSqlView( idB ) );

        sqlViewService.deleteSqlView( sqlViewService.getSqlView( idB ) );

        assertNull( sqlViewService.getSqlView( idA ) );
        assertNull( sqlViewService.getSqlView( idB ) );
    }

    @Test
    public void testGetSqlViewByName()
        throws Exception
    {
        SqlView sqlViewA = createSqlView( 'A', SQL1 );
        SqlView sqlViewB = createSqlView( 'B', SQL2 );

        int idA = sqlViewService.saveSqlView( sqlViewA );
        int idB = sqlViewService.saveSqlView( sqlViewB );

        assertEquals( sqlViewService.getSqlView( "SqlViewA" ).getId(), idA );
        assertEquals( sqlViewService.getSqlView( "SqlViewB" ).getId(), idB );
        assertNull( sqlViewService.getSqlView( "SqlViewC" ) );

        sqlViewService.deleteSqlView( sqlViewA );
        sqlViewService.deleteSqlView( sqlViewB );
    }

    @Test
    public void testGetAllSqlViews()
    {
        SqlView sqlViewA = createSqlView( 'A', SQL1 );
        SqlView sqlViewB = createSqlView( 'B', SQL2 );
        SqlView sqlViewC = createSqlView( 'C', SQL3 );
        SqlView sqlViewD = createSqlView( 'D', SQL4 );

        sqlViewService.saveSqlView( sqlViewA );
        sqlViewService.saveSqlView( sqlViewB );
        sqlViewService.saveSqlView( sqlViewC );

        Collection<SqlView> sqlViews = sqlViewService.getAllSqlViews();

        assertEquals( sqlViews.size(), 3 );
        assertTrue( sqlViews.contains( sqlViewA ) );
        assertTrue( sqlViews.contains( sqlViewB ) );
        assertTrue( sqlViews.contains( sqlViewC ) );
        assertTrue( !sqlViews.contains( sqlViewD ) );

        sqlViewService.deleteSqlView( sqlViewA );
        sqlViewService.deleteSqlView( sqlViewB );
        sqlViewService.deleteSqlView( sqlViewC );
    }

    @Test
    public void testMakeUpForQueryStatement()
    {
        SqlView sqlViewA = createSqlView( 'A', SQL1 );

        sqlViewA.setSqlQuery( sqlViewService.makeUpForQueryStatement( sqlViewA.getSqlQuery() ) );

        int idA = sqlViewService.saveSqlView( sqlViewA );

        assertEquals( sqlViewService.getSqlView( "SqlViewA" ).getId(), idA );

        SqlView sqlViewB = sqlViewService.getSqlView( idA );

        assertEq( 'A', sqlViewB, "SELECT * FROM _categorystructure;" );

        sqlViewService.deleteSqlView( sqlViewService.getSqlView( idA ) );
    }

    @Test
    public void testSetUpViewTableName()
    {
        SqlView sqlViewC = createSqlView( 'C', SQL3 );
        SqlView sqlViewD = createSqlView( 'D', SQL4 );

        assertEquals( "_view_sqlviewc", sqlViewC.getViewName() );
        assertNotSame( "_view_sqlviewc", sqlViewD.getViewName() );

    }

    @Test
    public void testTestSqlGrammar()
    {
        String sql = "select de.name, de.name from dataelement de";

        assertNotSame( sqlViewService.testSqlGrammar( sql ), "" );

        sql += " xyz";

        assertNotSame( sqlViewService.testSqlGrammar( sql ), "" );
    }
}
