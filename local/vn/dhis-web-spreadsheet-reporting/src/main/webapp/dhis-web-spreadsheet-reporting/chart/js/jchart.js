
function showPeriodSelection()
{
	var v = getFieldValue( "loadPeriodBy" );
	if( v == 'LOAD_PERIOD_SELECTED' ){
		showById( "periods_selection" );
		addValidatorRulesById( 'periodIds',{required:true});
	}else{
		hideById( "periods_selection" );
		removeValidatorRulesById( 'periodIds' );
	}
}

function loadPeriods( value )
{
	var target = jQuery("#availablePeriods");
	
	target.empty();
	
	jQuery.postJSON('../dhis-web-commons-ajax-json/getPeriods.action'
		, {name: value }
		, function( json ){	
			jQuery.each( json.periods, function(i, item){					
				target.append('<option value="' + item.id + '">' + item.name + '</option>');
			});					
	});
}

function loadIndicators( value )
{
	var target = jQuery("#availableIndicators table");
	
	target.empty();
	
	jQuery.postJSON('../dhis-web-commons-ajax-json/getIndicators.action'
		, {id: value }
		, function( json ){	
			jQuery.each( json.indicators, function(i, item){					
				target.append('<tr ondblclick="selectIndicator(' + item.id + ')" onclick="indicatorClicked(' + item.id + ')" id="' + item.id + '"><td>' + item.name + '</td></tr>');
			});					
	});
}

function indicatorClicked( id )
{
	var tr = jQuery( "#availableIndicators" + " #" + id );
	if( tr.hasClass("selected") ) tr.removeClass( "selected");
	else tr.addClass( "selected" );
}	


function selectIndicator( id )
{
	var selected = jQuery( "#availableIndicators" + " #" + id );
	
	var color = "<div class='colorSelector' id='colorSelector" + id + "' title=" + i18n_color_selection + "><div>";
	color += "<input type='hidden' name='color' value='#FF0000'/>";
	color += "<input type='hidden' name='selectedIndicators' value='" + id + "'/>";		
	color += "</div></div>";	
	
	var tr = '<tr id="' + id + '">';
	tr += '<td ondblclick="removeIndicator(' + id + ')" >' + selected.text() + '</td>';
	tr += '<td><select name="seriesTypes">';
	tr += '<option value="line">' + i18n_line + '</option>';
	tr += '<option value="column">' + i18n_column + '</option>';
	tr += '<option value="pie">' + i18n_pie + '</option>';
	tr += '<option value="bar">' + i18n_bar + '</option>';
	tr += '</select></td>';
	tr += '<td>' + color + '</td></tr>';
	
	jQuery( "#selectedIndicators tbody" ).append( tr );	
	
	selected.remove();
	
	colorSelector = jQuery( "#colorSelector" + id  );		

	colorSelector.ColorPicker({
		color: '#FF0000',
		onChange: function (hsb, hex, rgb) {
			jQuery( "#colorSelector" + id  + " div").css('backgroundColor', '#' + hex);
			jQuery( "#colorSelector" + id  + " input[name=color]").val( '#' + hex );
		}
	});
}

function removeIndicator( id )
{
	var selected = jQuery( "#selectedIndicators" + " #" + id  + " td:first");
	
	jQuery( "#availableIndicators table" ).append( '<tr class="selected" ondblclick="selectIndicator(' + id + ')" onclick="indicatorClicked(' + id + ')" id="' + id + '"><td>' + selected.text() + '</td></tr>' );	
	
	selected.parent().remove();
}

function getJChartLegendJSON()
{
	var position = getRadioValue( 'legend_position' );
	
	var enable = isChecked( 'legend_enable' );
	
	var layout = getRadioValue( 'legend_layout' );
	
	var legend = '{' + position + ', "enable":"' + enable + '", ' + layout + ',"borderWidth":"0"}';		
	
	setFieldValue( 'legend', legend );
}

function enableLegend( enable )
{
	jQuery( "input[name=legend_layout], input[name=legend_position]").each( function(i, item){
		
		item.disabled = !enable;	
	});
}

function selectCategoryType()
{
	value = getRadioValue( 'categoryType' );
	loadperiodBy = getRadioValue( "loadPeriodBy" );
	if( value == 'PERIOD_CATEGORY' ){
		showById( 'periods_selection' );		
		showById( 'selectLoadPeriodBy' );		
		showPeriodSelection();
	}else{
		hideById( "periods_selection" );			
		hideById( "selectLoadPeriodBy" );		
		removeValidatorRulesById( 'periodIds' );
	}	
	
}