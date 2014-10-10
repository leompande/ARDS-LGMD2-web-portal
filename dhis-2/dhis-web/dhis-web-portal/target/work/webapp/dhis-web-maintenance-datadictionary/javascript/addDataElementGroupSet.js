jQuery( document ).ready( function()
{
    validation2( 'addDataElementGroupSet', function( form )
    {
        form.submit();
    }, {
        'beforeValidateHandler' : beforeSubmit,
        'rules' : getValidationRules( "dataElementGroupSet" )
    } );

    checkValueIsExist( "name", "validateDataElementGroupSet.action" );
} );
