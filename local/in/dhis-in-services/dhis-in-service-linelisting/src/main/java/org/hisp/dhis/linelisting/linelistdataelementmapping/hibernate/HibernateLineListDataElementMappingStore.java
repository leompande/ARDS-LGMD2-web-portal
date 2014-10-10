/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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
package org.hisp.dhis.linelisting.linelistdataelementmapping.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.linelisting.linelistdataelementmapping.LineListDataElementMapping;
import org.hisp.dhis.linelisting.linelistdataelementmapping.LineListDataElementMappingStore;

/**
 * @author Mithilesh Kumar Thakur
 * 
 * @version HibernateLineListDataElementMappingStore.java Oct 12, 2010 1:04:55
 *          PM
 */

public class HibernateLineListDataElementMappingStore
    implements LineListDataElementMappingStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    // -------------------------------------------------------------------------
    // Line List and Data Element Mapping
    // -------------------------------------------------------------------------

    public int addLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( lineListDataElementMapping );
    }

    public void deleteLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( lineListDataElementMapping );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LineListDataElementMapping> getAllLineListDataElementMappings()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( LineListDataElementMapping.class ).list();
    }

    public LineListDataElementMapping getLineListDataElementMapping( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (LineListDataElementMapping) session.get( LineListDataElementMapping.class, id );
    }

    public void updateLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( lineListDataElementMapping );
    }
}
