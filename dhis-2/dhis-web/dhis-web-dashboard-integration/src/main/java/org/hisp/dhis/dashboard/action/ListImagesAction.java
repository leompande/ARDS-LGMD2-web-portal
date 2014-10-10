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
import com.google.gson.Gson;

/**
 * @author Lars Helge Overland
 */
public class ListImagesAction
        implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MessageService messageService;
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





    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
    }

    private InterpretationService interpretationService;

    public void setInterpretationService( InterpretationService interpretationService )
    {
        this.interpretationService = interpretationService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

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


    private Collection<Collection> images;
    public Collection<Collection> getImages()
    {
        return images;
    }

    private Collection<String> imageRows;
    public Collection<String> geImageRows()
    {
        return imageRows;
    }


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
//        messageCount = messageService.getUnreadMessageConversationCount();
//
//        interpretationCount = interpretationService.getNewInterpretationCount();



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

        images = new ArrayList<Collection> (  );


        SqlRowSet image_rows = jt.queryForRowSet("SELECT * FROM cms_files WHERE file_type='image'");

        while (image_rows.next()) {

            imageRows = new ArrayList<String> (  );


            if(image_rows.getString("id") != null && !image_rows.getString("id").isEmpty()) {
                imageRows.add(new String(image_rows.getString("id")));
            }
            if(image_rows.getString("file_name") != null && !image_rows.getString("file_name").isEmpty()) {
                imageRows.add(new String(image_rows.getString("file_name")));

            }

            if(image_rows.getString("status") != null && !image_rows.getString("status").isEmpty()) {
                imageRows.add(new String(image_rows.getString("status")));
            }

            images.add(imageRows);

        }

        ds.destroy();


        return SUCCESS;
    }


}
