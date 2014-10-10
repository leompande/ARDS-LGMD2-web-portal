package org.hisp.dhis.den.impl;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.den.api.LLDataValue;
import org.hisp.dhis.den.api.LLDataValueService;
import org.hisp.dhis.den.api.LLDataValueStore;
import org.hisp.dhis.den.api.LLImportParameters;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@Transactional
public class DefaultLLDataValueService
    implements LLDataValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LLDataValueStore dataValueStore;

    public void setDataValueStore( LLDataValueStore dataValueStore )
    {
        this.dataValueStore = dataValueStore;
    }

    // -------------------------------------------------------------------------
    // Basic DataValue
    // -------------------------------------------------------------------------

    public void addDataValue( LLDataValue dataValue )
    {
        if ( !(dataValue.getValue() == null && dataValue.getComment() == null) )
        {
            dataValueStore.addDataValue( dataValue );
        }
    }

    public void updateDataValue( LLDataValue dataValue )
    {
        if ( dataValue.getValue() == null && dataValue.getComment() == null )
        {
            dataValueStore.deleteDataValue( dataValue );
        }
        else
        {
            dataValueStore.updateDataValue( dataValue );
        }
    }

    public void deleteDataValue( LLDataValue dataValue )
    {
        dataValueStore.deleteDataValue( dataValue );
    }

    public int deleteDataValuesBySource( OrganisationUnit source )
    {
        return dataValueStore.deleteDataValuesBySource( source );
    }

    public int deleteDataValuesByDataElement( DataElement dataElement )
    {
        return dataValueStore.deleteDataValuesByDataElement( dataElement );
    }

    public LLDataValue getDataValue( OrganisationUnit source, DataElement dataElement, Period period,
        DataElementCategoryOptionCombo optionCombo, int recordNo )
    {
        return dataValueStore.getDataValue( source, dataElement, period, optionCombo, recordNo );
    }

    public Collection<LLDataValue> getDataValues( OrganisationUnit source, DataElement dataElement, Period period )
    {
        return dataValueStore.getDataValues( source, dataElement, period );
    }

    public Collection<LLDataValue> getDataValues( OrganisationUnit source, DataElement dataElement, Period period,
        DataElementCategoryOptionCombo optionCombo )
    {
        return dataValueStore.getDataValues( source, dataElement, period, optionCombo );
    }

    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

    public Collection<LLDataValue> getAllDataValues()
    {
        return dataValueStore.getAllDataValues();
    }

    public Collection<LLDataValue> getDataValues( OrganisationUnit source, Period period )
    {
        return dataValueStore.getDataValues( source, period );
    }

    public Collection<LLDataValue> getDataValues( OrganisationUnit source, DataElement dataElement )
    {
        return dataValueStore.getDataValues( source, dataElement );
    }

    public Collection<LLDataValue> getDataValues( Collection<OrganisationUnit> sources, DataElement dataElement )
    {
        return dataValueStore.getDataValues( sources, dataElement );
    }

    public Collection<LLDataValue> getDataValues( OrganisationUnit source, Period period, Collection<DataElement> dataElements )
    {
        return dataValueStore.getDataValues( source, period, dataElements );
    }

    public Collection<LLDataValue> getDataValues( OrganisationUnit source, Period period, Collection<DataElement> dataElements,
        Collection<DataElementCategoryOptionCombo> optionCombos )
    {
        return dataValueStore.getDataValues( source, period, dataElements, optionCombos );
    }

    public Collection<LLDataValue> getDataValues( DataElement dataElement, Collection<Period> periods,
        Collection<OrganisationUnit> sources )
    {
        return dataValueStore.getDataValues( dataElement, periods, sources );
    }

    public Collection<LLDataValue> getDataValues( DataElement dataElement, DataElementCategoryOptionCombo optionCombo,
        Collection<Period> periods, Collection<OrganisationUnit> sources )
    {
        return dataValueStore.getDataValues( dataElement, optionCombo, periods, sources );
    }

    public Collection<LLDataValue> getDataValues( Collection<DataElement> dataElements, Collection<Period> periods,
        Collection<OrganisationUnit> sources, int firstResult, int maxResults )
    {
        return dataValueStore.getDataValues( dataElements, periods, sources, firstResult, maxResults );
    }

    public Collection<LLDataValue> getDataValues( Collection<DataElementCategoryOptionCombo> optionCombos )
    {
        return dataValueStore.getDataValues( optionCombos );
    }

    public Collection<LLDataValue> getDataValues( DataElement dataElement )
    {
        return dataValueStore.getDataValues( dataElement );
    }

    public int getMaxRecordNo()
    {
        return dataValueStore.getMaxRecordNo();
    }

    public Map<String, String> processLineListBirths( OrganisationUnit organisationUnit, Period period )
    {
        return dataValueStore.processLineListBirths( organisationUnit, period );
    }

    public Map<String, String> processLineListDeaths( OrganisationUnit organisationUnit, Period periodL )
    {
        return dataValueStore.processLineListDeaths( organisationUnit, periodL );
    }

    public Map<String, String> processLineListMaternalDeaths( OrganisationUnit organisationUnit, Period periodL )
    {
        return dataValueStore.processLineListMaternalDeaths( organisationUnit, periodL );
    }

    /*
    public void saveLLdataValue( String query )
    {
        dataValueStore.saveLLdataValue( query );
    }*/

    public List<String> getLLImportFiles()
    {
        List<String> fileNames = new ArrayList<String>();

        try
        {
            String importFolderPath = System.getenv( "DHIS2_HOME" ) + File.separator + "lli" + File.separator
                + "pending";

            File dir = new File( importFolderPath );

            //System.out.println( dir.getAbsolutePath() );

            //System.out.println( dir.listFiles() );

            String[] files = dir.list();

            //System.out.println( "In getImportFiles Method: " + files.length );

            fileNames = Arrays.asList( files );

            //System.out.println( "In getImportFiles Method: " + fileNames.size() );
        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }

        return fileNames;
    }

    public List<LLImportParameters> getLLImportParameters( String fileName )
    {
        List<LLImportParameters> llImportParamList = new ArrayList<LLImportParameters>();

        String path = System.getenv( "DHIS2_HOME" ) + File.separator + "lli" + File.separator + fileName;

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "There is no DECodes related XML file in the DHIS home" );
                return null;
            }

            NodeList listOfDECodes = doc.getElementsByTagName( "de-code" );
            int totalDEcodes = listOfDECodes.getLength();

            for ( int s = 0; s < totalDEcodes; s++ )
            {
                Element deCodeElement = (Element) listOfDECodes.item( s );
                NodeList textDECodeList = deCodeElement.getChildNodes();
                String expression = ((Node) textDECodeList.item( 0 )).getNodeValue().trim();

                int sheetNo = new Integer( deCodeElement.getAttribute( "sheetno" ) );
                int rowNo = new Integer( deCodeElement.getAttribute( "rowno" ) );
                int colNo = new Integer( deCodeElement.getAttribute( "colno" ) );

                LLImportParameters llImportParameter = new LLImportParameters( sheetNo, rowNo, colNo, expression );

                llImportParamList.add( llImportParameter );

            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }
        return llImportParamList;
    }// getDECodes end

    public void removeLLRecord( int recordNo )
    {
        dataValueStore.removeLLRecord( recordNo );
    }
}
