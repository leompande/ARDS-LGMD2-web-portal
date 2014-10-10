package org.hisp.dhis.common.adapter;

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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.IdentifiableObject;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author Lars Helge Overland
 */
public class JacksonMapListIdentifiableObjectSerializer
    extends JsonSerializer<Map<String, List<IdentifiableObject>>>
{
    @Override
    public void serialize( Map<String, List<IdentifiableObject>> value, JsonGenerator jgen, SerializerProvider provider )
        throws IOException
    {
        if ( value != null )
        {
            jgen.writeStartObject();
            
            for ( String key : value.keySet() )
            {
                jgen.writeArrayFieldStart( key );
                
                for ( IdentifiableObject object : value.get( key ) )
                {
                    jgen.writeStartObject();
                    
                    if ( object.getUid() != null )
                    {
                        jgen.writeStringField( "id", object.getUid() );
                    }
                    
                    if ( object.getName() != null )
                    {
                        jgen.writeStringField( "name", object.getName() );
                    }
                    
                    if ( object.getCode() != null )
                    {
                        jgen.writeStringField( "code", object.getCode() );
                    }
                    
                    jgen.writeEndObject();
                }
                
                jgen.writeEndArray();                
            }
            
            jgen.writeEndObject();
        }
    }
}
