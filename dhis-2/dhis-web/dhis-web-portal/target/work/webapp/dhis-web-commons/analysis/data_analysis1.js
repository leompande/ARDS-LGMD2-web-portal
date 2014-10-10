 /**
 * Created by kelvin Mbwilo on 8/17/14.
 */
 $('.analysis-wraper').hide();
$(document).ready(function(){

    $('.analysis-wraper').hide();
    //building a menu from named report tables
    $.getJSON( "../api/reportTables.json", function( data ) {
        var mainmenu = new Array();
        var arrayCounter = 0;
        $.each( data.reportTables, function( key, value ) {
            var arr = value.name.split(':');
            if(arr.length != 1){
                if($.inArray(arr[0], mainmenu) == -1){
                    mainmenu[arrayCounter] = arr[0];
                    arrayCounter++;
                }
            }
        });
        var menu ="";
        var menu1 ="";
        for(var i=0;i<mainmenu.length; i++){
            menu += '<div class="panel panel-default">';
            menu += '<div class="panel-heading">';
            menu += '<h6 class="panel-title" style="padding: 5px 5px;font-size: 10pt">';
            menu += '<a class="text-success" data-toggle="collapse" data-parent="#accordion1" href="#collapseOneItem'+i+'">';
            menu += '<i class="fa fa-chevron-circle-down pull-right"></i>'+mainmenu[i];
            menu += '</a>';
            menu += '</h6>';
            menu += '</div>';
            menu += '<div id="collapseOneItem'+i+'" class="panel-collapse collapse">';
            menu += '<div class="panel-body">';
            menu += '<ul class="nav nav-pills nav-stacked">';
            $.each( data.reportTables, function( key, value ) {
                var arr = value.name.split(':');
                if(arr.length != 1){
                    if(mainmenu[i] == arr[0]){
                        menu += '<li style="padding: 8px 4px" class="menuitem listmenu" allstring="'+arr[1]+'"><a style="padding-left: 0px; padding-right: 0px" href="#" id="'+value.id+'" class="text-success"></i>'+arr[1].substring(0,20)+'...<i class="fa fa-chevron-right pull-right"></i> </a></li>';
                    }
                }
            })
            menu += '</ul>';
            menu += '</div>';
            menu += '</div>';
            menu += '</div>';

            menu1 += '<div class="panel panel-default">';
            menu1 += '<div class="panel-heading">';
            menu1 += '<h6 class="panel-title" style="padding: 5px 5px;font-size: 10pt">';
            menu1 += '<a class="text-success" data-toggle="collapse" data-parent="#accordion2" href="#collapseTwoItem'+i+'">';
            menu1 += '<i class="fa fa-chevron-circle-down pull-right"></i>'+mainmenu[i];
            menu1 += '</a>';
            menu1 += '</h6>';
            menu1 += '</div>';
            menu1 += '<div id="collapseTwoItem'+i+'" class="panel-collapse collapse">';
            menu1 += '<div class="panel-body">';
            menu1 += '<ul class="nav nav-pills nav-stacked">';
            $.each( data.reportTables, function( key, value ) {
                var arr = value.name.split(':');
                if(arr.length != 1){
                    if(mainmenu[i] == arr[0]){
                        menu1 += '<li style="padding: 8px 4px" class="menuitem listmenu" allstring="'+arr[1]+'"><a style="padding-left: 0px; padding-right: 0px" href="#" id="'+value.id+'" class="text-success"></i>'+arr[1]+'...<i class="fa fa-chevron-right pull-right"></i> </a></li>';
                    }
                }
            })
            menu1 += '</ul>';
            menu1 += '</div>';
            menu1 += '</div>';
            menu1 += '</div>';
        }
        $('#accordion1').append(menu);
        $('#accordion2').append(menu1);
        $(".listmenu").hover(function(){
            var origin = $(this).find('a').html()
            $(this).find('a').html($(this).attr('allstring'))
            $(this).attr('allstring',origin);
        },function(){
            var origin = $(this).find('a').html()
            $(this).find('a').html($(this).attr('allstring'))
            $(this).attr('allstring',origin);
        })
        var name ="";
        var first_orgunit =new Array();
        var first_period = new Array();

        $(".menuitem").click(function() {
            document.menuss = $(this);
            var url1 = '../api/reportTables/' + $(this).find('a').attr('id') + '.json';
            $('#mainarea').html("");

            //prepare data multselect from data_element.js

            $.getJSON(url1, function (data) {
                name = data.name;
                var data_element_select = "";
                $.each(data.dataElements, function (key, value) {
                    data_element_select += "<option value='" + value.id + "' selected='selected'>" + value.name + "</option>";
                });
                $.each(data.organisationUnits, function (key, value) {
                    first_orgunit[key] = value.id;
                });
                //creating organization unit multselect from orgUnit.js
                prepareOrganisationUnit(first_orgunit);
                $.each(data.periods, function (key, value) {
                    first_period[key] = value.id;
                });
                //prepare period Multselect from periods.js
                $('.data-element').multiselect().multiselectfilter();
                preparePeriods(first_period);
                $(".data-element").multiselectfilter("destroy");
                $(".data-element").multiselect("destroy");
                $(".data-element").css('width', '180px');
                $('.data-element').html(data_element_select).multiselect().multiselectfilter();
//            });


            //hiding error alert
            $('.alert').hide();

            $('.reports button').click(function () {
                $('#mainarea').html('<i class="fa fa-spinner fa-spin fa-3x"></i> Loading...')
                var orgunit = $(".adminUnit").val();
                var dataelement = $('.data-element').val();
                var timeperiod = $(".periods").val();
                if (!orgunit || !dataelement || !timeperiod) {
                    $('.alert').fadeIn('slow');
                    setTimeout(function () {
                        $('.alert').fadeOut('slow');
                    }, 3000);
                } else {
                    //identifying active report
                    $('.reports button').removeClass('btn-success').addClass('btn-default');
                    $(this).removeClass('btn-default').addClass('btn-success');

                    //preparing a link to send to analytics
                    var data_dimension = 'dimension=dx:';
                    for (var i = 0; i < dataelement.length; i++) {
                        data_dimension += (i == dataelement.length - 1) ? dataelement[i] : dataelement[i] + ';';
                    }

                    //creating column dimension
                    var column_dimension = 'dimension=' + $('select[name=category]').val() + ':';
                    //if column will be administrative units
                    if ($('select[name=category]').val() == 'ou') {
                        for (var i = 0; i < orgunit.length; i++) {
                            column_dimension += (i == orgunit.length - 1) ? orgunit[i] : orgunit[i] + ';';
                        }
                    }
                    else { //if column will be periods
                        for (var i = 0; i < timeperiod.length; i++) {
                            column_dimension += (i == timeperiod.length - 1) ? timeperiod[i] : timeperiod[i] + ';';
                        }
                    }

                    //creating filter dimensions
                    var filter = ($('select[name=category]').val() != 'ou') ? 'filter=ou:' : 'filter=pe:'
                    //if filter will be administrative units
                    if ($('select[name=category]').val() != 'ou') {
                        for (var i = 0; i < orgunit.length; i++) {
                            filter += (i == orgunit.length - 1) ? orgunit[i] : orgunit[i] + ';';
                        }
                    }
                    else { //if filter will be periods
                        for (var i = 0; i < timeperiod.length; i++) {
                            filter += (i == timeperiod.length - 1) ? timeperiod[i] : timeperiod[i] + ';';
                        }
                    }

                    var url = '../api/analytics.json?' + data_dimension + '&' + column_dimension + '&' + filter


                    //checking types of report needed and react accordingly
                    //drawing table
                    if ($(this).attr('id') == 'draw_table') {
                        drawTable(name, url, $('select[name=category]').val(), $('.data-element').val());
                    }
                    //drawing bar chart
                    if ($(this).attr('id') == 'draw_bar') {
                        drawBar(name, url, $('select[name=category]').val(), $('.data-element').val());
                    }
                    //drawing column chart
                    if ($(this).attr('id') == 'draw_column') {
                        drawColumn(name, url, $('select[name=category]').val(), $('.data-element').val());
                    }
                    //drawing line chart
                    if ($(this).attr('id') == 'draw_line') {
                        drawLine(name, url, $('select[name=category]').val(), $('.data-element').val());
                    }
                    //drawing pie chat
                    if ($(this).attr('id') == 'draw_pie') {
                        drawPie(name, url, $('select[name=category]').val(), $('.data-element').val());
                    }
                    //drawing staked chat
                    if ($(this).attr('id') == 'draw_staked') {
                        drawStaked(name, url, $('select[name=category]').val(), $('.data-element').val());
                    }
                    //drawing spider chat
                    if ($(this).attr('id') == 'draw_spider') {
                        drawSpider(name, url, $('select[name=category]').val(), $('.data-element').val());
                    }
                    //drawing combined chat
                    if ($(this).attr('id') == 'draw_combined') {
                        drawCombined(name, url, $('select[name=category]').val(), $('.data-element').val());
                    }
                    //exporting data to excel
                    if ($(this).attr('id') == 'export_cvs') {
                        window.location = '../api/analytics.xls?' + data_dimension + '&' + column_dimension + '&' + filter;
                    }
                }
            })
            $(".category").multiselect({
                multiple: false,
                header: "Select an option",
                noneSelectedText: "Select an Option",
                selectedList: 1
            });
            $(".category").css('width', '180px');
            $('#content_to_hide').hide();
            $('.analysis-wraper').fadeIn();
                $('#draw_table').trigger("click");

        });
            $('#draw_table').trigger("click");

        });
 });
})
