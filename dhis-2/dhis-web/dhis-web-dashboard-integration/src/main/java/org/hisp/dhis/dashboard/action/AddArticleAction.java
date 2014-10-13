package org.hisp.dhis.dashboard.action;

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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.system.util.AttributeUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.sql.Types;


import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public class AddArticleAction
        implements Action
{

    private String page_name;
    private String description;
    private String imposter_newpage;
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public void setPage_name( String page_name )
    {
        this.page_name = page_name;
    }
    public String getPage_name( )
    {
        return this.page_name;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getDescription( )
    {
        return this.description;
    }

    public void setImposter_newpage( String imposter_newpage )
    {
        this.imposter_newpage = imposter_newpage;
    }
    public String getImposter_newpage( )
    {
        return this.imposter_newpage;
    }

    public void setDataSource(DataSource dataSource) {

        this.dataSource = dataSource;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
            throws Exception
    {
        jdbcTemplate = new JdbcTemplate(dataSource);

        System.out.println("------- LETS PRINT THE ARTICLE BEFORE SAVING IT  ------");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println(this.getImposter_newpage());
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("------- NOW THE ARTICLE IS READY TO ADD  ------");
        String inserQuery = "INSERT INTO cms_articles (page_name,description,newpage) VALUES(?,?,?)";
        int[] types = {Types.VARCHAR,Types.VARCHAR,Types.VARCHAR};

        jdbcTemplate.update(inserQuery, new Object[] {this.getPage_name(),this.getDescription(),this.getImposter_newpage()

        });

        return SUCCESS;
    }
}


