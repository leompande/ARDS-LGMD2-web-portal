/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function disableSubmit(){
    document.getElementById("submitButton").onclick=changePage;
 
}

function changePage(){

    var form2= new document.createElement("bulkSMSPage2");
    document.getElementsByName("send");    
    
   
}
//
function checkCheckBox() {
    if (document.theForm.texttype.checked == false)
    {
        document.theForm.message.disabled = false;
        document.theForm.availableDataElements.disabled = true;
    }
    else
    {
        document.theForm.message.disabled = true;
        document.theForm.availableDataElements.disabled = false;
    }
}



function validateSend() 
{
    var validate=true;
    if (document.theForm.texttype.checked == false)
    {
        if(document.theForm.message.value.length == 0)

        {
            alert("Enter the message");
            validate=false;
        }
    }
    else 
    if(document.theForm.availableDataElements.selectedIndex < 0)
    {   
        alert("Select a data element");
        validate=false;
    }
   
    if (document.theForm.selectedOrganisationUnitGroups.value.length==0)
    {
        alert("Select atleast 1 organization unit group");
        validate=false;
    }
    if(validate==true)
    {
        // Select();
        alert("Sending");
    }
    
}
