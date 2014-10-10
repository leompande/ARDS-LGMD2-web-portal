
// J query validation
jQuery(document).ready(function()
{
    validation2('dataStatusMartForm', function( form )
    {
        validateDataEntryStatusForm(form);
    },
    {
        'beforeValidateHandler' : function() {
            $("#selectedPeriods option").each(function() { $(this).attr("selected", "true"); });
            $("#selectedDataSets option").each(function() { $(this).attr("selected", "true"); });
        },
        'rules' : getValidationRules("dataLocking")
    });
});

// for Validation of form
function validateDataEntryStatusForm( form )
{
	var url = "validateDataEntryStatus.action?";
		url += getParamString( "selectedPeriods", "selectedPeriods" );
		url += "&" + getParamString( "selectedDataSets", "selectedDataSets" );

	$.postJSON( url, {}, function( json )
	{
		if ( json.response == "input" )
		{
			setHeaderDelayMessage( json.message );
		}
		else if ( json.response == "success" )
		{
			selectAllById( "selectedPeriods" );
			selectAllById( "selectedDataSets" ); 
			form.submit();
		}
	});
}


function getPeriods()
{
	var periodTypeList = byId("periodTypeId");
	var periodTypeId = periodTypeList.options[periodTypeList.selectedIndex].value;

	if (periodTypeId != null) {
		var url = "getPeriodsDSMart.action?name=" + periodTypeId;
		$.ajax( {
			url :url,
			cache :false,
			success : function(response) {
				dom = parseXML(response);
				$('#availablePeriods >option').remove();
				$(dom).find('period').each(
						function() {
							$('#availablePeriods').append(
									"<option value="
											+ $(this).find('id').text() + ">"
											+ $(this).find('name').text()
											+ "</option>");
						});
			}

		});
	}
	
	enable("calculate");
	enable("unlock");
	enable("availablePeriods");
	
	getDataSets();

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

function getDataSets() {

	var periodTypeList = byId("periodTypeId");
	var periodType = periodTypeList.options[periodTypeList.selectedIndex].value;

	if (periodType != null) {
		var url = "getDataSetsForPeriodType.action?periodType=" + periodType;
		$.ajax( {
			url :url,
			cache :false,
			success : function(response) {
			$('#availableDataSets >option').remove();
			$(response).find('dataSet').each(
					function() {
						$('#availableDataSets').append(
								"<option value=" + $(this).find('id').text()
										+ ">" + $(this).find('name').text()
										+ "</option>");
					});
			enable("availableDataSets");
			$('#selectedPeriods >option').remove();
			$('#selectedDataSets >option').remove();
		}
		});
	}
}
