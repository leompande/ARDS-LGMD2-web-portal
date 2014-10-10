package org.hisp.dhis.ll.action.llgroup;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

public class AddLineListGroupAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
/*
    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }
*/
    private DataBaseManagerInterface dataBaseManagerInterface;

    public void setDataBaseManagerInterface( DataBaseManagerInterface dataBaseManagerInterface )
    {
        this.dataBaseManagerInterface = dataBaseManagerInterface;
    }
/*
    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }
*/
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String shortName;

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private List<PeriodType> periodTypes;

    public List<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }

    private String periodTypeSelect;

    public void setPeriodTypeSelect( String periodTypeSelect )
    {
        this.periodTypeSelect = periodTypeSelect;
    }

    private Collection<String> selectedList = new HashSet<String>();

    public void setSelectedList( Collection<String> selectedList )
    {
        this.selectedList = selectedList;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    @Override
   // @SuppressWarnings("empty-statement")
    public String execute()
    {

        // ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------

        if ( description != null && description.trim().length() == 0 )
        {
            description = null;
        }

        shortName = shortName.replaceAll( " ", "_" );

        // ---------------------------------------------------------------------
        // Create Line List Group
        // ---------------------------------------------------------------------
        PeriodType periodType = periodService.getPeriodTypeByName( periodTypeSelect );
        LineListGroup lineListGroup = new LineListGroup( name, shortName, description, periodType );

        List<String> columnNames = new ArrayList<String>();
        List<String> autoIncrement = new ArrayList<String>();
        List<String> dataTypes = new ArrayList<String>();
        List<Integer> sizeOfColumns = new ArrayList<Integer>();
        for ( String id : selectedList )
        {
            LineListElement lineListElement = lineListService.getLineListElement( Integer.parseInt( id ) );
            //System.out.println( "lineListElement = " + lineListElement + " group = " + lineListGroup );
            lineListGroup.getLineListElements().add( lineListElement );
            //lineListElements = lineListService.getLineListElementsBySortOrder( lineListGroup );
            columnNames.add( lineListElement.getShortName() );

            if ( lineListElement.getDataType().equalsIgnoreCase( "string" ) )
            {
                dataTypes.add( "VARCHAR" );
                sizeOfColumns.add( 255 );
                autoIncrement.add( "" );
            } else
            {
                if ( lineListElement.getDataType().equalsIgnoreCase( "bool" ) )
                {
                    dataTypes.add( "BIT" );
                    sizeOfColumns.add( 3 );
                    autoIncrement.add( "" );
                } else
                {
                    if ( lineListElement.getDataType().equalsIgnoreCase( "date" ) )
                    {
                        dataTypes.add( "DATE" );
                        sizeOfColumns.add( 10 );
                        autoIncrement.add( "" );
                    } else
                    {
                        if ( lineListElement.getDataType().equalsIgnoreCase( "int" ) )
                        {
                            dataTypes.add( "int" );
                            sizeOfColumns.add( 11 );
                            autoIncrement.add( "" );
                        } else
                        {
                        }
                    }
                }
            }

        }
        //Collections.sort(columnNames, String.CASE_INSENSITIVE_ORDER);
        columnNames.add( 0, "recordNumber" );
        dataTypes.add( 0, "INTEGER" );
        sizeOfColumns.add( 0, 11 );
        autoIncrement.add( 0, "NOT NULL AUTO_INCREMENT PRIMARY KEY" );


        columnNames.add( "periodid" );
        dataTypes.add( "INTEGER" );
        sizeOfColumns.add( 11 );
        autoIncrement.add( "" );


        columnNames.add( "sourceid" );
        dataTypes.add( "INTEGER" );
        sizeOfColumns.add( 11 );
        autoIncrement.add( "" );

        columnNames.add( "storedby" );
        dataTypes.add( "VARCHAR" );
        sizeOfColumns.add( 25 );
        autoIncrement.add( "" );

        columnNames.add( "lastupdated" );
        dataTypes.add( "DATE" );
        sizeOfColumns.add( 10 );
        autoIncrement.add( "" );

        //System.out.println("columnNames " + columnNames.size());
        System.out.println( "sizeOfColumns " + sizeOfColumns.size() + " lineListGroup = " + lineListGroup.getShortName() );
        boolean tableCreated = dataBaseManagerInterface.createTable( lineListGroup.getShortName(), columnNames, autoIncrement, dataTypes, sizeOfColumns );

        if ( tableCreated )
        {
            lineListService.addLineListGroup( lineListGroup );
        } 
        else
        {
            System.out.println("Linelist Group is not created");
            //JOptionPane.showMessageDialog( null, "LineListGroup is not created" );
        }

        return SUCCESS;
    }
}
