
// -----------------------------------------------------------------------------
// $Id: general.js 4486 2008-02-01 21:23:06Z larshelg $
// -----------------------------------------------------------------------------
// Selection
// -----------------------------------------------------------------------------

function organisationUnitSelected( orgUnits )
{
    window.location.href = 'select.action';
}

selection.setListenerFunction( organisationUnitSelected );

function changeOrder()
{
    window.open( 'getDataElementOrder.action', '_blank', 'width=700,height=500,scrollbars=yes' );
}

// -----------------------------------------------------------------------------
// Comments
// -----------------------------------------------------------------------------

function commentSelected( dataElementId )
{
    var commentSelector = document.getElementById( 'value[' + dataElementId + '].comments' );
    var commentField = document.getElementById( 'value[' + dataElementId + '].comment' );

    var value = commentSelector.options[commentSelector.selectedIndex].value;
    
    if ( value == 'custom' )
    {
        commentSelector.style.display = 'none';
        commentField.style.display = 'inline';
        
        commentField.select();
        commentField.focus();
    }
    else
    {
        commentField.value = value;
        
        saveComment( dataElementId, value );
    }
}

function commentLeft( dataElementId )
{
    var commentField = document.getElementById( 'value[' + dataElementId + '].comment' );
    var commentSelector = document.getElementById( 'value[' + dataElementId + '].comments' );

    saveComment( dataElementId, commentField.value );

    var value = commentField.value;
    
    if ( value == '' )
    {
        commentField.style.display = 'none';
        commentSelector.style.display = 'inline';

        commentSelector.selectedIndex = 0;
    }
}

// -----------------------------------------------------------------------------
// String Trim
// -----------------------------------------------------------------------------

function trim( stringToTrim ) 
{
    return stringToTrim.replace(/^\s+|\s+$/g,"");
}


//-----------------------------------------------------------------------------
//Linelisting ColdChain Related Methods for Validation
//-----------------------------------------------------------------------------
function isColdChainRemarksFieldEntered( )
{
    if(lastRecordNo == -1) return true;

    var dataElementId = 5792;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + lastRecordNo + '].value' );
    var resVal = field.selectedIndex;

    if( resVal <= 0 )
    {
        alert("Please enter Remarks Field in Previous Record" );
        return false;
    }
    else
    {
        return true;
    }
}

function validateColdChainEquipmentField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;

    if( isColdChainRemarksFieldEntered( recordNo ) )
    {
        if(resVal <= 0 || resVal == "---")
        {
            alert("Please Select Equipment");
            field.options[0].selected = true;

            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
        }
    }
    else
    {
        field.options[0].selected = true;
        return false;
    }
}

function isColdChainEquipmentFieldEntered( recordNo )
{
	var dataElementId = 5786;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    //alert( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
    
    if(resVal <= 0 || resVal == "---")
    {
        alert("Please select Equipment");
        field.focus();
        return false
    }
  
    return true;
}

function validateColdChainMachineNumberField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;
    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

    if( isColdChainEquipmentFieldEntered( recordNo ) )
    {
        if(resVal == null || resVal == "" )
        {
            alert("Please enter Machine Number");
            field.value = "";
            //field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
        }
    }
    else
    {
        field.value = "";

        return false;
    }
}

function isColdChainMachineNumberFieldEntered( recordNo )
{
	var dataElementId = 5787;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter Machine Number");
        field.focus();
        field.select();
        return false;
    }
    else
    {
        return true;
    }
}

function validateColdChainMachineWorkingField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;

    if( isColdChainMachineNumberFieldEntered( recordNo ) )
    {
        if(resVal <= 0 || resVal == "---")
        {
            alert("Please Select Whether Working?");
            field.options[0].selected = true;

            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
        	//alert( field.options[ resVal ].value );
        	
        	if( field.options[ resVal ].value == "N" )
        	{
        		document.getElementById( 'value[5789].value:value[' + recordNo + '].value' ).disabled = false;
        	}
        	else
        	{
        		document.getElementById( 'value[5789].value:value[' + recordNo + '].value' ).disabled = true;
        	}
        	saveLLbirthValue( dataElementId, recordNo );
        }
    }
    else
    {
        field.options[0].selected = true;
        return false;
    }
}

function isColdChainMachineWorkingFieldEntered( recordNo )
{
    var dataElementId = 5728;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
    
    if( resVal <= 0 )
    {
        alert("Please select Whether Working?");
        field.focus();
        return false
    }
  
    return true;
}

function validateColdChainDateOfBreakdownField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter Date of BreakDown");
        field.value = "";
        setTimeout(function(){
            field.focus();field.select();
        },2);
        return false;
    }
    
    var currentDate= new Date();
    var mm = currentDate.getMonth()+1;
    var dd = currentDate.getDate();
    ms = new String(mm);
    ds = new String(dd);
    if ( ms.length == 1 ) ms = "0" + ms;
    if ( ds.length == 1 ) ds = "0" + ds;
    var dateString = currentDate.getFullYear() + "-" + ms + "-" + ds;

    var startDateObj = document.getElementById('selStartDate');
    var endDateObj = document.getElementById('selEndDate');
    var startDate = startDateObj.value;
    var endDate = endDateObj.value;

    if( isColdChainMachineWorkingFieldEntered( recordNo ) )
    {
        if(isDate(resVal) )
        {
            if(resVal > dateString)
            {
                alert("The Selected date is greater than Today's Date");
                field.value = "";
                return false;
            }
            if(resVal < startDate || resVal > endDate)
            {
            	alert("The Selected Calendar date is not between Dataentry Month");
                field.value = "";
                return false;
            }
            else
            {
                saveLLbirthValue( dataElementId, recordNo );
            }
        }
        else
        {
            field.value = "";
            field.focus();
        }
    }
    else
    {
        field.value = "";
        return false;
    }
}

function isColdChainDateOfBreakdownFieldEntered( recordNo )
{
	 var dataElementId = 5789;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter Breakdown date");
        field.focus();
        field.select();
        return false;
    }

    return true;
}

function validateColdChainDateOfIntimationField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter Date of Intimation");
        field.value = "";
        setTimeout(function(){
            field.focus();field.select();
        },2);
        return false;
    }
    
    var currentDate= new Date();
    var mm = currentDate.getMonth()+1;
    var dd = currentDate.getDate();
    ms = new String(mm);
    ds = new String(dd);
    if ( ms.length == 1 ) ms = "0" + ms;
    if ( ds.length == 1 ) ds = "0" + ds;
    var dateString = currentDate.getFullYear() + "-" + ms + "-" + ds;

    var startDateObj = document.getElementById('selStartDate');
    var endDateObj = document.getElementById('selEndDate');
    var startDate = startDateObj.value;
    var endDate = endDateObj.value;

    var flag = 1;
    if( document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' ).value == "N" )
    {
    	flag = 2;
    }
    
    if( flag == 1 || isColdChainDateOfBreakdownFieldEntered( recordNo ) )
    {
        if(isDate(resVal) )
        {
            if(resVal > dateString)
            {
                alert("The Selected date is greater than Today's Date");
                field.value = "";
                return false;
            }
            if(resVal < startDate || resVal > endDate)
            {
            	alert("The Selected Calendar date is not between Dataentry Month");
                field.value = "";
                return false;
            }
            else
            {
                saveLLbirthValue( dataElementId, recordNo );
            }
        }
        else
        {
            field.value = "";
            field.focus();
        }
    }
    else
    {
        field.value = "";
        return false;
    }
}

function isColdChainDateOfIntimationFieldEntered( recordNo )
{
	var dataElementId = 5790;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter Intimation date");
        field.focus();
        field.select();
        return false;
    }

    return true;
}

function validateColdChainDateOfRepairField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter Date of Intimation");
        field.value = "";
        setTimeout(function(){
            field.focus();field.select();
        },2);
        return false;
    }
    
    var currentDate= new Date();
    var mm = currentDate.getMonth()+1;
    var dd = currentDate.getDate();
    ms = new String(mm);
    ds = new String(dd);
    if ( ms.length == 1 ) ms = "0" + ms;
    if ( ds.length == 1 ) ds = "0" + ds;
    var dateString = currentDate.getFullYear() + "-" + ms + "-" + ds;

    var startDateObj = document.getElementById('selStartDate');
    var endDateObj = document.getElementById('selEndDate');
    var startDate = startDateObj.value;
    var endDate = endDateObj.value;

    if( isColdChainDateOfIntimationFieldEntered( recordNo ) )
    {
        if(isDate(resVal) )
        {
            if(resVal > dateString)
            {
                alert("The Selected date is greater than Today's Date");
                field.value = "";
                return false;
            }
            if(resVal < startDate || resVal > endDate)
            {
            	alert("The Selected Calendar date is not between Dataentry Month");
                field.value = "";
                return false;
            }
            else
            {
                saveLLbirthValue( dataElementId, recordNo );
            }
        }
        else
        {
            field.value = "";
            field.focus();
        }
    }
    else
    {
        field.value = "";
        return false;
    }
}

function isColdChainDateOfRepairFieldEntered( recordNo )
{
	var dataElementId = 5791;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter Repair date");
        field.focus();
        field.select();
        return false;
    }

    return true;
}

function validateColdChainRemarksField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;
    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

    if( isColdChainDateOfRepairFieldEntered( recordNo ) )
    {
        if(resVal == null || resVal == "" )
        {
            alert("Please enter Remarks");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if (isInteger(resVal))
        {
            alert("For Remarks field Only Digits are not Allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if(havingSpecialChar(resVal))
        {
            alert("For Remarks field special characters are not allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if(isFirstLetter(resVal))
        {
            alert("Remarks field should start with Letter");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
        }
        addLLColdChainNewRow( resVal, 5786, recordNo );
    }
    else
    {
        field.value = "";
        return false;
    }
}

//-----------------------------------------------------------------------------
//Linelisting IDSP Form L Related Methods for Validation
//-----------------------------------------------------------------------------

function isIDSPLOutcomeFieldEntered( )
{
    if(lastRecordNo == -1) return true;

    var dataElementId = 3120;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + lastRecordNo + '].value' );
    var resVal = field.selectedIndex;

    if( resVal <= 0 )
    {
        alert("Please enter Outcome Field in Previous Record" );
        return false;
    }
    else
    {
        return true;
    }
}

function validateIDSPLNameField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;
    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

    if( isIDSPLOutcomeFieldEntered() )
    {
        if(resVal == null || resVal == "" )
        {
            alert("Please enter name");
            field.value = "";
            //field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if (isInteger(resVal))
        {
            alert("For Name field Only Digits are not Allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if(havingSpecialChar(resVal))
        {
            alert("For Name field special characters are not allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if(isFirstLetter(resVal))
        {
            alert("Name field should start with Letter");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    }
    else
    {
        field.value = "";

        return false;
    }
}


function isIDSPLNameFiledEntered( recordNo )
{
    var dataElementId = 1053;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter NAME");
        field.focus();
        field.select();
        return false;
    }
    else
    {
        return true;
    }
}

function validateIDSPLAgeField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;
    var resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
  
    if( isIDSPLNameFiledEntered( recordNo ) )
    {
        if(resVal == null || resVal == "" )
        {
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
	    
            return false;
        }
  
        if( isInteger( resVal) )
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
        else
        {
            alert("Please enter valid AGE");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
	    
            return false;
        }
    }
    else
    {
        field.value = "";
    
        return false;
    }
}

function isIDSPLAgeFiledEntered( recordNo )
{
    var dataElementId = 1055;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter AGE");
        field.focus();
        field.select();
        return false;
    }
    else
    {
        return true;
    }
}

function validateIDSPLSexField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;

    if( isIDSPLAgeFiledEntered( recordNo ) )
    {
        if(resVal <= 0 || resVal == "---")
        {
            alert("Please Select Sex");
            field.options[0].selected = false;

            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
        }
    }
    else
    {
        field.options[0].selected = true;

        return false;
    }
}

function isIDSPLSexFieldEntered( recordNo )
{
    var dataElementId = 1054;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
    
    if(resVal <= 0 || resVal == "---")
    {
        alert("Please enter SEX ");
        field.focus();
        return false
    }
  
    return true;
}

function validateIDSPLAddressField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;
    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

    if(isIDSPLSexFieldEntered( recordNo ))
    {
        if(resVal == null || resVal == "" )
        {
            alert("Please enter Address");
            field.value = "";
            setTimeout(function(){
                field.focus();
                field.select();
            },2);
            return false;
        }
        if (isInteger(resVal))
        {
            alert("For Address field Only Digits are not Allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();
                field.select();
            },2);
            return false;
        }
        if(isVillageNotValid(resVal))
        {
            alert("Please enter valid Address, only . - _ / special chars are allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
        }
    }
    else
    {
        field.value = "";

        return false;
    }
}

function isIDSPLAddressFiledEntered( recordNo )
{
    var dataElementId = 1056;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter ADDRESS");
        field.focus();
        field.select();
        return false;
    }
    else
    {
        return true;
    }
}

function validateIDSPLTestField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;
    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

    if( isIDSPLAddressFiledEntered( recordNo ) )
    {
        if(resVal == null || resVal == "" )
        {
            alert("Please enter Test");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if (isInteger(resVal))
        {
            alert("For Test field Only Digits are not Allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if(havingSpecialChar(resVal))
        {
            alert("For Test field special characters are not allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if(isFirstLetter(resVal))
        {
            alert("Test field should start with Letter");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    }
    else
    {
        field.value = "";
        return false;
    }
}

function isIDSPLTestFieldEntered( recordNo )
{
    var dataElementId = 1057;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter TEST");
        field.focus();
        field.select();
        return false;
    }
    else
    {
        return true;
    }
}

function validateIDSPLDaignosisField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;
    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

    if( isIDSPLTestFieldEntered( recordNo ) )
    {
        if(resVal == null || resVal == "" )
        {
            alert("Please enter Diagnosis");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if (isInteger(resVal))
        {
            alert("For Diagnosis field Only Digits are not Allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if(havingSpecialChar(resVal))
        {
            alert("For Diagnosis field special characters are not allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if(isFirstLetter(resVal))
        {
            alert("Diagnosis field should start with Letter");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
        }
        
    }
    else
    {
        field.value = "";
        return false;
    }
}

function isIDSPLDaignosisFieldEntered( recordNo )
{
    var dataElementId = 1058;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    //alert( field + '---' + 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    
    if( resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter Diagnosis Field" );
        return false;
    }
    else
    {
        return true;
    }
}

function validateIDSPLOutcomeField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;

    if( isIDSPLDaignosisFieldEntered( recordNo ) )
    {
        if( resVal <= 0 )
        {
            alert("Please Select Outcome");
            field.options[0].selected = false;

            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            addLLIDSPLNewRow( resVal, 1053, recordNo );
        }
    }
    else
    {
        field.options[0].selected = true;

        return false;
    }
}

// -----------------------------------------------------------------------------
// Linelisting LiveBirth Related Methods for Validation
// -----------------------------------------------------------------------------

function isLLBNameFiledEntered( recordNo )
{
    var dataElementId = 1020;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter NAME");
        field.focus();
        field.select();
        return false;
    }
 
    else
    {
        return true;
    }
}

function isLLBVillageFiledEntered( recordNo )
{
    var dataElementId = 1021;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter VILLAGE");
        field.focus();
        field.select();
        return false;
    }
    else
    {
        return true;
    }
}

function isLLBSexFieldEntered( recordNo )
{
	
    var dataElementId = 1022;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
    
    if(resVal <= 0 || resVal == "---")
    {
        alert("Please enter SEX ");
        field.focus();
        return false
    }
  
    return true;
}


function isLLBDOBFieldEntered( recordNo )
{
    var dataElementId = 1023;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;


    /*if(resVal > currentDate)
  {
  	alert("Please Enter Date Less than Today's Date!");
  	return false;
  }*/

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter DOB");
        field.focus();
        field.select();
        return false;
    }
    else
    {
        field.focus();
        return true;
    }
}

function isLLBDOBFieldCheck( recordNo )
{
    var dataElementId = 1023;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        return false;
    }
    else
    {
        field.focus();
        return true;
    }
}

function isLLBWeightFiledEntered( recordNo )
{
    var dataElementId = 1024;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter Weight");
        field.focus();
        field.select();
        return false;
    }
    else
    {
        return true;
    }
}

function isLLBBreastFedFiledEntered( )
{
    if(lastRecordNo == -1) return true;

    var dataElementId = 1025;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + lastRecordNo + '].value' );
    var resVal = field.selectedIndex;

    if( resVal <= 0 )
    {
        alert("Please enter BreasFeeding Field in Previous Record" );
        return false;
    }
    else
    {
        return true;
    }
}

function focusDOB( recordNo )
{
    var dataElementId = 1023;
    var dateImage = document.getElementById( 'getvalue[' + dataElementId + '].value:value[' + recordNo + '].value' );
    dateImage.style.background='orange';
    
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    setTimeout(function(){
        field.focus();field.select();
    },2);
}

function checkDOBBetweenMonth(recordNo)
{
    var dataElementId = 1023;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;
    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

    var startDateObj = document.getElementById('selStartDate');
    var endDateObj = document.getElementById('selEndDate');
    var startDate = startDateObj.value;
    var endDate = endDateObj.value;
    if( isLLBDOBFieldCheck( recordNo ) )
    {
        if(resVal < startDate || resVal > endDate)
        {
            alert("Please choose date from the selected month");
            field.value = "";
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            //document.getElementById(recordNo).style.display = 'block';
            //document.getElementById('actions').style.display = 'block';
        }
    }

}

function validateLLBNameField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;
    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

    if(isLLBBreastFedFiledEntered( ))
    {
        if(resVal == null || resVal == "" )
        {
            alert("Please enter name");
            field.value = "";
            //field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if (isInteger(resVal))
        {
            alert("For Name field Only Digits are not Allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if(havingSpecialChar(resVal))
        {
            alert("For Name field special characters are not allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if(isFirstLetter(resVal))
        {
            alert("Name field should start with Letter");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        //saveLLBSexValue( 1022, recordNo );
        }
    }
    else
    {
        field.value = "";

        return false;
    }
}

function validateLLBVillageField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;
    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

    if(isLLBNameFiledEntered( recordNo ))
    {
        if(resVal == null || resVal == "" )
        {
            alert("Please enter village");
            field.value = "";
            // field.focus();
            setTimeout(function(){
                field.focus();
                field.select();
            },2);
            return false;
        }
        if (isInteger(resVal))
        {
            alert("For Village field Only Digits are not Allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();
                field.select();
            },2);
            return false;
        }
        if(isVillageNotValid(resVal))
        {
            alert("Please enter valid Village Name, only . - _ / special chars are allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    }
    else
    {
        field.value = "";

        return false;
    }
}

function validateLLBSexField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;

    if(isLLBVillageFiledEntered( recordNo ))
    {
        //if(resVal <= 0 || resVal == 'NK' || resVal == '---')
        if(resVal <= 0 || resVal == "---")
        {
            alert("Please Select Sex");
            field.options[0].selected = false;
            // field.value = "";

            // field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    }
    else
    {
        field.options[0].selected = true;

        return false;
    }
}

function validateLLBDOBField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

    var currentDate= new Date();
    var mm = currentDate.getMonth()+1;
    var dd = currentDate.getDate();
    ms = new String(mm);
    ds = new String(dd);
    if ( ms.length == 1 ) ms = "0" + ms;
    if ( ds.length == 1 ) ds = "0" + ds;
    var dateString = currentDate.getFullYear() + "-" + ms + "-" + ds;

    var startDateObj = document.getElementById('selStartDate');
    var endDateObj = document.getElementById('selEndDate');
    var startDate = startDateObj.value;
    var endDate = endDateObj.value;

    if( isLLBSexFieldEntered( recordNo ) )
    {
        if( isLLBDOBFieldCheck( recordNo ) )
        {
            if(isDate(resVal) )
            {
                if(resVal > dateString)
                {
                    alert("The Selected date is greater than Today's Date");
                    field.value = "";
                    return false;
                }
                if(resVal < startDate || resVal > endDate)
                {
                    field.value = "";
                    return false;
                }
                else
                {
                    saveLLbirthValue( dataElementId, recordNo );
                //document.getElementById(recordNo).style.display = 'block';
                //document.getElementById('actions').style.display = 'block';
                }
            }
            else
            {
                field.value = "";
                field.focus();
            }
        }
    }
    else
    {
        field.value = "";
        return false;
    }
}

function validateLLBWeightField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;
    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

    if(isLLBDOBFieldEntered( recordNo ))
    {
        if(resVal == null || resVal == "" )
        {
            field.value = "";
            //field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        var resvalue = parseInt(resVal,10);
	  
        //alert("The resvalue is " + resvalue);
        // if (isInteger(resvalue) && resvalue < 500)
        if (resvalue < 500 || resvalue > 9999)
        {
            alert("Please enter weight in Grams between 500 and 9999" );
            field.value = "";
            //field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        
        if (!isInteger(resVal) && resVal.toUpperCase() != "NK")
        {
            alert("Please enter weight in Grams");
            field.value = "";
            //field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            //alert("Value is : " + resVal);
            saveLLbirthValue( dataElementId, recordNo );
            //document.getElementById(recordNo).style.display = 'block';
            //document.getElementById('actions').style.display = 'block';
            return true;
        }
    //else
    //{
    //	;
    //}
    }
    else
    {
        field.value = "";
      
        return false;
    }
}

function validateLLBBreastFedField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
    
    if(isLLBWeightFiledEntered( recordNo ))
    {
        if(resVal <= 0)
        {
            alert("Please Select BreastFed Option");
            field.options[0].selected = true;
      
            // field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
    
            return false;
        }
        //saveLLBValue( dataElementId, recordNo );
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    
        addLLBNewRow( resVal, 1020, recordNo );
    }
    else
    {
        field.options[0].selected = true;
    
        return false;
    }
}

function saveLLBSexValue( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = '';
    
    field.style.backgroundColor = '#ffffcc';
   
    field.options[1].selected = true;
    resVal = field.options[field.selectedIndex].value;
    if(resVal == "NONE") return;
    
    if ( resVal != '' )
    {
        var deIdRecordNo = dataElementId + ":" + recordNo;
        var valueSaver = new ValueSaver( deIdRecordNo, resVal, '#ccffcc' );
        valueSaver.save();        
    }
}

function saveLLBValue( dataElementId, recordNo )
{
    var llbDeIds = Array();
    llbDeIds[0] = 1020;
    llbDeIds[1] = 1021;
    llbDeIds[2] = 1022;
    llbDeIds[3] = 1023;
    llbDeIds[4] = 1024;
    llbDeIds[5] = 1025;
    
    for(i = 0; i < llbDeIds.length; i++)
    {	
        dataElementId = llbDeIds[i];
        var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
        var resVal = '';
    
        field.style.backgroundColor = '#ffffcc';
    
        if(dataElementId == 1022 || dataElementId == 1025 || dataElementId == 1029 || dataElementId == 1030 || dataElementId == 1031 || dataElementId == 1035 || dataElementId == 1036 || dataElementId == 1037 || dataElementId == 1038 || dataElementId == 1039 || dataElementId == 1040 || dataElementId == 1043 || dataElementId == 1046 || dataElementId == 1050 || dataElementId == 1051 || dataElementId == 1052 || dataElementId == 1054)
        {
            resVal = field.options[field.selectedIndex].value;
            if(resVal == "NONE") return;
        }
        else
            resVal = field.value;
    
        if ( resVal != '' )
        {
            var deIdRecordNo = dataElementId + ":" + recordNo;
            var valueSaver = new ValueSaver( deIdRecordNo, resVal, '#ccffcc' );
            valueSaver.save();
        }
    }
}

// -----------------------------------------------------------------------------
// Linelisting LiveBirth Related Methods for Validation
// -----------------------------------------------------------------------------

function isLLDNameFiledEntered( recordNo )
{
    var dataElementId = 1027;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter NAME");
        field.focus();field.select();
        return false;
    }
    else
    {
        return true;
    }
}

function isLLDVillageFiledEntered( recordNo )
{
    var dataElementId = 1028;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter VILLAGE");
        field.focus();field.select();
        return false;
    }
    else
    {
        return true;
    }
}

function isLLDSexFieldEntered( recordNo )
{  
    var dataElementId = 1029;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
  
    if(resVal <= 0)
    {
        alert("Please enter SEX");
        field.focus();field.select();
        return false
    }
  
    return true;
}

function isLLDAgeCategoryFieldEntered( recordNo )
{  
    var dataElementId = 1030;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
  
    if(resVal <= 0)
    {
        alert("Please enter AGE CATEGORY");
        field.focus();field.select();
        return false
    }
  
    return true;
}

function isLLDPCDFieldEntered( )
{ 
    if(lastRecordNo == -1) return true;
	 
    var dataElementId = 1031;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + lastRecordNo + '].value' );
    var resVal = field.selectedIndex;
  
    if(resVal <= 0)
    {
        alert("Please enter CAUSE OF DEATH");
        field.focus();field.select();
        return false
    }
  
    return true;
}

function validateLLDNameField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;
    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
    
    if(isLLDPCDFieldEntered( ))
    {
        if(resVal == null || resVal == "" )
        {
            alert("Please enter name");
            field.value = "";
            // field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
    
            return false;
        }
        if (isInteger(resVal))
        {
            alert("For Name field Only Digits are not Allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if(havingSpecialChar(resVal))
        {
            alert("For Name field special characters are not allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if(isFirstLetter(resVal))
        {
            alert("Name field should start with Letter");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    
    //saveLLBSexValue( 1029, recordNo )
        
    }
    else
    {
        field.value = "";
    
        return false;
    }
}

function validateLLDVillageField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;
    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
      
    if(isLLDNameFiledEntered( recordNo ))
    {
        if(resVal == null || resVal == "" )
        {
            alert("Please enter village");
            field.value = "";
            //field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
    
            return false;
        }
        if (isInteger(resVal))
        {
            alert("For Village field Only Digits are not Allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if(isVillageNotValid(resVal))
        {
            alert("Please enter valid Village Name, only . - _ / special chars are allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    }
    else
    {
        field.value = "";
    
        return false;
    }
}

function validateLLDSexField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
    
    if(isLLDVillageFiledEntered( recordNo ))
    {
        if(resVal <= 0)
        {
            alert("Please Select Sex");
            field.options[0].selected = true;
      
            //field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    }
    else
    {
        field.options[0].selected = true;
    
        return false;
    }
}

function validateLLDAgeCategoryField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
    
    if(isLLDSexFieldEntered( recordNo ))
    {
        if(resVal <= 0)
        {
            alert("Please Select Age Category");
            field.options[0].selected = true;
      
            // field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    }
    else
    {
        field.options[0].selected = true;
    
        return false;
    }
}

function validateLLDPCDField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
    
    if(isLLDAgeCategoryFieldEntered( recordNo ))
    {
        if(resVal <= 0)
        {
            alert("Please enter Cause for Death");
            field.options[0].selected = true;
      
            //field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    
        addLLDNewRow( resVal, 1027, recordNo );
    }
    else
    {
        field.options[0].selected = true;
    
        return false;
    }
}

// -----------------------------------------------------------------------------
// Linelisting Maternal Death Related Methods for Validation
// -----------------------------------------------------------------------------

function isLLMDNameFiledEntered( recordNo )
{
    var dataElementId = 1032;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter NAME");
        field.focus();field.select();
        return false;
    }
    else
    {
        return true;
    }
}

function isLLMDVillageFiledEntered( recordNo )
{
    var dataElementId = 1033;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter VILLAGE");
        field.focus();field.select();
        return false;
    }
    else
    {
        return true;
    }
}

function isLLMDAgeFiledEntered( recordNo )
{
    var dataElementId = 1034;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;

    if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
    {
        alert("Please enter AGE AT DEATH");
        field.focus();field.select();
        return false;
    }
    else
    {
        return true;
    }
}

function isLLMDDuringFieldEntered( recordNo )
{  
    var dataElementId = 1035;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
  
    if(resVal <= 0)
    {
        alert("Please enter DEATH DURING");
        field.focus();field.select();
    
        return false
    }
  
    return true;
}

function isLLMDDeliveryAtFieldEntered( recordNo )
{  
    var dataElementId = 1036;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
  
    if(resVal <= 0)
    {
        alert("Please select DELIVERY AT");
    	field.focus();field.select();
        return false
    }
  
    return true;
}

function isLLMDDeliveryByFieldEntered( recordNo )
{  
    var dataElementId = 1037;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
  
    if(resVal <= 0)
    {
        alert("Please select DELIVERY BY");
    	field.focus();field.select();
        return false
    }
  
    return true;
}

function isLLMDCauseFieldEntered( recordNo )
{  
    var dataElementId = 1038;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
  
    if(resVal <= 0)
    {
        alert("Please select CAUSE FOR DEATH");
    	field.focus();field.select();
        return false
    }
  
    return true;
}

function isLLMDAuditedFieldEntered(  )
{  
    if(lastRecordNo == -1) return true;
	
    var dataElementId = 1039;
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + lastRecordNo + '].value' );
    var resVal = field.selectedIndex;
  
    if(resVal <= 0)
    {
        alert("Please enter IS AUDITED");
    	field.focus();field.select();
        return false
    }
  
    return true;
}

function validateLLMDNameField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;
    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
    
    if(isLLMDAuditedFieldEntered( ))
    {
        if(resVal == null || resVal == "" )
        {
            alert("Please enter name");
            field.value = "";
            // field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if (isInteger(resVal))
        {
            alert("For Name field Only Digits are not Allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if(havingSpecialChar(resVal))
        {
            alert("For Name field special characters are not allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if(isFirstLetter(resVal))
        {
            alert("Name field should start with Letter");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    }
    else
    {
        field.value = "";
    
        return false;
    }
}

function validateLLMDVillageField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;
    resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
      
    if(isLLMDNameFiledEntered( recordNo ))
    {
        if(resVal == null || resVal == "" )
        {
            alert("Please enter village");
            field.value = "";
            // field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
    
            return false;
        }
        if (isInteger(resVal))
        {
            alert("For Village field Only Digits are not Allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        if(isVillageNotValid(resVal))
        {
            alert("Please enter valid Village Name, only . - _ / special chars are allowed");
            field.value = "";
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    }
    else
    {
        field.value = "";
    
        return false;
    }
}

function validateLLMDAgeAtDeathField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.value;
    var resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
  
    if(isLLMDVillageFiledEntered( recordNo ))
    {
        if(resVal == null || resVal == "" )
        {
            field.value = "";
            //  field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
	    
            return false;
        }
  
        if( isInteger( resVal) && parseInt(resVal) >= 15 && parseInt(resVal) <= 50 )
        {
	
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';

        }
        else
        {
            alert("Please enter valid AGE (between 15 - 50)");
            field.value = "";
            //field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
	    
            return false;
        }
    }
    else
    {
        field.value = "";
    
        return false;
    }
}

function validateLLMDDuringField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
    
    if(isLLMDAgeFiledEntered( recordNo ))
    {
        if(resVal <= 0)
        {
            alert("Please Enter Death During");
            field.options[0].selected = true;
      
            // field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
    
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    }
    else
    {
        field.options[0].selected = true;
    
        return false;
    }
}

function validateLLMDDeliveryAtField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
    
    if(isLLMDDuringFieldEntered( recordNo ))
    {
        if(resVal <= 0)
        {
            alert("Please Enter Delivery At");
            field.options[0].selected = true;
      
            // field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
    
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    }
    else
    {
        field.options[0].selected = true;
    
        return false;
    }
}

function validateLLMDDeliveryByField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
    
    if(isLLMDDeliveryAtFieldEntered( recordNo ))
    {
        if(resVal <= 0)
        {
            alert("Please Enter Delivery By");
            field.options[0].selected = true;
      
            //field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    }
    else
    {
        field.options[0].selected = true;
    
        return false;
    }
}

function validateLLMDCauseField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
    
    if(isLLMDDeliveryByFieldEntered( recordNo ))
    {
        if(resVal <= 0)
        {
            alert("Please Enter Cuase for Death");
            field.options[0].selected = true;
      
            // field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    }
    else
    {
        field.options[0].selected = true;
    
        return false;
    }
}

function validateLLMDAuditedField( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );
    var resVal = field.selectedIndex;
    
    if(isLLMDCauseFieldEntered( recordNo ))
    {
        if(resVal <= 0)
        {
            alert("Please enter Is Audited or not");
            field.options[0].selected = true;
      
            //field.focus();
            setTimeout(function(){
                field.focus();field.select();
            },2);
            return false;
        }
        else
        {
            saveLLbirthValue( dataElementId, recordNo );
            document.getElementById(recordNo).style.display = 'block';
            document.getElementById('actions').style.display = 'block';
        }
    
        addLLMDNewRow( resVal, 1032, recordNo );
    }
    else
    {
        field.options[0].selected = true;
    
        return false;
    }
}

// -----------------------------------------------------------------------------
// Date Validation for Linelisting
// -----------------------------------------------------------------------------

// Declaring valid date character, minimum year and maximum year
var dtCh= "-";
var minYear=1900;
var maxYear=2100;

function isInteger(s)
{
    var i;
    for (i = 0; i < s.length; i++)
    {
        // Check that current character is number.
        var c = s.charAt(i);
        if (((c < "0") || (c > "9"))) return false;
    }
    // All characters are numbers.
    return true;
}

function stripCharsInBag(s, bag)
{
    var i;
    var returnString = "";
  
    // Search through string's characters one by one.
    // If character is not in bag, append to returnString.
    for (i = 0; i < s.length; i++)
    {
        var c = s.charAt(i);
        if (bag.indexOf(c) == -1) returnString += c;
    }
  
    return returnString;
}

function daysInFebruary (year)
{
    // February has 29 days in any year evenly divisible by four,
    // EXCEPT for centurial years which are not also divisible by 400.
  
    return (((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28 );
}

function DaysArray(n) 
{
    for (var i = 1; i <= n; i++)
    {
        this[i] = 31
        if (i==4 || i==6 || i==9 || i==11) {
            this[i] = 30
        }
        if (i==2) {
            this[i] = 29
        }
    }
  
    return this
}

function isDate(dtStr)
{
    var daysInMonth = DaysArray(12)
    var pos1=dtStr.indexOf(dtCh)
    var pos2=dtStr.indexOf(dtCh,pos1+1)

    var strYear=dtStr.substring(0,pos1)
    var strMonth=dtStr.substring(pos1+1,pos2)
    var strDay=dtStr.substring(pos2+1)
    var strMonthWithZero = strMonth
    var strDayWithZero = strDay
    //var strMonth=dtStr.substring(0,pos1)
    //var strDay=dtStr.substring(pos1+1,pos2)
    //var strYear=dtStr.substring(pos2+1)

    strYr=strYear
    if (strDay.charAt(0)=="0" && strDay.length>1) strDay=strDay.substring(1)
    if (strMonth.charAt(0)=="0" && strMonth.length>1) strMonth=strMonth.substring(1)
    for (var i = 1; i <= 3; i++)
    {
        if (strYr.charAt(0)=="0" && strYr.length>1) strYr=strYr.substring(1)
    }
    month=parseInt(strMonth)
    day=parseInt(strDay)
    year=parseInt(strYr)
    if (pos1==-1 || pos2==-1 || strMonthWithZero.length<2 || strDayWithZero.length<2 )
    {
        alert("The date format should be : yyyy-mm-dd")
        return false
    }
  
    if (strMonth.length<1 || month<1 || month>12)
    {
        alert("Please enter a valid month")
        return false
    }
    if (strDay.length<1 || day<1 || day>31 || (month==2 && day>daysInFebruary(year)) || day > daysInMonth[month])
    {
        alert("Please enter a valid day")
        return false
    }
    if (strYear.length != 4 || year==0 || year<minYear || year>maxYear)
    {
        alert("Please enter a valid 4 digit year between "+minYear+" and "+maxYear)
        return false
    }
    if (dtStr.indexOf(dtCh,pos2+1)!=-1 || isInteger(stripCharsInBag(dtStr, dtCh))==false)
    {
        alert("Please enter a valid date")
        return false
    }

    return true
}

function isInteger(s)
{
    var n = trim(s);
    return n.length > 0 && !(/[^0-9]/).test(n);
}

function havingSpecialChar(s)
{
    var n = trim(s);
    for(var i=0;i<n.length;i++){
        if(n.length > 0 && !(/^[A-Za-z0-9\s]/).test(n.charAt(i)))
        {
            return true;
        }
    }
    return false;
    
}

function isFirstLetter(s)
{
    var n = trim(s);
    return n.length > 0 && !/^[A-Za-z]+$/i.test(n.charAt(0));
}

function isVillageNotValid(s)
{
    var n = trim(s);
    for(var i=0;i<n.length;i++){
        if(n.length > 0 && !(/^[\w-.,\/\s]/).test(n.charAt(i)))
        {
            return true;
        }
    }
    return false;
}

// -----------------------------------------------------------------------------
// Save
// -----------------------------------------------------------------------------

function saveLLbirthValue( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );    
    var resVal = '';
    
    field.style.backgroundColor = '#ffffcc';
    
    if(dataElementId == 1022 || dataElementId == 1025 || dataElementId == 1029 || dataElementId == 1030 || dataElementId == 1031 || 
    		dataElementId == 1035 || dataElementId == 1036 || dataElementId == 1037 || dataElementId == 1038 || 
    		dataElementId == 1039 || dataElementId == 1040 || dataElementId == 1043 || dataElementId == 1046 || 
    		dataElementId == 1050 || dataElementId == 1051 || dataElementId == 1052 || dataElementId == 1054 || 
    		dataElementId == 5786 || dataElementId == 5788 || dataElementId == 3120
    		)
    {
        resVal = field.options[field.selectedIndex].value;
        if(resVal == "NONE") return;
    }
    else
        resVal = field.value;
    
    if ( resVal != '' )
    {
        var deIdRecordNo = dataElementId + ":" + recordNo;
        var valueSaver = new ValueSaver( deIdRecordNo, resVal, '#ccffcc' );
        valueSaver.save();
    }

}

function saveValue( dataElementId, dataElementName )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value' );
    var type = document.getElementById( 'value[' + dataElementId + '].type' ).innerHTML;
    
    field.style.backgroundColor = '#ffffcc';
    
    if ( field.value != '' )
    {
        if ( type == 'int' )
        {
            if ( !isInt( field.value ))
            {
                field.style.backgroundColor = '#ffcc00';

                window.alert( i18n_value_must_integer + '\n\n' + dataElementName );

                field.select();
                field.focus();

                return;
            }
            else
            {
                var minString = document.getElementById( 'value[' + dataElementId + '].min' ).innerHTML;
                var maxString = document.getElementById( 'value[' + dataElementId + '].max' ).innerHTML;

                if ( minString.length != 0 && maxString.length != 0 )
                {
                    var value = new Number( field.value );
                    var min = new Number( minString );
                    var max = new Number( maxString );

                    if ( value < min )
                    {
                        var valueSaver = new ValueSaver( dataElementId, field.value, '#ffcccc' );
                        valueSaver.save();
                        
                        window.alert( i18n_value_of_data_element_less + '\n\n' + dataElementName );
                        
                        return;
                    }

                    if ( value > max )
                    {
                        var valueSaver = new ValueSaver( dataElementId, field.value, '#ffcccc' );
                        valueSaver.save();
                        
                        window.alert( i18n_value_of_data_element_greater + '\n\n' + dataElementName);
                        
                        return;
                    }
                }
            }
        }
    }

    var valueSaver = new ValueSaver( dataElementId, field.value, '#ccffcc' );
    valueSaver.save();

    if ( type == 'int')
    {
        calculateCDE(dataElementId);
    }

}

function saveBoolean( dataElementId )
{
    var select = document.getElementById( 'value[' + dataElementId + '].boolean' );
    
    select.style.backgroundColor = '#ffffcc';
    
    var valueSaver = new ValueSaver( dataElementId, select.options[select.selectedIndex].value, '#ccffcc' );
    valueSaver.save();
}

function saveComment( dataElementId, commentValue )
{
    var field = document.getElementById( 'value[' + dataElementId + '].comment' );
    var select = document.getElementById( 'value[' + dataElementId + '].comments' );
    
    field.style.backgroundColor = '#ffffcc';
    select.style.backgroundColor = '#ffffcc';
    
    var commentSaver = new CommentSaver( dataElementId, commentValue );
    commentSaver.save();
}

function isInt( value )
{
    var number = new Number( value );
    
    if ( isNaN( number ))
    {
        return false;
    }
    
    return true;
}

// -----------------------------------------------------------------------------
// Saver objects
// -----------------------------------------------------------------------------

function ValueSaver( dataElementId_, value_, resultColor_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';

    var dataElementId = dataElementId_;
    var value = value_;
    var resultColor = resultColor_;
    
    this.save = function()
    {
        /*
    	var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
        //request.send( 'saveValue.action?dataElementId=' + dataElementId + '&value=' + value );

        var requestString = "saveValue.action";
        var params = 'dataElementId=' + dataElementId + '&value=' + value;
        request.sendAsPost( params );
        request.send( requestString );
        */
    	$.post("saveValue.action",
    			{
    				dataElementId : dataElementId,
    				value : value
    			},
    			function (data)
    			{
    				handleResponse(data);
    				//handleHttpError(data);
    			},'xml');
    };
    
    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        
        if ( code == 0 )
        {            
            nextFlag = 0;

            markValue( resultColor );
            
        //var textNode;
            
        //var timestampElement = rootElement.getElementsByTagName( 'timestamp' )[0];
        //var timestampField = document.getElementById( 'value[' + dataElementId + '].timestamp' );
        //textNode = timestampElement.firstChild;
            
        //timestampField.innerHTML = ( textNode ? textNode.nodeValue : '' );
            
        //var storedByElement = rootElement.getElementsByTagName( 'storedBy' )[0];
        //var storedByField = document.getElementById( 'value[' + dataElementId + '].storedBy' );
        //textNode = storedByElement.firstChild;

        //storedByField.innerHTML = ( textNode ? textNode.nodeValue : '' );
        }
        else
        {
            markValue( ERROR );
            window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
        }
    }
    
    function handleHttpError( errorCode )
    {
        markValue( ERROR );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }
    
    function markValue( color )
    {
        //var type = document.getElementById( 'value[' + dataElementId + '].type' ).innerText;
        var element;
        
        /*        if ( type == 'bool' )
        {
            element = document.getElementById( 'value[' + dataElementId + '].boolean' );
        }
        else
        {
            element = document.getElementById( 'value[' + dataElementId + '].value' );
        }
*/
        var temp = new Array();
        temp = dataElementId.split(":");
				
        element = document.getElementById( 'value[' + temp[0] + '].value:value['+ temp[1] +'].value' );
        element.style.backgroundColor = color;
    }
}

function CommentSaver( dataElementId_, value_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';

    var dataElementId = dataElementId_;
    var value = value_;
    
    this.save = function()
    {
        /*
    	var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
        //request.send( 'saveComment.action?dataElementId=' + dataElementId + '&comment=' + value );

        var requestString = "saveComment.action";
        var params = 'dataElementId=' + dataElementId + '&value=' + value;
        request.sendAsPost( params );
        request.send( requestString );
        */
    	$.post("saveComment.action",
    			{
    				dataElementId : dataElementId,
    				comment : value
    			},
    			function (data)
    			{
    				handleResponse(data);
    				//handleHttpError(data);
    			},'xml');
    };
    
    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        
        if ( code == 0 )
        {
            markComment( SUCCESS );
            
        //var textNode;
            
        //var timestampElement = rootElement.getElementsByTagName( 'timestamp' )[0];
        //var timestampField = document.getElementById( 'value[' + dataElementId + '].timestamp' );
        //textNode = timestampElement.firstChild;
            
        //timestampField.innerHTML = ( textNode ? textNode.nodeValue : '' );
            
        //var storedByElement = rootElement.getElementsByTagName( 'storedBy' )[0];
        //var storedByField = document.getElementById( 'value[' + dataElementId + '].storedBy' );
        //textNode = storedByElement.firstChild;

        //storedByField.innerHTML = ( textNode ? textNode.nodeValue : '' );
        }
        else
        {
            markComment( ERROR );
            window.alert( i18n_saving_comment_failed_status_code + '\n\n' + code );
        }
    }
    
    function handleHttpError( errorCode )
    {
        markComment( ERROR );
        window.alert( i18n_saving_comment_failed_error_code + '\n\n' + errorCode );
    }
    
    function markComment( color )
    {
        var field = document.getElementById( 'value[' + dataElementId + '].comment' );
        var select = document.getElementById( 'value[' + dataElementId + '].comments' );

        field.style.backgroundColor = color;
        select.style.backgroundColor = color;
    }
}

// -----------------------------------------------------------------------------
// View history
// -----------------------------------------------------------------------------
/*
function viewHistory( dataElementId )
{
	
    window.open( 'viewHistory.action?dataElementId=' + dataElementId, '_blank', 'width=560,height=550,scrollbars=yes' );
}*/

// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validate()
{
    window.open( 'validate.action', '_blank', 'width=800, height=400, scrollbars=yes, resizable=yes' );
}

// -----------------------------------------------------------------------------
// CalculatedDataElements
// -----------------------------------------------------------------------------

/**
 * Calculate and display the value of any CDE the given data element is a part of.
 * @param dataElementId  id of the data element to calculate a CDE for
 */
function calculateCDE( dataElementId )
{
    var cdeId = getCalculatedDataElement(dataElementId);
  
    if ( ! cdeId )
    {
        return;
    }
    
    var factorMap = calculatedDataElementMap[cdeId];
    var value = 0;
    var dataElementValue;
    
    for ( dataElementId in factorMap )
    {
        dataElementValue = document.getElementById( 'value[' + dataElementId + '].value' ).value;
        value += ( dataElementValue * factorMap[dataElementId] );
    }
    
    document.getElementById( 'value[' + cdeId + '].value' ).value = value;
}

/**
 * Returns the id of the CalculatedDataElement this DataElement id is a part of.
 * @param dataElementId id of the DataElement
 * @return id of the CalculatedDataElement this DataElement id is a part of,
 *     or null if the DataElement id is not part of any CalculatedDataElement
 */
function getCalculatedDataElement( dataElementId )
{
    for ( cdeId in calculatedDataElementMap )
    {
        var factorMap = calculatedDataElementMap[cdeId];

        if ( deId in factorMap )
        {
            return cdeId;
        }

    }

    return null;
}

function calculateAndSaveCDEs()
{	
	/*
    var request = new Request();
    request.setCallbackSuccess( dataValuesReceived );
    request.setResponseTypeXML( 'dataValues' );
    //request.send( 'calculateCDEs.action' );

    var requestString = "calculateCDEs.action";
    request.send( requestString );
    */
	$.post("calculateCDEs.action",
			{
		
			},
			function (data)
			{
				dataValuesReceived(data);
			},'xml');

    
}

function dataValuesReceived( node )
{
    var values = node.getElementsByTagName('dataValue');
    var dataElementId;
    var value;

    for ( var i = 0, value; value = values[i]; i++ )
    {
        dataElementId = value.getAttribute('dataElementId');
        value = value.firstChild.nodeValue;
        document.getElementById( 'value[' + dataElementId + '].value' ).value = value;
    }
}

function showOverlay() {
    var o = document.getElementById('overlay');
    o.style.visibility = 'visible';
    jQuery("#overlay").css({
        "height": jQuery(document).height()
    });
    jQuery("#overlayImg").css({
        "top":jQuery(window).height()/2
    });
}
function hideOverlay() {
    var o = document.getElementById('overlay');
    o.style.visibility = 'hidden';
}

function saveLineListingAggData()
{
    showOverlay();
    /*
    var request = new Request();
    request.setCallbackSuccess( saveLineListingAggDataReceived );
    request.setResponseTypeXML( 'dataValues' );
    //request.send( 'saveLineListingAggData.action' );

    var requestString = "saveLineListingAggData.action";
    request.send( requestString );
    */
	$.post("saveLineListingAggData.action",
			{
		
			},
			function (data)
			{
				saveLineListingAggDataReceived(data);
			},'xml');
    
}

function saveLineListingAggDataReceived( node )
{
    hideOverlay();
	
    alert("Aggregated DataElements Saved");
    var values = node.getElementsByTagName('dataValue');
    //var dataElementId;
    //var value;

   /* for ( var i = 0, value; value = values[i]; i++ )
    {
        dataElementId = value.getAttribute('dataElementId');
        optionComboId = value.getAttribute('optionComboId');
		
        value = value.firstChild.nodeValue;
        //document.getElementById( 'value[' + dataElementId + '].value' + ':' +  'value[' + optionComboId + '].value').value = value;
        //document.getElementById( 'value[' + dataElementId + '].value' ).value = value;
    }*/
	
/*if(i == 0)
	{
		alert("No new records to SAVe Aggregated Dataelements");
	}
	else
	{
		alert("Aggregated DataElements Saved");
	}
	*/

}


function removeLLRecord( nextRecordNo )
{
    var result = window.confirm( 'Do you want to delete this record' );

    if ( result )
    {
       //window.location.href = 'delLLRecord.action?recordId=' + nextRecordNo;
        document.getElementById("recordId").value = nextRecordNo;
        document.delForm.submit();
    }
}
