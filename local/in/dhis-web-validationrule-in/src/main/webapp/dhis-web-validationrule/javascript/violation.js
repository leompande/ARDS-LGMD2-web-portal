







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

function getImmChildInfo(evt, dsId, selOrgUnit)
{
    immChildOption = "yes";
        
    evt.target.href = "dataStatusResult.action?immChildOption="+immChildOption+"&dsId="+dsId+"&sDateLB="+startDate+"&eDateLB="+endDate+"&ouId="+selOrgUnit;
    
}
      
function exportDataStatusResultToWorkBook()
{         
    document.getElementById('htmlCode').value = document.getElementById('formResult').innerHTML;
              
    return true;
}

// Category ListBox Change function
function categoryChangeFunction(evt)
{
    selCategory = evt.target.value;
    if(selCategory == "period")
    {
        document.ChartGenerationForm.facilityLB.disabled = true;
        var index = document.ChartGenerationForm.orgUnitListCB.options.length;
        for(i=0;i<index;i++)
        {
            document.ChartGenerationForm.orgUnitListCB.options[0] = null;
        }
    }
    else
    {
        document.ChartGenerationForm.facilityLB.disabled = false;
    }
}// categoryChangeFunction end
                
//Facility ListBox Change Function
function facilityChangeFunction(evt)
{
    selFacility = evt.target.value;
    if(selFacility == "children" || selFacility == "immChildren")
    {
        var index = document.ChartGenerationForm.orgUnitListCB.options.length;
        for(i=0;i<index;i++)
        {
            document.ChartGenerationForm.orgUnitListCB.options[0] = null;
        }
    }
}// facilityChangeFunction end

function textvalue(summary)
{
  
    document.getElementById("selectedButton").value = summary;
  
    if(formValidationsForDataStatus())
    {
        if(summary == "MinMaxViolation")
        {
            //document.getElementById("facilityLB").options[1].selected = true;
            document.ChartGenerationForm.action = "dataStatusResult.action";
            document.ChartGenerationForm.submit();
        }
        else if(summary == "IntegerViolation")
        {
            document.ChartGenerationForm.action = "groupWiseDataStatusResult.action";
            document.ChartGenerationForm.submit();
        }
        else
        {
            document.ChartGenerationForm.action = "dataStatusResult.action";
            document.ChartGenerationForm.submit();
        }
    }
     
}


// DataStatus Form Validations
function formValidationsForDataStatus()
{
    var selOUListIndex = document.ChartGenerationForm.orgUnitListCB.options.length;
    var selDSListSize  = document.ChartGenerationForm.selectedDataSets.options.length;
    sDateIndex    = document.ChartGenerationForm.sDateLB.selectedIndex;
    eDateIndex    = document.ChartGenerationForm.eDateLB.selectedIndex;
    sDateTxt = document.ChartGenerationForm.sDateLB.options[sDateIndex].text;
    sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM - y")),"yyyy-MM-dd");
    eDateTxt = document.ChartGenerationForm.eDateLB.options[eDateIndex].text;
    eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM - y")),"yyyy-MM-dd");

    if(selOUListIndex <= 0) {
        alert("Please Select OrganisationUnit"); return false;
    }
    else if(selDSListSize <= 0) {
        alert("Please Select DataSet(s)"); return false;
    }
    else if(sDateIndex < 0) {
        alert("Please Select Starting Period"); return false;
    }
    else if(eDateIndex < 0) {
        alert("Please Select Ending Period"); return false;
    }
    else if(sDate > eDate) {
        alert("Starting Date is Greater"); return false;
    }

    var k=0;
  
    for(k=0;k<selOUListIndex;k++)
    {
        document.ChartGenerationForm.orgUnitListCB.options[k].selected = true;
    }

    var sWidth = 850;
    var sHeight = 650;
    var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

    window.open('','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
    
      
    return true;

} // formValidations Function End 
// 
//
//  Getting corresponding Period List for Datasets. 
function getdSetPeriods()
{
    var dataSetList = document.getElementById("selectedDataSets");
    var dataSetId = dataSetList.options[ dataSetList.selectedIndex].value;

  
    //var url = "getDataSetPeriods.action?id=" + dataSetId;
    
    var request = new Request();
    request.setResponseTypeXML( 'period' );
    request.setCallbackSuccess( getdSetPeriodsReceived );
    //request.send( url );

    var requestString = "getDataSetPeriods.action";
    var params = "id=" + dataSetId ;
    request.sendAsPost( params );
    request.send( requestString );
 
}  



function getdSetPeriodsReceived( xmlObject )
{ 
    var sDateLB = document.getElementById( "sDateLB" );
    var eDateLB = document.getElementById( "eDateLB" );
  
    clearList( sDateLB );
    clearList( eDateLB );
  
    var periods = xmlObject.getElementsByTagName( "period" );
  
    for ( var i = 0; i < periods.length; i++)
    {
        var id = periods[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var periodName = periods[ i ].getElementsByTagName( "periodname" )[0].firstChild.nodeValue;
              
        var option1 = document.createElement( "option" );
        option1.value = id;
        option1.text = periodName;
        sDateLB.add( option1, null );
      
        var option2 = document.createElement( "option" );
        option2.value = id;
        option2.text = periodName;
        eDateLB.add( option2, null);
        
    }
  
  
}


function getOUDeatilsForAA(orgUnitIds)
{
    //var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;
    var request = new Request();
    request.setResponseTypeXML( 'orgunit' );
    request.setCallbackSuccess( getOUDetailsForAARecevied );
    //request.send( url );

    var requestString = "getOrgUnitDetails.action";
    var params = "orgUnitId=" + orgUnitIds;
    request.sendAsPost( params );
    request.send( requestString );
}

function getOUDetailsForNR(orgUnitIds)
{
    //var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;
  
    var request = new Request();
    request.setResponseTypeXML( 'orgunit' );
    request.setCallbackSuccess( getOUDetailsForNRRecevied );
    //request.send( url );

    var requestString = "getOrgUnitDetails.action";
    var params = "id=" + orgUnitIds ;
    request.sendAsPost( params );
    request.send( requestString );
}

function getOUDetailsForAARecevied(xmlObject)
{
    var orgUnits = xmlObject.getElementsByTagName("orgunit");
    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
    
        document.ChartGenerationForm.ouIDTB.value = id;
        document.ChartGenerationForm.ouNameTB.value = orgUnitName;
    }       
}

function getOUDetailsForNRRecevied(xmlObject)
{
    var orgUnits = xmlObject.getElementsByTagName("orgunit");
  
    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
    
        document.ChartGenerationForm.ouIDTB.value = id;
        document.ChartGenerationForm.ouNameTB.value = orgUnitName;
    }
            
}


function getOUDeatilsForGA(orgUnitIds)
{
    //var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;
  
    var request = new Request();
    request.setResponseTypeXML( 'orgunit' );
    request.setCallbackSuccess( getOUDetailsForGARecevied );
    //request.send( url );
    
    var requestString = "getOrgUnitDetails.action";
    var params = "id=" + orgUnitIds ;
    request.sendAsPost( params );
    request.send( requestString );
}

function getOUDetailsForGARecevied(xmlObject)
{
    var categoryIndex = document.ChartGenerationForm.categoryLB.selectedIndex;
    var facilityIndex =  document.ChartGenerationForm.facilityLB.selectedIndex;
    var index = 0;    
    var i=0;
    
    var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;

        if(document.ChartGenerationForm.categoryLB.options[categoryIndex].value == "period" || document.ChartGenerationForm.facilityLB.options[facilityIndex].value == "children")
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


function getOUDetails(orgUnitIds)
{
    //var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;
  
    var request = new Request();
    request.setResponseTypeXML( 'orgunit' );
    request.setCallbackSuccess( getOUDetailsRecevied );
    //request.send( url );

    var requestString = "getOrgUnitDetails.action";
    var params = "id=" + orgUnitIds ;
    request.sendAsPost( params );
    request.send( requestString );

}

function getOUDetailsRecevied(xmlObject)
{

    var categoryIndex = document.ChartGenerationForm.categoryLB.selectedIndex;
    var facilityIndex =  document.ChartGenerationForm.facilityLB.selectedIndex;
    var index = 0;    
    var i=0;
    
    var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;

        if(document.ChartGenerationForm.categoryLB.options[categoryIndex].value == "period" || document.ChartGenerationForm.facilityLB.options[facilityIndex].value == "children")
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


//Depends on dhis-web-commons/lists/lists.js for List functionality

function getOrgUnitGroups()
{
    var orgUnitGroupSetList = document.getElementById("orgUnitGroupSetListCB");
    var orgUnitGroupSetId = orgUnitGroupSetList.options[ orgUnitGroupSetList.selectedIndex ].value;
  
    if ( orgUnitGroupSetId != null )
    {
        //var url = "getOrgUnitGroups.action?orgUnitGroupSetId=" + orgUnitGroupSetId;
    
        var request = new Request();
        request.setResponseTypeXML( 'orgunitgroup' );
        request.setCallbackSuccess( getOrgUnitGroupsReceived );
        //request.send( url );

        var requestString = "getOrgUnitGroups.action";
        var params = "orgUnitGroupSetId=" + orgUnitGroupSetId ;
        request.sendAsPost( params );
        request.send( requestString );
    }
}
function groupChangeFunction(evt)
{
    document.ChartGenerationForm.selectedGroup.value = true;
    document.ChartGenerationForm.orgUnitListCB.value = null;
} //groupChangeFunction end

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

//--------------------------------------
//
//--------------------------------------
function getDataElements()
{
    var dataElementGroupList = document.getElementById("dataElementGroupId");
    var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
    
    var deSelectionList = document.getElementById("deSelection");    
    var deOptionValue = deSelectionList.options[ deSelectionList.selectedIndex ].value;
    
    if ( dataElementGroupId != null )
    {
        //var url = "getDataElements.action?id=" + dataElementGroupId + "&deOptionValue=" + deOptionValue;
        var request = new Request();
        request.setResponseTypeXML('dataElement');
        request.setCallbackSuccess(getDataElementsReceived);
        //request.send(url);

        var requestString = "getDataElements.action";
        var params = "id=" + dataElementGroupId + "&deOptionValue=" + deOptionValue ;
        request.sendAsPost( params );
        request.send( requestString );
    }
}// getDataElements end           

//Depends on dhis-web-commons/lists/lists.js for List functionality
function getDataElementsWithOutOptionCombo()
{
    var dataElementGroupList = document.getElementById("dataElementGroupId");
    var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
    
    var deSelectionList = document.getElementById("deSelection");   
    
    if ( dataElementGroupId != null )
    {
        //var url = "getDataElements.action?id=" + dataElementGroupId;
        var request = new Request();
        request.setResponseTypeXML('dataElement');
        request.setCallbackSuccess(getDataElementsReceived);
        //request.send(url);

        var requestString = "getDataElements.action";
        var params = "id=" + dataElementGroupId;
        request.sendAsPost( params );
        request.send( requestString );
    }
}// getDataElements end          


function getDataElementsGroupsInDataSet()
{
    var dataSetOptionList = document.getElementById("dataSetId");
    var dataSetId = dataSetOptionList.options[ dataSetOptionList.selectedIndex ].value;
    
    var deGroupSelectionList = document.getElementById("deGroupSelectionList");   
    
    if ( dataSetId != null )
    {
        //var url = "getDataElementsGroupAndDataSetAction.action?id=" + dataSetId;
        var request = new Request();
        request.setResponseTypeXML('dataElementGroup');
        request.setCallbackSuccess(getDataElementsGroupsReceived);
        //request.send(url);
        
        var requestString = "getDataElementsGroupAndDataSetAction.action";
        var params = "id=" + dataSetId;
        request.sendAsPost( params );
        request.send( requestString );
    }
}// getDataElementGroups end          


function getDataElementsReceived( xmlObject )
{
    var availableDataElements = document.getElementById("availableDataElements");
    var selectedDataElements = document.getElementById("selectedDataElements");

    clearList(availableDataElements);

    var dataElements = xmlObject.getElementsByTagName("dataElement");

    for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = dataElements[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var dataElementName = dataElements[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        if ( listContains(selectedDataElements, id) == false )
        {
            var option = document.createElement("option");
            option.value = id;
            option.text = dataElementName;
            option.title = dataElementName;
            availableDataElements.add(option, null);
        }
    }
    
// If the list of available dataelements is empty, an empty placeholder will be added
//addOptionPlaceHolder( availableDataElements );
}// getDataElementsReceived end


function getDataElementGroupsReceived( xmlObject )
{
    var availableDataElementGroups = document.getElementById("availableDataElementGroups");
    var selectedDataElementGroups = document.getElementById("selectedDataElementGroups");

    clearList(availableDataElementGroups);

    var dataElementGroups = xmlObject.getElementsByTagName("dataElementGroup");

    for ( var i = 0; i < dataElementGroups.length; i++ )
    {
        var id = dataElementGroups[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var dataElementGroupName = dataElementGroups[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        if ( listContains(selectedDataElementGroups, id) == false )
        {
            var option = document.createElement("option");
            option.value = id;
            option.text = dataElementGroupName;
            option.title = dataElementGroupName;
            availableDataElementGroups.add(option, null);
        }
    }
    
    // If the list of available dataelement groups is empty, an empty placeholder will be added
    addOptionPlaceHolder( availableDataElementGroups );
}// getDataElementGroupsReceived end


function getIndicators()
{
    var indicatorGroupList = document.getElementById( "indicatorGroupId" );
    var indicatorGroupId = indicatorGroupList.options[ indicatorGroupList.selectedIndex ].value;
  
    if ( indicatorGroupId != null )
    {
        //var url = "getIndicators.action?id=" + indicatorGroupId;
    
        var request = new Request();
        request.setResponseTypeXML( 'indicator' );
        request.setCallbackSuccess( getIndicatorsReceived );
        //request.send( url );

        var requestString = "getIndicators.action";
        var params = "id=" + indicatorGroupId;
        request.sendAsPost( params );
        request.send( requestString );
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
  
    // If the list of available indicators is empty, an empty placeholder will be added
    addOptionPlaceHolder( availableIndicators );
}