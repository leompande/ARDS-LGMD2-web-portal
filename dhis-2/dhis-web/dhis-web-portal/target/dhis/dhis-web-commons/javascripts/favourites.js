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

        var divCounter = 0;
        $.getJSON("../api/charts.json",function( data ){
            $.each(data.charts,function (key, val){

                for(var i=0;i<charts.length;i++){

                    if(val.id === charts[i]){
                        divCounter++;
                        console.log(val.id);
                    // checking the data elements maching json charts
                        var graphImage = '<img src="../api/charts/'+val.id+'/data?width=210&height=250" onclick="dhis2.db.exploreChart( &quot;O3kS3whDRKk&quot; )" title="Click to explore ">';
                        $('#statistics_chart_'+divCounter).html(graphImage);

                    }
                }
            });
        });

    });

})();

