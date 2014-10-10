
/*
jQuery(document).ready(	function(){
		validation( 'modelForm', function(form){
			form.submit();
		}, function(){
			isSubmit = true;
			
			});
		
		checkValueIsExist( "name", "validateModel.action");
	});

*/
//-----------------------------------------------------------------------------
//View model type change
//-----------------------------------------------------------------------------

function modelTypeChange()
{
    var modelTypeList = document.getElementById("modelType");
    var modelTypeId = modelTypeList.options[ modelTypeList.selectedIndex ].value;
	
    
    setInnerHTML('addModelFormDiv', '');
	showById('addModelFormDiv');
    
    
    //setInnerHTML('modelDataEntryFormDiv', '');
    setInnerHTML('addModelFormDiv', '');
    //showById('dataEntryFormDiv');
    showById('addModelFormDiv');
    
    //hideById('addModelFormDiv');
    //jQuery('#loaderDiv').show();
    //contentDiv = 'addModelFormDiv';
    
    
    //jQuery(".stage-object-selected").removeClass('stage-object-selected');
	//var tempModelTypeId = jQuery( '#' + modelTypeId );
	//tempModelTypeId.addClass('stage-object-selected');
    
    
    
    showLoader();
	jQuery('#addModelFormDiv').load('showAddModelForm.action',
		{
			modelTypeId:modelTypeId
		}, function()
		{
			
			hideLoader();
			//showById('addModelFormDiv');
			//jQuery('#loaderDiv').hide();
		});
	hideLoader();
}

//-----------------------------------------------------------------------------
//View model by model type change
//-----------------------------------------------------------------------------
function getModelByModelType( modelTypeId )
{
	window.location.href = "model.action?id=" + modelTypeId;
}


//-----------------------------------------------------------------------------
//View details
//-----------------------------------------------------------------------------

function showModelDetails( modelId )
{
	/*
	jQuery.getJSON( 'getModelDetails.action', { id: modelId }, function ( json ) {
		setInnerHTML( 'nameField', json.model.name );	
		setInnerHTML( 'descriptionField', json.model.description );
		setInnerHTML( 'modelTypeField', json.model.modelType );   
	   
		showDetails();
	});
	*/
	
	
	 $('#detailsModelInfo').load("getModelDetails.action", 
				{
					id:modelId
				}
				, function( ){
				}).dialog({
					title: i18n_model_details,
					maximize: true, 
					closable: true,
					modal:true,
					overlay:{background:'#000000', opacity:0.1},
					width: 650,
					height: 500
				});;
}

//-----------------------------------------------------------------------------
//Remove model
//-----------------------------------------------------------------------------
function removeModel( modelId, name )
{
	removeItem( modelId, name, i18n_confirm_delete, 'removeModel.action' );	
}


//-----------------------------------------------------------------
//
//-----------------------------------------------------------------


TOGGLE = {
	    init : function() {
	        jQuery(".togglePanel").each(function(){
	            jQuery(this).next("table:first").addClass("sectionClose");
	            jQuery(this).addClass("close");
	            jQuery(this).click(function(){
	                var table = jQuery(this).next("table:first");
	                if( table.hasClass("sectionClose")){
	                    table.removeClass("sectionClose").addClass("sectionOpen");
	                    jQuery(this).removeClass("close").addClass("open");
	                    window.scroll(0,jQuery(this).position().top);
	                }else if( table.hasClass("sectionOpen")){
	                    table.removeClass("sectionOpen").addClass("sectionClose");
	                    jQuery(this).removeClass("open").addClass("close");
	                }
	            });
	        });
	    }
	};



function entryFormContainerOnReady()
{
	alert( "options");
	var currentFocus = undefined;
	
    if( jQuery("#entryFormContainer") ) {
		
        jQuery("input[name='entryfield'],select[name='entryselect']").each(function(){
            jQuery(this).focus(function(){
                currentFocus = this;
            });
            
            jQuery(this).addClass("inputText");
        });
		
        TOGGLE.init();
				
		jQuery("#entryForm :input").each(function()
		{ 
			if( jQuery(this).attr( 'options' )!= null )
			{
				
				autocompletedField(jQuery(this).attr('id'));
			}
		});
    }
}


function autocompletedField( idField )
{
	var input = jQuery( "#" +  idField )
	var modelTypeAttributeId = input.attr( 'modelTypeAttributeId' );
	var options = new Array();
	options = input.attr('options').replace('[', '').replace(']', '').split(', ');
	options.push(" ");

	input.autocomplete({
			delay: 0,
			minLength: 0,
			source: options,
			select: function( event, ui ) {
				input.val(ui.item.value);
				//saveVal( modelTypeAttributeId );
				input.autocomplete( "close" );
			},
			change: function( event, ui ) {
				if ( !ui.item ) {
					var matcher = new RegExp( "^" + $.ui.autocomplete.escapeRegex( $(this).val() ) + "$", "i" ),
						valid = false;
					for (var i = 0; i < options.length; i++)
					{
						if (options[i].match( matcher ) ) {
							this.selected = valid = true;
							break;
						}
					}
					if ( !valid ) {
						// remove invalid value, as it didn't match anything
						$( this ).val( "" );
						input.data( "autocomplete" ).term = "";
						return false;
					}
				}
				//saveVal( modelTypeAttributeId );
			}
		})
		.addClass( "ui-widget" );

	this.button = $( "<button type='button'>&nbsp;</button>" )
		.attr( "tabIndex", -1 )
		.attr( "title", i18n_show_all_items )
		.insertAfter( input )
		.button({
			icons: {
				primary: "ui-icon-triangle-1-s"
			},
			text: false
		})
		.addClass( "optionset-small-button" )
		.click(function() {
			// close if already visible
			if ( input.autocomplete( "widget" ).is( ":visible" ) ) {
				input.autocomplete( "close" );
				return;
			}

			// work around a bug (likely same cause as #5265)
			$( this ).blur();

			// pass empty string as value to search for, displaying all results
			input.autocomplete( "search", "" );
			input.focus();
		});
}


//----------------------------------------------------------------
//	Update Model
//----------------------------------------------------------------
/*
function showUpdateModelForm( modelId )
{
	setInnerHTML('addModelFormDiv', '');
				
	jQuery('#loaderDiv').show();
	jQuery('#addModelFormDiv').load('showUpdateModelForm.action',
		{
			modelId:modelId
		}, function()
		{
			showById('addModelFormDiv');
		});
	hideLoader();
}


 $(document).ready(function() {
                $('#j_username').focus();

                $('#loginForm').bind('submit', function() {
					$('#submit').attr('disabled', 'disabled');
					$('#reset').attr('disabled', 'disabled');

	                sessionStorage.removeItem( 'orgUnitSelected' );
                });
            });
            
*/            




// for search ----




// ----------------------------------------------------------------
// On ModelTypeChange  - Loading ModelType Attributes
// ----------------------------------------------------------------
//function getModelTypeChange( modelTypeId )
function getModelTypeChange()
{
	loadAllModels();
	var modelTypeId = $( '#modelType' ).val();
	
	if( modelTypeId == "0" )
		return;
	
	showById('selectDiv');
    disable('listAllModelBtn');
    
    hideById('searchModelDiv');
    hideById('listModelDiv');
    hideById('addEditModelFormDiv');
	hideById('resultSearchDiv');
	hideById('editEquipmentStatusDiv');
	
	jQuery('#loaderDiv').show();
	
	$.post("getModelTypeAttribute.action",
			{
				id:modelTypeId
			},
			function(data)
			{
				showById('searchModelDiv');
				enable('listAllModelBtn');
				jQuery('#loaderDiv').hide();
				populateModelTypeAttributes( data );
			},'xml');	
}

function populateModelTypeAttributes( data )
{
	var searchingAttributeId = document.getElementById("searchingAttributeId");
	clearList( searchingAttributeId );
	
	var modelTypeAttribs = data.getElementsByTagName("model-type-attribute");
    for ( var i = 0; i < modelTypeAttribs.length; i++ )
    {
        var id = modelTypeAttribs[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var name = modelTypeAttribs[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
        var option = document.createElement("option");
        option.value = id;
        option.text = name;
        option.title = name;
        searchingAttributeId.add(option, null);
    }    	
}


function getModelTypeAttributeOptions()
{
	loadAllModels();
	var modelTypeAttributeId = $( '#searchingAttributeId' ).val();
	
	if( modelTypeAttributeId == "0" )
		return;
	
	showById('selectDiv');
    disable('listAllModelBtn');
    
    hideById('searchModelDiv');
    hideById('listModelDiv');
    hideById('addEditModelFormDiv');
	hideById('resultSearchDiv');
	hideById('editEquipmentStatusDiv');
	
	jQuery('#loaderDiv').show();
	
	$.post("getModelTypeAttributeOption.action",
			{
				id:modelTypeAttributeId
			},
			function(data)
			{
				showById('searchModelDiv');
				enable('listAllModelBtn');
				jQuery('#loaderDiv').hide();
				populateModelTypeAttributeOption( data );
			},'xml');
}

function populateModelTypeAttributeOption( data )
{
	var searchingAttributeOptionId = document.getElementById("searchingAttributeOptionId");
	clearList( searchingAttributeOptionId );
	
	var modelTypeAttribOption = data.getElementsByTagName("model-type-attribute-option");
	
	if( modelTypeAttribOption.length == 0 )
	{
		document.getElementById("searchingAttributeOptionId").disabled=true;
	}
	else
	{
		document.getElementById("searchingAttributeOptionId").disabled=false;
	}
	//alert( modelTypeAttribOption.length);
    for ( var i = 0; i < modelTypeAttribOption.length; i++ )
    {
        var id = modelTypeAttribOption[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var name = modelTypeAttribOption[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
        var option = document.createElement("option");
        option.value = id;
        option.text = name;
        option.title = name;
        searchingAttributeOptionId.add(option, null);
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
	
	document.getElementById("searchText").value = "";

	
	if( modelTypeId == 0 )
	{	
		showWarningMessage( i18n_select_please_select_model_type );
		return;
	}
	
	hideById('addEditModelFormDiv');
	hideById('resultSearchDiv');
	hideById('uploadModelImageDiv');
	
	showById('selectDiv');
	showById('searchModelDiv');

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
//Load Equipments On Filter by modelType Attribute
//----------------------------------------------------------------
function loadModelsByFilter( )
{
	var modelType = document.getElementById('modelType');
	var modelTypeId = modelType.options[ modelType.selectedIndex ].value;
	var searchText = document.getElementById('searchText').value;
	
	if( modelTypeId == 0 )
	{	
		showWarningMessage( i18n_select_please_select_model_type );
		return;
	}
	
	var modelTypeAttribute = document.getElementById('searchingAttributeId');
	var modelTypeAttributeId = modelTypeAttribute.options[ modelTypeAttribute.selectedIndex ].value;
	
	hideById('addEditModelFormDiv');
	hideById('resultSearchDiv');
	hideById('uploadModelImageDiv');
	showById('selectDiv');
	showById('searchModelDiv');

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
//Add Model
//----------------------------------------------------------------

function showAddModelForm()
{
	var modelType = document.getElementById('modelType');
	var modelTypeId = modelType.options[ modelType.selectedIndex ].value;
	
	if( modelTypeId == 0 )
	{	
		showWarningMessage( i18n_select_please_select_model_type );
		return;
	}

	hideById('listModelDiv');
	hideById('selectDiv');
	hideById('searchModelDiv');
	hideById('uploadModelImageDiv');
	
	jQuery('#loaderDiv').show();
	jQuery('#addEditModelFormDiv').load('showAddModelForm.action',{
		modelTypeId:modelTypeId
		}, 
		function()
		{
			showById('addEditModelFormDiv');
			jQuery('#loaderDiv').hide();
		});	
}

function addModel()
{
	$.ajax({
    type: "POST",
    url: 'addModel.action',
    data: getParamsForDiv('addEditModelFormDiv'),
    success: function(json) {
		var type = json.response;
		jQuery('#resultSearchDiv').dialog('close');
		loadAllModels();
    }
   });
  return false;
}

//----------------------------------------------------------------
//Update Model
//----------------------------------------------------------------

function showUpdateModelForm( modelId )
{
	hideById('listModelDiv');
	hideById('selectDiv');
	hideById('searchModelDiv');
	hideById('uploadModelImageDiv');
	
	setInnerHTML('addEditModelFormDiv', '');
	
	jQuery('#loaderDiv').show();
	jQuery('#addEditModelFormDiv').load('showUpdateModelForm.action',
		{
			id:modelId
		}, function()
		{
			showById('addEditModelFormDiv');
			jQuery('#searchModelDiv').dialog('close');
			jQuery('#loaderDiv').hide();
		});
		
	jQuery('#resultSearchDiv').dialog('close');
}

function updateModel()
{
	$.ajax({
      type: "POST",
      url: 'updateModel.action',
      data: getParamsForDiv('addEditModelFormDiv'),
      success: function( json ) {
		loadAllModels();
      }
     });
}


/*
function showUploadModelImageForm( modelId )
{
	setInnerHTML('uploadModelImageDiv', '');
	jQuery('#uploadModelImageDiv').dialog('destroy').remove();
	jQuery('<div id="uploadModelImageDiv">' ).load( 'showUploadImageForm.action?id='+modelId ).dialog({
		title: i18n_upload_model_image,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 800,
		height: 450
	});
	
}
*/
function upLoadImage()
{
	
		$( '#imageSaveDiv' ).html( ' ' );
		
		var modelID = $( '#modelID' ).val();
		//var sDateLB = $( '#sDateLB' ).val();
		//var eDateLB = $( '#eDateLB' ).val();
		
		//jQuery('#loaderDiv').show();
		//document.getElementById( "aggregate" ).disabled = true;
		
		jQuery('#imageSaveDiv').load('uploadModelImage.action',
			{
				modelID:modelID,
				contentType:"multipart/form-data"
				//eDateLB:eDateLB
			}, function()
			{
				showById('imageSaveDiv');
				//document.getElementById( "aggregate" ).disabled = false;
				//jQuery('#loaderDiv').hide();
			});	
}	
/*
jQuery('#imageSaveDiv').load('uploadModelImage.action',
		{
			modelID:modelID,
			contentType:"multipart/form-data"
			//eDateLB:eDateLB
		}, function()
		{
			showById('imageSaveDiv');
			//document.getElementById( "aggregate" ).disabled = false;
			//jQuery('#loaderDiv').hide();
		});	

*/
function showUploadModelImageForm( modelId )
{
	hideById('listModelDiv');
	hideById('selectDiv');
	hideById('searchModelDiv');
	hideById('addEditModelFormDiv');
	
	setInnerHTML('uploadModelImageDiv', '');
	
	jQuery('#loaderDiv').show();
	jQuery('#uploadModelImageDiv').load('showUploadImageForm.action',
		{
			id:modelId
		}, function()
		{
			showById('uploadModelImageDiv');
			jQuery('#searchModelDiv').dialog('close');
			jQuery('#loaderDiv').hide();
		});
		
	jQuery('#resultSearchDiv').dialog('close');
}
/*
function uploadModelImage()
{
	$.ajax({
      type: "POST",
      contentType: 'multipart/form-data',
      url: 'uploadModelImage.action',
      data: getParamsForDiv('uploadModelImageDiv'),
      success: function( json ) {
		loadAllModels();
      }
     });
}
*/


//----------------------------------------------------------------
//Get Params form Div
//----------------------------------------------------------------

function getParamsForDiv( modelDiv )
{
	var params = '';
	
	jQuery("#" + modelDiv + " :input").each(function()
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
	
	//alert( params );
	
	return params;
}
