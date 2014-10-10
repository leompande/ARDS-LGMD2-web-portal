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
package org.hisp.dhis.linelisting;

import java.io.Serializable;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;

/**
 * @author Mithilesh Kumar Thakur
 * 
 * @version LineListDataElementMap.java Oct 12, 2010 11:38:59 AM
 */
@SuppressWarnings("serial")
public class LineListDataElementMap implements Serializable
{
   // private int id;

    /**
     * Linelist Element
     */
    private LineListElement linelistElement;

    /**
     * Linelist Option
     */
    private LineListOption linelistOption;

    /**
     * DataElement
     */
    private DataElement dataElement;

    /**
     * Option Combo for DataElement.
     */
    private DataElementCategoryOptionCombo dataElementOptionCombo;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public LineListDataElementMap()
    {

    }

    public LineListDataElementMap( LineListElement linelistElement, LineListOption linelistOption,
        DataElement dataElement, DataElementCategoryOptionCombo dataElementOptionCombo )
    {
        this.linelistElement = linelistElement;
        this.dataElement = dataElement;
        this.dataElementOptionCombo = dataElementOptionCombo;
        this.linelistOption = linelistOption;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public LineListElement getLinelistElement()
    {
        return linelistElement;
    }

    public void setLinelistElement( LineListElement linelistElement )
    {
        this.linelistElement = linelistElement;
    }

    
    public DataElement getDataElement()
    {
        return dataElement;
    }
/*
    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }
*/
    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    public DataElementCategoryOptionCombo getDataElementOptionCombo()
    {
        return dataElementOptionCombo;
    }

    public void setDataElementOptionCombo( DataElementCategoryOptionCombo dataElementOptionCombo )
    {
        this.dataElementOptionCombo = dataElementOptionCombo;
    }

    public LineListOption getLinelistOption()
    {
        return linelistOption;
    }

    public void setLinelistOption( LineListOption linelistOption )
    {
        this.linelistOption = linelistOption;
    }

}
