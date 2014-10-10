package org.hisp.dhis.aggregation.jdbc;

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
import java.sql.SQLException;
import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.mapper.RowMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.aggregation.StoreIterator;

/**
 * @author bobj
 */
public class JdbcStoreIterator<T>
    implements StoreIterator<T>
{
    private static final Log log = LogFactory.getLog( JdbcStoreIterator.class );

    private RowMapper<T> rowmapper;

    private ResultSet resultSet;

    public ResultSet getResultSet()
    {
        return resultSet;
    }

    public void setResultSet( ResultSet resultSet )
    {
        this.resultSet = resultSet;
    }

    private StatementHolder holder;

    public StatementHolder getHolder()
    {
        return holder;
    }

    public void setHolder( StatementHolder holder )
    {
        this.holder = holder;
    }

    public JdbcStoreIterator( ResultSet resultSet, StatementHolder holder, RowMapper<T> rowmapper )
    {
        this.resultSet = resultSet;
        this.holder = holder;
        this.rowmapper = rowmapper;
    }

    @Override
    public T next()
    {
        T row = null;
        try
        {
            if ( resultSet.next() )
            {
                row = rowmapper.mapRow( resultSet );
            }
            else
            {
                close();
            }
        }
        catch ( SQLException ex )
        {
            log.warn( "Error reading row: " + ex );
        }
        return row;
    }

    @Override
    public void close()
    {
        try
        {
            if ( !resultSet.isClosed() )
            {
                resultSet.close();
            }
        }
        catch ( SQLException ex )
        {
            log.warn( "Error closing resultset: " + ex );
        }

        holder.close();
    }
}
