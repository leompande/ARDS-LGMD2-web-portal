$(document).ready(function() {
    $("#region").attr("checked", false);
    $("#subRegionRow").hide();
    $("#divisionRow").hide();
    $("#urbanRow").hide();
    $("#relapse").hide();
    $(".mbRows").hide();
    $("#deformityTimeRow").hide();
    $("#jobType").hide();
    $("#dob").datepicker({
        dateFormat: 'dd/mm/yy',
        changeMonth: true,
        changeYear: true,
        yearRange: '-100:+50'
    });
    $("#regDate").datepicker({
        dateFormat: 'dd/mm/yy',
        changeMonth: true,
        changeYear: true,
        yearRange: '-100:+50'
    });
    $("#firstDose").datepicker({
        dateFormat: 'dd/mm/yy',
        changeMonth: true,
        changeYear: true,
        yearRange: '-100:+50'
    });
    $("#dateRFT").datepicker({
        dateFormat: 'dd/mm/yy',
        changeMonth: true,
        changeYear: true,
        yearRange: '-100:+50'
    });
    $("#rcsDate").datepicker({
        dateFormat: 'dd/mm/yy',
        changeMonth: true,
        changeYear: true,
        yearRange: '-100:+50'
    });
    $("#treatmentStartDate").datepicker({
        dateFormat: 'dd/mm/yy',
        changeMonth: true,
        changeYear: true,
        yearRange: '-100:+50'
    });
    $("#treatmentEndDate").datepicker({
        dateFormat: 'dd/mm/yy',
        changeMonth: true,
        changeYear: true,
        yearRange: '-100:+50'
    });

    for(i=1;i<13;i++){
        $("#dose"+i).datepicker({
            dateFormat: 'dd/mm/yy',
            changeMonth: true,
            changeYear: true,
            yearRange: '-100:+50'
        });
    }
});
function showRural(){
    $("#subRegionRow").show();
    $("#subRegion").attr("checked", false);
    $("#urbanRow").hide();
}
function showUrban(){
    $("#subRegionRow").hide();
    $("#divisionRow").hide();
    $("#urbanRow").show();
}

function showFacility(){
    $("#divisionRow").show();
}
function calcIfAdult(){
    if(calculateAge() > 14){
        $("#adultOrChild").html("Age Category: Adult (> 14yrs)");
    } else {
        $("#adultOrChild").html("Age Category: Child (<= 14yrs)");
    }
}
function showRelapse(){
    if($("#caseDetection").attr('value') == 'RELAPSE'){
        $("#relapse").show();
    } else {
        $("#relapse").hide();
    }
}
function showMBDoses(){
    if($("#diseaseType").attr('value')=='MB'){
        $(".mbRows").show();
    }else{
        $(".mbRows input").attr('value','');
        $(".mbRows").hide();
    }
}
function showDeformityTime(){
    $("#deformityTimeRow").show();
    $("#deformityTimeRow input:radio").attr('checked',false);
}
function showJobType(){
    if($("#jobServices").attr('checked')==true){
        $("#jobType").show();
    }else{
        $("#jobType").hide();
    }
}

//TODO: Fix leap year bug
function calculateAge(){
    var dob = $("#dob").attr('value');
    var dayOfBirth = dob.substring(0,2);
    var monthOfBirth = dob.substring(3,5);
    var yearOfBirth = dob.substring(6,10);

    //Variables containg values of current date
    var today = new Date();
    var dayOfToday = parseInt(today.getDate());
    var monthOfToday = today.getMonth() + 1;
    var yearOfToday = today.getFullYear();

    //Variables required to calculate age.
    var yearDiff;
    if (monthOfToday > monthOfBirth){
        yearDiff = yearOfToday - yearOfBirth;
    }
    else if (monthOfToday == monthOfBirth) {
        if((dayOfToday == dayOfBirth) || (dayOfToday > dayOfBirth)) {
            yearDiff = yearOfToday - yearOfBirth;
        }
        else
            yearDiff = yearOfToday - yearOfBirth - 1;
    }
    else {
        yearDiff = yearOfToday - yearOfBirth - 1;
    }
    return yearDiff;
}