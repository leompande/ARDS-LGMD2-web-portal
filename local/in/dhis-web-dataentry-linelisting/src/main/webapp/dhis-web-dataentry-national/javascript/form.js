	function addLLUUIDSPENewRow()
	{
		if(nextFlag == 0)
		{
		   nextFlag = 1;
		}
		else
		{
		    return;
		}
		
		nextRecordNo++;
			
		var tbl = document.getElementById("tblGrid");
		var lastRow = tbl.rows.length;
		var newRow = tbl.insertRow(lastRow);		
            
    	var oCell = newRow.insertCell(0);
    	oCell.innerHTML = "<select name='entryfield' id='value[1040].value:value["+nextRecordNo+"].value' onchange='saveLLbirthValue(1040,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center' onblur='addLLUUIDSPENewRow()'>"+orgUnitInfo+"</select>";    	
    
    	oCell = newRow.insertCell(1);
    	oCell.innerHTML = "<input name='entryfield' id='value[1041].value:value["+nextRecordNo+"].value' type='text' value='' onchange='saveLLbirthValue(1041,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:10em text-align:center'> <img src='../images/calendar_icon.gif' width='16' height='16' id='getvalue[1041].value:value["+nextRecordNo+"].value' cursor: pointer;' title='$i18n.getString( 'date_selector' )' onmouseover='this.style.background='orange';' onmouseout='this.style.background='''>";
    		    	        	
    	oCell = newRow.insertCell(2);
    	oCell.innerHTML = "<input name='entryfield' id='value[1042].value:value["+nextRecordNo+"].value' type='text' value=' ' onchange='saveLLbirthValue(1042,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:20em text-align:center'>";

    	oCell = newRow.insertCell(3);
    	oCell.innerHTML = "<select name='entryfield' id='value[1043].value:value["+nextRecordNo+"].value' onchange='saveLLbirthValue(1043,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'><option value='NONE' selected>---</option><option value='Y'>YES</option><option value='N'>NO</option><option value='NK'>NOT KNOWN</option></select>";

		var inputFieldVal1 = "value[1041].value:value["+nextRecordNo+"].value";
		var butVal1 = "getvalue[1041].value:value["+nextRecordNo+"].value";
    	Calendar.setup({inputField:inputFieldVal1,ifFormat:"$i18n.getString('format.date.label')",button:butVal1});
    	    	
	}



	function addLLMDNewRow()
	{
		if(nextFlag == 0)
		{
		   nextFlag = 1;
		}
		else
		{
		    return;
		}
		
		nextRecordNo++;
			
		var tbl = document.getElementById("tblGrid");
		var lastRow = tbl.rows.length;
		var newRow = tbl.insertRow(lastRow);		
            
    	var oCell = newRow.insertCell(0);
    	oCell.innerHTML = "<input name='entryfield' id='value[1032].value:value["+nextRecordNo+"].value' type='text' value=' ' onchange='saveLLbirthValue(1032,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' onblur='addLLMDNewRow()' style='width:100% text-align:center'>";
    
    	oCell = newRow.insertCell(1);
    	oCell.innerHTML = "<input name='entryfield' id='value[1033].value:value["+nextRecordNo+"].value' type='text' value=' ' onchange='saveLLbirthValue(1033,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'>";

    	oCell = newRow.insertCell(2);
    	oCell.innerHTML = "<input name='entryfield' id='value[1034].value:value["+nextRecordNo+"].value' type='text' value=' ' onchange='saveLLbirthValue(1034,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'>";
    
    	oCell = newRow.insertCell(3);
    	oCell.innerHTML = "<select name='entryfield' id='value[1035].value:value["+nextRecordNo+"].value' onchange='saveLLbirthValue(1035,"+nextRecordNo+")' onkeypress='return keyPress(event, his)' style='width:100% text-align:center'><option value='NONE' SELECTED>---</option><option value='DELIVERY'>DELIVERY</option><option value='PREGNANCY'>PREGNANCY</option><option value='B1WEEK'>WITHIN 1 WEEK</option><option value='B42DAYS'>1 WEEK - 42 DAYS</option></select>";    	

    	oCell = newRow.insertCell(4);
    	oCell.innerHTML = "<select name='entryfield' id='value[1036].value:value["+nextRecordNo+"].value' onchange='saveLLbirthValue(1036,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'><option value='NONE'>---</option><option value='HOME'>HOME</option><option value='SC'>SUBCENTER</option><option value='PHC'>PHC</option><option value='CHC'>CHC</option><option value='MC'>MEDICAL COLLEGE</option></select>";
    		    	        	
    	oCell = newRow.insertCell(5);
    	oCell.innerHTML = "<select name='entryfield' id='value[1037].value:value["+nextRecordNo+"].value' onchange='saveLLbirthValue(1037,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'><option value='NONE' SELECTED>---</option><option value='UNTRAINED'>UNTRAINED</option><option value='TRAINED'>TRAINED</option><option value='ANM'>ANM</option><option value='NURSE'>NURSE</option><option value='DOCTOR'>DOCTOR</option><option value='OTHERS'>OTHERS</option></select>";    	    	

    	oCell = newRow.insertCell(6);
    	oCell.innerHTML = "<select name='entryfield' id='value[1038].value:value["+nextRecordNo+"].value' onchange='saveLLbirthValue(1038,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'><option value='NONE' SELECTED>---</option><option value='ABORTION'>ABORTION</option><option value='OPL'>OBSTRUCTED/PROLONGED LABOUR</option><option value='FITS'>FITS</option><option value='SH'>SEVERE HYPERTENSION</option><option value='BBCD'>BLEEDING BEFORE CHILD DELIVERY</option><option value='BACD'>BLEEDING AFTER CHILD DELIVERY</option><option value='HFBD'>HIGH FEVER BEFORE DELIVERY</option><option value='HFAD'>HIGH FEVER AFTER DELIVERY</option><option value='OTHERS'>ANY OTHERS</option><option value='NK'>NOT KNOWN</option></select>";    	    	

    	oCell = newRow.insertCell(7);
    	oCell.innerHTML = "<select name='entryfield' id='value[1039].value:value["+nextRecordNo+"].value' onchange='saveLLbirthValue(1039,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'><option value='NONE' SELECTED>---</option><option value='Y'>YES</option><option value='N'>NO</option><option value='NK'>NOT KNOWN</option></select>";    	    	

	}


	function addLLDNewRow()
	{
		if(nextFlag == 0)
		{
		   nextFlag = 1;
		}
		else
		{
		    return;
		}
		
		nextRecordNo++;
			
		var tbl = document.getElementById("tblGrid");
		var lastRow = tbl.rows.length;
		var newRow = tbl.insertRow(lastRow);		
            
    	var oCell = newRow.insertCell(0);
    	oCell.innerHTML = "<input name='entryfield' id='value[1027].value:value["+nextRecordNo+"].value' type='text' value=' ' onchange='saveLLbirthValue(1027,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' onblur='addLLDNewRow()' style='width:100% text-align:center'>";
    
    	oCell = newRow.insertCell(1);
    	oCell.innerHTML = "<input name='entryfield' id='value[1028].value:value["+nextRecordNo+"].value' type='text' value=' ' onchange='saveLLbirthValue(1028,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'>";
    
    	oCell = newRow.insertCell(2);
    	oCell.innerHTML = "<select name='entryfield' id='value[1029].value:value["+nextRecordNo+"].value' onchange='saveLLbirthValue(1029,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'><option value='NONE' selected>---</option><option value='M'>Male</option><option value='F'>Female</option></select>";    	

    	oCell = newRow.insertCell(3);
    	oCell.innerHTML = "<select name='entryfield' id='value[1030].value:value["+nextRecordNo+"].value' onchange='saveLLbirthValue(1030,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'><option value='NONE' selected>---</option><option value='B1DAY'>BELOW 1 DAY</option><option value='B1WEEK'>1 DAY - 1 WEEK</option><option value='B1MONTH'>1 WEEK - 1 MONTH</option><option value='B1YEAR'>1 MONTH - 1 YEAR</option><option value='B5YEAR'>1 YEAR - 5 YEARS</option><option value='O5YEAR'>OVER 5 YEARS</option></select>";
    		    	        	
    	oCell = newRow.insertCell(4);
    	oCell.innerHTML = "<select name='entryfield' id='value[1031].value:value["+nextRecordNo+"].value' onchange='saveLLbirthValue(1031,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'><option value='NONE'>---</option><option value='ASPHYXIA'>ASPHYXIA</option><option value='FITS'>FITS</option><option value='SEPSIS'>SEPSIS</option><option value='LOWBIRTHWEIGH'>LOWBIRTHWEIGH</option><option value='NK'>NOT KNOWN</option></select>";    	    	
	}



function addLLBNewRow()
	{
	
		if(nextFlag == 0)
		{
		   nextFlag = 1;
		}
		else
		{
		    return;
		}
		
		nextRecordNo++;
			
		var tbl = document.getElementById("tblGrid");
		var lastRow = tbl.rows.length;
		var newRow = tbl.insertRow(lastRow);		
            
    	var oCell = newRow.insertCell(0);
    	oCell.innerHTML = "<input name='entryfield' id='value[1020].value:value["+nextRecordNo+"].value' type='text' value=' ' onchange='saveLLbirthValue(1020,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' onblur='addNewRow()' style='width:100% text-align:center'>";
    
    	oCell = newRow.insertCell(1);
    	oCell.innerHTML = "<input name='entryfield' id='value[1021].value:value["+nextRecordNo+"].value' type='text' value=' ' onchange='saveLLbirthValue(1021,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'>";
    
    	oCell = newRow.insertCell(2);
    	//oCell.innerHTML = "<input name='entryfield' id='value[1022].value:value["+nextRecordNo+"].value' type='text' value=' ' onchange='saveLLbirthValue(1022,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'>";
    	oCell.innerHTML = "<select name='entryfield' id='value[1022].value:value["+nextRecordNo+"].value' onchange='saveLLbirthValue(1022,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'><option value='NONE' selected>---</option><option value='M'>Male</option><option value='F'>Female</option></select>";

    	oCell = newRow.insertCell(3);
    	//oCell.innerHTML = "<input name='entryfield' id='value[1023].value:value["+nextRecordNo+"].value' type='text' value=' ' onchange='saveLLbirthValue(1023,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'>";
    	oCell.innerHTML = "<input name='entryfield' id='value[1023].value:value["+nextRecordNo+"].value' type='text' value='' onchange='saveLLbirthValue(1023,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:10em text-align:center'> <img src='../images/calendar_icon.gif' width='16' height='16' id='getvalue[1023].value:value["+nextRecordNo+"].value' cursor: pointer;' title='$i18n.getString( 'date_selector' )' onmouseover='this.style.background='orange';' onmouseout='this.style.background='''>";
    	
    	oCell = newRow.insertCell(4);
    	oCell.innerHTML = "<input name='entryfield' id='value[1024].value:value["+nextRecordNo+"].value' type='text' value=' ' onchange='saveLLbirthValue(1024,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'>";

    	oCell = newRow.insertCell(5);
    	//oCell.innerHTML = "<input name='entryfield' id='value[1025].value:value["+nextRecordNo+"].value' type='text' value=' ' onchange='saveLLbirthValue(1025,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'>";
    	oCell.innerHTML = "<select name='entryfield' id='value[1025].value:value["+nextRecordNo+"].value' onchange='saveLLbirthValue(1025,"+nextRecordNo+")' onkeypress='return keyPress(event, this)' style='width:100% text-align:center'><option value='NONE' selected>---</option><option value='Y'>YES</option><option value='N'>NO</option><option value='NK'>NOT KNOWN</option></select>";
    	
		var inputFieldVal1 = "value[1023].value:value["+nextRecordNo+"].value";
		var butVal1 = "getvalue[1023].value:value["+nextRecordNo+"].value";
    	Calendar.setup({inputField:inputFieldVal1,ifFormat:"$i18n.getString('format.date.label')",button:butVal1});
	}


var ocId = -1;
window.onload = function () 
{
	var inputs = document.getElementsByName( "entryfield" ) 

	for ( var i = 0, input; input = inputs[i]; i++ )
	{
		$('#'+ input.name).focus(function() {
			valueFocus(input);
		});
		//input.addEventListener('focus', valueFocus, false);
	}

    var selects = document.getElementsByName( "entryselect" );

	for ( var i = 0, select; select = selects[i]; i++ )
	{
		select.addEventListener('focus', valueFocus, false);
	}

}

function viewHistory( dataElementId )
{
    window.open( 'viewHistory.action?dataElementId=' + dataElementId + '&optionComboId=' + ocId, '_blank', 'width=560,height=550,scrollbars=yes' );
}

/**
 * Display data element name in selection display when a value field recieves
 * focus.
 * XXX May want to move this to a separate function, called by valueFocus.
 * @param e focus event
 * @author Hans S. Tommerholt
 */
var customDataEntryFormExists = "false";
function valueFocus(e) 
{
	//Retrieve the data element id from the id of the field
	//var baseId = e.target.id;	
	var baseId = e.id;
	
	var opId = baseId;
	var str = baseId;
	
	if(	baseId.indexOf(':') != -1 )
	{
		opId = baseId.substr( baseId.indexOf(':')+1, baseId.length );
		str = baseId.substr( 0, baseId.indexOf(':') );
	}
	
	var match1 = /.*\[(.*)\]/.exec(str); //value[-dataElementId-]	
	var match2 = /.*\[(.*)\]/.exec(opId); //value[-optionComboId-]
	
	if ( ! match1 )
	{				
		return;
	}

	deId = match1[1];
	ocId = match2[1];	
	
	//window.alert('value[option'+ocId+'].name');
	
	//Get the data element name
	var nameContainer = document.getElementById('value['+deId+'].name');
	var opCbContainer = document.getElementById('value[option'+ocId+'].name');
	var minContainer = document.getElementById('value['+deId+'].min');
	var maxContainer = document.getElementById('value['+deId+'].max');
	
	if ( ! nameContainer )
	{		
		return;
	}

	var name = '';
	var optionName = '';
	
	var as = nameContainer.getElementsByTagName('a');

	if ( as.length > 0 )	//Admin rights: Name is in a link
	{
		name = as[0].firstChild.nodeValue;
	} 
	else 
	{
		name = nameContainer.firstChild.nodeValue;
	}
	
	if( opCbContainer )
	{
		optionName = opCbContainer.firstChild.nodeValue;		
	}
	
    var curDeSpan = document.getElementById('currentDataElement');
    //var curOpSpan = document.getElementById('currentOptionCombo');
    
    curDeSpan.firstChild.nodeValue = name;
    //curOpSpan.firstChild.nodeValue = optionName;
    document.getElementById("currentOptionCombo").innerHTML  = optionName;
    
	/*var name = '';
	var as = nameContainer.getElementsByTagName('a');

	if ( as.length > 0 )	//Admin rights: Name is in a link
	{
		name = as[0].firstChild.nodeValue;
	} 
	else 
	{
		if(customDataEntryFormExists == "true")
		{
		    name = nameContainer.firstChild.nodeValue;
		    name += " - "+minContainer.firstChild.nodeValue;
		    name += " - "+maxContainer.firstChild.nodeValue; 
		}
		else
		{
		    name = nameContainer.firstChild.nodeValue;
		}
	}

    var curDeSpan = document.getElementById('currentDataElement');
    curDeSpan.firstChild.nodeValue = name;*/  
	
}

function keyPress( event, field )
{
    var key = 0;
    if ( event.charCode )
    {
    	key = event.charCode; /* Safari2 (Mac) (and probably Konqueror on Linux, untested) */
    }
    else
    {
		if ( event.keyCode )
		{
			key = event.keyCode; /* Firefox1.5 (Mac/Win), Opera9 (Mac/Win), IE6, IE7Beta2, Netscape7.2 (Mac) */
		}
		else
		{
			if ( event.which )
			{
				key = event.which; /* Older Netscape? (No browsers triggered yet) */
			}
	    }
	}
    
    if ( key == 13 ) /* CR */
    {
		nextField = getNextEntryField( field );
        if ( nextField )
        {
            nextField.focus(); /* Does not seem to actually work in Safari, unless you also have an Alert in between */
        }
        return true;
    }
    
    /* Illegal characters can be removed with a new if-block and return false */
    return true;
}

function getNextEntryField( field )
{
    var inputs = document.getElementsByName( "entryfield" );
    
    // Simple bubble sort
    for ( i = 0; i < inputs.length - 1; ++i )
    {
        for ( j = i + 1; j < inputs.length; ++j )
        {
            if ( inputs[i].tabIndex > inputs[j].tabIndex )
            {
                tmp = inputs[i];
                inputs[i] = inputs[j];
                inputs[j] = tmp;
            }
        }
    }
    
    i = 0;
    for ( ; i < inputs.length; ++i )
    {
        if ( inputs[i] == field )
        {
            break;
        }
    }
    
    if ( i == inputs.length - 1 )
    {
    	// No more fields after this:
    	return false;
    	// First field:
        //return inputs[0];
    }
    else
    {
        return inputs[i + 1];
    }
}

/**
 * Set min/max limits for dataelements that has one or more values, and no 
 * manually entred min/max limits.
 */
function SetGeneratedMinMaxValues()
{
    this.save = function()
    {
       /*
    	var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'minmax' );
        //request.send( 'minMaxGeneration.action' );

        var requestString = "minMaxGeneration.action";
        request.send( requestString );
        */
    	$.post("minMaxGeneration.action",
    			{
    		
    			},
    			function (data)
    			{
    				handleResponse(data);
    				//handleHttpError(data);
    			},'xml');

    };
    
    function handleResponse( rootElement )
    {
        var dataElements = rootElement.getElementsByTagName( 'dataelement' );
        
        for( i = 0; i < dataElements.length; i++ )
        {
            var id = getElementValue( dataElements[i], 'dataelementId' );
            setFieldValue('value[' + id + '].min', getElementValue( dataElements[i], 'minLimit'));
            setFieldValue('value[' + id + '].max', getElementValue( dataElements[i], 'maxLimit'));
        }
        
    }
    
    function handleHttpError( errorCode )
    {
        window.alert( i18n_saving_minmax_failed_error_code + '\n\n' + errorCode );
    }
    
    function setFieldValue( fieldId, value )
    {
        document.getElementById( fieldId ).innerHTML = value;
    }
    
    function getElementValue( parentElement, childElementName )
    {
        var textNode = parentElement.getElementsByTagName( childElementName )[0].firstChild;
        
        if ( textNode )
        {
            return textNode.nodeValue;
        }
        else
        {
            return null;
        }
    }    
}

function generateMinMaxValues()
{    
    var setGeneratedMinMaxValues = new SetGeneratedMinMaxValues();
    setGeneratedMinMaxValues.save();
}