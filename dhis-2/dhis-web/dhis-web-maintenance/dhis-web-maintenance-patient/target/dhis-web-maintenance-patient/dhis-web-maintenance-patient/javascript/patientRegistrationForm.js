$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

function defineForm( context ){
	window.location.href='viewPatientRegistrationForm.action?programId=' + context.id;
}

function removeRegistrationForm( context )
{
	var result = window.confirm( i18n_confirm_delete + "\n\n" + context.name );
    
    if ( result )
    {
		jQuery.postJSON("delRegistrationFormAction.action", {id:context.id}
		,function(json) {});
	}
}
