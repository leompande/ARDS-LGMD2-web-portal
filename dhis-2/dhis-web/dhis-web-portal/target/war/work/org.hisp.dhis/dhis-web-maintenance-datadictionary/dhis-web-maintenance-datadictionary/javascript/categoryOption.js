
function addCategoryOption()
{
	var value = $( '#categoryOptionName' ).val();
	
	if ( value.length == 0 ) 
	{
		markInvalid( 'categoryOptionName', i18n_specify_category_option_name );
	}
	else if ( listContainsById( 'categoryOptionNames', value ) ) 
	{
		markInvalid( 'categoryOptionName', i18n_category_option_name_already_exists );
	}
	else 
	{
		jQuery.postJSON( 'validateDataElementCategoryOption.action', { name:value }, function( json ) 
		{
			if ( json.response == 'success' )
			{					
				addOptionById( 'categoryOptionNames', value, value );
				setFieldValue( 'categoryOptionName', '' );
			}
			else
			{
				markInvalid( 'categoryOptionName', i18n_category_option_name_already_exists );
			}
		} );
	}
}

function getSelectedCategoryOption()
{
	var name = $( '#categoryOptions :selected' ).text();
	$( '#categoryOptionName' ).val( name );
}

function updateCategoryOptionName()
{
	var id = $( '#categoryOptions :selected' ).val();
	var name = $( '#categoryOptionName' ).val();
	
	var url = 'updateDataElementCategoryOption.action?id=' + id + '&name=' + name;
	
	$.postUTF8( url, {}, function() 
	{
		$( '#categoryOptions :selected' ).text( name );
	} );
}

function saveCategoryOption( id, name )
{
	var url = 'addDataElementCategoryOption.action';
	
	$.postJSON( url, { categoryId:id, name:name }, function( json )
	{
		addOptionById( 'categoryOptions', json.dataElementCategoryOption.id, name );
	} );
}
