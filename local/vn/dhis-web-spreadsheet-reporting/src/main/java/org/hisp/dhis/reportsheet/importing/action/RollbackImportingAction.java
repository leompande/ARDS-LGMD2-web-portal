package org.hisp.dhis.reportsheet.importing.action;

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

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.reportsheet.action.ActionSupport;
import org.hisp.dhis.reportsheet.state.SelectionManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */

public class RollbackImportingAction
    extends ActionSupport
{
    @Autowired
    private SelectionManager selectionManager;

    @Autowired
    private DataValueService dataValueService;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        message = "";

        Set<DataValue> oldDataValues = selectionManager.getOldDataValueList();
        Set<DataValue> newDataValues = selectionManager.getNewDataValueList();

        int i = 0;

        if ( oldDataValues != null && !oldDataValues.isEmpty() )
        {
            for ( DataValue dv : oldDataValues )
            {
                dataValueService.updateDataValue( dv );

                i++;
            }

            selectionManager.setOldDataValueList( new HashSet<DataValue>() );

            message = i18n.getString( "old_value" ) + ": " + i + "/" + oldDataValues.size() + " "
                + i18n.getString( "reverted" ) + "<br/>";
        }

        i = 0;

        if ( newDataValues != null && !newDataValues.isEmpty() )
        {
            for ( DataValue dv : newDataValues )
            {
                dataValueService.deleteDataValue( dv );

                i++;
            }

            selectionManager.setNewDataValueList( new HashSet<DataValue>() );

            message += i18n.getString( "new_value" ) + ": " + i + "/" + newDataValues.size() + " "
                + i18n.getString( "deleted" );
        }

        oldDataValues = null;
        newDataValues = null;

        return SUCCESS;
    }
}