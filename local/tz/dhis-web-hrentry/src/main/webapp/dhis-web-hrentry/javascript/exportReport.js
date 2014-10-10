//-----------------------------------------------------------------------------
//Export Report
//----------------------------------------------------------------------------

function exportReport( type, unitId, hrDataSetId, selectedUnitOnly )
{
	var url = "exportTable.action?type=" + type + "&unitId=" + unitId + "&hrDataSetId=" + hrDataSetId + "&selectedUnitOnly=" + selectedUnitOnly;
	
	url += $( "#id" ).length ? ( "&id=" + $( "#id" ).val() ) : "";
	
	window.location.href = url;
}

