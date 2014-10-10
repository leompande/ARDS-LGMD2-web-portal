
// -----------------------------------------------------------------------------
// Report params
// -----------------------------------------------------------------------------

function getOrganisationUnitsParent()
{
    var organisationUnitLevel = getListValue( "organisationUnitLevelParent" );
    
    if ( organisationUnitLevel != null )
    {
    	/* // var url = "../dhis-web-commons-ajax/getOrganisationUnits.action?level=" + organisationUnitLevel;
        
        var request = new Request();
        request.setResponseTypeXML( 'organisationUnit' );
        request.setCallbackSuccess( getOrganisationUnitsParentReceived );
        //request.send( url );

        var requestString = "../dhis-web-commons-ajax/getOrganisationUnits.action";
        var params = "level=" + organisationUnitLevel;
        request.sendAsPost( params );
        request.send( requestString ); */
		
		$.post("../dhis-web-commons-ajax/getOrganisationUnits.action",
			{
				level : organisationUnitLevel
			},
			function (data)
			{
				getOrganisationUnitsParentReceived(data);
			},'xml');
    }
}

function getOrganisationUnitsParentReceived( xmlObject )
{   
    var availableOrganisationUnits = document.getElementById( "parentOrganisationUnitId" );
    
    clearList( availableOrganisationUnits );
    
    var organisationUnits = xmlObject.getElementsByTagName( "organisationUnit" );
    
    for ( var i = 0; i < organisationUnits.length; i++ )
    {
        var id = organisationUnits[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var organisationUnitName = organisationUnits[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
        
        /* var option = document.createElement( "option" );
        option.value = id;
        option.text = organisationUnitName;
        availableOrganisationUnits.add( option, null ); */
		
		$("#parentOrganisationUnitId").append("<option value='"+ id +"'>" + organisationUnitName + "</option>");
    }
}

function getOrganisationUnitsSingle()
{
    var organisationUnitLevel = getListValue( "organisationUnitLevelSingle" );
    
    if ( organisationUnitLevel != null )
    {
        /* //var url = "../dhis-web-commons-ajax/getOrganisationUnits.action?level=" + organisationUnitLevel;
        
        var request = new Request();
        request.setResponseTypeXML( 'organisationUnit' );
        request.setCallbackSuccess( getOrganisationUnitsSingleReceived );
        //request.send( url );

        var requestString = "../dhis-web-commons-ajax/getOrganisationUnits.action";
        var params = "level=" + organisationUnitLevel;
        request.sendAsPost( params );
        request.send( requestString ); */
		
		$.post("../dhis-web-commons-ajax/getOrganisationUnits.action",
			{
				level : organisationUnitLevel
			},
			function (data)
			{
				getOrganisationUnitsSingleReceived(data);
			},'xml');
    }
}

function getOrganisationUnitsSingleReceived( xmlObject )
{   
    var availableOrganisationUnits = document.getElementById( "organisationUnitId" );
    
    clearList( availableOrganisationUnits );
    
    var organisationUnits = xmlObject.getElementsByTagName( "organisationUnit" );
    
    for ( var i = 0; i < organisationUnits.length; i++ )
    {
        var id = organisationUnits[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var organisationUnitName = organisationUnits[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
        
        /* var option = document.createElement( "option" );
        option.value = id;
        option.text = organisationUnitName;
        availableOrganisationUnits.add( option, null ); */
		
		$("#organisationUnitId").append("<option value='"+ id +"'>" + organisationUnitName + "</option>");
    }
}
