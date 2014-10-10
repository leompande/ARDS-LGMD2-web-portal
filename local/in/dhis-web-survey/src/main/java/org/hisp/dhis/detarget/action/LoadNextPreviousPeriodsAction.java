package org.hisp.dhis.detarget.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.survey.state.SelectedStateManager;

import com.opensymphony.xwork2.Action;

public class LoadNextPreviousPeriodsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private boolean next;

    public void setNext( boolean next )
    {
        this.next = next;
    }

    private boolean previous;

    public void setPrevious( boolean previous )
    {
        this.previous = previous;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        if ( next )
        {
            selectedStateManager.nextPeriodSpan();
        }
        else if ( previous )
        {
            selectedStateManager.previousPeriodSpan();
        }

        periods = selectedStateManager.getPeriodList();
        
        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
        }
        
        return SUCCESS;
    }
}
