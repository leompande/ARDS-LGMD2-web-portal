package org.hisp.dhis.startup;

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

import java.sql.ResultSet;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.jdbc.batchhandler.MapBatchHandler;
import org.hisp.dhis.mapping.Map;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.hisp.dhis.user.User;

public class MapViewUpgrader
    extends AbstractStartupRoutine
{
    private static final Log log = LogFactory.getLog( MapViewUpgrader.class );
    
    private StatementManager statementManager;
    
    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    
    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }
    
    @Override
    public void execute()
    {
        executeSql( "update mapview set valuetype=mapvaluetype where valuetype is null" );
        executeSql( "update mapview set legendtype=maplegendtype where legendtype is null" );
        executeSql( "update mapview set legendsetid=maplegendsetid where legendsetid is null" );
        
        executeSql( "alter table mapview drop column mapvaluetype" );
        executeSql( "alter table mapview drop column maplegendtype" );
        executeSql( "alter table mapview drop column maplegendsetid" );
        
        executeSql( "alter table mapview drop column bounds" );
        executeSql( "alter table mapview drop column code" );
        executeSql( "alter table mapview drop column periodtypeid" );
        
        executeSql( "update mapview set layer='thematic1' where layer is null" );
        executeSql( "update mapview set valuetype='dataElement' where valuetype='dataelement'" );
        executeSql( "alter table mapview alter column opacity type double precision" );
        
        String sql = "select mapviewid, name, userid, longitude, latitude, zoom from mapview where mapviewid not in (" +
            "select mapviewid from mapmapviews)";

        BatchHandler<Map> batchHandler = batchHandlerFactory.createBatchHandler( MapBatchHandler.class ).init();
        
        try
        {
            ResultSet rs = statementManager.getHolder().getStatement().executeQuery( sql );
            
            while ( rs.next() )
            {
                log.info( "Upgrading map view..." );
                
                User user = null;
                int userId = rs.getInt( "userid" );
                int mapViewId = rs.getInt( "mapviewid" );
                
                if ( userId != 0 )
                {
                    user = new User();
                    user.setId( userId );
                }

                String uid = CodeGenerator.generateCode();
                
                Map map = new Map( rs.getString( "name" ), user, 
                    rs.getDouble( "longitude" ), rs.getDouble( "latitude" ), rs.getInt( "zoom" ) );
                map.setUid( uid );

                int mapId = batchHandler.insertObject( map, true );
                
                statementManager.getHolder().executeUpdate( "insert into mapmapviews (mapid,mapviewid,sort_order) values(" + mapId + "," + mapViewId + ",0);" );
                
                log.info( "Upgraded map view: " + map );
            }
        }
        catch ( Exception ex )
        {
            log.debug( "Error", ex );
            return;
        }
        finally
        {
            batchHandler.flush();
        }

        executeSql( "alter table mapview drop column name" );
        executeSql( "alter table mapview drop column userid" );
        executeSql( "alter table mapview drop column longitude" );
        executeSql( "alter table mapview drop column latitude" );
        executeSql( "alter table mapview drop column zoom" );
    }

    private int executeSql( String sql )
    {
        try
        {
            return statementManager.getHolder().executeUpdate( sql );
        }
        catch ( Exception ex )
        {
            log.debug( "Execution error", ex );
            return -1;
        }
    }
}
