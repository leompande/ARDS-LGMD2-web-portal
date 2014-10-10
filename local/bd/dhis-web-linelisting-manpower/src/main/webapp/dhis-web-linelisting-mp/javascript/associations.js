
var numberOfSelects = 0;

function selectAllAtLevel( id )
{
	var list = document.getElementById( 'levelList' );
    
    var level = list.options[ list.selectedIndex ].value;
    
    window.location.href = 'selectLevel.action?level=' + level + '&id=' + id;
}

function unselectAllAtLevel( id )
{
	var list = document.getElementById( 'levelList' );
    
    var level = list.options[ list.selectedIndex ].value;
    
    window.location.href = 'unselectLevel.action?level=' + level + '&id=' + id;
}

function treeClicked()
{
    numberOfSelects++;
    
    setMessage( i18n_loading );
    
    document.getElementById( "submitButton" ).disabled = true;
}

function selectCompleted( selectedUnits )
{
    numberOfSelects--;
    
    if ( numberOfSelects <= 0 )
    {
        hideMessage();
        
        document.getElementById( "submitButton" ).disabled = false;
    }
}
