var left = true;
var dialog = null;

jQuery(document).ready(function() 
{
	dialog = jQuery( "#expression-container" ).dialog({
		modal: true,
		autoOpen: false,
		minWidth: 840,
		minHeight: 560,
		width: 840,
		height: 560,
		title: "Expression"
	});
		
	jQuery( "#periodTypeName, #ruleType" ).change(function(){
		getOperandsPage();
	});
	
	getConstantsPage();
	getOperandsPage();
});

function editLeftExpression()
{		
	left = true;
	
	$( '#expression' ).val( $( '#leftSideExpression' ).val() );
	$( '#description' ).val( $( '#leftSideDescription' ).val() );
	$( '#formulaText' ).text( $( '#leftSideTextualExpression' ).val() );
	$( '#nullIfBlank' ).attr( 'checked', ( $( '#leftSideNullIfBlank' ).val() == 'true' || $( '#leftSideNullIfBlank' ).val() == '' ) );
	
	dialog.dialog("open");
}

function editRightExpression()
{
	left = false;
	
	$( '#expression' ).val( $( '#rightSideExpression' ).val() );
	$( '#description' ).val( $( '#rightSideDescription' ).val() );
	$( '#formulaText' ).text( $( '#rightSideTextualExpression' ).val() );
	$( '#nullIfBlank' ).attr( 'checked', ( $( '#rightSideNullIfBlank' ).val() == 'true' || $( '#rightSideNullIfBlank' ).val() == '' ) );
	
	dialog.dialog("open");
}

function getConstantsPage()
{
	var target = jQuery( "#expression-container select[id=constantId]" );
	target.children().remove();
	
	jQuery.get( '../api/constants.json?paging=false&links=false', {}, function( json ) 
	{
		if ( !json.constants || json.constants.length == 0 )
		{
			setInnerHTML( 'constantHeader', "<i style='color:red'>"+i18n_no_constant_to_select+"</i>" );
			return;
		}
		
		jQuery.each( json.constants, function(i, item) {
			target.append( '<option value="C{' + item.id + '}">' + item.name + '</option>' );
		});
	});
}

function getOperandsPage()
{
	var key = getFieldValue( "expression-container input[id=filter]" );
	
	var periodType = getFieldValue( "periodTypeName" );
	var ruleType = getFieldValue( "ruleType" );
	var periodTypeAllowAverage = ( ruleType && ruleType == "surveillance" ) ? true : false;

	dataDictionary.loadOperands( "#expression-container select[id=dataElementId]", 
		{usePaging: true, key: key, periodType: periodType, periodTypeAllowAverage: periodTypeAllowAverage } );	
}

function clearSearchText()
{
	jQuery( "#expression-container input[id=filter]").val("");
	getOperandsPage();
}

function getExpressionText()
{
	if( hasText('expression') )
	{
		jQuery.postJSON( '../dhis-web-commons-ajax-json/getExpressionText.action', 
		{
			expression: $( '#expression' ).val()
		}, 
		function( json )
		{
			if( json.response == 'success' || json.response == 'error' )
			{
				jQuery( "#formulaText").html( json.message );
			}
			else {
				jQuery( "#formulaText").html( '' );
			}
		});
	}
	else
	{
		jQuery( "#formulaText").html( '' );
	}
}

function insertText( inputAreaName, inputText )
{
	insertTextCommon( inputAreaName, inputText );
	
	getExpressionText();
}

function insertExpression()
{
	var expression = $( '#expression' ).val();
	var description = $( '#description' ).val();
	
	jQuery.postJSON( '../dhis-web-commons-ajax-json/getExpressionText.action', 
	{
		expression: expression
	},
	function( json )
	{
		if ( json.response == 'error' )
		{
			markInvalid( 'expression-container textarea[id=expression]' , json.message );
		}
		else 
		{								
			if ( left )
			{
				$( '#leftSideExpression' ).val( expression );
				$( '#leftSideDescription' ).val( description );					
				$( '#leftSideTextualExpression' ).val( $( '#formulaText' ).text() );
				$( '#leftSideNullIfBlank' ).val( $( '#nullIfBlank' ).is( ':checked' ) );
			}
			else
			{
				$( '#rightSideExpression' ).val( expression );
				$( '#rightSideDescription' ).val( description );					
				$( '#rightSideTextualExpression' ).val( $( '#formulaText' ).text() );
				$( '#rightSideNullIfBlank' ).val( $( '#nullIfBlank' ).is( ':checked' ) );								
			}
			
			dialog.dialog( "close" );
		}
	});
}
