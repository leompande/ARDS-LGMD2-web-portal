package org.hisp.dhis.dataadmin.action.maintenance;

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

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.analytics.AnalyticsTableService;
import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.completeness.DataSetCompletenessService;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.datamart.DataMartManager;
import org.hisp.dhis.maintenance.MaintenanceService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class PerformMaintenanceAction
    implements Action
{
    private static final Log log = LogFactory.getLog( PerformMaintenanceAction.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Resource(name="org.hisp.dhis.analytics.AnalyticsTableService")
    private AnalyticsTableService analyticsTableService;

    @Resource(name="org.hisp.dhis.analytics.CompletenessTableService")
    private AnalyticsTableService completenessTableService;
    
    @Resource(name="org.hisp.dhis.analytics.CompletenessTargetTableService")
    private AnalyticsTableService completenessTargetTableService;
    
    @Resource(name="org.hisp.dhis.analytics.EventAnalyticsTableService")
    private AnalyticsTableService eventAnalyticsTableService;
    
    private MaintenanceService maintenanceService;

    public void setMaintenanceService( MaintenanceService maintenanceService )
    {
        this.maintenanceService = maintenanceService;
    }
    
    private DataSetCompletenessService completenessService;

    public void setCompletenessService( DataSetCompletenessService completenessService )
    {
        this.completenessService = completenessService;
    }

    private AggregatedDataValueService aggregatedDataValueService;
    
    public void setAggregatedDataValueService( AggregatedDataValueService aggregatedDataValueService )
    {
        this.aggregatedDataValueService = aggregatedDataValueService;
    }
        
    private DataMartManager dataMartManager;

    public void setDataMartManager( DataMartManager dataMartManager )
    {
        this.dataMartManager = dataMartManager;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    
    private boolean clearAnalytics;
    
    public void setClearAnalytics( boolean clearAnalytics )
    {
        this.clearAnalytics = clearAnalytics;
    }

    private boolean clearDataMart;

    public void setClearDataMart( boolean clearDataMart )
    {
        this.clearDataMart = clearDataMart;
    }

    public boolean dataMartIndex;
    
    public void setDataMartIndex( boolean dataMartIndex )
    {
        this.dataMartIndex = dataMartIndex;
    }

    private boolean zeroValues;

    public void setZeroValues( boolean zeroValues )
    {
        this.zeroValues = zeroValues;
    }
    
    private boolean dataSetCompleteness;

    public void setDataSetCompleteness( boolean dataSetCompleteness )
    {
        this.dataSetCompleteness = dataSetCompleteness;
    }
    
    private boolean prunePeriods;

    public void setPrunePeriods( boolean prunePeriods )
    {
        this.prunePeriods = prunePeriods;
    }
    
    private boolean updateCategoryOptionCombos;

    public void setUpdateCategoryOptionCombos( boolean updateCategoryOptionCombos )
    {
        this.updateCategoryOptionCombos = updateCategoryOptionCombos;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute() 
        throws Exception
    {
        String username = currentUserService.getCurrentUsername();
        
        if ( clearAnalytics )
        {
            analyticsTableService.dropTables();
            completenessTableService.dropTables();
            completenessTargetTableService.dropTables();
            eventAnalyticsTableService.dropTables();
        }
        
        if ( clearDataMart )
        {
            aggregatedDataValueService.dropDataMart();
            aggregatedDataValueService.createDataMart();
            
            log.info( "'" + username + "': Cleared data mart" );
        }
        
        if ( dataMartIndex )
        {
            dataMartManager.dropDataValueIndex();
            dataMartManager.dropIndicatorValueIndex();
            dataMartManager.dropOrgUnitDataValueIndex();
            dataMartManager.dropOrgUnitIndicatorValueIndex();
            
            dataMartManager.createDataValueIndex();
            dataMartManager.createIndicatorValueIndex();
            dataMartManager.createOrgUnitDataValueIndex();
            dataMartManager.createOrgUnitIndicatorValueIndex();
            
            completenessService.dropIndex();
            completenessService.createIndex();
            
            log.info( "'" + username + "': Rebuilt data mart indexes" );
        }
        
        if ( zeroValues )
        {
            maintenanceService.deleteZeroDataValues();
            
            log.info( "Cleared zero values" );
        }
        
        if ( dataSetCompleteness )
        {
            completenessService.deleteDataSetCompleteness();
            
            log.info( "'" + username + "': Cleared data completeness" );
        }
        
        if ( prunePeriods )
        {
            prunePeriods();
            
            log.info( "'" + username + "': Pruned periods" );
        }
        
        if ( updateCategoryOptionCombos )
        {
            categoryService.updateAllOptionCombos();
            
            log.info( "'" + username + "': Updated category option combos" );
        }
        
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    private void prunePeriods()
    {
        for ( Period period : periodService.getAllPeriods() )
        {
            int periodId = period.getId();
            
            try
            {
                periodService.deletePeriod( period );
                
                log.info( "Deleted period with id: " + periodId );
            }
            catch ( DeleteNotAllowedException ex )
            {
                log.debug( "Period has associated objects and could not be deleted: " + periodId );
            }
        }
        
        log.info( "Period pruning done" );
    }
}
