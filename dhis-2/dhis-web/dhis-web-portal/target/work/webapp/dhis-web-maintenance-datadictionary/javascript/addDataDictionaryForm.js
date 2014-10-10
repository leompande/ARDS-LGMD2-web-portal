jQuery( document ).ready( function()
{
    function preSubmit() {
        var $selectedDataElements = $('#selectedDataElements');
        var $selectedIndicators = $('#selectedIndicators');

        var hasDataElements = $selectedDataElements.val() != null && $selectedDataElements.val().length > 0;
        var hasIndicators = $selectedIndicators.val() != null && $selectedIndicators.val().length > 0;

        if( !hasDataElements && !hasIndicators )
        {
            setHeaderDelayMessage('Data Elements or Indicators are required.')
            return false;
        }

        return true;
    }

    validation2( 'addDataDictionaryForm', function( form )
    {
        preSubmit() && form.submit();
    }, {
        'beforeValidateHandler' : function()
        {
            listValidator( 'memberValidator', 'selectedDataElements' );
            listValidator( 'memberValidatorIn', 'selectedIndicators' );
        },
        'rules' : getValidationRules( "dataDictionary" )
    } );
} );
