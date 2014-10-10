
function getOUDetails(orgUnitIds)
{
	//var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;
	/*
	var request = new Request();
	request.setResponseTypeXML( 'orgunit' );
	request.setCallbackSuccess( getOUDetailsRecevied );
	
    var requestString = "getOrgUnitDetails.action";
    var params = "orgUnitId=" + orgUnitIds;
    request.sendAsPost( params );
    request.send( requestString );
    */
	$.post("getOrgUnitDetails.action",
			{
				orgUnitId : orgUnitIds[0]
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
		
		document.getElementById("selOrgUnitName").value = orgUnitName;	
    }    		
}
//form Validation

function formValidationsLLAggregation()
{
	var startPeriodObj = document.getElementById('sDateLB');
	var endPeriodObj = document.getElementById('eDateLB');
	
	sDateTxt = startPeriodObj.options[startPeriodObj.selectedIndex].text;
	sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM-y")),"yyyy-MM-dd");
	
	eDateTxt = endPeriodObj.options[endPeriodObj.selectedIndex].text;
	eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM-y")),"yyyy-MM-dd");
	
	if(sDate > eDate) 
	{
		alert( "Starting Date is Greater" );
		return false;
	}
	
	return true;
}

function llAggregate()
{
	if( formValidationsLLAggregation() )
	{        
		$( '#llAggregateDiv' ).html( ' ' );
		
		var selOrgUnitId = $( '#selOrgUnitId' ).val();
		var sDateLB = $( '#sDateLB' ).val();
		var eDateLB = $( '#eDateLB' ).val();
		
		jQuery('#loaderDiv').show();
		document.getElementById( "aggregate" ).disabled = true;
		
		jQuery('#llAggregateDiv').load('autoLLAgg.action',
			{
				selOrgUnitId:selOrgUnitId,
				sDateLB:sDateLB,
				eDateLB:eDateLB
			}, function()
			{
				showById('llAggregateDiv');
				document.getElementById( "aggregate" ).disabled = false;
				jQuery('#loaderDiv').hide();
			});
	}
}	

