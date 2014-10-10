package org.hisp.dhis.api.view;

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

import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class XsltHtmlView
    extends AbstractUrlBasedView
{
    public static final String HTML_CONTENT_TYPE = "text/html";

    public XsltHtmlView()
    {
        setContentType( HTML_CONTENT_TYPE );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected void renderMergedOutputModel( Map<String, Object> model, HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        Object object = model.get( "model" );
        Class<?> viewClass = JacksonUtils.getViewClass( model.get( "viewClass" ) );
        response.setContentType( getContentType() );

        Assert.notNull( object );

        InputStream input = new ByteArrayInputStream( JacksonUtils.toXmlWithViewAsString( object, viewClass ).getBytes("UTF-8") );
        Source xmlSource = new StreamSource( input );

        Transformer transformer = TransformCacheImpl.instance().getHtmlTransformer();

        // pass on any parameters set in xslt-params
        Map<String, String> params = (Map<String, String>) model.get( "xslt-params" );

        if ( params != null )
        {
            for ( Map.Entry<String, String> entry : params.entrySet() )
            {
                transformer.setParameter( entry.getKey(), entry.getValue() );
            }
        }

        transformer.transform( xmlSource, new StreamResult( response.getOutputStream() ) );
    }
}
