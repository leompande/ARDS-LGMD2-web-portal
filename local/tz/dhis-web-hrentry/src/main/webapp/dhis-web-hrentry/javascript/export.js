
// -----------------------------------------------------------------------------
// DataMartExport
// -----------------------------------------------------------------------------

function exportDataValue()
{
    if ( validateDataValueExportForm() )
    {
        var aggregatedData = getListValue( "aggregatedData" );
        
        if ( aggregatedData == "true" )
        {
            var request = new Request();
            request.setResponseTypeXML( 'message' );
            request.setCallbackSuccess( validateAggregatedExportCompleted );
            request.send( "validateAggregatedExport.action" );
        }
        else
        {
            submitDataValueExportForm();
        }
    }
}
function exportHrDataValue()
{
    if ( validateHrDataValueExportForm() )
    {
        submitHrDataValueExportForm();
    }
}

function validateAggregatedExportCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var generateDataSource = getListValue( "generateDataSource" );
        
        if ( generateDataSource && generateDataSource == "true" )
        {
            var request = new Request();
            request.sendAsPost( getDataMartExportParams() );
            request.setCallbackSuccess( exportDataMartReceived );
            request.send( "exportDataMart.action" );   
        }
        else
        {
            submitDataValueExportForm();
        }
    }
    else if ( type == 'error' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

function exportDataMartReceived( messageElement )
{
    getExportStatus();
}

function getExportStatus()
{
    var url = "getExportStatus.action";
    
    var request = new Request();
    request.setResponseTypeXML( "status" );
    request.setCallbackSuccess( exportStatusReceived );    
    request.send( url );
}

function exportStatusReceived( xmlObject )
{
    var statusMessage = getElementValue( xmlObject, "statusMessage" );
    var finished = getElementValue( xmlObject, "finished" );
    
    if ( finished == "true" )
    {        
        submitDataValueExportForm();
    }
    else
    {
        setMessage( statusMessage );
        
        setTimeout( "getExportStatus();", 2000 );
    }
}

// -----------------------------------------------------------------------------
// Supportive methods
// -----------------------------------------------------------------------------

function getDataMartExportParams()
{
    var params = getParamString( "selectedDataSets", "selectedDataSets" );
    
    params += "startDate=" + document.getElementById( "startDate" ).value + "&";
    params += "endDate=" + document.getElementById( "endDate" ).value + "&";
    params += "dataSourceLevel=" + getListValue( "dataSourceLevel" );
    
    return params;
}

// -----------------------------------------------------------------------------
// Export
// -----------------------------------------------------------------------------

function submitDataValueExportForm()
{
	var domainType = getListValue( "domainType" );
	if ( domainType == "aggregate" )
	{
	    selectAll( document.getElementById( "selectedDataSets" ) );
	    // Clear other selected dataset options
	    moveAllById( 'selectedPrograms', 'availablePrograms' );
	    moveAllById( 'selectedHrDataSets', 'availableHrDataSets' );
		
		if ( validateDataValueExportForm() )
		{
		   document.getElementById( "exportForm" ).submit();
		}
	}
	else if ( domainType == "hr" )
	{
		selectAll( document.getElementById( "selectedHrDataSets" ) );
		//Clear Other selected datasets options
		moveAllById( 'selectedPrograms', 'availablePrograms' );
		moveAllById( 'selectedDataSets', 'availableDataSets' );
		
		if ( validateDataValueExportForm() )
		{
		   document.getElementById( "exportForm" ).submit();
		}
	}
	else if ( domainType == "patient" )
	{
		selectAll( document.getElementById( "selectedPrograms" ) );
		// Clear Other selected datasets options
		moveAllById( 'selectedDataSets', 'availableDataSets' );
		moveAllById( 'selectedHrDataSets', 'availableHrDataSets' );
		
		if ( validateDataValueExportForm() )
		{
		   document.getElementById( "exportForm" ).submit();
		}
	}
}


function setDataType()
{
    var aggregatedData = getListValue( "aggregatedData" );
  
    if ( aggregatedData == "true" )
    {
        showById( "aggregatedDataDiv" );
        hideById( "regularDataDiv" );
    }
    else
    {
        hideById( "aggregatedDataDiv" );
        showById( "regularDataDiv" );
    }
}
function showRowById( id )
{
  jQuery("#" + id).show();
  jQuery("#" + id).css('visibility', 'visible');
}
function hideRowById( id )
{
  jQuery("#" + id).hide();
  jQuery("#" + id).css('visibility', 'collapse');
}

function setDatasetType()
{
    var domainType = getListValue( "domainType" );
  
    if ( domainType == "aggregate" )
    {
        showRowById( "dataSetDiv1" );
        showRowById( "dataSetDiv2" );
        showRowById("startDateDiv1");
        showRowById("startDateDiv2");
        hideRowById( "hrDataSetDiv1" );
        hideRowById( "hrDataSetDiv2" );
        hideRowById( "programDiv1");
        hideRowById( "programDiv2");
    }
    else if ( domainType == "hr")
    {
    	showRowById( "hrDataSetDiv1" );
    	showRowById( "hrDataSetDiv2" );
        hideRowById( "dataSetDiv1" );
        hideRowById( "dataSetDiv2" );
        hideRowById( "programDiv1" );
        hideRowById( "programDiv2" );
        hideRowById("startDateDiv1");
        hideRowById("startDateDiv2");
    }
    else if ( domainType == "patient")
    {
    	showRowById( "programDiv1" );
    	showRowById( "programDiv2" );
    	showRowById("startDateDiv1");
        showRowById("startDateDiv2");
    	hideRowById("startDateDiv1");
        hideRowById("startDateDiv2");
        hideRowById( "dataSetDiv1" );
        hideRowById( "dataSetDiv2" );
        hideRowById( "hrDataSetDiv1");
        hideRowById( "hrDataSetDiv2");
    }
}


// -----------------------------------------------------------------------------
// MetaDataExport
// -----------------------------------------------------------------------------

function submitMetaDataExportForm()
{
    if ( validateMetaDataExportForm() )
    {
       document.getElementById( "exportForm" ).submit();
    }
}

function toggle( knob )
{
    var toggle = (knob == "all" ? true : false);
	
	jQuery.each( jQuery("input[type=checkbox]"), function(i, item){
		item.checked = toggle;
	});
}

// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validateMetaDataExportForm()
{
	if ( jQuery("input:checked").length == 0 )
	{
		setMessage( i18n_select_one_or_more_object_types );
		return false;
	}
	
	hideMessage();
	return true;
}

function validateDataValueExportForm()
{
	var domainType = getListValue( "domainType" );
	
    if ( selectedOrganisationUnitIds == null || selectedOrganisationUnitIds.length == 0 )
    {
        setMessage( i18n_select_organisation_unit );
        return false;
    }
    if ( !hasText( "startDate" ) )
    {
        setMessage( i18n_select_startdate );
        return false;
    }
    if ( !hasText( "endDate" ) )
    {
        setMessage( i18n_select_enddate );
        return false;
    }
    if ( domainType == "aggregate" )
    {
	    if ( !hasElements( "selectedDataSets" ) )
	    {
	        setMessage( i18n_select_datasets );
	        return false;
	    }
    }
    else if ( domainType == "hr" )
    {
    	if ( !hasElements( "selectedHrDataSets" ) )
	    {
	        setMessage( i18n_select_hr_datasets );
	        return false;
	    }
    }
    else if ( domainType == "patient" )
    {
    	if ( !hasElements( "availablePrograms" ) )
	    {
	        setMessage( i18n_select_programs );
	        return false;
	    }
    }
    
    hideMessage();
    return true;
}

