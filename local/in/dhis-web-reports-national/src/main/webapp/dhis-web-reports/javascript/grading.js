
// get Orgunit groups
function getOrgUnitGroups()
{
    var orgUnitGroupSetList = document.getElementById("orgUnitGroupSetListCB");
    var orgUnitGroupSetId = orgUnitGroupSetList.options[ orgUnitGroupSetList.selectedIndex ].value;
	
    if ( orgUnitGroupSetId != null )
    {
    	/* //var url = "getOrgUnitGroups.action?orgUnitGroupSetId=" + orgUnitGroupSetId;
		
        var request = new Request();
        request.setResponseTypeXML( 'orgunitgroup' );
        request.setCallbackSuccess( getOrgUnitGroupsReceived );
        //request.send( url );

        var requestString = "getOrgUnitGroups.action";
        var params = "orgUnitGroupSetId=" + orgUnitGroupSetId;
        request.sendAsPost( params );
        request.send( requestString ); */
		
		$.post("getOrgUnitGroups.action",
			{
				orgUnitGroupSetId : orgUnitGroupSetId
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

        /* var option = document.createElement("option");
        option.value = id;
        option.text = orgUnitGroupName;
        option.title = orgUnitGroupName;
        orgUnitGroupList.add(option, null); */
		
		$("#orgUnitListCB").append("<option value='"+ id +"' title='" + orgUnitGroupName + "'>" + orgUnitGroupName + "</option>");
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
    }
    clearList(orgUnitList);
}

// Removes slected orgunits from the Organisation List
function remOUFunction()
{
    var index = document.reportForm.orgUnitListCB.options.length;
    var i=0;
    for(i=index-1;i>=0;i--)
    {
        if(document.reportForm.orgUnitListCB.options[i].selected)
            document.reportForm.orgUnitListCB.options[i] = null;
    }
}// remOUFunction end


// View By Option Change
function viewByCBChange()
{
    var viewBy = document.getElementById("viewByCB");
    if(viewBy.options[viewBy.selectedIndex].value == "children")
    {
        var ouListLen = document.reportForm.orgUnitListCB.options.length;
        for(i=0;i<ouListLen;i++)
        {
            document.reportForm.orgUnitListCB.options[0] = null;
        }
    }
}
// Grading Report Form Validations
function formValidations()
{			         
    var selOUListLength = document.reportForm.orgUnitListCB.options.length;
    var startDateValue = document.reportForm.startDate.value;
    var endDateValue = document.reportForm.endDate.value;
			               			            
    if(selOUListLength <= 0) {
        alert("Please Select OrganisationUnit"); return false;
    }
    else if(startDateValue == null || startDateValue== "" || endDateValue == null || endDateValue=="") {
        alert("Please Select Period"); return false;
    }
    else if(startDateValue > endDateValue) {
        alert("Starte Date is Greaterthan End Date"); return false;
    }

    if(document.getElementById( 'ougSetCB' ).checked)
    {
        if(document.reportForm.orgUnitListCB.selectedIndex <= -1)
        {
            alert("Please Select OrganisationUnitGroup(s)");return false;
        }
    }
    else
    {
        for(k=0;k<selOUListLength;k++)
        {
            document.reportForm.orgUnitListCB.options[k].selected = true;
        }
    }
		  
    var viewBy = document.getElementById("viewByCB");
			 
    var form = document.getElementById( 'reportForm' );

    if(viewBy.options[viewBy.selectedIndex].value == 'children')
        form.action = 'generateGradingReport.action';
    else if(viewBy.options[viewBy.selectedIndex].value == 'ImmChildren')
        form.action = 'generateImmChildGradingReport.action';
    else
        form.action = 'generateSelChildGradingReport.action';
	        		 
    form.submit(); 
	
    return true;
}			
