package org.hisp.dhis.api.webdomain;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.BaseCollection;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dxf2.metadata.ExchangeClasses;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

/**
 * At some point this class will be extended to show all available options
 * for a current user for this resource. For now it is only used for index page.
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "dxf2", namespace = DxfNamespaces.DXF_2_0 )
public class Resources
    extends BaseCollection
{
    private List<Resource> resources = new ArrayList<Resource>();

    public Resources()
    {
        generateResources();
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "resources", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "resource", namespace = DxfNamespaces.DXF_2_0 )
    public List<Resource> getResources()
    {
        return resources;
    }

    public void setResources( List<Resource> resources )
    {
        this.resources = resources;
    }

    //----------------------------------------------------------------------------------------------
    // Helpers
    //----------------------------------------------------------------------------------------------

    private void generateResources()
    {
        List<String> requestMethods = new ArrayList<String>();
        requestMethods.add( RequestMethod.GET.toString() );

        List<String> mediaTypes = new ArrayList<String>();
        mediaTypes.add( MediaType.TEXT_HTML.toString() );
        mediaTypes.add( MediaType.APPLICATION_JSON.toString() );
        mediaTypes.add( MediaType.APPLICATION_XML.toString() );
        mediaTypes.add( new MediaType( "application", "javascript" ).toString() );

        for ( Map.Entry<Class<? extends IdentifiableObject>, String> entry : ExchangeClasses.getAllExportMap().entrySet() )
        {
            resources.add( new Resource( StringUtils.capitalize( entry.getValue() ), entry.getKey(), requestMethods, mediaTypes ) );
        }

        Collections.sort(resources, new Comparator<Resource>()
        {
            @Override
            public int compare( Resource o1, Resource o2 )
            {
                return o1.getName().compareTo( o2.getName() );
            }
        });
    }
}
