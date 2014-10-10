
function changeLocale()
{	
	$.get( 'systemAppearanceSettingsString.action?localeCode=' + $( '#localeSelect' ).val(), function( json ) {
		$( '#applicationTitle' ).val( json.applicationTitle );
		$( '#applicationIntro' ).val( json.keyApplicationIntro );
		$( '#applicationNotification' ).val( json.keyApplicationNotification );
		$( '#applicationFooter' ).val( json.keyApplicationFooter );
	} );	
}