function drawTable(title,url,category){
    $.getJSON( "data/analytics.json", function( data ) {
        var dataelements =new Array();
        var test = "";
        $.each( data.rows, function( key, value ) {
            if($.inArray(value[0], dataelements) == -1){
                dataelements[key] = value[0];
            }
        });

        //creating orgunit title
        var orgunit_title ="";
        var orgunit_series= [];
        var data_series = [];
        $.each( data.metaData.ou, function( key, value ) {
            orgunit_title += data.metaData.names[value]+',';
            orgunit_series.push(data.metaData.names[value]);
        });
        var title_to_use = (category == 'pe')?period_series:orgunit_series
        //creating data series
        var table ='<h3>'+title+' '+title_to_use+'</h3>'
        table += '<table class="table table-bordered table-responsive" id="example">';
        var theads = "<thead></thead><tr><th></th>";
        var rows = "";
        if(category == 'ou'){
            for(var j =0 ;j<dataelements.length ;j++){
                var name = data.metaData.names[dataelements[j]];
                theads += '<th>'+name+'</th>';
            }
            $.each( data.metaData.ou, function( key, value ) {
                rows += '<tr>';
                rows += '<td>'+data.metaData.names[value]+'</td>'
                $.each( data.rows, function( key, serie ) {
                    if(serie[1] == value ){
                        rows += '<td>'+serie[2]+'</td>';
                    }
                });
                rows += '</tr>'
            })
        }else{
            for(var j =0 ;j<dataelements.length ;j++){
                var name = data.metaData.names[dataelements[j]];
                theads += '<th>'+name+'</th>';
            }
            $.each( data.metaData.pe, function( key, value ) {
                rows += '<tr>';
                rows += '<td>'+data.metaData.names[value]+'</td>'
                $.each( data.rows, function( key, serie ) {
                    if(serie[1] == value ){
                        rows += '<td>'+serie[2]+'</td>';
                    }
                });
                rows += '</tr>'
            })
        }
        theads += '</tr></thead>'
        table += theads+'<tbody>'+rows+'</tbody></table>'

        //creating period title
        var period_title ="";
        var period_series= [];
        $.each( data.metaData.pe, function( key, value ) {
            period_title += data.metaData.names[value]+',';
            period_series.push(data.metaData.names[value]);
        });

        //creating series
        $('#mainarea').html(table);
        $('#example').dataTable({
            "sDom": "<'row'<'col-md-6'l><'col-md-6'f>r>t<'row'<'col-md-6'i><'col-md-6'p>>",
            "sPaginationType": "bootstrap",
            "oLanguage": {
                "sLengthMenu": "_MENU_ records per page"
            }
        });
    });
}
