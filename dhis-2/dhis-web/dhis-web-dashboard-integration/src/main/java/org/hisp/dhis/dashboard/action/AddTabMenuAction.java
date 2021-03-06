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

public class AddTabMenuAction
        implements Action
{

    private String menu;
    private int order = 0;
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

    public void setOrder( int order )
    {
        this.order = order;
    }
    public int getOrder( )
    {
        return this.order;
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

        String inserQuery = "insert into cms_tabmenus (menu,preference_order) values (?,?) ";
        jdbcTemplate = new JdbcTemplate(dataSource);
        if(this.getMenu() == null || this.getMenu().isEmpty()){

        }else{
            if(this.getMenu().equals("Agriculture")){
                this.setOrder(1);
            }
            if(this.getMenu().equals("Livestock")){
                this.setOrder(2);
            }
            if(this.getMenu().equals("Fisheries")){
                this.setOrder(3);
            }
            if(this.getMenu().equals("Trade")){
                this.setOrder(4);
            }
            jdbcTemplate.update(inserQuery, new Object[] {this.getMenu(),this.getOrder()});

        }

        return SUCCESS;


    }
}
