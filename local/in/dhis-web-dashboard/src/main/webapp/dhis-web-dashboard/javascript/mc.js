

function getXAxisIndicators( )
{
    var indicatorGroupList = document.getElementById( "xaxisindicatorGroupId" );
    var indicatorGroupId = indicatorGroupList.options[ indicatorGroupList.selectedIndex ].value;
	
    if ( indicatorGroupId != null )
    {
		$.post("getIndicators.action",
		{
			id:indicatorGroupId
		},
		function (data)
		{
			getXAxisIndicatorsReceived(data);
		},'xml');
    }
}

function getXAxisIndicatorsReceived( xmlObject )
{	
    var xaxisIndicators = document.getElementById( "xaxisIndicator" );
	
    clearList( xaxisIndicators );
	
    var indicators = xmlObject.getElementsByTagName( "indicator" );
	
    for ( var i = 0; i < indicators.length; i++ )
    {
        var id = indicators[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var indicatorName = indicators[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		
        var option = document.createElement( "option" );
        option.value = id;
        option.text = indicatorName;
        if(i==0) option.selected = true;
        xaxisIndicators.add( option, null );
    }
}


function getYAxisIndicators( )
{
    var indicatorGroupList = document.getElementById( "yaxisindicatorGroupId" );
    var indicatorGroupId = indicatorGroupList.options[ indicatorGroupList.selectedIndex ].value;
	
    if ( indicatorGroupId != null )
    {
		$.post("getIndicators.action",
		{
			id:indicatorGroupId
		},
		function (data)
		{
			getYAxisIndicatorsReceived(data);
		},'xml');

    }
}

function getYAxisIndicatorsReceived( xmlObject )
{	
    var yaxisIndicators = document.getElementById( "yaxisIndicator" );
	
    clearList( yaxisIndicators );
	
    var indicators = xmlObject.getElementsByTagName( "indicator" );
	
    for ( var i = 0; i < indicators.length; i++ )
    {
        var id = indicators[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var indicatorName = indicators[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		
        var option = document.createElement( "option" );
        option.value = id;
        option.text = indicatorName;
        if(i==0) option.selected = true;
        yaxisIndicators.add( option, null );
    }
}


function getZAxisDataElements()
{
    var dataElementGroupList = document.getElementById("dataElementGroupId");
    var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
            
    if ( dataElementGroupId != null )
    {
		$.post("getDataElements.action",
		{
			id:dataElementGroupId
		},
		function (data)
		{
			getDataElementsReceived(data);
		},'xml');

    }
}// getDataElements end          


function getDataElementsReceived( xmlObject )
{
    var zaxisDataelements = document.getElementById("zaxisDataelements");
    
    clearList(zaxisDataelements);

    var dataElements = xmlObject.getElementsByTagName("dataElement");

    for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = dataElements[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var dataElementName = dataElements[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        
        var option = document.createElement("option");
        option.value = id;
        option.text = dataElementName;
        option.title = dataElementName;
        if(i==0) option.selected = true;
        zaxisDataelements.add(option, null);        
    }    
}// getDataElementsReceived end



function getOUDeatilsForMC(orgUnitIds)
{
	$.post("getOrgUnitDetails.action",
		{
			orgUnitId:orgUnitIds
		},
		function (data)
		{
			getOUDetailsForMARecevied(data);
		},'xml');
}

function getOUDetailsForMARecevied(xmlObject)
{
    var facilityList = document.getElementById("facilityLB");
    var facilityIndex = facilityList.selectedIndex;
	
    var index = 0;		
    var i=0;
		
    var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;

        if(facilityList.options[facilityIndex].value == "children")
        {
            index = document.ChartGenerationForm.orgUnitListCB.options.length;
            for(i=0;i<index;i++)
            {
                document.ChartGenerationForm.orgUnitListCB.options[0] = null;
            }
            document.ChartGenerationForm.orgUnitListCB.options[0] = new Option(orgUnitName,id,false,false);
        }
        else
        {
            index = document.ChartGenerationForm.orgUnitListCB.options.length;
            for(i=0;i<index;i++)
            {
                if(id == document.ChartGenerationForm.orgUnitListCB.options[i].value) return;
            }
            document.ChartGenerationForm.orgUnitListCB.options[index] = new Option(orgUnitName,id,false,false);
        }
    }	    		
}

function getOrgUnitGroups()
{
    var orgUnitGroupSetList = document.getElementById("orgUnitGroupSetListCB");
    var orgUnitGroupSetId = orgUnitGroupSetList.options[ orgUnitGroupSetList.selectedIndex ].value;
	
    if ( orgUnitGroupSetId != null )
    {
		$.post("getOrgUnitGroups.action",
		{
			orgUnitGroupSetId:orgUnitGroupSetId
		},
		function (data)
		{
			getOrgUnitGroupsReceived(data);
		},'xml');
    }
}

function getOrgUnitGroupsReceived(xmlObject)
{
    var orgUnitGroupList = document.getElementById("orgUnitListCB");
    clearList(orgUnitGroupList);
	
    var orgUnitGroups = xmlObject.getElementsByTagName("orgunitgroup");

    for ( var i = 0; i < orgUnitGroups.length; i++ )
    {
        var id = orgUnitGroups[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitGroupName = orgUnitGroups[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;

        var option = document.createElement("option");
        option.value = id;
        option.text = orgUnitGroupName;
        option.title = orgUnitGroupName;
        orgUnitGroupList.add(option, null);
    }	
}

// OrgUnit GroupSet Change Function
function orgUnitGroupSetCB()
{
    var orgUnitGroupSetList = document.getElementById( 'orgUnitGroupSetListCB' );
    var orgUnitList = document.getElementById( 'orgUnitListCB' );
    if(document.getElementById( 'ougSetCB' ).checked)
    {
        orgUnitGroupSetList.disabled = false;
        getOrgUnitGroups();
    }
    else
    {
        orgUnitGroupSetList.disabled = true;
        clearList(orgUnitList);
        getOUDeatilsForMC( document.getElementById( 'ouIdTB' ).value );
    }
//clearList(orgUnitList);
}

//Facility ListBox Change Function
function facilityChangeFunction(evt)
{
    selFacility = evt.target.value;
    if(selFacility == "children")
    {
        //var index = document.ChartGenerationForm.orgUnitListCB.options.length;
        //for(i=0;i<index;i++)
        //{
        //	document.ChartGenerationForm.orgUnitListCB.options[0] = null;
        //}
        var orgUnitList = document.getElementById( 'orgUnitListCB' );
        clearList(orgUnitList);
    	
        getOUDeatilsForMC( document.getElementById( 'ouIdTB' ).value );
    	
    }
}// facilityChangeFunction end


// Removes slected orgunits from the Organisation List
function remOUFunction()
{
    var index = document.ChartGenerationForm.orgUnitListCB.options.length;
    var i=0;
    for(i=index-1;i>=0;i--)
    {
        if(document.ChartGenerationForm.orgUnitListCB.options[i].selected)
            document.ChartGenerationForm.orgUnitListCB.options[i] = null;
    }
}// remOUFunction end



//MotionChart Form Validations
function formValidations()
{		
    var selOUListLength = document.ChartGenerationForm.orgUnitListCB.options.length;
	
    var xaxisIndicators = document.getElementById( "xaxisIndicator" );
    var yaxisIndicators = document.getElementById( "yaxisIndicator" );
    var zaxisDataelements = document.getElementById("zaxisDataelements");
	
    var selXAxis = xaxisIndicators.selectedIndex;
    var selYAxis = yaxisIndicators.selectedIndex;
    var selZAxis = zaxisDataelements.selectedIndex;
    
    sDate = document.ChartGenerationForm.startDate.value;
    eDate = document.ChartGenerationForm.endDate.value;
    

    if(selOUListLength <= 0) {
        alert("Please Select OrganisationUnit");return false;
    }
    else if( selXAxis < 0 )	 {
        alert("Please Select Indicator for X-Axis");return false;
    }
    else if( selYAxis < 0 ) {
        alert("Please Select Indicator for Y-Axis");return false;
    }
    else if( selZAxis < 0 ) {
        alert("Please Select Dataelement for Z-Axis");return false;
    }
    else if( sDate == null || sDate == "" ) {
        alert("Please Enter Starting Period");return false;
    }
    else if( eDate == null || eDate == "" ) {
        alert("Please Enter Ending Period");return false;
    }
    else if(sDate > eDate) {
        alert("Starting Date is Greater");return false;
    }
	
    
    if(document.getElementById( 'ougSetCB' ).checked)
    {
        if(document.ChartGenerationForm.orgUnitListCB.selectedIndex <= -1)
        {
            alert("Please Select OrganisationUnit");return false;
        }
    }
    else
    {
        for(var k=0;k<selOUListLength;k++)
        {
            document.ChartGenerationForm.orgUnitListCB.options[k].selected = true;
        }
    }
	
    var sWidth = 850;
    var sHeight = 650;
    var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

    window.open('','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
    return true;
} // formValidations Function End
