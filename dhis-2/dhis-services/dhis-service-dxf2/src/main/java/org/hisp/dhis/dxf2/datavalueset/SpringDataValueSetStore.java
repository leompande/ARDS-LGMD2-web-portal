package org.hisp.dhis.dxf2.datavalueset;

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

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.DateUtils.getMediumDateString;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.amplecode.staxwax.factory.XMLFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dxf2.datavalue.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.StreamUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.csvreader.CsvWriter;

public class SpringDataValueSetStore
    implements DataValueSetStore
{
    private static final char CSV_DELIM = ',';

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //--------------------------------------------------------------------------
    // DataValueSetStore implementation
    //--------------------------------------------------------------------------

    public void writeDataValueSetXml( DataSet dataSet, Date completeDate, Period period, OrganisationUnit orgUnit,
        Set<DataElement> dataElements, Set<Period> periods, Set<OrganisationUnit> orgUnits, OutputStream out )
    {
        DataValueSet dataValueSet = new StreamingDataValueSet( XMLFactory.getXMLWriter( out ) );

        writeDataValueSet( dataSet, completeDate, period, orgUnit, dataElements, periods, orgUnits, dataValueSet );

        StreamUtils.closeOutputStream( out );
    }

    public void writeDataValueSetCsv( Set<DataElement> dataElements, Set<Period> periods, Set<OrganisationUnit> orgUnits, Writer writer )
    {
        DataValueSet dataValueSet = new StreamingCsvDataValueSet( new CsvWriter( writer, CSV_DELIM ) );

        writeDataValueSet( null, null, null, null, dataElements, periods, orgUnits, dataValueSet );
    }

    private void writeDataValueSet( DataSet dataSet, Date completeDate, Period period, OrganisationUnit orgUnit,
        Set<DataElement> dataElements, Set<Period> periods, Set<OrganisationUnit> orgUnits, DataValueSet dataValueSet )
    {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( getDataValueSql( dataElements, periods, orgUnits ) );
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

        dataValueSet.setDataSet( dataSet != null ? dataSet.getUid() : null );
        dataValueSet.setCompleteDate( getMediumDateString( completeDate ) );
        dataValueSet.setPeriod( period != null ? period.getIsoDate() : null );
        dataValueSet.setOrgUnit( orgUnit != null ? orgUnit.getUid() : null );


        while ( rowSet.next() )
        {
            DataValue dataValue = dataValueSet.getDataValueInstance();

            String periodType = rowSet.getString( "name" );
            Date startDate = rowSet.getDate( "startdate" );
            Period isoPeriod = PeriodType.getPeriodTypeByName( periodType ).createPeriod( startDate );

            dataValue.setDataElement( rowSet.getString( "deuid" ) );
            dataValue.setPeriod( isoPeriod.getIsoDate() );
            dataValue.setOrgUnit( rowSet.getString( "ouuid" ) );
            dataValue.setCategoryOptionCombo( rowSet.getString( "cocuid" ) );
            dataValue.setValue( rowSet.getString( "value" ) );
            dataValue.setStoredBy( rowSet.getString( "storedby" ) );

            java.sql.Date lastUpdated = rowSet.getDate( "lastupdated" );

            if ( lastUpdated != null )
            {
                DateTime dt = new DateTime( lastUpdated );
                dataValue.setLastUpdated( fmt.print( dt ) );
            }

            dataValue.setComment( rowSet.getString( "comment" ) );
            dataValue.setFollowup( rowSet.getBoolean( "followup" ) );
            dataValue.close();
        }

        dataValueSet.close();
    }

    private String getDataValueSql( Collection<DataElement> dataElements, Collection<Period> periods, Collection<OrganisationUnit> orgUnits )
    {
        return
            "select de.uid as deuid, pe.startdate, pt.name, ou.uid as ouuid, coc.uid as cocuid, dv.value, dv.storedby, dv.lastupdated, dv.comment, dv.followup " +
                "from datavalue dv " +
                "join dataelement de on (dv.dataelementid=de.dataelementid) " +
                "join period pe on (dv.periodid=pe.periodid) " +
                "join periodtype pt on (pe.periodtypeid=pt.periodtypeid) " +
                "join organisationunit ou on (dv.sourceid=ou.organisationunitid) " +
                "join categoryoptioncombo coc on (dv.categoryoptioncomboid=coc.categoryoptioncomboid) " +
                "where dv.dataelementid in (" + getCommaDelimitedString( getIdentifiers( DataElement.class, dataElements ) ) + ") " +
                "and dv.periodid in (" + getCommaDelimitedString( getIdentifiers( Period.class, periods ) ) + ") " +
                "and dv.sourceid in (" + getCommaDelimitedString( getIdentifiers( OrganisationUnit.class, orgUnits ) ) + ")";
    }
}
