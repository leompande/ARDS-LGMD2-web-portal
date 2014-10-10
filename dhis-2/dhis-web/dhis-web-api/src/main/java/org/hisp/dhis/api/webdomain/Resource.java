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
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.BaseLinkableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;

import java.util.ArrayList;
import java.util.List;

/**
 * At some point this class will be extended to show all available options
 * for a current user for this resource. For now it is only used for index page.
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "resource", namespace = DxfNamespaces.DXF_2_0 )
public class Resource
    extends BaseLinkableObject
{
    private String name;

    private Class<?> clazz;

    private List<String> methods = new ArrayList<String>();

    private List<String> mediaTypes = new ArrayList<String>();

    public Resource()
    {

    }

    public Resource( String name, Class<?> clazz, List<String> methods, List<String> mediaTypes )
    {
        this.name = name;
        this.clazz = clazz;
        this.methods = methods;
        this.mediaTypes = mediaTypes;
    }

    @JsonProperty
    @JsonView( {DetailedView.class} )
    @JacksonXmlProperty( isAttribute = true )
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "methods", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "method", namespace = DxfNamespaces.DXF_2_0 )
    public List<String> getMethods()
    {
        return methods;
    }

    public void setMethods( List<String> methods )
    {
        this.methods = methods;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "mediaTypes", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "mediaType", namespace = DxfNamespaces.DXF_2_0 )
    public List<String> getMediaTypes()
    {
        return mediaTypes;
    }

    public void setMediaTypes( List<String> mediaTypes )
    {
        this.mediaTypes = mediaTypes;
    }

    public Class<?> getClazz()
    {
        return clazz;
    }

    public void setClazz( Class<?> clazz )
    {
        this.clazz = clazz;
    }
}
