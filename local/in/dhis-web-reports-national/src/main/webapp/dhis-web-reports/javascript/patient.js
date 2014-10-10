//-----------------------------------------------------------------------------
// Search Patient
//-----------------------------------------------------------------------------

function searchPatientsOnKeyUp( event )
{
	var key = getKeyCode( event );
	
	if ( key==13 )// Enter
	{
		searchPatients();
	}
}

function getKeyCode(e)
{
	 if (window.event)
		return window.event.keyCode;
	 return (e)? e.which : null;
}


function searchPatients()
{
	hideById( 'listPatientDiv' );
	var searchTextFields = jQuery('[name=searchText]');
	var flag = true;
	jQuery( searchTextFields ).each( function( i, item )
    {
		if( jQuery( item ).val() == '' )
		{
			showWarningMessage( i18n_specify_search_criteria );
			flag = false;
		}
	});
	
	if(!flag) return;
	
	contentDiv = 'listPatientDiv';
	jQuery( "#loaderDiv" ).show();
	$.ajax({
		url: 'searchRegistredPatient.action',
		type:"POST",
		data: getParamsForDiv('searchPatientDiv'),
		success: function( html ){
				statusSearching = 1;
				setInnerHTML( 'listPatientDiv', html );
				showById('listPatientDiv');
				jQuery( "#loaderDiv" ).hide();
			}
		});
}

function getParamsForDiv( patientDiv )
{
	var params = '';
	jQuery("#" + patientDiv + " :input").each(function()
		{
			var elementId = $(this).attr('id');
			
			if( $(this).attr('type') == 'checkbox' )
			{
				var checked = jQuery(this).attr('checked') ? true : false;
				params += elementId + "=" + checked + "&";
			}
			else if( $(this).attr('type') != 'button' )
			{
				params += elementId + "="+ htmlEncode(jQuery(this).val()) + "&";
			}
		});
		
	return params;
}

//-----------------------------------------------------------------------------
//View patient details
//-----------------------------------------------------------------------------

function showPatientDetails( patientId )
{
 $('#detailsInfo').load("getPatientDetails.action", 
		{
			id:patientId
		}
		, function( ){
		}).dialog({
			title: i18n_patient_details,
			maximize: true, 
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 500,
			height: 500
		});;
}


function getSelectedOrgUnit( orgUnitIds )
{
	jQuery.postJSON("getOrgUnitName.action",{
    	  id : orgUnitIds[0]
     }, function( json ){
           setFieldValue( "ouNameTB",json.organisationUnit.name );
     });
}

function getOUDetails(orgUnitIds)
{
	$.post("getOrgUnitDetails.action",
		{
			orgUnitId : orgUnitIds
		},
		function (data)
		{
			getOUDetailsRecevied(data);
		},'xml');
}

function getOUDetailsRecevied(xmlObject)
{
		
    var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        var level = orgUnits[ i ].getElementsByTagName("level")[0].firstChild.nodeValue;
		
        document.getElementById("ouNameTB").value = orgUnitName;
        //document.reportForm.ouNameTB.value = orgUnitName;
    }    		
}


function generatePatientReport( event, patientId )
{
	
	var tempPatientId = "";
	var tempProgramId = "";
	
	var programDropDown = document.getElementById("programId_"+patientId);
	var selProgramId = programDropDown.options[ programDropDown.selectedIndex ].value;
	//alert(patientId +"----" + selProgramId );
	
	var excelTemplateName = "";
	var xmlTemplateName = "";
	
	tempPatientId = patientId;
	tempProgramId = selProgramId;
	
	
	var flag = 1;
	
	
    for ( i = 0; i < programIds.length; i++ )
    {
    	if( selProgramId == programIds[i] )
    	{
    		//alert( programIds[i] + "----" + selProgramId );
    		excelTemplateName = programExcelFileNames.get(programIds[i]);
    		xmlTemplateName = programXmlFileNames.get(programIds[i]);
    		//alert( excelTemplateName + "----" + xmlTemplateName );
    		//document.getElementById("exportToExcel").href="generatePatientReport.action?patientId=" + patientId + "&selProgramId=" + selProgramId + "&excelTemplateName=" + excelTemplateName + "&xmlTemplateName=" + xmlTemplateName;
    		
    		flag = 2;
    		break;
    	}

    }
    if( flag != 2 )
    {
    	//alert("There is no xml and xls template for selected program");
    	showWarningMessage( i18n_no_xls_xml );
    	return;
    	
    }
    
	else
    {	
		/*
		document.forms[0].method="Post";
	    document.forms[0].action="../Customer";
	    document.forms[0].submit();
	    */
	    
	    //event.target.href = "generatePatientReport.action?patientId=" + patientId + "&selProgramId=" + selProgramId + "&excelTemplateName=" + excelTemplateName + "&xmlTemplateName=" + xmlTemplateName;
		
	    //alert( tempPatientId + "----" + tempProgramId + "----" + excelTemplateName + "----" + xmlTemplateName  );
		
	    document.getElementById("patientId").value = tempPatientId;
	    document.getElementById("selProgramId").value = tempProgramId;
	    document.getElementById("excelTemplateName").value = excelTemplateName;
	    document.getElementById("xmlTemplateName").value = xmlTemplateName;
	    
	    /*
	    alert( document.getElementById("patientId").value );
	    alert( document.getElementById("selProgramId").value );
	    alert( document.getElementById("excelTemplateName").value );
	    alert( document.getElementById("xmlTemplateName").value );
	    */
	    document.patientForm.action = "generatePatientReport.action";
	    document.patientForm.submit();
	    
		/*
	    document.PatientForm.method="get";
	    
		document.PatientForm.action = "generatePatientReport.action?patientId=" + patientId + "&selProgramId=" + selProgramId + "&excelTemplateName=" + excelTemplateName + "&xmlTemplateName=" + xmlTemplateName;
        document.PatientForm.submit();
		*/
		//document.getElementById("exportToExcel").href="generatePatientReport.action?patientId=" + patientId + "&selProgramId=" + selProgramId + "&excelTemplateName=" + excelTemplateName + "&xmlTemplateName=" + xmlTemplateName;
    }
	
	//event.target.href = "generatePatientReport.action?patientId=" + patientId + "&selProgramId=" + selProgramId;
	
	//document.getElementById("exportToExcel").href="generatePatientReport.action?patientId=" + patientId + "&selProgramId=" + selProgramId;
	
	//alert( event.target.href );
	
}


function generatePatientFollowUpReport( event, patientId )
{
	
	var tempPatientId = "";
	var tempProgramId = "";
	
	var programDropDown = document.getElementById("programId_"+patientId);
	var selProgramId = programDropDown.options[ programDropDown.selectedIndex ].value;
	
	var excelTemplateName = "SNCUFollowUpSheet.xls";
	var xmlTemplateName = "SNCUFollowUpSheet.xml";
	
	tempPatientId = patientId;
	tempProgramId = selProgramId;



    //event.target.href = "generatePatientReport.action?patientId=" + patientId + "&selProgramId=" + selProgramId + "&excelTemplateName=" + excelTemplateName + "&xmlTemplateName=" + xmlTemplateName;
	
    //alert( tempPatientId + "----" + tempProgramId + "----" + excelTemplateName + "----" + xmlTemplateName  );
	
    document.getElementById("patientId").value = tempPatientId;
    document.getElementById("selProgramId").value = tempProgramId;
    document.getElementById("excelTemplateName").value = excelTemplateName;
    document.getElementById("xmlTemplateName").value = xmlTemplateName;
    
    document.patientForm.action = "generatePatientReport.action";
    document.patientForm.submit();
	    
   
	
}
