
/*
 *     function getOUDetails(orgUnitIds)
    {
         jQuery.postJSON("../dhis-web-commons-ajax-json/getOrganisationUnit.action",{
          id: orgUnitIds
         }, function( json ){
               setFieldValue("your_oranisation_unit_field_id", json.organisationUnit.name );
         });
    }

jQuery.extend({
	postJSON: function( url, data, callback ) {
		return jQuery.post(url, data, callback, "json");
	}
});

 * 
 */

	
// function for displaying OrgUnit
//function getSelectedOrgUnit( orgUnitIds )
//{
    //alert( orgUnitIds );
	//if( orgUnitIds != "" )
   // {
	//	var url = "getOrgUnitName.action?selectedOrgUnitId=" + orgUnitIds;
	//	var request = new Request();
	//	request.setResponseTypeXML('dataelement');
	//	request.setCallbackSuccess( responseGetSelectedOrgUnitName );
	//	request.send(url); 
		
function getSelectedOrgUnit( orgUnitIds )
{
	//alert( orgUnitIds );  
	jQuery.postJSON("getOrgUnitName.action",{
    	  //selectedOrgUnitId: orgUnitIds
    	  id : orgUnitIds[0]
     }, function( json ){
           setFieldValue( "ouNameTB",json.organisationUnit.name );
     });
}
	/*
		
		$.post("getOrgUnitName.action",
			{
				selectedOrgUnitId : orgUnitIds
			},
			function (data)
			{
				 responseGetSelectedOrgUnitName(data);
			},'xml');
			*/
			
//	}
//}

function responseGetSelectedOrgUnitName( dataelement )
{
    var element = dataelement.getElementsByTagName("dataelement");
    var orgUnitname = element[0].getElementsByTagName("OugUnitName")[0].firstChild.nodeValue;
    //document.reportForm.ouNameTB.value=element[0].getElementsByTagName("OugUnitName")[0].firstChild.nodeValue;
    document.reportForm.ouNameTB.value = orgUnitname;
}

//function getAllPeriods() {
	//var periodTypeList = document.getElementById("periodTypeId");
//	var periodTypeId = periodTypeList.options[periodTypeList.selectedIndex].value;

//	if (periodTypeId != null) {
	//	var url = "getPeriodsForLock.action?name=" + periodTypeId;
	//	$.ajax( {
		//	url :url,
		//	cache :false,
		//	success : function(response) {
				/* dom = parseXML(response);
				$('#periodIds >option').remove();
				$(dom).find('period').each(
						function() {
							$('#periodIds').append(
									"<option value="
											+ $(this).find('id').text() + ">"
											+ $(this).find('name').text()
											+ "</option>");
						});
				enable("periodIds"); */
				
			//	getAllPeriodsReceived( response );
				
	//			getDataSets();
		//	}
	//	});
	//}
//}



/*
function getAllPeriodsReceived(xmlObject) {

	var periodList = byId("periodIds");

	clearList(periodList);

	var periods = xmlObject.getElementsByTagName("period");
	for ( var i = 0; i < periods.length; i++) {
		var id = periods[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = periods[i].getElementsByTagName("name")[0].firstChild.nodeValue;;

		/* var option = document.createElement("option");
		option.value = id;
		option.text = name;
		reportsList.add(option, null); */
		
	//	$("#periodIds").append("<option value='"+ id +"'>" + name + "</option>");
	//}
	//$("#periodIds").attr('disabled', false);
//}

// Functions for get availabe periods
function getAllPeriods() {
    var periodTypeList = document.getElementById( "periodTypeId" );
    var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;

    if ( periodTypeId != null ) {
        var url = "getPeriodsForLock.action?name=" + periodTypeId;
        $.ajax({
            url: url,
            cache: false,
            success: function(response){
                dom = parseXML(response);
                $( '#periodIds >option' ).remove();
                $(dom).find('period').each(function(){
                    $('#periodIds').append("<option value="+$(this).find('id').text()+">" +$(this).find('name').text()+ "</option>");
                });
                enable( "periodIds" );
                getDataSets();
            }
        });
    }
}


function parseXML(xml) {
	if (window.ActiveXObject && window.GetObject) {
		var dom = new ActiveXObject('Microsoft.XMLDOM');
		dom.loadXML(xml);
		return dom;
	}
	if (window.DOMParser)
		return new DOMParser().parseFromString(xml, 'text/xml');
	throw new Error('No XML parser available');
}
// functions for get all corresponding dataSets
function getDataSets() {
	var periodTypeList = document.getElementById("periodTypeId");
	var periodType = periodTypeList.options[periodTypeList.selectedIndex].value;

	if (periodType != null) {
		var url = "getDataSetsForLockAction.action?periodType=" + periodType;
		$.ajax( {
			url :url,
			cache :false,
			success : function(response) {
				$('#dataSets >option').remove();
				// $( '#lockedDataSets >option' ).remove();
			$(response).find('dataSet').each(
					function() {
						$('#dataSets').append(
								"<option value=" + $(this).find('id').text()
										+ ">" + $(this).find('name').text()
										+ "</option>");
					});
			enable("dataSets");
			// enable( "lockedDataSets" );
			enable("generate");
			// loadEmptyOrgUnitTree();
		}
		});
	}
}