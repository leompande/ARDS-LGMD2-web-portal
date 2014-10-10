 /**
 * Created by kelvin Mbwilo on 8/17/14.
 */
$(document).ready(function(){
    //creating organization unit multselect from orgUnit.js
    prepareOrganisationUnit();

    //prepare period Multselect from periods.js
    preparePeriods();

    //prepare data multselect from data_element.js
    var name ="";
    $.getJSON( "data/YTsHSYChmaD.json", function( data ) {
        name += data.name;
        var data_element_select = "";
        $.each( data.dataElements, function( key, value ) {
            data_element_select += "<option value='"+value.id+"'>" + value.name + "</option>";
        });
        $('.data-element').html(data_element_select).multiselect().multiselectfilter();
    });

    //hiding error alert
    $('.alert').hide();

    $('.reports button').click(function(){
        var orgunit = $(".adminUnit").val();
        var dataelement = $('.data-element').val();
        var timeperiod = $(".periods").val();
        if(!orgunit  || !dataelement || !timeperiod){
          $('.alert').fadeIn('slow');
            setTimeout(function() {
                $('.alert').fadeOut('slow');
            }, 3000);
        }else{
            //identifying active report
            $('.reports button').removeClass('btn-warning').addClass('btn-primary');
            $(this).removeClass('btn-primary').addClass('btn-warning');

            //preparing a link to send to analytics
            var data_dimension ='dimension=dx:';
            for (var i = 0; i < dataelement.length; i++) {
                data_dimension +=(i == dataelement.length-1)?dataelement[i]:dataelement[i]+';';
            }

            //creating column dimension
            var column_dimension = 'dimension='+$('select[name=category]').val()+':';
            //if column will be administrative units
            if($('select[name=category]').val() == 'ou'){
                for (var i = 0; i < orgunit.length; i++) {
                    column_dimension +=(i == orgunit.length-1)?orgunit[i]:orgunit[i]+';';
                }
            }
            else{ //if column will be periods
                for (var i = 0; i < timeperiod.length; i++) {
                    column_dimension +=(i == timeperiod.length-1)?timeperiod[i]:timeperiod[i]+';';
                }
            }

            //creating filter dimensions
            var filter = ($('select[name=category]').val() != 'ou')?'filter=ou:':'filter=pe:'
            //if filter will be administrative units
            if($('select[name=category]').val() != 'ou'){
                for (var i = 0; i < orgunit.length; i++) {
                    filter +=(i == orgunit.length-1)?orgunit[i]:orgunit[i]+';';
                }
            }
            else{ //if filter will be periods
                for (var i = 0; i < timeperiod.length; i++) {
                    filter +=(i == timeperiod.length-1)?timeperiod[i]:timeperiod[i]+';';
                }
            }

            var url = 'http://41.86.162.25:8180/api/analytics.json?'+data_dimension+'&'+column_dimension+'&'+filter


            //checking types of report needed and react accordingly
            //drawing table
            if($(this).attr('id') == 'draw_table'){
                drawTable(name,url,$('select[name=category]').val());
            }
            //drawing bar chart
            if($(this).attr('id') == 'draw_bar'){
                drawBar(name,url,$('select[name=category]').val());
            }
            //drawing column chart
            if($(this).attr('id') == 'draw_column'){
                drawColumn(name,url,$('select[name=category]').val());
            }
            //drawing line chart
            if($(this).attr('id') == 'draw_line'){
                drawLine(name,url,$('select[name=category]').val());
            }
            //drawing pie chat
            if($(this).attr('id') == 'draw_pie'){
                drawPie(name,url,$('select[name=category]').val())
            }
            //drawing staked chat
            if($(this).attr('id') == 'draw_staked'){
                drawStaked(name,url,$('select[name=category]').val())
            }
            //drawing spider chat
            if($(this).attr('id') == 'draw_spider'){
                drawSpider(name,url,$('select[name=category]').val())
            }
            //exporting data to excel
            if($(this).attr('id') == 'export_cvs'){
                 window.location = 'http://41.86.162.25:8180/api/analytics.xls?'+data_dimension+'&'+column_dimension+'&'+filter;
            }


        }



    })

    $(".category").multiselect({
        multiple: false,
        header: "Select an option",
        noneSelectedText: "Select an Option",
        selectedList: 1
    });
})