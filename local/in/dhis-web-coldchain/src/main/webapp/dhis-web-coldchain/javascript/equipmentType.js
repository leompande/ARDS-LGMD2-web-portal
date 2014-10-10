// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showEquipmentTypeDetails( equipmentTypeId )
{
	jQuery.getJSON( 'getEquipmentType.action', { id: equipmentTypeId },
		function ( json ) {
			setInnerHTML( 'nameField', json.equipmentType.name );	
			setInnerHTML( 'descriptionField', json.equipmentType.description );
			
			var tracking = ( json.equipmentType.tracking == 'true') ? i18n_yes : i18n_no;
			setInnerHTML( 'trackingField', tracking );
			
			setInnerHTML( 'modelTypeField', json.equipmentType.modelType );    
	   
			showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove InvenotryType
// -----------------------------------------------------------------------------
function removeEquipmentType( invenotryTypeId, name )
{
	removeItem( invenotryTypeId, name, i18n_confirm_delete, 'removeEquipmentType.action' );	
}

//-----------------------------------------------------------------------------
//Move Table Row Up and Down
//-----------------------------------------------------------------------------

/**
* Moves the selected option in a select list up one position.
* 
* @param listId the id of the list.
*/
function moveUpSelectedOption( listId ){
  var list = document.getElementById( listId );
  for ( var i = 0; i < list.length; i++ ) {
      if ( list.options[ i ].selected ) {
          if ( i > 0 ) {	// Cannot move up the option at the top
              var precedingOption = new Option( list.options[ i - 1 ].text, list.options[ i - 1 ].value );
              var currentOption = new Option( list.options[ i ].text, list.options[ i ].value );

              list.options[ i - 1 ] = currentOption; // Swapping place in the
                                                      // list
              list.options[ i - 1 ].selected = true;
              list.options[ i ] = precedingOption;
          }
      }
  }
}
/**
* Moves the selected option in a list down one position.
* 
* @param listId the id of the list.
*/
function moveDownSelectedOption( listId ) {
  var list = document.getElementById( listId );

  for ( var i = list.options.length - 1; i >= 0; i-- ) {
      if ( list.options[ i ].selected ) {
          if ( i < list.options.length - 1 ) { 	// Cannot move down the
                                                  // option at the bottom
              var subsequentOption = new Option( list.options[ i + 1 ].text, list.options[ i + 1 ].value );
              var currentOption = new Option( list.options[ i ].text, list.options[ i ].value );

              list.options[ i + 1 ] = currentOption; // Swapping place in the
                                                      // list
              list.options[ i + 1 ].selected = true;
              list.options[ i ] = subsequentOption;
          }
      }
  }
}


//-----------------------------------------------------------------------------
//select unselect EquipmentTypeAttribute
//-----------------------------------------------------------------------------

function selectEquipmentTypeAttributes()
{
	var selectedEquipmentTypeAttributeList = jQuery("#selectedEquipmentTypeAttributeList");
	jQuery("#availableEquipmentTypeAttributeList").children().each(function(i, item){
		if( item.selected ){
			html = "<tr class='selected' id='" + item.value + "' ondblclick='unSelectEquipmentTypeAttribute( this )'><td onclick='select(this)'>" + item.text + "</td>";
			html += "<td align='center'><input type='checkbox' name='forDisplay' value='" + item.value + "'></td>";
			html += "</tr>";
			selectedEquipmentTypeAttributeList.append( html );
			jQuery( item ).remove();
		}
	});
}


function unSelectEquipmentTypeAttributes()
{
	var availableEquipmentTypeAttributeList = jQuery("#availableEquipmentTypeAttributeList");
	jQuery("#selectedEquipmentTypeAttributeList").find("tr").each( function( i, item ){
		item = jQuery(item);
		if( item.hasClass("selected") )
		{		
			availableEquipmentTypeAttributeList.append( "<option value='" + item.attr( "id" ) + "' selected='true'>" + item.find("td:first").text() + "</option>" );
			item.remove();
		}
	});
}


function selectAllEquipmentTypeAttributes()
{
	var selectedEquipmentTypeAttributeList = jQuery("#selectedEquipmentTypeAttributeList");
	jQuery("#availableEquipmentTypeAttributeList").children().each(function(i, item){
		html = "<tr class='selected' id='" + item.value + "' ondblclick='unSelectEquipmentTypeAttribute( this )'><td onclick='select(this)'>" + item.text + "</td>";
		html += "<td align='center'><input type='checkbox' name='forDisplay' value='" + item.value + "'></td>";
		html += "</tr>";
		selectedEquipmentTypeAttributeList.append( html );
		jQuery( item ).remove();
	});
}


function unSelectAllEquipmentTypeAttributes()
{
	var availableEquipmentTypeAttributeList = jQuery("#availableEquipmentTypeAttributeList");
	jQuery("#selectedEquipmentTypeAttributeList").find("tr").each( function( i, item ){
		item = jQuery(item);
		availableEquipmentTypeAttributeList.append( "<option value='" + item.attr( "id" ) + "' selected='true'>" + item.find("td:first").text() + "</option>" );
		item.remove();
	});
}



function unSelectEquipmentTypeAttribute( element )
{
	element = jQuery(element);	
	jQuery("#availableEquipmentTypeAttributeList").append( "<option value='" + element.attr( "id" ) + "' selected='true'>" + element.find("td:first").text() + "</option>" );
	element.remove();
}


function select( element )
{
	element = jQuery( element ).parent();
	if( element.hasClass( 'selected') ) element.removeClass( 'selected' );
	else element.addClass( 'selected' );
}
//-----------------------------------------------------------------------------
//Move Table Row Up and Down
//-----------------------------------------------------------------------------


function moveUpEquipmentTypeAttribute()
{
	var selectedList = jQuery("#selectedEquipmentTypeAttributeList");

	jQuery("#selectedEquipmentTypeAttributeList").find("tr").each( function( i, item ){
		item = jQuery(item);
		if( item.hasClass("selected") )
		{
			var prev = item.prev('#selectedEquipmentTypeAttributeList tr');
			if (prev.length == 1) 
			{ 
				prev.before(item);
			}
		}
	});
}

function moveDownEquipmentTypeAttribute()
{
	var selectedList = jQuery("#selectedEquipmentTypeAttributeList");
	var items = new Array();
	jQuery("#selectedEquipmentTypeAttributeList").find("tr").each( function( i, item ){
		items.push(jQuery(item));
	});
	
	for( var i=items.length-1;i>=0;i--)
	{	
		var item = items[i];
		if( item.hasClass("selected") )
		{
			var next = item.next('#selectedEquipmentTypeAttributeList tr');
			if (next.length == 1) 
			{ 
				next.after(item);
			}
		}
	}
}






