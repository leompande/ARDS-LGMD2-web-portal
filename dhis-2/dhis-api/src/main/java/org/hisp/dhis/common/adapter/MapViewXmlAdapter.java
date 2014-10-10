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

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.mapping.MapView;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.UUID;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class MapViewXmlAdapter extends XmlAdapter<BaseIdentifiableObject, MapView>
{
    private BaseIdentifiableObjectXmlAdapter baseIdentifiableObjectXmlAdapter = new BaseIdentifiableObjectXmlAdapter();

    @Override
    public MapView unmarshal( BaseIdentifiableObject identifiableObject ) throws Exception
    {
        MapView mapView = new MapView();

        mapView.setUid( identifiableObject.getUid() );
        mapView.setLastUpdated( identifiableObject.getLastUpdated() );
        mapView.setName( identifiableObject.getName() == null ? UUID.randomUUID().toString() : identifiableObject.getName() );

        return mapView;
    }

    @Override
    public BaseIdentifiableObject marshal( MapView mapView ) throws Exception
    {
        return baseIdentifiableObjectXmlAdapter.marshal( mapView );
    }
}
