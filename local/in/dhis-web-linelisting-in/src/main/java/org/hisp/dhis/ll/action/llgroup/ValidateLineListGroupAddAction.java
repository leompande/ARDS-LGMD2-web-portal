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

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;

import com.opensymphony.xwork2.Action;
public class ValidateLineListGroupAddAction
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

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
/*
    private DataBaseManagerInterface dataBaseManagerInterface;

    public void setDataBaseManagerInterface( DataBaseManagerInterface dataBaseManagerInterface )
    {
        this.dataBaseManagerInterface = dataBaseManagerInterface;
    }
*/
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

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
/*
    private List<String> selectedList;
    
    public void setSelectedList( List<String> selectedList )
    {
        this.selectedList = selectedList;
    }
 */   
    private Integer selectedListNumber;
    
    
    public void setSelectedListNumber( Integer selectedListNumber )
    {
        this.selectedListNumber = selectedListNumber;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    {
        // ---------------------------------------------------------------------
        // Validating LineList fields
        // ----------------------------------------------------------------------
        
        //System.out.println( "Size of selectedList is: " + selectedList.size() );
        System.out.println( "selectedList Number  is: " + selectedListNumber );
      
 
        if ( name == null )
        {
            message = i18n.getString( "specify_name" );

            return INPUT;
        } else
        {
            name = name.trim();

            if ( name.length() == 0 )
            {
                message = i18n.getString( "specify_name" );

                return INPUT;
            }
            /*
             * if(lineListService == null) {
             * System.out.println("Linelist Service is null"); lineListService =
             * new DefaultLineListService(); }
             */
            LineListGroup match = lineListService.getLineListGroupByName( name );

            if ( match != null && ( id == null || match.getId() != id ) )
            {
                message = i18n.getString( "name_in_use" );

                return INPUT;
            }
        }

        if ( shortName == null )
        {
            message = i18n.getString( "specify_short_name" );

            return INPUT;
        } else
        {
            shortName = shortName.trim();

            if ( shortName.length() == 0 )
            {
                message = i18n.getString( "specify_short_name" );

                return INPUT;
            }

            if ( shortName.length() > 25 )
            {
                message = "Short name too long, please give 25 characters ";

                return INPUT;
            }

            LineListGroup match = lineListService.getLineListGroupByShortName( shortName );

            if ( match != null && ( id == null || match.getId() != id ) )
            {
                message = i18n.getString( "short_name_in_use" );

                return INPUT;
            }
        }
        
      if ( selectedListNumber == 0 )
            
        {
            //System.out.println( "selectedList is null" + selectedList );
            message = i18n.getString( "specify_group_members" );
            
            return INPUT;
            
         }         
 /*       
       if ( selectedList == null || selectedList.size() == 0 )
            
        {
            //System.out.println( "selectedList is null" + selectedList );
            message = i18n.getString( "specify_group_members" );
            
            return INPUT;
            
         } 
  */       
		/*
        LineListGroup lineListGroup = lineListService.getLineListGroup( id );
        List<LineListElement> oldElements = new ArrayList<LineListElement>( lineListGroup.getLineListElements() );
        Collection<LineListElement> updatedDataElementList = new ArrayList<LineListElement>();

        if ( selectedList == null )
        {
            System.out.println( "selectedList is null" + selectedList );
        } else
        {
            String[] selectedElementIDs = selectedList.split( "," );
            for ( String str : selectedElementIDs )
            {
                if ( str != null && !str.equals( "" ) )
                {
                    LineListElement element = lineListService.getLineListElement( Integer.parseInt( str ) );
                    updatedDataElementList.add( element );
                }
            }

            for ( int i = 0; i < oldElements.size(); i++ )
            {
                if ( !( updatedDataElementList.contains( oldElements.get( i ) ) ) )
                {
                    boolean doNotDelete = dataBaseManagerInterface.checkDataFromTable( lineListGroup.getShortName(), oldElements.get( i ) );
                    if ( doNotDelete )
                    {
                        message = "\"" + oldElements.get( i ).getName() + "\"cannot delete, its having data";
                        return INPUT;
                    }
                }
            }
        }
       */
        // ---------------------------------------------------------------------
        // Validation success
        // ---------------------------------------------------------------------

        message = i18n.getString( "everything_is_ok" );

        // System.out.println(message);

        return SUCCESS;
    }
}
