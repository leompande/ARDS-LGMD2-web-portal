/**
 * Created by kelvin Mbwilo on 8/17/14.
 */
function preparePeriods(){
    var mounthList = "";
    var yearlist ="";
    var quarterList = "";
    var currentYear = new Date().getFullYear();
    for (var i= 0 ;i < 10 ;i++){
        //defining years
        var year = currentYear - i;
        var year1 = currentYear - (i+1);
        var financial = 'july '+year1+' june '+year;
        yearlist += '<option value="'+year1+'July">'+financial+'</option>';

        //defining quarters
        quarterList += '<option value="'+year+'Q1">Jan to Mar '+year+'</option>';
        quarterList += '<option value="'+year+'Q2">Apr to Jun '+year+'</option>';
        quarterList += '<option value="'+year+'Q3">Jul to Sep '+year+'</option>';
        quarterList += '<option value="'+year+'Q4">Oct to Dec '+year+'</option>';

        //defining months
        mounthList += '<option value="'+year+'01">January '+year+'</option>';
        mounthList += '<option value="'+year+'02">February '+year+'</option>';
        mounthList += '<option value="'+year+'03">March '+year+'</option>';
        mounthList += '<option value="'+year+'04">April '+year+'</option>';
        mounthList += '<option value="'+year+'05">May '+year+'</option>';
        mounthList += '<option value="'+year+'06">June '+year+'</option>';
        mounthList += '<option value="'+year+'07">July '+year+'</option>';
        mounthList += '<option value="'+year+'08">August '+year+'</option>';
        mounthList += '<option value="'+year+'09">September '+year+'</option>';
        mounthList += '<option value="'+year+'10">October '+year+'</option>';
        mounthList += '<option value="'+year+'11">November '+year+'</option>';
        mounthList += '<option value="'+year+'12">December '+year+'</option>';

    }

    $('.periods').multiselect().multiselectfilter();

    $('#years').click(function(){
        $('.periodfilter button').removeClass('btn-primary').addClass('btn-default');
        $(this).removeClass('btn-default').addClass('btn-primary');
        $(".periods").multiselectfilter("destroy");
        $(".periods").multiselect("destroy");
        $('.periods').html(yearlist);
        $('.periods').multiselect().multiselectfilter();
    })
    $('#quarters').click(function(){
        $('.periodfilter button').removeClass('btn-primary').addClass('btn-default');
        $(this).removeClass('btn-default').addClass('btn-primary');
        $(".periods").multiselectfilter("destroy");
        $(".periods").multiselect("destroy");
        $('.periods').html(quarterList);
        $('.periods').multiselect().multiselectfilter();
    })
    $('#mounths').click(function(){
        $('.periodfilter button').removeClass('btn-primary').addClass('btn-default');
        $(this).removeClass('btn-default').addClass('btn-primary');
        $(".periods").multiselectfilter("destroy");
        $(".periods").multiselect("destroy");
        $('.periods').html(mounthList);
        $('.periods').multiselect().multiselectfilter();
    })
    $('#years').trigger('click')
}