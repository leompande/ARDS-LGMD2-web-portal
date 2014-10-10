package org.hisp.dhis.mobile.sms;

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

import java.util.Collection;
import java.util.Date;
import org.hisp.dhis.mobile.sms.api.SmsInbound;
import org.hisp.dhis.mobile.sms.api.SmsInboundStore;
import org.hisp.dhis.mobile.sms.api.SmsInboundStoreService;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Saptarshi
 */
@Transactional
public class DefaultSmsInboundStoreService implements SmsInboundStoreService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private SmsInboundStore smsInboundStore;

    public void setSmsInboundStore( SmsInboundStore smsInboundStore )
    {
        this.smsInboundStore = smsInboundStore;
    }

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------
    @Override
    public void saveSms( SmsInbound sms )
    {
        smsInboundStore.saveSms( sms );
    }

    @Override
    public Collection<SmsInbound> getSmsByDate( Date startDate, Date endDate )
    {
        return smsInboundStore.getSms( null, null, startDate, endDate);
    }

    @Override
    public Collection<SmsInbound> getSmsByOriginator( String originator )
    {
        return smsInboundStore.getSms( originator, null, null, null );
    }

    @Override
    public Collection<SmsInbound> getSmsByProcess( int process )
    {
        return smsInboundStore.getSms( null, process, null, null );
    }

    @Override
    public Collection<SmsInbound> getAllReceivedSms()
    {
        return smsInboundStore.getSms( null, null, null, null );
    }

    @Override
    public void updateSms( SmsInbound sms )
    {
        smsInboundStore.updateSms( sms );
    }

    @Override
    public long getSmsCount()
    {
        return smsInboundStore.getSmsCount();
    }
}
