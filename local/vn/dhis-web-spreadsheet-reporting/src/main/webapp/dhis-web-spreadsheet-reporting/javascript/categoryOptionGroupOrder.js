var selectedCategoryOptionMap = new Array();

function showCategoryOptionGroupOrderDetails( id )
{
	jQuery.post( 'getCategoryOptionGroupOrder.action', { id: id }, function( json ) {
		
		setInnerHTML( 'nameField', json.categoryOptionGroupOrder.name );
		setInnerHTML( 'memberCountField', json.categoryOptionGroupOrder.memberCount );

		showDetails();
	});
}

function resetForm()
{
	setFieldValue( "name", "" );
	setFieldValue( "categoryOptionGroupOrderId", "" );

	var availableList = jQuery( '#availableCategoryOptions' );
	availableList.empty();
	var selectedList = jQuery( '#categoryOptionIds' );
	selectedList.empty();
}

/*
* 	Open Add Category Option Group Order 
*/
function openAddCategoryOptionGroupOrder()
{
	resetForm();
	validator.resetForm();

	categoryLib.loadCategories( "categoryId" );

	dialog.dialog("open");
	
	jQuery( "#categoryOptionGroupsForm" ).attr( "action", "addCategoryOptionGroupOrderFor" + clazzName + ".action?clazzName=" + clazzName );
}

/*
* 	Open Update Category Option Order
*/

function openUpdateCategoryOptionGroupOrder( id )
{
	validator.resetForm();
	setFieldValue("categoryOptionGroupOrderId", id );
	
	jQuery.post( 'getCategoryOptionGroupOrder.action', { id: id }, function( json )
	{
		var categoryOptions = json.categoryOptionGroupOrder.categoryOptions;
		var categoryId = ( categoryOptions.length > 0 ? categoryOptions[ 0 ].categoryId : "" );
		var list = jQuery( "#categoryOptionIds" );
		list.empty();
		selectedCategoryOptionMap = [];
		var items = [];
		
		setFieldValue( "name", json.categoryOptionGroupOrder.name );
		categoryLib.loadCategories( "categoryId", categoryId );
		categoryLib.loadCategoryOptionsByCategory( categoryId, items, "availableCategoryOptions", "categoryOptionIds", true );
		
		for ( var i = 0 ; i < categoryOptions.length ; i++ )
		{
			items.push( new CategoryOption( categoryOptions[ i ].id, categoryOptions[ i ].name ) );
			list.append( '<option value="' + categoryOptions[ i ].id + '">' + categoryOptions[ i ].name + '</option>' );
		}

		selectedCategoryOptionMap[ id + "-" + categoryId ] = items;

		categoryLib.removeDuplicatedItem( "availableCategoryOptions", "categoryOptionIds" );

		jQuery( "#categoryOptionGroupsForm" ).attr( "action", "updateCategoryOptionGroupOrderFor" + clazzName + ".action" );
		dialog.dialog( "open" );
	} );
}

function validateCategoryOptionGroupOrder( _form )
{
	var categoryId = getFieldValue( "categoryId" );

	if ( categoryId && categoryId != -1 )
	{
		jQuery.postUTF8( "validateCategoryOptionGroupOrder.action", {
			name: getFieldValue( 'name' ),
			id: getFieldValue( 'categoryOptionGroupOrderId' ),
			reportId: reportId,
			clazzName: clazzName
		}, function( json )
		{
			if ( json.response == "success" )
			{
				if ( hasElements( 'categoryOptionIds' ) )
				{
					selectAllById( 'categoryOptionIds' );
					_form.submit();
				}
				else { markInvalid( "categoryOptionIds", i18n_selected_list_empty ); }
			}
			else { markInvalid( "name", json.message ); }
		} );
	} else { markInvalid( "categoryId", i18n_verify_category ); }
}

/*
* 	Delete Category Option Group Order
*/
function deleteCategoryOptionGroupOrder( id, name )
{
	removeItem( id, name, i18n_confirm_delete, 'deleteCategoryOptionGroupOrder.action', function(){ window.location.reload(); } );
}

/*
*	Update Category Option Group Order
*/
function updateSortCategoryOptionGroupOrder()
{
	var categoryOptionGroups = document.getElementsByName( 'categoryOptionGroupOrder' );
	var url = "updateSortCategoryOptionGroupOrder.action?reportId=" + reportId + "&clazzName=" + clazzName;
	
	for ( var i = 0 ; i < categoryOptionGroups.length ; i++ )
	{
		url += "&categoryOptionGroupOrderId=" + categoryOptionGroups.item(i).value;
	}
	
	jQuery.get( url, {}, function( json ) {
		showSuccessMessage( json.message );
	});
}

function openSortCategoryOptionForGroupOrder( id )
{
	window.location = "openSortCategoryOption.action?id="+id+"&reportId="+reportId+"&clazzName="+clazzName;
}

/*
* 	Update Sorted Category Option
*/
function updateSortedCategoryOption()
{	
	moveAllById( 'availableList', 'selectedList' );
	selectAllById( 'selectedList' );
	document.forms[0].submit();
}

/*
 * Open Category Option Associations
 */
function openCategoryOptionAssociations( id )
{
	window.location = "openCategoryOptionAssociations.action?id="+id+"&reportId="+reportId;
}