
// -----------------------------------------------------------------------------
// View report
// ----------------------------------------------------------------------------

function previewDataSetReport()
{
	document.getElementById("reportForm").action = "getDataSetReportTypeForPreview.action";
	document.getElementById("reportForm").submit();	
}

function generateDataSetReport()
{
	document.getElementById("reportForm").action = "getDataSetReportTypeForPDF.action";
	document.getElementById("reportForm").submit();
}

// -----------------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------------

var selectedOrganisationUnitIds = null;

function setSelectedOrganisationUnitIds( ids )
{
    selectedOrganisationUnitIds = ids;
}

if ( selectionTreeSelection )
{
    selectionTreeSelection.setListenerFunction( setSelectedOrganisationUnitIds );
}

function validateDataSetReport()
{
    if ( !getListValue( "selectedDataSetId" ) || getListValue( "selectedDataSetId" ) == "null" )
    {
        setMessage( i18n_select_data_set );
        return false;
    }
    if ( !getListValue( "selectedPeriodIndex" ) || getListValue( "selectedPeriodIndex" ) == "null" )
    {
        setMessage( i18n_select_period );
        return false;
    }
    if ( selectedOrganisationUnitIds == null || selectedOrganisationUnitIds.length == 0 )
    {
        setMessage( i18n_select_organisation_unit );
        return false;
    }
    
    return true;
}

// -----------------------------------------------------------------------------
// Generate data source
// ----------------------------------------------------------------------------

var tempPreviewReport;

function runAndViewDataSetReport( previewReport )
{
    if ( validateDataSetReport() )
    {
        setMessage( i18n_generating_report + "..." );
        
        tempPreviewReport = previewReport;
        
        /* var request = new Request();
        request.setCallbackSuccess( runAndViewDataSetReportReceived );    
        request.send( "createDataSetReportDataSource.action" ); */
		
		$.post("createDataSetReportDataSource.action",
			{
			},
			function (data)
			{
				 runAndViewDataSetReportReceived(data);
			},'xml');
    }
}

function runAndViewDataSetReportReceived( messageElement )
{
    getDataSetReportStatus();
}

function getDataSetReportStatus()
{   
    /* var url = "getStatus.action";
    
    var request = new Request();
    request.setResponseTypeXML( "status" );
    request.setCallbackSuccess( dataSetReportStatusReceived );    
    request.send( url ); */
	
	$.post("getStatus.action",
		{
		},
		function (data)
		{
			dataSetReportStatusReceived(data);
		},'xml');
}

function dataSetReportStatusReceived( xmlObject )
{
    var finished = getElementValue( xmlObject, "finished" );
    
    if ( finished == "true" )
    {
        hideMessage();
        
        if ( tempPreviewReport )
        {
            previewDataSetReport();
        }
        else
        {
            generateDataSetReport();
        }
    }
    else
    {
        setTimeout( "getDataSetReportStatus();", 2000 );
    }
}
