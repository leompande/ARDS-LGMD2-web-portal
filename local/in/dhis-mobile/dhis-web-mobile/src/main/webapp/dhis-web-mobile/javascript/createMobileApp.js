$(document).ready(function() {
    $("#mvnDiv").hide();
    $("#accordion").accordion({
        collapsible: true
    });
    $("#splashUpdate input:radio:first").attr("checked","checked");
    var splashOptions = {
        url: 'splashUpload.action',
        target: '#splashUpdate',
        success: function(){
            $("#splashUpdate input:radio:first").attr("checked","checked");
        }
    }
    $("#splashForm").ajaxForm(splashOptions);

    var dataElementsOptions = {
        url: 'getDataElements.action',
        type: 'POST',
        target: '#dataElementsListDiv'
    /* TODO: Multiple datasets
            success: function(responseXML){
                $("#dataElementsListDiv").append(responseXML);
            }*/
    }
    $("#dataSetForm").ajaxForm(dataElementsOptions);
});

function showPathField(){
    $("#mvnDiv").toggle(200);
}

function submitMvnForm(){
    $("#mvnForm").submit();
}

function submitJarGenerator(){
    var form = document.createElement("form");
    form.style.visibility = "hidden";
    form.setAttribute("method", "post");
    form.setAttribute("action", "jarGenerator.action");

    var aField = document.createElement("input");
    aField.setAttribute("type", "radio");
    aField.setAttribute("name", "splash");
    aField.setAttribute("checked", "checked");
    aField.setAttribute("value", $("input[name=splash]:checked").val());
    form.appendChild(aField);
    var bField = document.createElement("input");
    bField.setAttribute("type", "text");
    bField.setAttribute("name", "mvnBin");
    bField.setAttribute("value", $("#mvnBin").attr("value"));
    form.appendChild(bField);
    var cField = document.createElement("input");
    cField.setAttribute("type", "text");
    cField.setAttribute("name", "selectDataSet");
    cField.setAttribute("value", $("select[name=dataSet]").attr('id'));
    form.appendChild(cField);
    var deStr="";
    $("select[name=dataSet] option").each(function(){
        deStr += "\""+$(this).text()+"\",";
    });
    deStr = deStr.substring(0,deStr.length-1)
    var dField = document.createElement("input");
    dField.setAttribute("type", "text");
    dField.setAttribute("name", "dataElements");
    dField.setAttribute("value", deStr);
    form.appendChild(dField);

    document.body.appendChild(form);
    form.submit();

    $("#genButton").attr('disabled', 'disabled');
}

function addDataSet(){
    $("#dataSetForm").submit();
}