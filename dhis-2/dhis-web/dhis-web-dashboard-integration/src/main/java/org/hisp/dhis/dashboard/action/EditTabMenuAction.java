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
import java.sql.Types;
import javax.sql.DataSource;

public class EditTabMenuAction
        implements Action
{

    private String menu;
    private String menu_id;
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public void setMenu( String menu )
    {
        this.menu = menu;
    }
    public String getMenu( )
    {
        return this.menu;
    }

    public void setMenu_id( String menu_id )
    {
        this.menu_id = menu_id;
    }
    public String getMenu_id( )
    {
        return this.menu_id;
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

        int[] types = {Types.VARCHAR,Types.BIGINT};
        jdbcTemplate = new JdbcTemplate(dataSource);
        if (this.getMenu() == null || this.getMenu().isEmpty()) {

        }else{
            String inserQuery = "UPDATE  cms_tabmenus SET menu = ? WHERE id = ?";
            jdbcTemplate.update(inserQuery, new Object[] {this.getMenu(),this.getMenu_id()},types);
        }

        return SUCCESS;
    }
}


