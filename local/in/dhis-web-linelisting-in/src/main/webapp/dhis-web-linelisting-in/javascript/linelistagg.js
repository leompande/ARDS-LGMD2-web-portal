
//--------------------------------------------------------------
// Get Aggregated Dataelements
//--------------------------------------------------------------
function getAggDataElements( )
{
  var degroup = document.getElementById( 'degroup' );
  var degId = degroup.options[ degroup.selectedIndex ].value;
  
  /*
  var requestString = 'getAggDataElements.action?degId=' + degId;

  var request = new Request();
  request.setResponseTypeXML( 'dataelement' );
  request.setCallbackSuccess( getAggDataElementsCompleted );

  request.send( requestString );
  */
	$.post("getAggDataElements.action",
			{
				degId : degId
			},
			function (data)
			{
				getAggDataElementsCompleted(data);
			},'xml');
}

function getAggDataElementsCompleted( dataelementElement )
{
  var de = document.getElementById( 'aggde' );
  
  clearList( de );
  	
  var dataElementList = dataelementElement.getElementsByTagName( 'dataelement' );
 
  for ( var i = 0; i < dataElementList.length; i++ )
    {
        var id = dataElementList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var name = dataElementList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;

        var option = document.createElement("option");
        option.value = id;
        option.text = name;
        option.title = name;
        
        de.add(option, null);       	
    }
  
  getLinelistAggExpression();
}

//--------------------------------------------------------------
//Get LinelistGroup Elements
//--------------------------------------------------------------
function getLinelistElements()
{
	var llgroup = document.getElementById( 'llgroup' );
	var llgId = llgroup.options[ llgroup.selectedIndex ].value;

	/*
	var requestString = 'getLLGroupElements.action?llgId=' + llgId;

	var request = new Request();
	request.setResponseTypeXML( 'lineListElement' );
	request.setCallbackSuccess( getLinelistElementsCompleted );

	request.send( requestString );
	*/
	
	$.post("getLLGroupElements.action",
			{
				llgId : llgId
			},
			function (data)
			{
				getLinelistElementsCompleted(data);
			},'xml');
	
}

function getLinelistElementsCompleted( linelistelementElement )
{
  var lle = document.getElementById( 'linelistGroupDE' );
  
  clearList( lle );
  	
  var llElementList = linelistelementElement.getElementsByTagName( 'lineListElement' );
 
  for ( var i = 0; i < llElementList.length; i++ )
    {
        var id = llElementList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var name = llElementList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;

        var option = document.createElement("option");
        option.value = id;
        option.text = name;
        option.title = name;
        
        lle.add(option, null);       	
    }
}

//--------------------------------------------------------------
// Get LinelistAgg Expression
//--------------------------------------------------------------
function getLinelistAggExpression( )
{
  var aggde = document.getElementById( 'aggde' );
  var aggdeId = aggde.options[ aggde.selectedIndex ].value;

  /*
  var requestString = 'getLinelistAggExpression.action?aggdeId=' + aggdeId;

  var request = new Request();
  request.setResponseTypeXML( 'expression' );
  request.setCallbackSuccess( getLinelistAggExpressionCompleted );

  request.send( requestString );
  */
	$.post("getLinelistAggExpression.action",
			{
				aggdeId : aggdeId
			},
			function (data)
			{
				getLinelistAggExpressionCompleted(data);
			},'xml');
  
}

function getLinelistAggExpressionCompleted( expressionElement )
{
  var expressionTA = document.getElementById( 'expression' );
 
  var onchangeVal = expressionElement.getAttribute( 'onchangeval' );
  
  var expression = expressionElement.firstChild.nodeValue;
 
  expressionTA.value = expression;

  var onchangeCB = document.getElementById( 'onchangeCB' );
  
  if( onchangeVal == "true" )
  {
	  onchangeCB.checked = true;
  }
  else
  {
	  onchangeCB.checked = false;
  }	  
}

//--------------------------------------------------------------
//Get Dataset Periods
//--------------------------------------------------------------
function getdSetPeriods()
{
  var dataSetList = document.getElementById("selectedDataSets");
  var dataSetId = dataSetList.options[ dataSetList.selectedIndex].value;
  
  /*
  var url = "getDataSetPeriods.action?id=" + dataSetId;
    
  var request = new Request();
  request.setResponseTypeXML( 'period' );
  request.setCallbackSuccess( getdSetPeriodsReceived );
  request.send( url );
  */
	$.post("getDataSetPeriods.action",
			{
				id : dataSetId
			},
			function (data)
			{
				getdSetPeriodsReceived(data);
			},'xml');
}	 

function getdSetPeriodsReceived( xmlObject )
{	
	var sDateLB = document.getElementById( "sDateLB" );
	var eDateLB = document.getElementById( "eDateLB" );
	
	var periods = xmlObject.getElementsByTagName( "period" );
	
	for ( var i = 0; i < periods.length; i++)
	{
		var periodType = periods[ i ].getElementsByTagName( "periodtype" )[0].firstChild.nodeValue;
		
		if(i ==0 )
		{
		  if( periodType == curPeriodType )
		  {
			   break;
		  }
		  else
		  {
			   curPeriodType = periodType;
			   clearList( sDateLB );
               clearList( eDateLB );
		  }
		}
		
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

//--------------------------------------------------------------
//Get OrgUnit Details
//--------------------------------------------------------------
function getOrgUDetails(orgUnitIds)
{
	/*
	var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;
	
	var request = new Request();
	request.setResponseTypeXML( 'orgunit' );
	request.setCallbackSuccess( getOrgUDetailsRecevied );
	request.send( url );
	*/
	$.post("getOrgUnitDetails.action",
			{
				orgUnitId : orgUnitIds
			},
			function (data)
			{
				getOrgUDetailsRecevied(data);
			},'xml');
	
}

function getOrgUDetailsRecevied(xmlObject)
{		
	var ouList = document.getElementById("orgUnitListCB");
	var orgUnits = xmlObject.getElementsByTagName("orgunit");
	
	clearList(ouList);
	
    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
		ouList.options[0] = new Option(orgUnitName,id,false,false);
    }
}

//--------------------------------------------------------------
//Linelisting Aggregation Form Validations
//--------------------------------------------------------------
function formValidationsForLLAggMapping()
{
	var selOUListIndex = document.caseAggregationForm.orgUnitListCB.options.length;
	var selDSListSize  = document.caseAggregationForm.selectedDataSets.options.length;
	
    sDateIndex = document.caseAggregationForm.sDateLB.selectedIndex;
    eDateIndex = document.caseAggregationForm.eDateLB.selectedIndex;
    sDateTxt = document.caseAggregationForm.sDateLB.options[sDateIndex].text;
    sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM - y")),"yyyy-MM-dd");
    eDateTxt = document.caseAggregationForm.eDateLB.options[eDateIndex].text;
    eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM - y")),"yyyy-MM-dd");
    if(selOUListIndex <= 0) {alert("Please Select OrganisationUnit"); return false;}
    else if(selDSListSize <= 0) {alert("Please Select DataSet"); return false;}
    else if(sDateIndex < 0) {alert("Please Select Starting Period"); return false;}
    else if(eDateIndex < 0) {alert("Please Select Ending Period"); return false;}
    else if(sDate > eDate) {alert("Starting Date is Greater"); return false;}

	var k=0;
	
    for(k=0;k<selOUListIndex;k++)
    {
    	document.caseAggregationForm.orgUnitListCB.options[k].selected = true;
    }
  	  	 	
  	return true;

} 
	// formValidations Function End	


//-----------------------------------------------------------------
//
//-----------------------------------------------------------------

function updateFocus(x)
{
	holdFocus = x;	
}

function displayLLEInfo()
{
	var linelistGroupDE = document.getElementById( 'linelistGroupDE' );
	
	var data = jQuery(holdFocus).metadata({type:"attr",name:"data"});
	
	if( data.pos == "left" )
	{
		holdFocus.value += linelistGroupDE.options[ linelistGroupDE.selectedIndex ].value;
	}	
	else
	{
		holdFocus.value = "klkjk";
	}
}

//-----------------------------------------------------------------
//
//-----------------------------------------------------------------


function removeRecord( delRecNo )
{
	var tbl = document.getElementById("tblGrid");
	tbl.deleteRow(delRecNo);
	
	var delRecTB = document.getElementById("delRecTB");
	delRecTB.value += delRecNo + ",";
}

function addNewRow( )
{
	var tbl = document.getElementById("tblGrid");
	var lastRow = tbl.rows.length;
	var newRow = tbl.insertRow(lastRow);
	tableRowCount++;
            
    var oCell = newRow.insertCell(0);
    oCell.innerHTML = "<input type='text' id='le"+ tableRowCount +"' name='le"+ tableRowCount +"' data='{pos:\"left\"}' onfocus='updateFocus(this)'>";

    oCell = newRow.insertCell(1);
    oCell.innerHTML = "<select id='operator"+ tableRowCount +"' name='operator"+ tableRowCount +"' onchange=''><option value='NA'>Select</option><option value='less_than'><</option><option value='greater_than'>></option><option value='less_than_equal'><=</option><option value='greater_than_equal'>>=</option><option value='equal'>=</option><option value='not_equal'>!=</option><option value='in'>IN</option><option value='diff'>DIFF</option></select>";
        
    oCell = newRow.insertCell(2);
    oCell.innerHTML = "<input type='text' id='re"+ tableRowCount +"' name='re"+ tableRowCount +"' data='{pos:\"right\"}' onfocus='updateFocus(this)'>";
    
    oCell = newRow.insertCell(3);    	
	oCell.innerHTML = "<select id='andor"+ tableRowCount +"' name='andor"+ tableRowCount +"' onchange='addNewRow()'><option value='NA'>Select</option><option value='and'>AND</option><option value='or'>OR</option></select>";

    oCell = newRow.insertCell(4);
	oCell.innerHTML = "<a href='javascript:removeRecord("+ tableRowCount +")' title='remove' )><img src='../images/delete.png' alt='remove'></a>";
}

function prepareExpression()
{	
	var i = 0;
	var delRecTB = document.getElementById("delRecTB");
	var delRecNos = delRecTB.value.split(",");
	
	var finalExp="NOTHING";
	
	var csRadio = document.getElementById("csRadio");
	
	if(csRadio.value == "sumRadio")
		finalExp = "SUM@";
	else
		finalExp = "COUNT@"; 
	
	for( i = 0; i <= tableRowCount; i++ )
	{				
		var flag = 0;
		for( var j=0; j< delRecNos.lenght; j++ )
		{
			if( delRecNos[j] == i ) { falg =1; break; }
		}
		if(flag == 0 )
		{
			var lCell = document.getElementById("le"+i);
			var opeCell = document.getElementById("operator"+i);
			var rCell = document.getElementById("re"+i);
			var and_orCell = document.getElementById("andor"+i);
		
			if( opeCell.options[ opeCell.selectedIndex ].value == "NA" && ( rCell.value == null || trim(rCell.value) == "" ) )
			{
				finalExp += " SCOND{ ( " + lCell.value + " ) } ";
			}
			else
			{
				finalExp += " COND{ ( " + lCell.value + " ) " + opeCell.options[ opeCell.selectedIndex].value + " ( [" + rCell.value + "] ) } ";	
			}
			
			if( and_orCell.options[ and_orCell.selectedIndex ].value != "NA" ) finalExp += and_orCell.options[ and_orCell.selectedIndex ].value;
		}
	}
	
	var expTA = document.getElementById("expression");
	expTA.value = finalExp;	
}

// -----------------------------------------------------------------------------
// String Trim
// -----------------------------------------------------------------------------

function trim( stringToTrim ) 
{
  return stringToTrim.replace(/^\s+|\s+$/g,"");
}

