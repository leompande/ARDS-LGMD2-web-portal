jQuery( document ).ready( function()
{
    validation2( 'addIndicatorGroupSet', function( form )
    {
        form.submit();
    }, {
        'beforeValidateHandler' : function()
        {
            listValidator( 'memberValidator', 'groupMembers' );
        },
        'rules' : getValidationRules( "indicatorGroupSet" )
    } );
} );
