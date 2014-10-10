function organisationUnitSelected( orgUnits )
{	
	window.location.reload();
}
selection.setListenerFunction( organisationUnitSelected );

function viewData( dataSetId, periodId, organisationUnitId ) {

	var url = "viewCustomDataSetReport.action?dataSetId=" + dataSetId + "&periodId=" + periodId + "&organisationUnitId=" + organisationUnitId;
	
	window.open(  url , '_blank', 'width=' + document.documentElement.clientWidth+',height='+document.documentElement.clientHeight+',scrollbars=yes' );
}
