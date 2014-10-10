
// -----------------------------------------------------------------------------
// $Id: general.js 4948 2008-04-21 06:11:33Z leifas $
// -----------------------------------------------------------------------------
// Selection
// -----------------------------------------------------------------------------

function changeOrder()
{
    window.open( 'getDataElementOrder.action', '_blank', 'width=700,height=500,scrollbars=yes' );
}


// -----------------------------------------------------------------------------
// Save
// -----------------------------------------------------------------------------

function saveValue( indicatorId )
{
    var field = document.getElementById( 'value[' + indicatorId + '].value' );
    var type = 'int'; 
    
    field.style.backgroundColor = '#ffffcc';   
    
    
    //if ( field.value != '' )
    {
    	
        if ( type == 'int' )        
        {
        	var value = new Number( field.value );       	        	
        	 
        	//if( value * 1 == 0 )
        	//{
        	//	window.alert( i18n_saving_zero_values_unnecessary  );
        	//	field.select();
   	      //  field.focus(); 
   	                   		
				  //  return;				    	
        	//}
        	
            if ( !isInt( field.value ))
            {
                field.style.backgroundColor = '#ffcc00';

                window.alert( i18n_value_must_integer );

                field.select();
                field.focus();

                return;
            }
            else
            {
                var valueSaver = new ValueSaver( indicatorId, field.value, '#ccffcc', '' );
                valueSaver.save();    
            }
        }
    }
}

function saveTargetValue( dataElementId, optionComboId )
{
    var field = document.getElementById( 'value[' + dataElementId +':' + optionComboId + '].value' );
    var type = 'int'; 
    
    field.style.backgroundColor = '#ffffcc';   
    
    
    //if ( field.value != '' )
    {
    	
        if ( type == 'int' )        
        {
        	var value = new Number( field.value );       	        	
        	 
            if ( !isInt( field.value ))
            {
                field.style.backgroundColor = '#ffcc00';

                window.alert( i18n_value_must_integer );

                field.select();
                field.focus();

                return;
            }
            else
            {
                var valueSaver = new targetValueSaver( dataElementId, optionComboId, field.value, '#ccffcc', '' );
                valueSaver.save();    
            }
        }
    }
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

// -----------------------------------------------------------------------------
// Saver objects
// -----------------------------------------------------------------------------

function ValueSaver( indicatorId_, value_, resultColor_, selectedOption_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';

    var indicatorId = indicatorId_;
    
    var value = value_;
    var resultColor = resultColor_;
    var selectedOption = selectedOption_; 
    
    this.save = function()
    {
		$.ajax({
			   type: "POST",
			   url: "saveValue.action",
			   data: "indicatorId=" + indicatorId + "&value=" + value,
			   success: function(result){
					handleResponse (result);
			   },
			   error: function(request,status,errorThrown) {
					handleHttpError (request);
			   }
		});
    };
    
    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        
        if ( code == 0 )
        {
            markValue( resultColor );                   
        }
        else
        {
            markValue( ERROR );
            window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
        }
    }
    
    function handleHttpError( errorCode )
    {
        markValue( ERROR );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }   
    
    function markValue( color )
    {        
        var element = byId( 'value[' + indicatorId + '].value' );
                
        element.style.backgroundColor = color;
    }
}

function targetValueSaver( dataElementId_, optionComboid_, value_, resultColor_, selectedOption_ )
{
	var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';

    var dataElementId = dataElementId_;
    var optionComboId = optionComboid_;
    
    var value = value_;
    var resultColor = resultColor_;
    var selectedOption = selectedOption_; 
    
    this.save = function()
    {
		$.ajax({
			   type: "POST",
			   url: "saveTargetValue.action",
			   data: "dataElementId=" + dataElementId + "&optionComboId=" +optionComboId + "&value=" + value,
			   success: function(result){
					handleResponse (result);
			   },
			   error: function(request,status,errorThrown) {
					handleHttpError (request);
			   }
		});
    };
    
    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        if ( code == 0 )
        {
            markTargetValue( resultColor );
        }
        else
        {
        	markTargetValue( ERROR );
            window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
        }
    }
    
    function handleHttpError( errorCode )
    {
    	markTargetValue( ERROR );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }   
    
    function markTargetValue( color )
    {  
    	var elementId = 'value[' + dataElementId + ':' + optionComboId +'].value'
       //var element = document.getElementById( 'value[' + deOptionComboId + '].value' );
       // var element = document.getElementById( 'value[' + optionComboId + '].value' );
        var element = document.getElementById( elementId );
        element.style.backgroundColor = color;
    }
}



