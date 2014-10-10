var selectedAttributeValueMap = new Array();

function showAttributeValueGroupOrderDetails( id )
{
	jQuery.post( 'getAttributeValueGroupOrder.action',  { id: id }, function( json ) {
		
		setInnerHTML( 'nameField', json.attributeValueGroupOrder.name );
		setInnerHTML( 'memberCountField', json.attributeValueGroupOrder.memberCount );

		showDetails();
	});
}

function resetForm()
{
	setFieldValue( "name", "" );
	setFieldValue( "attributeValueGroupOrderId", "" );

	var availableList = jQuery( '#availableAttributeValues' );
	availableList.empty();
	var selectedList = jQuery( '#attributeValues' );
	selectedList.empty();
}

/*
* 	Open Add Attribute Value Group Order 
*/
function openAddAttributeValueGroupOrder()
{
	resetForm();
	validator.resetForm();

	attributeLib.loadAttributes( "attributeId" );

	jQuery( "#attributeValueGroupsForm" ).attr( "action", "addAttributeValueGroupOrder.action" );
	
	dialog.dialog("open");
}

/*
* 	Open Update Data Element Order
*/

function openUpdateAttributeValueGroupOrder( id )
{
	validator.resetForm();
	setFieldValue( "attributeValueGroupOrderId", id );
	
	jQuery.post( 'getAttributeValueGroupOrder.action', { id: id }, function( json )
	{
		var attributeId = json.attributeValueGroupOrder.attributeId;
		var values = json.attributeValueGroupOrder.attributeValues;
		var list = jQuery( "#attributeValues" );
		list.empty();
		selectedAttributeValueMap = [];
		var items = [];
		
		setFieldValue( "name", json.attributeValueGroupOrder.name );
		attributeLib.loadAttributes( "attributeId", attributeId );
		attributeLib.loadAttributeValuesByAttribute( attributeId, items, "availableAttributeValues", "attributeValues", true );
		
		for ( var i = 0 ; i < values.length ; i++ )
		{
			items.push( new AttributeValue( values[ i ].value ) );
			list.append( '<option value="' + values[ i ].value + '">' + values[ i ].value + '</option>' );
		}

		selectedAttributeValueMap[ id + "-" + attributeId ] = items;

		attributeLib.removeDuplicatedItem( "availableAttributeValues", "attributeValues" );
		
		jQuery( "#attributeValueGroupsForm" ).attr( "action", "updateAttributeValueGroupOrder.action" );

		dialog.dialog( "open" );
	} );
}

function validateAttributeValueGroupOrder( _form )
{
	var attributeId = getFieldValue( "attributeId" );

	if ( attributeId && attributeId != -1 )
	{
		jQuery.postUTF8( "validateAttributeValueGroupOrder.action", {
			name: getFieldValue( 'name' ),
			id: getFieldValue( 'attributeValueGroupOrderId' )
		}, function( json )
		{
			if ( json.response == "success" )
			{
				if ( hasElements( 'attributeValues' ) )
				{
					selectAllById( 'attributeValues' );
					_form.submit();
				}
				else { markInvalid( "attributeValues", i18n_selected_list_empty ); }
			}
			else { markInvalid( "name", json.message ); }
		} );
	} else { markInvalid( "attributeId", i18n_verify_attribute ); }
}

/*
* 	Delete Attribute Value Group Order
*/
function deleteAttributeValueGroupOrder( id, name )
{
	removeItem( id, name, i18n_confirm_delete, 'deleteAttributeValueGroupOrder.action', function(){ window.location.reload(); } );
}

/*
*	Update Attribute Value Group Order
*/
function updateSortAttributeValueGroupOrder()
{
	var groups = document.getElementsByName( 'attributeValueGroupOrder' );
	var url = "updateSortAttributeValueGroupOrder.action?";
	
	for ( var i = 0 ; i < groups.length ; i++ )
	{
		url += "groupIds=" + groups.item(i).value + "&";
	}
	
	url = url.substring( 0, url.length - 1 );
	
	jQuery.postJSON( url, {}, function( json ) {
		showSuccessMessage( json.message );
	});
}

function openSortAttributeValue( id )
{
	window.location = "openSortAttributeValue.action?id="+id;
}

/*
* 	Update Sorted Attribute Value
*/
function updateSortedAttributeValue()
{	
	moveAllById( 'availableList', 'selectedList' );
	selectAllById( 'selectedList' );
	document.forms[0].submit();
}