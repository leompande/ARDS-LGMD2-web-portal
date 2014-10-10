
function validationCaseAggregation()
{
	$.get( 'validateCaseAggregation.action', 
		{}, validationCaseAggregationCompleted, 'xml' );				
}

function validationCaseAggregationCompleted( message )
{
    var type = $(message).find('message').attr('type');
    if( type == "success" ){
        caseAggregationResult();
    }
    else{
        showWarningMessage( $(message).find('message').text() );
    }
}

function viewResultDetails( aggConditionName, orgunitId, isoPeriod ) 
{
	$('#contentDetails' ).load('caseAggregationResultDetails.action',
		{
			orgunitId:orgunitId,
			isoPeriod:isoPeriod,
			aggConditionName:aggConditionName
		}).dialog({
			title: i18n_aggregate_details,
			maximize: true, 
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 800,
			height: 400
		});
}

function caseAggregationResult()
{
	var autoSave = getFieldValue('autoSave');
	if(autoSave=='true')
	{
		if( confirm(i18n_confirm_data_values_aggregated_saved_into_database_directly) )
		{
			runAggregate(autoSave);
		}
	}
	else
	{
		previewAggregate(autoSave);
	}
}

function runAggregate(autoSave)
{
	hideById('caseAggregationForm');
	hideById('caseAggregationResult');
	showLoader();
	
	$('#caseAggregationResult').load("caseAggregationResult.action", 
		{
			facilityLB: getFieldValue('facilityLB'),
			dataSetId: getFieldValue('dataSetId'),
			startDate: getFieldValue('startDate'),
			endDate: getFieldValue('endDate'),
			autoSave: getFieldValue('autoSave')
		}
		, function(){
			$( "#loaderDiv" ).hide();
			showById('caseAggregationForm');
			setHeaderDelayMessage(i18n_aggregate_successfully);
		});
}

function previewAggregate(autoSave)
{
	hideById('caseAggregationForm');
	hideById('message');
	showLoader();
	$('#caseAggregationResult').load("caseAggregationResult.action", 
		{
			facilityLB: getFieldValue('facilityLB'),
			dataSetId: getFieldValue('dataSetId'),
			startDate: getFieldValue('startDate'),
			endDate: getFieldValue('endDate'),
			autoSave: getFieldValue('autoSave')
		}
		, function(){
			$( "#loaderDiv" ).hide();
			showById('caseAggregationResult');
		});
}

function backBtnOnClick()
{
	hideById('caseAggregationResult');
	showById('caseAggregationForm');
}

function toggleResult( id )
{
	$( "#div-" + id ).slideToggle( "fast" );
}

function saveAggregateDataValues( isSaveAll )
{
	lockScreen();
	
	var params = ""
	if( isSaveAll )
	{
		jQuery("input[name=aggregateValues]").each(function( ){
			params += "aggregateValues=" + $(this).val() + "&";
		}); 
	}
	else
	{
		jQuery("input[name=aggregateValues]:checked").each(function( ){
			params += "aggregateValues=" + $(this).val() + "&";
		}); 
	}
		
	if( params!=""){
		$.ajax({
			   type: "POST",
			   url: "saveAggregateDataValue.action",
			   data: params,
			   dataType: "json",
			   success: function(json){
					if( isSaveAll )
					{
						jQuery("input[name=aggregateValues]").each(function( ){
								$(this).replaceWith('<span>' + i18n_saved + '<span>' );
							}); 
					}
					else
					{
						jQuery("input[name=aggregateValues]:checked").each(function( ){
								$(this).replaceWith('<span>' + i18n_saved + '<span>' );
							}); 
					}
					unLockScreen();
					showSuccessMessage( i18n_save_success );
			   }
			});
	}
}

function toogleAllCheckBoxes( tableDiv, checked )
{
	jQuery("#div-" + tableDiv + " input[name=aggregateValues]").attr( 'checked', checked );
}