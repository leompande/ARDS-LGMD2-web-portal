package org.hisp.dhis.dataapproval;

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

/**
 * Current state of data approval for a given combination of data set, period
 * and organisation unit.
 *
 * @author Jim Grace
 * @version $Id$
 */

public enum DataApprovalState
{
    /**
     * Data in this data set is approved for this period and organisation unit.
     */
    APPROVED,

    /**
     * Data in this data set is ready to be approved for this period and
     * organisation unit.
     */
    READY_FOR_APPROVAL,

    /**
     * Data in this data set is not yet ready to be approved for this period
     * and organisation unit, because it is waiting for approval at a
     * lower-level organisation unit under this one.
     */
    WAITING_FOR_LOWER_LEVEL_APPROVAL,

    /**
     * Data in this data set does not need approval for this period and
     * organisation unit, for one of the following reasons:
     * <ul>
     *     <li>Data approval is not enabled for this data set.</li>
     *     <li>No data is collected for this data set for this organisation
     *         unit or any lower-level organisation units under it.</li>
     * </ul>
     */
    APPROVAL_NOT_NEEDED
}
