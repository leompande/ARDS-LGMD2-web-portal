
var numberOfSelects = 0;

function selectAllAtLevel( reportId )
{
	var list = document.getElementById( 'levelList' );
    
    var level = list.options[ list.selectedIndex ].value;
    
    window.location.href = 'selectLevel.action?level=' + level + '&reportId=' + reportId;
}

function unselectAllAtLevel( reportId )
{
	var list = document.getElementById( 'levelList' );
    
    var level = list.options[ list.selectedIndex ].value;
    
    window.location.href = 'unselectLevel.action?level=' + level + '&reportId=' + reportId;
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
// selectAllAtGroup function start

function selectAllAtGroup( reportId )
{
	var list = document.getElementById( 'groupList' );
    
    var group = list.options[ list.selectedIndex ].value;
    
    window.location.href = 'selectgroup.action?group=' + group + '&reportId=' + reportId;
}
// end  selectAllAtGroup function start

// unselectAllAtGroup function start

function unselectAllAtGroup( reportId )
{
	var list = document.getElementById( 'groupList' );
    
    var group = list.options[ list.selectedIndex ].value;
    
    window.location.href = 'unselectgroup.action?group=' + group + '&reportId=' + reportId;
}
//end  unselectAllAtGroup function start