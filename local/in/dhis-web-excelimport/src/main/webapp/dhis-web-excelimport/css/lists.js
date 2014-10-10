
// -------------------------------------------------------------------------
// Data retrieval methods
// -------------------------------------------------------------------------

function getDataElements()
{
	var dataElementGroupList = document.getElementById( "dataElementGroupId" );
	var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
	
	if ( dataElementGroupId != null )
	{
		$.post("../dhis-web-commons-ajax/getDataElements.action",
		{
			id :dataElementGroupId,
			aggregate : "true"
		},
		function (data)
		{
			getDataElementsReceived(data);
		},'xml');
	}
}

function getDataElementsReceived( xmlObject )
{   
    var availableDataElements = document.getElementById( "availableDataElements" );
    var selectedDataElements = document.getElementById( "selectedDataElements" );
    
    clearList( availableDataElements );
    
    var dataElements = xmlObject.getElementsByTagName( "dataElement" );
    
    for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = dataElements[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var dataElementName = dataElements[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
        
        if ( listContains( selectedDataElements, id ) == false )
        {               
            var option = document.createElement( "option" );
            option.value = id;
            option.text = dataElementName;
            availableDataElements.add( option, null );
        }
    }
}

function getCategoryComboDataElements()
{
    var categoryComboList = document.getElementById( "categoryComboId" );
    var categoryComboId = categoryComboList.options[ categoryComboList.selectedIndex ].value;
    
    if ( categoryComboId != null )
    {
		$.post("../dhis-web-commons-ajax/getDataElements.action",
		{
			categoryComboId :categoryComboId,
			aggregate : "true"
		},
		function (data)
		{
			getCategoryComboDataElementsReceived(data);
		},'xml');
    }
}

function getCategoryComboDataElementsReceived( xmlObject )
{   
    var availableDataElements = document.getElementById( "availableDataElements" );
    var selectedDataElements = document.getElementById( "selectedDataElements" );
    
    clearList( availableDataElements );
    clearList( selectedDataElements );
    
    var dataElements = xmlObject.getElementsByTagName( "dataElement" );
    
    for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = dataElements[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var dataElementName = dataElements[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
        
        var option = document.createElement( "option" );
        option.value = id;
        option.text = dataElementName;
        availableDataElements.add( option, null );
    }
}

function getIndicators()
{
	var indicatorGroupList = document.getElementById( "indicatorGroupId" );
	var indicatorGroupId = indicatorGroupList.options[ indicatorGroupList.selectedIndex ].value;
	
	if ( indicatorGroupId != null )
	{
		$.post("../dhis-web-commons-ajax/getIndicators.action",
		{
			id :indicatorGroupId
		},
		function (data)
		{
			getIndicatorsReceived(data);
		},'xml');
		
	}
}

function getIndicatorsReceived( xmlObject )
{   
    var availableIndicators = document.getElementById( "availableIndicators" );
    var selectedIndicators = document.getElementById( "selectedIndicators" );
    
    clearList( availableIndicators );
    
    var indicators = xmlObject.getElementsByTagName( "indicator" );
    
    for ( var i = 0; i < indicators.length; i++ )
    {
        var id = indicators[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var indicatorName = indicators[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
        
        if ( listContains( selectedIndicators, id ) == false )
        {               
            var option = document.createElement( "option" );
            option.value = id;
            option.text = indicatorName;
            availableIndicators.add( option, null );
        }
    }
}

function getOrganisationUnits()
{
	var organisationUnitLevelList = document.getElementById( "organisationUnitLevel" );
	var organisationUnitLevel = organisationUnitLevelList.options[ organisationUnitLevelList.selectedIndex ].value;
	
	if ( organisationUnitLevel != null )
	{
		$.post("../dhis-web-commons-ajax/getOrganisationUnits.action",
		{
			level :organisationUnitLevel
		},
		function (data)
		{
			getOrganisationUnitsReceived(data);
		},'xml');
	    
	}
}

function getOrganisationUnitsReceived( xmlObject )
{   
    var availableOrganisationUnits = document.getElementById( "availableOrganisationUnits" );
    var selectedOrganisationUnits = document.getElementById( "selectedOrganisationUnits" );
    
    clearList( availableOrganisationUnits );
    
    var organisationUnits = xmlObject.getElementsByTagName( "organisationUnit" );
    
    for ( var i = 0; i < organisationUnits.length; i++ )
    {
        var id = organisationUnits[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var organisationUnitName = organisationUnits[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
        
        if ( listContains( selectedOrganisationUnits, id ) == false )
        {                       
            var option = document.createElement( "option" );
            option.value = id;
            option.text = organisationUnitName;
            availableOrganisationUnits.add( option, null );
        }
    }
}

function getOrganisationUnitsToSelected()
{
    var organisationUnitLevelList = document.getElementById( "organisationUnitLevel" );
    var organisationUnitLevel = organisationUnitLevelList.options[ organisationUnitLevelList.selectedIndex ].value;
    
    if ( organisationUnitLevel != null )
    {
		$.post("../dhis-web-commons-ajax/getOrganisationUnits.action",
		{
			level :organisationUnitLevel
		},
		function (data)
		{
			getOrganisationUnitsToSelectedReceived(data);
		},'xml');
		
    }
}

function getOrganisationUnitsToSelectedReceived( xmlObject )
{   
    var selectedOrganisationUnits = document.getElementById( "selectedOrganisationUnits" );
    
    clearList( selectedOrganisationUnits );
    
    var organisationUnits = xmlObject.getElementsByTagName( "organisationUnit" );
    
    for ( var i = 0; i < organisationUnits.length; i++ )
    {
        var id = organisationUnits[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var organisationUnitName = organisationUnits[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
        
        var option = document.createElement( "option" );
        option.value = id;
        option.text = organisationUnitName;
        selectedOrganisationUnits.add( option, null );
    }
}

function getOrganisationUnitChildren()
{
	var organisationUnitList = document.getElementById( "availableOrganisationUnits" );
	var organisationUnitId = organisationUnitList.options[ organisationUnitList.selectedIndex ].value;
	
	if ( organisationUnitId != null )
	{
		$.post("../dhis-web-commons-ajax/getOrganisationUnitChildren.action",
		{
			id :organisationUnitId
		},
		function (data)
		{
			getOrganisationUnitChildrenReceived(data);
		},'xml');
		
	}
}

function getOrganisationUnitChildrenReceived( xmlObject )
{
    var selectedOrganisationUnits = document.getElementById( "selectedOrganisationUnits" );
    
    var organisationUnits = xmlObject.getElementsByTagName( "organisationUnit" );
    
    for ( var i = 0; i < organisationUnits.length; i++ )
    {
        var id = organisationUnits[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        
        var organisationUnitName = organisationUnits[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
        
        if ( listContains( selectedOrganisationUnits, id ) == false )
        {
            var option = document.createElement( "option" );
            option.value = id;
            option.text = organisationUnitName;
            selectedOrganisationUnits.add( option, null );
        }
    }
}

function getPeriods()
{
	var periodTypeList = document.getElementById( "periodTypeId" );
	var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;
	
	if ( periodTypeId != null )
	{
		$.post("../dhis-web-commons-ajax/getPeriods.action",
		{
			name : periodTypeId
		},
		function (data)
		{
			getPeriodsReceived(data);
		},'xml');
	}
}

function getPeriodsReceived( xmlObject )
{	
	var availablePeriods = document.getElementById( "availablePeriods" );
	var selectedPeriods = document.getElementById( "selectedPeriods" );
	
	clearList( availablePeriods );
	
	var periods = xmlObject.getElementsByTagName( "period" );
	
	for ( var i = 0; i < periods.length; i++)
	{
		var id = periods[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var periodName = periods[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		
		if ( listContains( selectedPeriods, id ) == false )
		{						
			var option = document.createElement( "option" );
			option.value = id;
			option.text = periodName;
			availablePeriods.add( option, null );
		}			
	}
}

function getPeriodsToSelected()
{
    var periodTypeList = document.getElementById( "periodTypeId" );
    var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;
    
    if ( periodTypeId != null )
    {   
		$.post("../dhis-web-commons-ajax/getPeriods.action",
		{
			name : periodTypeId
		},
		function (data)
		{
			getPeriodsToSelectedReceived(data);
		},'xml');
    }
}

function getPeriodsToSelectedReceived( xmlObject )
{   
    var selectedPeriods = document.getElementById( "selectedPeriods" );
    
    clearList( selectedPeriods );
    
    var periods = xmlObject.getElementsByTagName( "period" );
    
    for ( var i = 0; i < periods.length; i++)
    {
        var id = periods[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var periodName = periods[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
        
        var option = document.createElement( "option" );
        option.value = id;
        option.text = periodName;
        selectedPeriods.add( option, null );
    }
}
