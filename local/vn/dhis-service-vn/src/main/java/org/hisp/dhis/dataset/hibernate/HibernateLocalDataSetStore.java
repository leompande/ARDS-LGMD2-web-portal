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

package org.hisp.dhis.dataset.hibernate;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Query;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.LocalDataSetStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.program.ProgramDataEntryService;

/**
 * @author Chau Thu Tran
 * 
 * @version $HibernateLocalDataSetStore.java May 10, 2012 8:04:58 AM$
 */
public class HibernateLocalDataSetStore
    extends HibernateGenericStore<DataSet>
    implements LocalDataSetStore
{
    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataSet> getByDescription( String description )
    {
        String hql = "from DataSet d where d.description = '" + description + "'";
        Query query = sessionFactory.getCurrentSession().createQuery( hql );

        return query.list();
    }

    public DataElementCategoryOptionCombo getDepartmentByDataSet( DataSet dataSet )
    {
        String htmlCode = dataSet.getDataEntryForm().getHtmlCode();

        final Pattern INPUT_PATTERN = Pattern.compile( "(<input.*?)[/]?>", Pattern.DOTALL );

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code
        // ---------------------------------------------------------------------

        Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields
        // ---------------------------------------------------------------------
        DataElementCategoryOptionCombo optionCombo = null;
        String dataElementCode = null;
        String inputHTML = null;

        while ( inputMatcher.find() )
        {
            dataElementCode = inputMatcher.group( 1 );

            inputHTML = inputMatcher.group();
            inputHTML = inputHTML.replace( ">", "" );

            Matcher identifierMatcher = ProgramDataEntryService.IDENTIFIER_PATTERN_FIELD.matcher( dataElementCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                int optionComboId = Integer.parseInt( identifierMatcher.group( 2 ) );

                optionCombo = categoryService.getDataElementCategoryOptionCombo( optionComboId );

                return optionCombo;
            }
        }

        return null;
    }
}
