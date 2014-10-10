
function AttributeLib()
{
	var attributeMap = new Array();
	var attributeValueMap = new Array();
	
	this.loadAttributes = function( elementId, id )
	{
		var target = jQuery( "#" + elementId );
		target.children().remove();

		if ( attributeMap.length == 0 )
		{
			jQuery.ajax(
			{
				type: 'GET',
				url: '../dhis-web-commons-ajax-json/getAttributes.action',
				dataType: 'json',
				async: false,
				success: function( json )
				{
					attributeMap.push( new Attribute( -1, '[ ' + i18n_label + ' ]' ) );
					target.append( '<option value="-1">[ ' + i18n_label + ' ]</option>' );
					
					jQuery.each( json.attributes, function( i, item )
					{
						if ( id && item.id == id ) {
							target.append( '<option value="' + item.id + '" selected="true">' + item.name + '</option>' );
						}
						else {
							target.append( '<option value="' + item.id + '">' + item.name + '</option>' );
						}
						
						attributeMap.push( new Attribute( item.id, item.name ) );
					} );
				}
			} );
		}
		else
		{
			jQuery.each( attributeMap, function( i, item )
			{
				if ( id && item.id == id ) {
					target.append( '<option value="' + item.id + '" selected="true">' + item.name + '</option>' );
				}
				else {
					target.append( '<option value="' + item.id + '">' + item.name + '</option>' );
				}
			} );
		}
	};

	this.loadAttributeValuesByAttribute = function( id, curItems, sourceList, destList, isFirstLoad )
	{
		var source = jQuery( "#" + sourceList );
		var dest = jQuery( "#" + destList );
		
		if ( source )
		{
			source.empty();
		}

		if ( dest && !isFirstLoad )
		{
			dest.empty();
		}

		var valueList = attributeValueMap[ id ];

		if ( valueList == null )
		{
			valueList = new Array();

			jQuery.ajax(
			{
				type: 'GET',
				url: 'getAttributeValuesByAttribute.action',
				data: 'attributeId=' + id,
				dataType: 'json',
				async: false,
				success: function( json )
				{
					jQuery.each( json.values, function( i, item )
					{
						valueList.push( new AttributeValue( item.value ) );
						source.append( '<option value="' + item.value + '">' + item.value + '</option>' );
					} );
					
					attributeValueMap[ id ] = valueList;
				}
			} );
		}
		else
		{
			jQuery.each( valueList, function( i, item )
			{
				source.append( '<option value="' + item.value + '">' + item.value + '</option>' );
			} );
		}

		if ( curItems )
		{
			jQuery.each( curItems, function( i, item )
			{
				dest.append( '<option value="' + item.value + '">' + item.value + '</option>' );
			} );
		}
		
		if ( source && dest )
		{
			this.removeDuplicatedItem( sourceList, destList );
		}
	};
	
	this.removeDuplicatedItem = function( availableList, selectedList )
	{
		var $list1 = jQuery('#' + availableList);
		var $list2 = jQuery('#' + selectedList);
		
		if ( $list1 && $list2 )
		{
			jQuery.each( $list2.children(), function( i, item )
			{
				$list1.find( "option[value='" + item.value + "']" ).remove();
			} );
		}		
	};

	this.resetParams = function()
	{
		this.attributeMap = new Array();
		this.attributeValueMap = new Array();
	};
}


function Attribute( _id, _name )
{
	this.id = _id;
	this.name = _name;
}

function AttributeValue( _value )
{
	this.value = _value;
}

var attributeLib = new AttributeLib();