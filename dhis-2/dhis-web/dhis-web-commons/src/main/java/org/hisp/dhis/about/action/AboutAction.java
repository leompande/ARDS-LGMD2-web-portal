package org.hisp.dhis.about.action;

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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.system.SystemInfo;
import org.hisp.dhis.system.SystemService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.util.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;

//import com.opensymphony.xwork2.Action;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.interpretation.InterpretationService;
import org.hisp.dhis.message.MessageService;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.system.util.AttributeUtils;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
/**
 * @author Dang Duy Hieu
 */
public class AboutAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private SystemService systemService;

    @Autowired
    private CurrentUserService currentUserService;

    private MessageService messageService;
    public DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
        
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public JdbcTemplate getTemplate(){
        return jdbcTemplate;

    }

    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
    }

    private InterpretationService interpretationService;

    public void setInterpretationService( InterpretationService interpretationService )
    {
        this.interpretationService = interpretationService;
    }



    private SystemInfo info;
    
    public SystemInfo getInfo()
    {
        return info;
    }

    private String userAgent;

    public String getUserAgent()
    {
        return userAgent;
    }
    
    private boolean currentUserIsSuper;

    public boolean getCurrentUserIsSuper()
    {
        return currentUserIsSuper;
    }



    private long messageCount;

    public long getMessageCount()
    {
        return messageCount;
    }

    private long interpretationCount;

    public long getInterpretationCount()
    {
        return interpretationCount;
    }

    //////////// for html ///////////////
    private Collection<Collection> htmlContents;
    public Collection<Collection> getHtmlContents()
    {
        return htmlContents;
    }
    private Collection<String> htmlRows;
    public Collection<String> getHtmlRows()
    {
        return htmlRows;
    }
    //////////// for Links ///////////////
    private Collection<Collection> otherLinks;
    public Collection<Collection> getOtherLinks()
    {
        return otherLinks;
    }
    private Collection<String> linkRows;
    public Collection<String> getLinkRows()
    {
        return linkRows;
    }

    ///////// for documents ///////////

    private Collection<Collection> docs;
    public Collection<Collection> getDocs()
    {
        return docs;
    }

    private Collection<String> docRows;
    public Collection<String> getDocRows()
    {
        return docRows;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        info = systemService.getSystemInfo();
                
        HttpServletRequest request = ServletActionContext.getRequest();

        userAgent = request.getHeader( ContextUtils.HEADER_USER_AGENT );
        
        currentUserIsSuper = currentUserService.currentUserIsSuper();




        //messageCount = messageService.getUnreadMessageConversationCount();

        //interpretationCount = interpretationService.getNewInterpretationCount();



        ////////////////// start database driver //////////////////
        SingleConnectionDataSource ds = new SingleConnectionDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql:dhis");
        ds.setUsername("dhis");
        ds.setPassword("dhis");

        JdbcTemplate jt = new JdbcTemplate(ds);
        ///////////////end  database dirver ///////////////


        ///////////////////////////////////////////////
        /////////// HTML CONETNTS/////////////////////

        htmlContents = new ArrayList<Collection> (  );


        SqlRowSet html_rows = jt.queryForRowSet("SELECT * FROM cms_frontpage ");

        while (html_rows.next()) {

            htmlRows = new ArrayList<String> (  );


            if(html_rows.getString("id") != null && !html_rows.getString("id").isEmpty()) {
                htmlRows.add(new String(html_rows.getString("id")));
            }
            if(html_rows.getString("content") != null && !html_rows.getString("content").isEmpty()) {
                htmlRows.add(new String(html_rows.getString("content")));
            }
            if(html_rows.getString("origin") != null && !html_rows.getString("origin").isEmpty()) {
                htmlRows.add(new String(html_rows.getString("origin")));
            }
            if(html_rows.getString("status") != null && !html_rows.getString("status").isEmpty()) {
                htmlRows.add(new String(html_rows.getString("status")));
            }


            htmlContents.add(htmlRows);

        }

        ///////////////// HTML CONTENTS ////////////////////////

        /////////// OTHER LINKS/////////////////////

        otherLinks = new ArrayList<Collection> (  );


        SqlRowSet link_rows = jt.queryForRowSet("SELECT * FROM cms_other_links where status = 'enabled'");

        while (link_rows.next()) {

            linkRows = new ArrayList<String> (  );

            if(link_rows.getString("id") != null && !link_rows.getString("id").isEmpty()) {
                linkRows.add(new String(link_rows.getString("id")));
            }
            if(link_rows.getString("organisation_name") != null && !link_rows.getString("organisation_name").isEmpty()) {
                linkRows.add(new String(link_rows.getString("organisation_name")));
            }
            if(link_rows.getString("url") != null && !link_rows.getString("url").isEmpty()) {
                linkRows.add(new String(link_rows.getString("url")));
            }


            otherLinks.add(linkRows);

        }

        ///////////////// DOCUMENTS ////////////////////////

        docs = new ArrayList<Collection> (  );


        SqlRowSet doc_rows = jt.queryForRowSet("SELECT * FROM cms_files WHERE file_type='document'");

        while (doc_rows.next()) {

            docRows = new ArrayList<String> (  );

            if(doc_rows.getString("id") != null && !doc_rows.getString("id").isEmpty()) {
                docRows.add(new String(doc_rows.getString("id")));
            }
            if(doc_rows.getString("file_name") != null && !doc_rows.getString("file_name").isEmpty()) {
                docRows.add(new String(doc_rows.getString("file_name")));

            }
            if(doc_rows.getString("url") != null && !doc_rows.getString("url").isEmpty()) {
                docRows.add(new String(doc_rows.getString("url")));
            }
            if(doc_rows.getString("status") != null && !doc_rows.getString("status").isEmpty()) {
                docRows.add(new String(doc_rows.getString("status")));
            }

            docs.add(docRows);

        }


        ds.destroy();

        return SUCCESS;
    }
}
