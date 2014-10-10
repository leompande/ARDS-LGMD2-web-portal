
// -----------------------------------------------------------------------------
// OrganisationUnit Details
// -----------------------------------------------------------------------------

function getOUDetails(orgUnitIds)
{
    var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;
    
    var request = new Request();
    request.setResponseTypeXML( 'orgunit' );
    request.setCallbackSuccess( getOUDetailsRecevied );
    request.send( url );
   
}

function getOUDetailsRecevied(xmlObject)
{        
    var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var orgUnitId = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        var parentId = orgUnits[ i ].getElementsByTagName("parentid")[0].firstChild.nodeValue;
        var parentName = orgUnits[ i ].getElementsByTagName("parentname")[0].firstChild.nodeValue;
        var trcNumber = orgUnits[ i ].getElementsByTagName("trcnumber")[0].firstChild.nodeValue;
                
        document.getElementById("phc").value = parentName;
        document.getElementById("sc_name").value = orgUnitName;
        
        document.getElementById("parentouIDTB").value = parentId;
        document.getElementById("ouIDTB").value = orgUnitId;      
        
        document.getElementById("patientId").value = trcNumber;
    }           
}


// -----------------------------------------------------------------------------
// Save
// -----------------------------------------------------------------------------

function saveName()
{
    var name = document.getElementById( "patientName" ).value;
    var gender = document.getElementById( "gender" ).value;
    alert(name);
    var url = "index.action?patientName=" + name;
    var request = new Request();
    request.setCallbackSuccess( nameReceived );
    request.send( url );
    request.setResponseTypeXML('');
    return false;
}
function nameReceived( name )
{
    document.getElementById( "patientName" ).disabled = true;
}
