package org.hisp.dhis.survey.state;

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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.detarget.DeTarget;
import org.hisp.dhis.detarget.DeTargetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.survey.Survey;
import org.hisp.dhis.survey.SurveyService;

import com.opensymphony.xwork2.ActionContext;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: DefaultSelectedStateManager.java 5282 2008-05-28 10:41:06Z
 *          larshelg $
 */
public class DefaultSelectedStateManager
    implements SelectedStateManager
{

    private static final Log log = LogFactory.getLog( DefaultSelectedStateManager.class );
    
    public static final String SESSION_KEY_SELECTED_SURVEY_ID = "data_entry_selected_survey_id";
    
    public static final String SESSION_KEY_SELECTED_DETARGET_ID = "data_entry_selected_deTarget_id";
    
    public static final String SESSION_KEY_SELECTED_PERIOD_INDEX = "data_entry_selected_period_index";
    
    public static final String SESSION_KEY_BASE_PERIOD = "data_entry_base_period";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SurveyService surveyService;
    
    public void setSurveyService( SurveyService surveyService )
    {
        this.surveyService = surveyService;
    }
    
    private DeTargetService deTargetService;
    
    public void setDeTargetService( DeTargetService deTargetService )
    {
        this.deTargetService = deTargetService;
    }

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    
    private PeriodService periodService;
    
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // SelectedStateManager implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // OrganisationUnit
    // -------------------------------------------------------------------------

    public OrganisationUnit getSelectedOrganisationUnit()
    {
        return selectionManager.getSelectedOrganisationUnit();
    }

    // -------------------------------------------------------------------------
    // Survey
    // -------------------------------------------------------------------------

    public void setSelectedSurvey( Survey survey )
    {
        getSession().put( SESSION_KEY_SELECTED_SURVEY_ID, survey.getId() );
    }

    public Survey getSelectedSurvey()
    {
        Integer id = (Integer) getSession().get( SESSION_KEY_SELECTED_SURVEY_ID );

        return id != null ? surveyService.getSurvey( id ) : null;
    }

    public void clearSelectedSurvey()
    {
        getSession().remove( SESSION_KEY_SELECTED_SURVEY_ID );
    }

    public List<Survey> loadSurveysForSelectedOrgUnit( OrganisationUnit organisationUnit )
    {
        List<Survey> surveys = new ArrayList<Survey>( surveyService.getSurveysBySource( organisationUnit ));

        return surveys;
    }
    
    //--------------------------------------------------------------------------
    //DeTarget
    //--------------------------------------------------------------------------
    
    public void setSelectedDeTarget( DeTarget deTarget )
    {
        getSession().put( SESSION_KEY_SELECTED_DETARGET_ID, deTarget.getId() );
    }
    
    public DeTarget getSelectedDeTarget()
    {
        Integer deTargetId = (Integer) getSession().get( SESSION_KEY_SELECTED_DETARGET_ID );
        
        return deTargetId != null ? deTargetService.getDeTarget( deTargetId ) : null;
    }
    
    public void clearSelectedDeTarget()
    {
        getSession().remove( SESSION_KEY_SELECTED_DETARGET_ID );
    }
    
    public List<DeTarget> loadDeTargetsForSelectedOrgUnit( OrganisationUnit organisationUnit )
    {
        List<DeTarget> deTargets = new ArrayList<DeTarget>( deTargetService.getDeTargetBySource( organisationUnit ) );
        
        return deTargets;
    }
    
    
    // -------------------------------------------------------------------------
    // Period
    // -------------------------------------------------------------------------
    
    public void setSelectedPeriodIndex( Integer index )
    {
        getSession().put( SESSION_KEY_SELECTED_PERIOD_INDEX, index );
    }

    public Integer getSelectedPeriodIndex()
    {
        return (Integer) getSession().get( SESSION_KEY_SELECTED_PERIOD_INDEX );
    }

    public Period getSelectedPeriod()
    {
        Integer index = getSelectedPeriodIndex();
        
        if ( index == null )
        {
            return null;
        }

        List<Period> periods = getPeriodList();
        
        if ( index >= 0 && index < periods.size() )
        {
            return periods.get( index );
        }

        return null;
    }

    public void clearSelectedPeriod()
    {
        getSession().remove( SESSION_KEY_SELECTED_PERIOD_INDEX );
    }

    public List<Period> getPeriodList()
    {
        Period basePeriod = getBasePeriod();
        CalendarPeriodType periodType = (CalendarPeriodType) getPeriodType();
        List<Period> periods = periodType.generatePeriods( basePeriod );

        Date now = new Date();

        Iterator<Period> iterator = periods.iterator();
        
        while ( iterator.hasNext() )
        {
            if ( iterator.next().getStartDate().after( now ) )
            {
                iterator.remove();
            }
        }
        
        return periods;
    }
    
    public void nextPeriodSpan()
    {
        List<Period> periods = getPeriodList();

        Period basePeriod = periods.get( periods.size() - 1 );
        CalendarPeriodType periodType = (CalendarPeriodType) getPeriodType();
        Period newBasePeriod = periodType.getNextPeriod( basePeriod );

        if ( newBasePeriod.getStartDate().before( new Date() ) ) // Future periods not allowed
        {
            getSession().put( SESSION_KEY_BASE_PERIOD, newBasePeriod );
        }
    }

    public void previousPeriodSpan()
    {
        List<Period> periods = getPeriodList();

        CalendarPeriodType periodType = (CalendarPeriodType) getPeriodType();
        Period basePeriod = periods.get( 0 );
        Period newBasePeriod = periodType.getPreviousPeriod( basePeriod );

        getSession().put( SESSION_KEY_BASE_PERIOD, newBasePeriod );
    }

    public Period reloadPeriod(){
        
        Period period = getSelectedPeriod();

        return periodService.reloadPeriod( period );
    }

    // -------------------------------------------------------------------------
    // Support methods
    // -------------------------------------------------------------------------
    private PeriodType getPeriodType()
    {
        return new YearlyPeriodType();
    }
    
    private Period getBasePeriod()
    {
        Period basePeriod = (Period) getSession().get( SESSION_KEY_BASE_PERIOD );
        
        PeriodType periodType = new YearlyPeriodType();

        if ( basePeriod == null )
        {
            log.debug( "Base period is null, creating new" );

            basePeriod = periodType.createPeriod();
            getSession().put( SESSION_KEY_BASE_PERIOD, basePeriod );
        }
        else if ( !basePeriod.getPeriodType().equals( periodType ) )
        {
            log.debug( "Wrong type of base period, transforming" );

            basePeriod = periodType.createPeriod( basePeriod.getStartDate() );
            getSession().put( SESSION_KEY_BASE_PERIOD, basePeriod );
        }

        return basePeriod;
    }
    
    private static final Map<String, Object> getSession()
    {
        return ActionContext.getContext().getSession();
    }   
}
