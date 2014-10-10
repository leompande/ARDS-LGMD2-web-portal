

var modelTypeAttributeSelector;


jQuery(function(){
	modelTypeAttributeSelector = jQuery("#modelTypeAttributeSelection").dialog({
		title: i18n_modelType_attribute,
		height: 350,
		width:350,
		autoOpen: false,
		zIndex:99999
	});




});


function openModelTypeAttributeSelector()
{
	modelTypeAttributeSelector.dialog("open");
}

// delete dataentry form
function deleteModelDataEntryForm( modelDataEntryFormId, modelTypeId )
{
	if( window.confirm( i18n_delete_modelType_data_entry_confirm ) )
	{
		window.location.href = 'delModelDataEntryForm.action?dataEntryFormId=' + modelDataEntryFormId + "&modelTypeId=" + modelTypeId;
	}
}


function filterModelTypeAttributes( filter, container, list )
{
	var filterLower = filter.toString().toLowerCase();
	
	var modelTypeAttributeList = jQuery( container + " " + list );
	
	modelTypeAttributeList.empty();
	
	jQuery( container + " " + list + "Store" ).children().each( function(i, item){
		item = jQuery( item );		
		var toMatch = item.text().toString().toLowerCase();		
        if( toMatch.indexOf(filterLower) != -1 ){
        	modelTypeAttributeList.append( "<option value='" + item.attr('value') + "'>" + item.text() + "</option>" );
		};
	});	
}




function insertModelTypeAttribute( source, modelTypeId )
{
	var oEditor = jQuery("#designTextarea").ckeditorGet();
	var modelTypeAttribute = JSON.parse( jQuery( source + ' #modelTypeAttributeIds').val() );

	if( modelTypeAttribute == null )
	{
		jQuery( source + " #message_").html( "<span class='bold'>" + i18n_specify_modelType_attribute + "</span>" );
		return;
	} else {
		jQuery( source + " #message_").html( "" );
	}

	var modelTypeAttributeId = modelTypeAttribute.id;	
	var modelTypeAttributeName = modelTypeAttribute.name;	
	var modelTypeAttributevalueType = modelTypeAttribute.valueType;
	
	var htmlCode = "";
	//var id = modelTypeId + "-" + modelTypeAttributeId + "-val" ;
	var id = "attr"+modelTypeAttributeId;
	
	if ( modelTypeAttributevalueType == "YES/NO" )
	{
		var titleValue = "-- " + modelTypeAttributeId + "." + modelTypeAttributeName + " ("+modelTypeAttributevalueType+") --";
		var displayName = modelTypeAttributeName;
		htmlCode = "<input title=\"" + titleValue + "\" name=\"entryselect\" id=\"" + id + "\" value=\"" + displayName + "\" title=\"" + displayName + "\">";
	} 
	else if ( modelTypeAttributevalueType == "DATE" )
	{
		var titleValue = "-- " + modelTypeAttributeId + "." + modelTypeAttributeName + " ("+modelTypeAttributevalueType+") --";
		var displayName = modelTypeAttributeName;
		htmlCode = "<input title=\"" + titleValue + " \"name=\"entryfield\" id=\"" + id + "\" value=\"" + displayName + "\" title=\"" + displayName + "\">";
	} 
	else if ( modelTypeAttributevalueType == "NUMBER" || modelTypeAttributevalueType == "TEXT"  ) 
	{
		var titleValue = "-- " + modelTypeAttributeId + "." + modelTypeAttributeName +" (" + modelTypeAttributevalueType + ") --";
		var displayName = modelTypeAttributeName;
		htmlCode += "<input title=\"" + titleValue + "\" value=\"" + displayName + "\" name=\"entryfield\" id=\"" + id + "\" />";
	}		
	else if ( modelTypeAttributevalueType == "COMBO" ) 
	{
		var titleValue = "-- " + modelTypeAttributeId + "." + modelTypeAttributeName +" (" + modelTypeAttributevalueType + ") --";
		var displayName = modelTypeAttributeName;
		htmlCode += "<input title=\"" + titleValue + "\" value=\"" + displayName + "\" name=\"entryfield\" id=\"" + id + "\" />";
	}
	
	if( checkExisted( id ) )
	{		
		jQuery( source + " #message_").html( "<span class='bold'>" + i18n_modelType_attribute_is_inserted + "</span>" );
		return;
	}else{
		jQuery( source + " #message_").html("");
	}

	oEditor.insertHtml( htmlCode );
}

function checkExisted( id )
{	
	var result = false;
	var html = jQuery("#designTextarea").ckeditorGet().getData();
	var input = jQuery( html ).find("select, :text");

	input.each( function(i, item){		
		if( id == item.id ) result = true;		
	});

	return result;
}


