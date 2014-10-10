function showDataElementGroupOrderDetails( id )
{
	jQuery.post( 'getDataElementGroupOrder.action',  { id: id }, function( json ) {
		
		setInnerHTML( 'nameField', json.dataElementGroupOrder.name );
		setInnerHTML( 'codeField', json.dataElementGroupOrder.code );
		setInnerHTML( 'memberCountField', json.dataElementGroupOrder.memberCount );

		showDetails();
	});
}

/*
* 	Open Add Data Element Group Order 
*/
function openAddDataElementGroupOrder()
{
	validator.resetForm();
	setFieldValue( "dataElementGroupOrderId", "" );

	dataDictionary.loadDataElementGroups( "#availableDataElementGroups" );
	dataDictionary.loadDataElementsByGroup( "" , "#availableDataElements" );
	
	dialog.dialog("open");
	
	jQuery( "#dataElementGroupsForm" ).attr( "action", "addDataElementGroupOrderFor" + clazzName + ".action?clazzName=" + clazzName );
}

/*
* 	Open Update Data Element Order
*/

function openUpdateDataElementGroupOrder( id )
{
	validator.resetForm();
	setFieldValue("dataElementGroupOrderId", id );
	
	jQuery.post( 'getDataElementGroupOrder.action', { id: id }, function( json )
	{
		var listDataElement = jQuery('#dataElementIds');
		listDataElement.empty();
		setFieldValue( "name", json.dataElementGroupOrder.name );
		setFieldValue( "code", json.dataElementGroupOrder.code );
		
		var dataElements = json.dataElementGroupOrder.dataElements;
		
		for ( var i = 0 ; i < dataElements.length ; i++ )
		{
			listDataElement.append( '<option value="' + dataElements[ i ].id + '">' + dataElements[ i ].name + '</option>' );
		}
		
		dataDictionary.loadDataElementGroups( "#availableDataElementGroups" );
		dataDictionary.loadDataElementsByGroup( "" , "#availableDataElements" );	
			
		dialog.dialog( "open" );
		
		jQuery( "#dataElementGroupsForm" ).attr( "action", "updateDataElementGroupOrderFor" + clazzName + ".action" );
	});
}

function validateDataElementGroupOrder( _form )
{
	jQuery.post( "validateDataElementGroupOrder.action", {
		name: getFieldValue( 'name' ),
		id: getFieldValue( 'dataElementGroupOrderId' ),
		reportId: reportId,
		clazzName: clazzName
	}, function( json ){
		if ( json.response == "success" )
		{
			listValidator( 'dataElementIdsValidate', 'dataElementIds' );
			_form.submit();
		}
		else { markInvalid( "name", json.message ); }
	} );
}

/*
* 	Delete Data Element Order
*/
function deleteDataElementGroupOrder( id, name )
{
	removeItem( id, name, i18n_confirm_delete, 'deleteDataElementGroupOrder.action', function(){ window.location.reload(); } );
}

/*
*	Update data element group order
*/
function updateSortDataElementGroupOrder()
{
	var dataElements = document.getElementsByName('dataElementGroupOrder');
	var url = "updateSortDataElementGroupOrder.action?reportId=" + reportId;
	url += "&clazzName=" + clazzName;
	
	for ( var i = 0 ; i < dataElements.length ; i++ )
	{
		url += "&dataElementGroupOrderId=" + dataElements.item(i).value;
	}
	
	jQuery.postJSON( url, {}, function( json ) {
		showSuccessMessage( json.message );
	});
}

function openSortDataElementForGroupOrder( id )
{
	window.location = "openSortDataElement.action?id="+id+"&reportId="+reportId+"&clazzName="+clazzName;
}

/*
* 	Update Sorted Data Element 
*/
function updateSortedDataElement()
{	
	moveAllById( 'availableList', 'selectedList' );
	selectAllById('selectedList');
	document.forms[0].submit();
}

/*
*	Tooltip
*/
function showToolTip( e, value)
{	
	var tooltipDiv = byId('tooltip');
	tooltipDiv.style.display = 'block';
	
	var posx = 0;
    var posy = 0;
	
    if (!e) var e = window.event;
    if (e.pageX || e.pageY)
    {
        posx = e.pageX;
        posy = e.pageY;
    }
    else if (e.clientX || e.clientY)
    {
        posx = e.clientX;
        posy = e.clientY;
    }
	
	tooltipDiv.style.left= posx  + 8 + 'px';
	tooltipDiv.style.top = posy  + 8 + 'px';
	tooltipDiv.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +   value;
}

function hideToolTip()
{
	byId('tooltip').style.display = 'none';
}