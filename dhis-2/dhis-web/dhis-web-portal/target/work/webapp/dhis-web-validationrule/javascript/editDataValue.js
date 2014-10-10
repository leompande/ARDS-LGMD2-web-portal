
function editValue( valueId )
{
	var field = document.getElementById( 'value-' + valueId + '-val' );
	
	var dataElementId = $( '#value-' + valueId + '-de' ).val();
    var categoryOptionComboId = $( '#value-' + valueId + '-coc' ).val();
	var periodId = $( '#value-' + valueId + '-pe' ).val();
	var sourceId = $( '#value-' + valueId + '-ou' ).val();
	
	if ( field.value != '' )
	{
		if ( !isInt( field.value ) )
		{
			alert( i18n_value_must_be_a_number );
			
			field.select();
	        field.focus(); 
	        
			return;   
		}
		else
		{
			var minString = $( '#value-' + valueId + '-min' ).val();
			var maxString = $( '#value-' + valueId + '-max' ).val();
			
			var min = new Number( minString );
			var max = new Number( maxString );
			var value = new Number( field.value );
			
			if ( !( min == 0 && max == 0 ) ) // No min max found
			{
				if ( value < min )
				{
					var valueSaver = new ValueSaver( dataElementId, periodId, sourceId, categoryOptionComboId, field.value, valueId, '#ffcccc' );
					valueSaver.save();
					
					alert( i18n_value_is_lower_than_min_value );
					return;
				}
				
				if ( value > max )
				{
					var valueSaver = new ValueSaver( dataElementId, periodId, sourceId, categoryOptionComboId, field.value, valueId, '#ffcccc' );
					valueSaver.save();
					
					alert( i18n_value_is_higher_than_max_value );
					return;
				}
			}
		}
	}
	
    var valueSaver = new ValueSaver( dataElementId, periodId, sourceId, categoryOptionComboId, field.value, valueId, '#ccffcc' );
    valueSaver.save();
}

function isInt( value )
{
    var number = new Number( value );
    
    if ( isNaN( number ))
    {
        return false;
    }
    
    return true;
}

function markFollowUp( valueId )
{	
    var dataElementId = $( '#value-' + valueId + '-de' ).val();
    var categoryOptionComboId = $( '#value-' + valueId + '-coc' ).val();
    var periodId = $( '#value-' + valueId + '-pe' ).val();
    var sourceId = $( '#value-' + valueId + '-ou' ).val();
    
    $.ajax( {
    	url: 'markForFollowup.action',
    	data: { dataElementId:dataElementId, periodId:periodId, sourceId:sourceId, categoryOptionComboId:categoryOptionComboId },
    	type: 'POST',
    	dataType: 'json',
    	success: function( json )
		{
            var $image = $( '#value-' + valueId + '-followUp' );
			
            if ( json.message == "marked" )
		    {
		        $image.attr( "src", "../images/marked.png" );
		        $image.attr( "title", i18n_unmark_value_for_followup );
		    }
		    else if ( json.message == "unmarked" )
		    {
		        $image.attr( "src", "../images/unmarked.png" );
		        $image.attr( "title", i18n_mark_value_for_followup );   
		    }
		} } );
}

// -----------------------------------------------------------------------------
// Saver object (modified version of dataentry/javascript/general.js)
// -----------------------------------------------------------------------------

function ValueSaver( dataElementId, periodId, organisationUnitId, categoryOptionComboId, value, valueId_, resultColor_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';

    var valueId = valueId_;
    var resultColor = resultColor_;
    
    this.save = function()
    {        
        $.ajax( {
        	url: 'editDataValue.action',
        	data: { dataElementId:dataElementId, periodId:periodId, organisationUnitId:organisationUnitId, categoryOptionComboId:categoryOptionComboId, value:value },
        	type: 'POST',
    		dataType: 'json',
        	success: function( json )
        	{
        		if ( json.response == "success" )
        		{
        			markValue( resultColor );
        		}
        		else
		        {
		            markValue( ERROR );
		            window.alert( "Failed saving value" );
		        }
        	},
        	error: function( json )
        	{
        		markValue( ERROR );
        		window.alert( "Failed saving value" );
        	}
        } );        
    };
    
    function markValue( color )
    {
    	$( '#value-' + valueId + '-val' ).css( "background-color", color );
    }
}
