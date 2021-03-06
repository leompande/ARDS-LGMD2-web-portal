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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;



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

import java.io.File;

/**
 * @author Lars Helge Overland
 */
public class UploadImagesAction
        implements Action
{
    private static final Log log = LogFactory.getLog( AddDocsAction.class );

    private static final String HTTP_PREFIX = "http://";

    private static final String HTTPS_PREFIX = "https://";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LocationManager locationManager;

    public void setLocationManager( LocationManager locationManager )
    {
        this.locationManager = locationManager;
    }

    private DocumentService documentService;

    public void setDocumentService( DocumentService documentService )
    {
        this.documentService = documentService;
    }

    @Autowired
    private CurrentUserService currentUserService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String url;

    public void setUrl( String url )
    {
        this.url = url;
    }

    private Boolean external;

    public void setExternal( Boolean external )
    {
        this.external = external;
    }

    private Boolean attachment = false;

    public void setAttachment( Boolean attachment )
    {
        this.attachment = attachment;
    }

    private File file;

    public void setUpload( File file )
    {
        this.file = file;
    }

    private String fileName;

    public void setUploadFileName( String fileName )
    {
        this.fileName = fileName;
    }

    private String contentType;

    public void setUploadContentType( String contentType )
    {
        this.contentType = contentType;
    }


    public DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    /**
     *
     */

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public JdbcTemplate getTemplate(){
        return jdbcTemplate;

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
    public Collection<String> geLinkRows()
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
    ///////// for tab menus ///////////

    private Collection<Collection> menus;
    public Collection<Collection> getMenus()
    {
        return menus;
    }

    private Collection<String> menuRows;
    public Collection<String> getMenuRows()
    {
        return menuRows;
    }

    ///////// for articles ///////////

    private Collection<Collection> article;
    public Collection<Collection> getArticle()
    {
        return article;
    }

    private Collection<String> articleRows;
    public Collection<String> getArticleRows()
    {
        return articleRows;
    }


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
            throws Exception
    {
        Document document = new Document();

        if ( id != null )
        {
            document = documentService.getDocument( id );

        }

        if ( !external && file != null )
        {
            log.info( "Uploading file: '" + fileName + "', content-type: '" + contentType + "'" );

            File destination = locationManager.getFileForWriting( fileName, DocumentService.DIR );

            log.info( "Destination: '" + destination.getAbsolutePath() + "'" );

            boolean fileMoved = file.renameTo( destination );

            if ( !fileMoved )
            {
                throw new RuntimeException( "File could not be moved to: '" + destination.getAbsolutePath() + "'" );
            }

            url = fileName;
            document.setUrl( url );
            document.setContentType( contentType );

        }

        else if ( external )
        {
            if ( !(url.startsWith( HTTP_PREFIX ) || url.startsWith( HTTPS_PREFIX )) )
            {
                url = HTTP_PREFIX + url;
            }

            log.info( "Document name: '" + name + "', url: '" + url + "', external: '" + external + "'" );

            document.setUrl( url );
        }

        document.setAttachment( attachment );

        document.setExternal( external );

        document.setName( name );
        documentService.saveDocument( document );



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


        SqlRowSet html_rows = jt.queryForRowSet("SELECT * FROM cms_frontpage ORDER BY preference_order DESC");

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

        ///////////////////////////////////////////////
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


        ///////////////// TAB MENUS ////////////////////////

        menus = new ArrayList<Collection> (  );


        SqlRowSet menu_rows = jt.queryForRowSet("SELECT * FROM cms_tabmenus ORDER BY menu ASC");

        while (menu_rows.next()) {

            menuRows = new ArrayList<String> (  );

            if(menu_rows.getString("id") != null && !menu_rows.getString("id").isEmpty()) {
                menuRows.add(new String(menu_rows.getString("id")));
            }
            if(menu_rows.getString("menu") != null && !menu_rows.getString("menu").isEmpty()) {
                menuRows.add(new String(menu_rows.getString("menu")));

            }
            menus.add(menuRows);

        }
        ///////////////// ARTICLES ////////////////////////

        article = new ArrayList<Collection> (  );


        SqlRowSet article_rows = jt.queryForRowSet("SELECT * FROM cms_articles");

        while (article_rows.next()) {

            articleRows = new ArrayList<String> (  );

            if(article_rows.getString("id") != null && !article_rows.getString("id").isEmpty()) {
                articleRows.add(new String(article_rows.getString("id")));
            }

            if(article_rows.getString("page_name") != null && !article_rows.getString("page_name").isEmpty()) {
                articleRows.add(new String(article_rows.getString("page_name")));
            }

            if(article_rows.getString("newpage") != null && !article_rows.getString("newpage").isEmpty()) {
                articleRows.add(new String(article_rows.getString("newpage")));
            }
            article.add(articleRows);

        }


        return SUCCESS;
    }
}
