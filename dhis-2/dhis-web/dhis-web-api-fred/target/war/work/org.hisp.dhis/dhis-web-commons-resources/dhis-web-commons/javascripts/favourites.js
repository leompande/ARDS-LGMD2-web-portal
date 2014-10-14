var ChartInformation = [];
var chartsCounting = 1;
(function(){
    //prepare data multselect from data_element.js


    $.getJSON( "/api/charts.json", function( data ) {
        var data_element_select = "";
        $.each( data.charts, function( key, value ) {

            data_element_select += "<option value='"+value.id+"'>" + value.name + "</option>";
        });
        $('.data-element').html(data_element_select).multiselect().multiselectfilter();
    });
/// pull the selected favourites charts from database
    $.ajax({
        url: "listCharts.action",
        dataType: 'json',
        async: false
    }).done(function(response) {
        var charts = new Array();
        for(var tdCounter=0;tdCounter<response.length;tdCounter++){
            charts[tdCounter] = response[tdCounter].json_url;
        }

        var ChartDetails = new Array();
        var chartString = "";
        $.getJSON("../api/charts.json",function( data ){
            var chartsCounter = 0;
            var IdCount = 1;
            $.each(data.charts,function (key, val){

                for(var i=0;i<charts.length;i++){

                    if(val.id === charts[i]){
                        var DataElements = new Array();
                        var Periods = new Array();
                        var OrgUnits = new Array();
                        var Types = new Array();
//                    // checking the data elements maching json charts

                        $.getJSON("../api/charts/"+val.id+".json",function( dataCharts ){

                            var dataElementCouter = 0;
                            var periodsCouter = 0;
                            var orgUnitsCouter = 0;
                            $.each(dataCharts.dataElements,function (key, vals){
                                DataElements[dataElementCouter] = "dataelements:"+vals.id+"#"+vals.name;
                                dataElementCouter++;
                            });
                            $.each(dataCharts.periods,function (key, vals){
                                Periods[periodsCouter] = "periods:"+vals.id+"#"+vals.name;
                                periodsCouter++;
                            });
                            $.each(dataCharts.organisationUnits,function (key, vals){
                                OrgUnits[orgUnitsCouter] = "orgUnits:"+vals.id+"#"+vals.name;
                                orgUnitsCouter++;
                            });

                            ChartDetails[0] = DataElements;
                            ChartDetails[1] = Periods;
                            ChartDetails[2] = OrgUnits;
                            ChartDetails[3] = "Types:"+dataCharts.type;
                            ChartDetails[4] = "title:"+dataCharts.name;


                            var ChartToDraw = ChartDetails;
                            var StringArray = prepareAnalyticsJson(ChartToDraw).split(">");
                            var jsonString  = StringArray[0];
                            var chartType   = StringArray[1];
                            var chartTitle  = StringArray[2];

                            if(chartType==="stackedcolumn"){
                                chartType = "column";
                            }
                            if(chartType==="stackedcolumn"){
                                chartType = "column";
                            }
                            if(chartType==="stackedcolumn"){
                                chartType = "column";
                            }
console.log(jsonString);
                            $.getJSON(jsonString,function( dataResponse ){

//                                var options = {
//                                    chart: {
//                                        type: ""
//                                    },
//                                    title: {
//                                        text: ""
//                                    },
//                                    xAxis: {
//                                        categories:[],
//                                        title: {
//                                            text: null
//                                        },
//                                        series: []
//                                    },
//                                    yAxis: {
//                                        min: 0,
//                                        title: {
//                                            text: '',
//                                            align: 'high'
//                                        },
//                                        labels: {
//                                            overflow: 'justify'
//                                        }
//                                    },
//                                    tooltip: {
//                                        valueSuffix: ''
//                                    },
//                                    plotOptions: {
//                                        bar: {
//                                            dataLabels: {
//                                                enabled: true
//                                            }
//                                        }
//                                    },
//                                    legend: {
//                                        layout: 'vertical',
//                                        align: 'right',
//                                        verticalAlign: 'top',
//                                        x: 150,
//                                        y: 50,
//                                        floating: true,
//                                        borderWidth: 1,
//                                        backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor || '#FFFFFF'),
//                                        shadow: true
//                                    },
//                                    credits: {
//                                        enabled: false
//                                    },
//                                    series:[]
//                                }
//
//
//
//                                var LabelingArray = [];
//                                $.each(dataResponse.metaData['names'],function(index,val){
//                                    LabelingArray[index] = val;
//                                });
//
//                                var existingCat = [];
//                                var existingDat = [];
//                                var datas = [];
//                                $.each(dataResponse.rows,function(index,val){
//
//                                    if($.inArray( LabelingArray[val[1]], existingCat )===-1){
//                                        options.xAxis.categories.push(LabelingArray[val[1]]);
//                                        existingCat.push(LabelingArray[val[1]]);
//                                    }else{
//
//                                    }
//
//                                    if($.inArray( LabelingArray[val[0]], existingDat )===-1){
//
//                                    }else{
//
//                                    }
//                                    options.series.push({'name':LabelingArray[val[0]],'data':parseInt(val[2])});
//
//                                    options.title.text = chartTitle;
//                                    options.chart.type = chartType;
//
//                                });
//                                console.log(JSON.stringify(options.xAxis.categories));
//                                console.log(JSON.stringify(options.series));
//
////                                $('#statistics_chart_'+IdCount).highcharts({
////                                    chart: {
////                                        type: chartType
////                                    },
////                                    title: {
////                                        text: chartTitle
////                                    },
////                                    xAxis: {
////                                        categories: Categories,
////                                        title: {
////                                            text: null
////                                        }
////                                    },
////                                    yAxis: {
////                                        min: 0,
////                                        title: {
////                                            text: '',
////                                            align: 'high'
////                                        },
////                                        labels: {
////                                            overflow: 'justify'
////                                        }
////                                    },
////                                    tooltip: {
////                                        valueSuffix: ''
////                                    },
////                                    plotOptions: {
////                                        bar: {
////                                            dataLabels: {
////                                                enabled: true
////                                            }
////                                        }
////                                    },
////                                    legend: {
////                                        layout: 'vertical',
////                                        align: 'right',
////                                        verticalAlign: 'top',
////                                        x: 150,
////                                        y: 50,
////                                        floating: true,
////                                        borderWidth: 1,
////                                        backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor || '#FFFFFF'),
////                                        shadow: true
////                                    },
////                                    credits: {
////                                        enabled: false
////                                    },
////                                    series:JSON.stringify(SeriesArray)
////                                });
//                                $('#statistics_chart_'+IdCount).highcharts(options);
//                                IdCount++;
                            });

//                            }


                        });
                        chartsCounter++;
                    }
                }
            });
        });

    });

})();

function prepareAnalyticsJson(ChartToDraw){

    var analyticsJsonString = "../api/analytics.json?";
//dimension=dx:J2eDsDShu8s;JEnRoOB1YSN;WtOVCx4fha8;sJZpLgWm8KK&dimension=ou:jsmCsZqDV8r;k5Xsii5SdeH;mBBKzZonsgl;xA4ZniCM17O&filter=pe:2012July
    if(ChartToDraw[0].length>0){
        analyticsJsonString += "dimension=dx:";
        for(var dataelemensCounter=0;dataelemensCounter<ChartToDraw[0].length;dataelemensCounter++){//dataelemens

            if(dataelemensCounter<ChartToDraw[0].length-1){
                ;
                analyticsJsonString += ChartToDraw[0][dataelemensCounter].split(":")[1].split("#")[0]+";"
            }else{
                analyticsJsonString +=""
            }
        }
    }

    if(ChartToDraw[2].length>0){
        analyticsJsonString += "&dimension=ou:";
        for(var OrgUnitCounter=0;OrgUnitCounter<ChartToDraw[2].length;OrgUnitCounter++){//orgunits
            if(OrgUnitCounter<ChartToDraw[0].length-1){

                analyticsJsonString += ChartToDraw[2][OrgUnitCounter].split(":")[1].split("#")[0]+";"
            }else{
                analyticsJsonString +=""
            }
        }
    }


    if(ChartToDraw[1].length>0){

        analyticsJsonString += "&filter=pe:";
        for(var periodsCounter=0;periodsCounter<ChartToDraw[1].length;periodsCounter++){//periods

            if(periodsCounter<ChartToDraw[0].length-1){

                analyticsJsonString += (((ChartToDraw[1][0].split(":"))[1]).split("#"))[0]+""
            }else{
                analyticsJsonString +="";
            }
        }
    }

    var type =  ChartToDraw[3].split(":");//charttype
    var title =  ChartToDraw[4].split(":");//charttype

    return analyticsJsonString+">"+type[1]+">"+title[1];
}