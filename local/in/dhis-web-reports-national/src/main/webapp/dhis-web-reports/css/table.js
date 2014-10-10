
// -----------------------------------------------------------------------------
// Create ReportTable
// -----------------------------------------------------------------------------

function createTable( tableId )
{
    /* var url = "createTable.action?id=" + tableId + "&mode=table";
    
    if ( document.getElementById( "reportingPeriod" ) != null )
    {
        url += "&reportingPeriod=" + getListValue( "reportingPeriod" );
    }
    
    if ( document.getElementById( "parentOrganisationUnitId" ) != null )
    {
        url += "&parentOrganisationUnitId=" + getListValue( "parentOrganisationUnitId" );
    }
    
    if ( document.getElementById( "organisationUnitId" ) != null )
    {
        url += "&organisationUnitId=" + getListValue( "organisationUnitId" );
    }
    
    var request = new Request();
    request.setCallbackSuccess( createTableReceived );    
    request.send( url ); */
	
	$.post("createTable.action",
		{
			id : tableId,
			mode : "table",
			reportingPeriod : getListValue( "reportingPeriod" ),
			parentOrganisationUnitId : getListValue( "parentOrganisationUnitId" ),
			organisationUnitId : getListValue( "organisationUnitId" )
		},
		function (data)
		{
			createTableReceived(data);
		},'xml');	
}

function createTableReceived( messageElement )
{
    getTableStatus();
}

function getTableStatus()
{
    /* var url = "getStatus.action";
    
    var request = new Request();
    request.setResponseTypeXML( 'status' );
    request.setCallbackSuccess( tableStatusReceived );    
    request.send( url ); */
	
	$.post("getStatus.action",
		{
		},
		function (data)
		{
			tableStatusReceived(data);
		},'xml');	
}

function tableStatusReceived( xmlObject )
{
    var statusMessage = getElementValue( xmlObject, 'statusMessage' );
    var finished = getElementValue( xmlObject, 'finished' );
    
    if ( finished == "true" )
    {
        setMessage( i18n_process_completed );
    }
    else if ( statusMessage == null )
    {
        setMessage( i18n_please_wait );
    }
    else
    {
        setMessage( i18n_please_wait + ". " + statusMessage + "..."  );
    }
    
    waitAndGetTableStatus( 2000 );
}

function waitAndGetTableStatus( millis )
{
    setTimeout( "getTableStatus();", millis );
}

// -----------------------------------------------------------------------------
// Save ReportTable
// -----------------------------------------------------------------------------

function saveTable()
{
    if ( validateCollections() )
    {
        var tableId = document.getElementById( "tableId" ).value;
        var tableName = document.getElementById( "tableName" ).value;
        
        /* var url = "validateTable.action?id=" + tableId + "&name=" + tableName;
        
        var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( saveTableReceived );
        request.send( url ); */
		
		$.post("validateTable.action",
		{
			id : tableId,
			name : tableName
		},
		function (data)
		{
			saveTableReceived(data);
		},'xml');	
    }
}

function saveTableReceived( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == "input" )
    {
        setMessage( message );
        
        return false;
    }
    else if ( type == "success" )
    {        
        selectTableForm();
        
        document.getElementById( "tableForm" ).submit();
    }
}

function selectTableForm()
{
    if ( isNotNull( "selectedDataElements" ) )
    {
        selectAllById( "selectedDataElements" );
    }
    
    if ( isNotNull( "selectedIndicators" ) )
    {
       selectAllById( "selectedIndicators" );
    }
        
    if ( isNotNull( "selectedDataSets" ) )
    {
        selectAllById( "selectedDataSets" );
    }
    
    selectAllById( "selectedPeriods" );
    selectAllById( "selectedOrganisationUnits" );   
}

// -----------------------------------------------------------------------------
// Remove
// -----------------------------------------------------------------------------

function removeTable( tableId, tableName )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + tableName );
    
    if ( result )
    {
        window.location.href = "removeTable.action?id=" + tableId;
    }
}

// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validateCollections()
{
    if ( isChecked( "regression" ) && document.getElementById( "selectedOrganisationUnits" ).options.length > 1 )
    {
        setMessage( i18n_cannot_include_more_organisation_unit_regression );
        
        return false;
    }
    
    if ( isChecked( "doIndicators" ) && isChecked( "doPeriods" ) && isChecked( "doOrganisationUnits" ) )
    {
        setMessage( i18n_cannot_crosstab_all_dimensions );
        
        return false;
    }
        
    if ( !isChecked( "doIndicators" ) && !isChecked( "doPeriods" ) && !isChecked( "doOrganisationUnits" ) && !isChecked( "doCategoryOptionCombos" ) )
    {
        setMessage( i18n_cannot_crosstab_no_dimensions );
        
        return false;
    }
    
    if ( isNotNull( "selectedDataElements" ) && !hasElements( "selectedDataElements" ) )
    {
        setMessage( i18n_must_select_at_least_one_dataelement );
        
        return false;
    }
    
    if ( isNotNull( "selectedIndicators" ) && !hasElements( "selectedIndicators" ) )
    {
        setMessage( i18n_must_select_at_least_one_indicator );
        
        return false;
    }
    
    if ( !hasElements( "selectedOrganisationUnits" ) && !organisationUnitReportParamsChecked() )
    {
        setMessage( i18n_must_select_at_least_one_unit );
        
        return false;
    }
    
    if ( !hasElements( "selectedPeriods" ) && !relativePeriodsChecked() )
    {
        setMessage( i18n_must_select_at_least_one_period );
        
        return false;
    }
    
    return true;
}

function relativePeriodsChecked()
{
    if ( isChecked( "reportingMonth" ) == true ||
         isChecked( "last3Months" ) == true ||
         isChecked( "last6Months" ) == true ||
         isChecked( "last9Months" ) == true ||
         isChecked( "last12Months" ) == true ||
         isChecked( "last3To6Months" ) == true ||
         isChecked( "last6To9Months" ) == true ||
         isChecked( "last9To12Months" ) == true ||
         isChecked( "last12IndividualMonths" ) == true ||
         isChecked( "soFarThisYear" ) == true ||
         isChecked( "soFarThisFinancialYear" ) == true ||
         isChecked( "individualMonthsThisYear" ) == true ||
         isChecked( "individualQuartersThisYear" ) == true )
    {
        return true;
    }
    
    return false;
}

function organisationUnitReportParamsChecked()
{
    if ( isChecked( "paramParentOrganisationUnit" ) == true ||
         isChecked( "paramOrganisationUnit" ) == true )
    {
        return true;
    }
    
    return false;
}

// -----------------------------------------------------------------------------
// Details
// -----------------------------------------------------------------------------

function showTableDetails( tableId )
{
	/* var request = new Request();
    request.setResponseTypeXML( 'reportTable' );
    request.setCallbackSuccess( tableReceived );
    request.send( 'getTable.action?id=' + tableId );	*/
	
	$.post("getTable.action",
		{
			id : tableId
		},
		function (data)
		{
			tableReceived(data);
		},'xml');	
}

function tableReceived( xmlObject )
{
	setFieldValue( 'nameField', getElementValue( xmlObject, 'name' ) );
	setFieldValue( 'tableNameField', getElementValue( xmlObject, 'tableName' ) );
	setFieldValue( 'indicatorsField', getElementValue( xmlObject, 'indicators' ) );
	setFieldValue( 'periodsField', getElementValue( xmlObject, 'periods' ) );
	setFieldValue( 'unitsField', getElementValue( xmlObject, 'units' ) );
	setFieldValue( 'doIndicatorsField', parseBool( getElementValue( xmlObject, 'doIndicators' ) ) );
	setFieldValue( 'doPeriodsField', parseBool( getElementValue( xmlObject, 'doPeriods' ) ) );
	setFieldValue( 'doUnitsField', parseBool( getElementValue( xmlObject, 'doUnits' ) ) );
	
	showDetails();
}

function parseBool( bool )
{
	return ( bool == "true" ) ? i18n_yes : i18n_no;
}

// -----------------------------------------------------------------------------
// Regression
// -----------------------------------------------------------------------------

function toggleRegression()
{
    if ( document.getElementById( "regression" ).checked )
    {
        check( "doIndicators" );
        uncheck( "doOrganisationUnits" );
        uncheck( "doPeriods" );
        uncheck( "doCategoryOptionCombos" );
        
        disable( "doOrganisationUnits" );
        disable( "doPeriods" );        
        disable( "doCategoryOptionCombos" );
    }
    else
    {
        enable( "doOrganisationUnits" );
        enable( "doPeriods" );        
        enable( "doCategoryOptionCombos" );
    }
}
