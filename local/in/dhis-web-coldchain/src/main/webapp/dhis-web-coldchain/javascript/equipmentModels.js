
// ----------------------------------------------------------------
// On ModelTypeChange  - Loading ModelType Attributes
// ----------------------------------------------------------------
//function getModelTypeChange( modelTypeId )
window.onload=function(){
	jQuery('#addEditModelFormDiv').dialog({autoOpen: false});	
	jQuery('#modelDetailsDiv').dialog({autoOpen: false});	
}
function getModelTypeChange()
{
	loadAllModels();
	var modelTypeId = $( '#modelType' ).val();
	
	if( modelTypeId == "0" )
	{
		return;
	}
		
	$.post("getModelTypeAttribute.action",
			{
				id:modelTypeId
			},
			function(data)
			{
				populateModelTypeAttributes( data );
			},'xml');	
}

function populateModelTypeAttributes( data )
{
	var searchingModelAttributeId = document.getElementById("searchingModelAttributeId");
	clearList( searchingModelAttributeId );
	
	var name_text = "Name";
	var name_value = "modelname";
	
	
	var modelTypeAttribs = data.getElementsByTagName("model-type-attribute");
	
	//$("#searchingModelAttributeId").append("<option value='"+ name_value + "' title='" + name_text + "' text='" + name_text + "'>" + name_text + "</option>");
    for ( var i = 0; i < modelTypeAttribs.length; i++ )
    {
        var id = modelTypeAttribs[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var name = modelTypeAttribs[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
        var option = document.createElement("option");
        option.value = id;
        option.text = name;
        option.title = name;
        searchingModelAttributeId.add(option, null);
    }
}


// 

//----------------------------------------------------------------
// Loading VaccinesType Attributes
//----------------------------------------------------------------

function getVaccinesTypeAttribute()
{
	loadAllVaccines();
	
	var modelTypeId = document.getElementById('modelTypeId').value;
	
	//alert( modelTypeId );
	
	if( modelTypeId == "0" )
	{
		return;
	}
		
	$.post("getModelTypeAttribute.action",
			{
				id:modelTypeId
			},
			function(data)
			{
				populateVaccinesTypeAttributes( data );
			},'xml');	
}

function populateVaccinesTypeAttributes( data )
{
	var searchingModelAttributeId = document.getElementById("searchingModelAttributeId");
	clearList( searchingModelAttributeId );
	
	var name_text = "Name";
	var name_value = "modelname";
	
	
	var modelTypeAttribs = data.getElementsByTagName("model-type-attribute");
	
	$("#searchingModelAttributeId").append("<option value='"+ name_value + "' title='" + name_text + "' text='" + name_text + "'>" + name_text + "</option>");
	 for ( var i = 0; i < modelTypeAttribs.length; i++ )
	 {
	     var id = modelTypeAttribs[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
	     var name = modelTypeAttribs[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
			
	     var option = document.createElement("option");
	     option.value = id;
	     option.text = name;
	     option.title = name;
	     searchingModelAttributeId.add(option, null);
	 }
}


//----------------------------------------------------------------
//On LoadAllModels
//----------------------------------------------------------------

function loadAllModels()
{
	var modelType = document.getElementById('modelType');
	var modelTypeId = modelType.options[ modelType.selectedIndex ].value;
	
	//document.getElementById('searchText').value == "";
	
	document.getElementById("searchModelText").value = "";

	
	if( modelTypeId == 0 )
	{	
		return;
	}
	
	showById('selectDiv');

	jQuery('#loaderDiv').show();
	contentDiv = 'listModelDiv';
	isAjax = true;
	
	jQuery('#listModelDiv').load('getModelList.action',{
		listAll:true,
		modelTypeId:modelTypeId	
	},
	function(){
		statusSearching = 0;
		showById('listModelDiv');
		jQuery('#loaderDiv').hide();
	});
	hideLoader();
}


//----------------------------------------------------------------
//On LoadAllVaccines
//----------------------------------------------------------------

function loadAllVaccines()
{
	var modelTypeId = document.getElementById('modelTypeId').value;
	
	document.getElementById("searchModelText").value = "";
	
	//alert( modelTypeId );
	
	if( modelTypeId == 0 )
	{	
		return;
	}
	
	showById('selectDiv');

	jQuery('#loaderDiv').show();
	contentDiv = 'listModelDiv';
	isAjax = true;
	
	jQuery('#listModelDiv').load('getModelList.action',{
		listAll:true,
		modelTypeId:modelTypeId	
	},
	function(){
		statusSearching = 0;
		showById('listModelDiv');
		jQuery('#loaderDiv').hide();
	});
	hideLoader();
}



//----------------------------------------------------------------
//Load Models On Filter by modelType Attribute and Cataog Name
//----------------------------------------------------------------
function loadModelsByFilter( )
{
	var modelType = document.getElementById('modelType');
	var modelTypeId = modelType.options[ modelType.selectedIndex ].value;
	
	var searchText = document.getElementById('searchModelText').value;
	
	if( modelTypeId == 0 )
	{	
		return;
	}
	
	var modelTypeAttribute = document.getElementById('searchingModelAttributeId');
	var modelTypeAttributeId = modelTypeAttribute.options[ modelTypeAttribute.selectedIndex ].value;
	
	
	showById('selectDiv');
	
	jQuery('#loaderDiv').show();
	contentDiv = 'listModelDiv';
	isAjax = true;
	
	jQuery('#listModelDiv').load('getModelList.action',{		
		modelTypeId:modelTypeId,
		modelTypeAttributeId:modelTypeAttributeId,
		searchText:searchText
	},
	function(){
		statusSearching = 0;
		showById('listModelDiv');
		jQuery('#loaderDiv').hide();
	});
	hideLoader();
}







//----------------------------------------------------------------
//Load Vaccines On Filter by Vaccines Attribute and Vaccines Name
//----------------------------------------------------------------

function loadVaccinesByFilter()
{
	var modelTypeId = document.getElementById('modelTypeId').value;
	
	var searchText = document.getElementById('searchModelText').value;
	
	if( modelTypeId == 0 )
	{	
		return;
	}
	
	var modelTypeAttribute = document.getElementById('searchingModelAttributeId');
	var modelTypeAttributeId = modelTypeAttribute.options[ modelTypeAttribute.selectedIndex ].value;
	
	
	showById('selectDiv');
	
	jQuery('#loaderDiv').show();
	contentDiv = 'listModelDiv';
	isAjax = true;
	
	jQuery('#listModelDiv').load('getModelList.action',{		
		modelTypeId:modelTypeId,
		modelTypeAttributeId:modelTypeAttributeId,
		searchText:searchText
	},
	function(){
		statusSearching = 0;
		showById('listModelDiv');
		jQuery('#loaderDiv').hide();
	});
	hideLoader();
}





/**
* Hides the document element with the given identifier.
* 
* @param id the element identifier.
*/


function hideVaccineFilter()
{
	hideById('filterModelDiv');
	showById('searchingModelAttributeTD');
	showById('searchingModelTextTD');
	showById('searchModelDiv');
	showById('clearModelDiv');
}

function hideVaccinesClear()
{
	hideById('clearModelDiv');
	hideById('searchModelDiv');
	hideById('searchingModelTextTD');
	hideById('searchingModelAttributeTD');
	
	getVaccinesTypeAttribute();
	
	showById('filterModelDiv');
	
}



function hideModelFilter()
{
	hideById('filterModelDiv');
	showById('searchingModelAttributeTD');
	showById('searchingModelTextTD');
	showById('searchModelDiv');
	showById('clearModelDiv');
}

function hideModelClear()
{
	hideById('clearModelDiv');
	hideById('searchModelDiv');
	hideById('searchingModelTextTD');
	hideById('searchingModelAttributeTD');
	
	getModelTypeChange();
	
	showById('filterModelDiv');
	
}


function isModelEnter( e )
{
	if ( e.keyCode == 13) 
    {   
		loadModelsByFilter();
    }   
}

function isVaccinesEnter( e )
{
	if ( e.keyCode == 13) 
    {   
		loadVaccinesByFilter();
    }   
}


function searchingModelAttributeOnChange( modelTypeAttributeId )
{
	//alert( equipmentTypeAttributeId );
	var searchModelText = document.getElementById('searchModelText').value;
	document.getElementById("searchModelText").value = "";
}

//----------------------------------------------------------------
//Add New Model
//----------------------------------------------------------------

function showAddModelForm()
{
	var modelType = document.getElementById('modelType');
	var modelTypeId = modelType.options[ modelType.selectedIndex ].value;
	
	var modelTypeName = modelType.options[ modelType.selectedIndex ].text;
	
	//alert( modelTypeId );
	
	if( modelTypeId == 0 )
	{	
		return;
	}
	
	jQuery('#addEditModelFormDiv').dialog('destroy').remove();
	jQuery('<div id="addEditModelFormDiv">' ).load( 'showAddModelForm.action?modelTypeId='+ modelTypeId ).dialog({
		title: 'Add Model Item in ' + modelTypeName ,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});	
}


function showAddVaccineForm()
{
	var modelTypeId = document.getElementById('modelTypeId').value;
	
	var modelTypeName = document.getElementById('modelTypeName').value;
	
	//alert( modelTypeId );
	
	if( modelTypeId == 0 )
	{	
		return;
	}
	
	jQuery('#addEditModelFormDiv').dialog('destroy').remove();
	jQuery('<div id="addEditModelFormDiv">' ).load( 'showAddModelForm.action?modelTypeId='+ modelTypeId ).dialog({
		title: 'Add Model Item in ' + modelTypeName ,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});	
}


function addModel()
{	
	var modelTypeName = document.getElementById('modelTypeName').value;
	
	$.ajax({
    type: "POST",
    url: 'addModel.action',
    data: getParamsForDiv('addEditModelFormDiv'),
    success: function(json) {
		var type = json.response;
		jQuery('#addEditModelFormDiv').dialog('destroy').remove();
		
		//getModelTypeChange();
		
		if( modelTypeName == "Vaccines" )
		{
			getVaccinesTypeAttribute();
		}
		else
		{
			getModelTypeChange();
		}
    }
   });
  
}


function closeAddModelWindow()
{
	jQuery('#addEditModelFormDiv').dialog('destroy').remove();
}

function getParamsForDiv( equipmentDiv )
{
	var params = '';
	
	jQuery("#" + equipmentDiv + " :input").each(function()
	{
		var elementId = $(this).attr('id');
			
		if( $(this).attr('type') == 'checkbox' )
		{
			var checked = jQuery(this).attr('checked') ? true : false;
			params += elementId + "=" + checked + "&";
		}
		else if( $(this).attr('type') != 'button' )
		{
			var value = "";
			if( jQuery(this).val() != '' )
			{
				value = htmlEncode(jQuery(this).val());
			}
			
			params += elementId + "="+ value + "&";
		}
			
	});
	
	return params;
}

//----------------------------------------------------------------
//Show Model Details
//----------------------------------------------------------------


function showModelDetails( modelId , modelName, modelTypeName )
{
	var modelName = modelName;
	
	jQuery('#modelDetailsDiv').dialog('destroy').remove();
	jQuery('<div id="modelDetailsDiv">' ).load( 'getModelDetails.action?id='+ modelId ).dialog({
		title: modelTypeName + " , " + modelName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
}

function closeModelDetailsWindow()
{
	jQuery('#modelDetailsDiv').dialog('destroy').remove();
}

//----------------------------------------------------------------
//Update Model
//----------------------------------------------------------------

function showUpdateModelForm( modelId, modelName, modelTypeName )
{
	
	var modelName = modelName;
	
	jQuery('#modelDetailsDiv').dialog('close');
	
	jQuery('#addEditModelFormDiv').dialog('destroy').remove();
	jQuery('<div id="addEditModelFormDiv">' ).load( 'showUpdateModelForm.action?id='+ modelId ).dialog({
		title: 'Edit ' + modelTypeName + " , " + modelName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	
}

function updateModel()
{
	var modelId = document.getElementById('modelID').value;
	var modelName = document.getElementById('modelName').value;
	var modelTypeName = document.getElementById('modelTypeName').value;
	
	$.ajax({
    type: "POST",
    url: 'updateModel.action',
    data: getParamsForDiv('addEditModelFormDiv'),
    success: function( json ) {
	
		jQuery('#addEditModelFormDiv').dialog('destroy').remove();
		
		//getModelTypeChange();
		
		if( modelTypeName == "Vaccines" )
		{	
			getVaccinesTypeAttribute();
		}
		else
		{
			getModelTypeChange();
		}
		
		
		showModelDetails( modelId ,modelName );
		
		}
	
	});
}

function closeModelUpdateWindow()
{
	var modelId = document.getElementById('modelID').value;
	var modelName = document.getElementById('modelName').value;
	
	jQuery('#addEditModelFormDiv').dialog('destroy').remove();
	showModelDetails( modelId ,modelName );
}


//-----------------------------------------------------------------------------
//Remove model
//-----------------------------------------------------------------------------
function removeModel( modelId , modelName , modelTypeName )
{
	var itemName = modelName;
	
	var result = window.confirm( i18n_confirm_delete_model + "\n\n" + itemName );
	
	if ( result )
	{
		$.ajax({
		    type: "POST",
		    url: 'removeModel.action?id='+ modelId,
		    	//data: getParamsForDiv('addEditModelFormDiv'),
		    	success: function( json ) {
				jQuery('#addEditModelFormDiv').dialog('destroy').remove();
				
					// for load vaccines
					if( modelTypeName == "Vaccines" )
					{
						getVaccinesTypeAttribute();
					}
					else
					{
						getModelTypeChange();
					}
				
				}
			});
	}
}







