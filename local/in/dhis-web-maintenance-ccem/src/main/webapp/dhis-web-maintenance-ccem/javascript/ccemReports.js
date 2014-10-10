// ----------------------------------------------------------------
// Organisation Unit Selected
// ----------------------------------------------------------------

function exportReport( type )
{
    var url = "exportTable.action?type=" + type + "&useLast=true";

    url += $( "#id" ).length ? ( "&id=" + $( "#id" ).val() ) : "";

    window.location.href = url;
}

function organisationUnitSelected( orgUnits )
{   
	$.getJSON( 'getOrganisationUnit.action', {orgunitId:orgUnits[0]}
        , function( json ) 
        {
            //json.message
        	var flag = 0;
            var orgUnitListBox = document.getElementById('selOrgUnitList');
            for( var i = 0; i < orgUnitListBox.options.length; i++ )
            {
            	if( orgUnits[0] == orgUnitListBox.options[i].value ) 
                {
                	flag = 1;
                	break;
                }
            }
            
            if( flag == 0 )
            {
            	orgUnitListBox.options[orgUnitListBox.options.length] = new Option( json.message, orgUnits[0], false, false);
            }
           
        } );
}

selection.setListenerFunction( organisationUnitSelected );

//----------------------------------------------------------------
// Removes selected orgunits from orgunit list
//----------------------------------------------------------------
function remOUFunction()
{
    var orgUnitListBox = document.getElementById( "selOrgUnitList" );

    for( var i = orgUnitListBox.options.length-1; i >= 0; i-- )
    {
        if( orgUnitListBox.options[i].selected )
        {
        	orgUnitListBox.options[i] = null;
        }
    }    
}

//----------------------------------------------------------------
//Select all options in the list
//----------------------------------------------------------------
function selectAllOptionsInList( elementId )
{
    var element = document.getElementById( elementId );

    for( var i = 0; i < element.options.length; i++ )
    {
        element.options[i].selected = true;
    }    
}

//----------------------------------------------------------------
// Enable/Disable radio options for period based on report selection
//----------------------------------------------------------------
function periodBox( reportId )
{
	if( document.getElementById( reportId ).value == "NO" )
	{
		//alert( document.getElementById( reportId ).value );
		document.forms[0].periodRadio[0].disabled=true;
        document.forms[0].periodRadio[1].disabled=true;
        document.forms[0].periodRadio[2].disabled=true;
        document.forms[0].periodRadio[3].disabled=true;
        
        document.getElementById("last3months").style.fontWeight = '';
        document.getElementById("currentYear").style.fontWeight = '';
        document.getElementById("last6months").style.fontWeight = '';
        document.getElementById("lastYear").style.fontWeight = '';
        
	}
	else if( document.getElementById( reportId ).value == "Yearly" )
	{
		//alert( document.getElementById( reportId ).value );
		document.forms[0].periodRadio[0].disabled=true;
        document.forms[0].periodRadio[1].disabled=false;
        document.forms[0].periodRadio[2].disabled=true;
        document.forms[0].periodRadio[3].disabled=false;
        //alert(document.getElementById( "last3months" ).innerHTML );
        /*
        var last3months = document.getElementById( "last3months" );
        last3months.style.fontWeight = 'bold';
        */
        //last3months.style.color = "green";
        //document.getElementById(id).style.fontWeight = 'bold';
        
        var currentYear = document.getElementById( "currentYear" );
        //last3months.style.color = "green";
        currentYear.style.fontWeight = 'bold';
	}
    else if( document.getElementById( reportId ).value == "Monthly" )
    {
    	//alert( document.getElementById( reportId ).value );
    	document.forms[0].periodRadio[0].disabled=false;
        document.forms[0].periodRadio[1].disabled=false;
        document.forms[0].periodRadio[2].disabled=false;
        document.forms[0].periodRadio[3].disabled=false;
        
        var last3months = document.getElementById( "last3months" );
        //last3months.style.color = "green";
        last3months.style.fontWeight = 'bold';
        
        var currentYear = document.getElementById( "currentYear" );
        //currentYear.style.color = "green";
        currentYear.style.fontWeight = 'bold';
        
        var last6months = document.getElementById( "last6months" );
        //last6months.style.color = "green";
        last6months.style.fontWeight = 'bold';
        
        var lastYear = document.getElementById( "lastYear" );
        //lastYear.style.color = "green";
        lastYear.style.fontWeight = 'bold';
    }   
}

//----------------------------------------------------------------
//CCEM Report Form validations
//----------------------------------------------------------------
function formValidations()
{
	var reportListBox = document.getElementById( "reportList" );
	var ouGroupListBox = document.getElementById( "orgunitGroupList" );
	var orgUnitListBox = document.getElementById( "selOrgUnitList" );
	
	var selPeriodOption = $( "input[name='periodRadio']:checked" ).val();
	
	selectAllOptionsInList( 'selOrgUnitList' );
	
	if( reportListBox.selectedIndex < 0 ) { alert( "Please select report" ); return false; }
	else if( ouGroupListBox.selectedIndex < 0 ) { alert( "Please select orgunit group(s)" ); return false; }
	else if( orgUnitListBox.selectedIndex < 0 ) { alert( "Please select orgunit(s)" ); return false; }	
	else if( document.getElementById( reportListBox.options[reportListBox.selectedIndex].value ).value != "NO" ) 
	{ 
		if( selPeriodOption == null ) { alert( "Please select period" ); return false; }
	}
	
	return true;
}

//----------------------------------------------------------------
//Generate CCEM Report
//----------------------------------------------------------------
function generateJRXMLReport()
{
	if( formValidations() )
	{		
		$("#ccemReportForm").attr("action", "exportTable.action"); 
        $("#ccemReportForm").attr("method", "post");      
        $("#ccemReportForm").attr('target', '_blank');
        $("#ccemReportForm").submit();
		
	}
}


function generateCCEMReport()
{
	if( formValidations() )
	{
	
		var reportListBox = document.getElementById( "reportList" );
		
		var selReportId = reportListBox.options[ reportListBox.selectedIndex ].value;
		
		var selPeriodOption = $( "input[name='periodRadio']:checked" ).val();
		
		hideById('resultContent');
		
		jQuery('#loaderDiv').show();
		
		var url = "generateCCEMReport.action?";
		url += getParamStringForSelectedItems( 'selOrgUnitList', 'selOrgUnitList' ) + "&"
		url += getParamStringForSelectedItems( 'orgunitGroupList', 'orgunitGroupList' ) + "&"
		//url += "selReportId="+selReportId;
	
		jQuery('#resultContent').load(url,{
			selReportId:selReportId,
			periodRadio:selPeriodOption,
			}, 
			function()
			{
				showById('resultContent');
				jQuery('#loaderDiv').hide();
			});
	}
}

//----------------------------------------------------------------
//Get Params form Div
//----------------------------------------------------------------

function getParamStringForSelectedItems( elementId, param )
{
	 var result = "";
	 var element = document.getElementById( elementId );
	 
	 var flag = 0;
	 for( var i = 0; i < element.options.length; i++ )
	 {
		 if( element.options[i].selected ) 
		 {
			 result += param + "=" + element.options[i].value + "&";
		 }
	 }

	 return result.substring( 0, result.length-1 );
}

//----------------------------------------------------------------
//Get Params form Div
//----------------------------------------------------------------

function getParamsForDiv( selectedDiv )
{
	var params = '';
	
	jQuery("#" + selectedDiv + " :input").each(function()
		{
			var elementId = $(this).attr('id');
			
			if( $(this).attr('type') == 'checkbox' )
			{
				var checked = jQuery(this).attr('checked') ? true : false;
				params += elementId + "=" + checked + "&";
			}
			else if( $(this).attr('type') != 'button' )
			{
				var value = "";
				if( jQuery(this).val() != '' )
				{
					value = htmlEncode(jQuery(this).val());
				}
				params += elementId + "="+ value + "&";
			}
			
		});
	
	return params;
}
