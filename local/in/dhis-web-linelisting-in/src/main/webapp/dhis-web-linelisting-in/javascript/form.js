



function addLLBNewRow()
{
    var tbl = document.getElementById("tblGrid");
    lastRow = tbl.rows.length;
    curRow = lastRow + 1;
    var newRow = tbl.insertRow(lastRow);
    var oCell = "";
    var i=1;
    oCell = newRow.insertCell(0);
    oCell.innerHTML = '<label id="sr.no">'+lastRow+'</label>';

    for( var element in jsllElementOptions)
    {
        var row = lastRow-1;
        var tempStr = element + ":"+lastRow ;
        if(tempStr==null && tempStr=="")
        {
            alert("Insert value into all cells");
            break;
        }
    }
    for( var element in jsllElementOptions)
    {
        oCell = newRow.insertCell(i);

        tempStr = element + ":"+lastRow ;

        date = "getDate:"+tempStr;
        var options = jsllElementOptions[element];
        var type = jsllElementPtype[element];
        var inputFieldVal = "";
         var butVal = "";
         //alert("jsllElementSize = "+jsllElementSize);
        if( options == null || options.length == 0 )
        {
            if(type=='text')
            {

                if(i==jsllElementSize)
                {
                    oCell.innerHTML = '<input type="text" name="'+tempStr+'" id = "'+tempStr+'" style="width:10em" onchange="addLLBNewRow()" />';
                }
                else
                {
                    oCell.innerHTML = '<input type="text" name="'+tempStr+'" id = "'+tempStr+'" style="width:10em"/>';
                }
            }
            else if(type=='calender')
            {
                if(i==jsllElementSize)
                {
                    oCell.innerHTML = "<input type='text' id='"+tempStr+"' name='"+tempStr+"' onchange='addLLBNewRow()' style='width:10em'> <img src='../images/calendar_icon.gif' width='16' height='16' id='"+date+"' cursor: pointer;' title='$i18n.getString( 'date_selector' )'>";
                    inputFieldVal =  tempStr;
                    butVal = date;

                    Calendar.setup({
                        inputField:inputFieldVal,
                        ifFormat:"%Y-%m-%d",
                        button:butVal
                    });
                }
                else
                {
                    oCell.innerHTML = "<input name='"+tempStr+"' id='"+tempStr+"' type='text' style='width:10em'> <img src='../images/calendar_icon.gif' width='16' height='16' id='"+date+"' cursor: pointer;' title='$i18n.getString( 'date_selector' )'>";
                    inputFieldVal =  tempStr;
                    butVal = date;

                    Calendar.setup({
                        inputField:inputFieldVal,
                        ifFormat:"%Y-%m-%d",
                        button:butVal
                    });
                }
            }
        }
        else
        {
            var tempStr1;

            if(i==jsllElementSize)
                tempStr1 = '<select name="'+tempStr+'" id="'+tempStr+'" onchange="addLLBNewRow()" >';
            else
                tempStr1 = '<select name="'+tempStr+'" id="'+tempStr+'" >';
            //alert(oCell.innerHTML);
            //<select name="+tempStr+" id="+tempStr+">  <option value="NONE" selected="selected">--Select--</option></select>

            for( var j=0; j<options.length; j++ )
            {
                tempStr1 += '<option value="'+options[j]+'">'+options[j]+'</option>'
            }
            tempStr1 += '</select>';

            oCell.innerHTML = tempStr1;

        }


        i++;
    }

}


function removeLLRecord( delRecordNo )
{
    var result = window.confirm( 'Do you want to save new records and delete this record' );

    if ( result )
    {
        document.getElementById('totalRecords').value = (lastRow-1);
        document.getElementById('delRecordNo').value = delRecordNo;

        document.getElementById('LineListDataEntryForm').submit();

    //window.location.href = 'saveandDelValueAction.action?recordId=' + nextRecordNo;
    }

}

