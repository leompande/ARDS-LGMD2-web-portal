// ---------------------------------------------------------------------------
// Dialog
// ---------------------------------------------------------------------------

function setUpDialog( elementId, title, width, height )
{
	var dialog = jQuery( '#'+elementId ).dialog({
		title: title,
		modal: true,
		autoOpen: false,
		minWidth: width,
		minHeight: height,
		width: width,
		height: height
	});
	
	return dialog;
}

function openDialog( _dialog )
{
	_dialog.dialog( 'open' );
}

function closeDialog( _dialog )
{
	_dialog.dialog( 'close' );
}

// ---------------------------------------------------------------------------------
// Methods
// ---------------------------------------------------------------------------------	

function toggleAll( elementList, checked ) {

	var list = jQuery( elementList );
	
	for ( var i in list )
	{
		list[i].checked = checked;
	}
}

function exportItemSelect( checked )
{
	toggleAll( "input[type=checkbox][name=exportItemCheck]", checked );
}

function changeItemType()
{
	value = getFieldValue( 'itemType' );
	enable( 'expression-button' );
	
	setFieldValue( 'exportItem input[id=expression]', getFieldValue( 'exportItem input[id=currentExpression]') );
	
	if( value == 'dataelement' )
	{
		if ( attribute ) {
			byId('expression-button' ).onclick = avExpressionBuilderForm;
		} else if ( categoryVertical ) {
			byId('expression-button' ).onclick = coExpressionBuilderForm;
		} else {
			byId('expression-button' ).onclick = deExpressionBuilderForm;
		}
	}
	else if( value == 'indicator' )
	{
		byId('expression-button' ).onclick = inExpressionBuilderForm ;
	}
	else if( value == 'formulaexcel' )
	{
		byId('expression-button' ).onclick = excelFormulaExpressionBuilderForm ;
	}
	else if( value == 'organisation' || value == 'serial' || value == 'dataelement_code' || value == 'dataelement_name' ){
		disable( 'expression-button' );
		setFieldValue( 'exportItem input[id=expression]', value );
		removeValidatorRulesById( 'exportItem input[id=expression]' );

		if ( !attribute && !categoryVertical )
		{
			removeValidatorRulesById( 'dataelement textarea[id=formula]' );
		}
	}
}

function insertOperation( value ) {
	byId('formula').value += value;	
}

function cleanFormula()
{
	setFieldValue( 'formula','');
	setInnerHTML( 'expression-description', '');
} 

function insertExpression() 
{
	var expression = '';

	if ( attribute ) {
		expression = "[" + getFieldValue( "attributevalue [id=groupSelect]" ) + "@" + getFieldValue( "attributevalue [id=elementSelect]" ) + "]";
		setFieldValue( 'attributevalue [id=formula]', getFieldValue( 'attributevalue [id=formula]' ) + expression );
	}
	else if ( category ) {
		expression = "[*." + getFieldValue( "dataelement-elementSelect" ) + "]";
		setFieldValue( 'dataelement [id=formula]', getFieldValue( 'dataelement [id=formula]' ) + expression );
	}
	else if ( categoryVertical ) {
		expression = "[" + getFieldValue( "categoryoption-elementSelect" ) + ".*]";
		setFieldValue( 'categoryoption [id=formula]', getFieldValue( 'categoryoption [id=formula]' ) + expression );
	}
	else {
		expression = getFieldValue( 'dataelement-elementSelect' );
		setFieldValue( 'dataelement [id=formula]', getFieldValue( 'dataelement [id=formula]' ) + expression );
	}

	if ( !attribute ) { getExpression(); }
}

function getExpression()
{
	jQuery.postJSON( '../dhis-web-commons-ajax-json/getExpressionText.action',
	{ expression: getFieldValue('formula')}, function( json ){
		if(json.response == 'success'){
			setInnerHTML( 'expression-description', json.message );
		}
	});		
}

function validateAddExportItem( form )
{
	jQuery.postUTF8('validationExportItem.action',
	{
		exportReportId: getFieldValue( 'exportReportId' ),
		name: getFieldValue( 'name' ),
		sheetNo: getFieldValue( 'sheetNo' ),
		row: getFieldValue( 'row' ),
		column: getFieldValue( 'column' )
	},function( json ){
		if( json.response == 'success' ) {					
			form.submit();
		}else {
			showErrorMessage( json.message, 7000 );
		}
	});
}

function validateUpdateExportItem( form )
{
	jQuery.postUTF8('validationExportItem.action',
	{
		id: getFieldValue( 'id' ),
		exportReportId: getFieldValue( 'exportReportId' ),
		name: getFieldValue( 'name' ),
		sheetNo: getFieldValue( 'sheetNo' ),
		row: getFieldValue( 'row' ),
		column: getFieldValue( 'column' )
	},function( json ){
		if( json.response == 'success' ) {
			form.submit();
		}else {
			showErrorMessage( json.message );
		}
	});
}

/*
*	Delete multi export items
*/

function deleteMultiExportItem( confirm )
{
	if ( window.confirm( confirm ) ) 
	{			
		var listRadio = document.getElementsByName( 'exportItemCheck' );
		var url = "deleteMultiExportItem.action?";
		var j = 0;
		
		for( var i=0; i < listRadio.length; i++ )
		{
			var item = listRadio.item(i);
			
			if( item.checked == true )
			{		
				url += "ids=" + item.getAttribute( 'exportItemID' );
				url += (i < listRadio.length-1) ? "&" : "";
				j++;
			}
		}
		
		if( j>0 )
		{
			$.getJSON(
				url, {}, function( json )
				{
					if ( json.response == "success" )
					{
						window.location.reload();
					}
					else if ( json.response == "error" )
					{
						setMessage( json.message ); 
					}
				}
			);
		}		
	}
}

/**
 *	COPY EXPORT_ITEM(s) TO ANOTHER EXPORT_REPORT
 */
function copyExportItemToExportReport() {
	
	jQuery.postJSON( "getAllExportReportByType.action", {
		reportType: getFieldValue( "exportReportType" )
	}, function( json ) {
		var exportReports = json.exportReports;
		var options = byId( "targetExportReport" ).options;

		options.length = 0;

		for( var i = 0 ; i < exportReports.length ; i++ )
		{
			options.add( new Option( exportReports[i].name, exportReports[i].id ), null );
		}

		openDialog( dialog1 );
	} );
}


/**
 *	Validate Copy Export Items to another Export Report
 */

sheetId = 0;
NumberOfItemsChecked = 0;
ItemsSaved = null;
itemsCurTarget = null;
itemsDuplicated = null;
warningMessages = "";

function validateCopyExportItemsToExportReport()
{
	if ( jQuery( 'input:checked' ).length == 0 )
	{
		setMessage( i18n_no_item );
		return;
	}

	sheetId	= getFieldValue( "targetSheetNo" );
	
	if ( sheetId < 1 )
	{
		setMessage( i18n_input_sheet_no );
		return;
	}
	
	if ( getFieldValue("targetExportReport") == -1 )
	{
		setMessage( i18n_choose_export_report );
		return;
	}
	
	itemsCurTarget = null;
	itemsDuplicated = null;
	
	itemsCurTarget = new Array();
	itemsDuplicated = new Array();
	
	jQuery.postJSON( "getExportItemsBySheet.action", {
		exportReportId: getFieldValue( "targetExportReport" ),
		sheetNo: sheetId
	}, function( json ) {
		var items = json.exportItems;
			
		for ( var i = 0 ; i < items.length ; i ++ ) 
		{
			itemsCurTarget.push( items[i].name );
		}
		
		splitDuplicatedItems( 'exportItemID', 'exportItemName' );
		saveCopyExportItemsToExportReport();
	} );
}

function splitDuplicatedItems( itemIDAttribute, itemNameAttribute )
{
	var flag = -1;
	var itemsChecked = new Array();
	var listRadio = document.getElementsByName( 'exportItemCheck' );

	ItemsSaved = null;
	ItemsSaved = new Array();
	
	for (var i = 0 ; i < listRadio.length ; i++)
	{
		if ( listRadio.item(i).checked )
		{
			itemsChecked.push( listRadio.item(i).getAttribute(itemIDAttribute) + "#" + listRadio.item(i).getAttribute(itemNameAttribute));
		}
	}
	
	NumberOfItemsChecked = itemsChecked.length;
	
	for (var i in itemsChecked)
	{
		flag = i;
		
		for (var j in itemsCurTarget)
		{
			if ( itemsChecked[i].split("#")[1] == itemsCurTarget[j] )
			{
				flag = -1;
				itemsDuplicated.push( itemsChecked[i].split("#")[1] );
				break;
			}
		}
		if ( flag >= 0 )
		{
			ItemsSaved.push( itemsChecked[i].split("#")[0] );
		}
	}
}

function saveCopyExportItemsToExportReport() {
	
	warningMessages = " ======= Sheet [" + sheetId + "] ========";
	
	// If have ExportItem(s) in Duplicating list
	// Preparing the warning message
	if ( itemsDuplicated.length > 0 )
	{
		setUpDuplicatedItemsMessage();
	}
	
	// If have also ExportItem(s) in Copying list
	// Do copy and prepare the message notes
	if ( ItemsSaved.length > 0 )
	{
		var url = "copyExportItemToExportReport.action";
			url += "?exportReportId=" + getFieldValue("targetExportReport");
			url += "&sheetNo=" + sheetId;
			
		for (var i in ItemsSaved)
		{
			url += "&exportItems=" + ItemsSaved[i];
		}
		
		executeCopyItems( url );
	}
	// If have no any ExportItem(s) will be copied
	// and also have ExportItem(s) in Duplicating list
	else if ( itemsDuplicated.length > 0 )
	{
		setMessage( warningMessages );
	}
		
	closeDialog( dialog1 );
}


/** 
*	COPY SELECTED EXPORT_ITEM(s) TO IMPORT_REPORT
*/

function copyExportItemToImportReport()
{
	jQuery.postJSON( "getAllImportReportByType.action", {
		reportType: getFieldValue( "exportReportType" )
	}, function( json ) {
		var list = json.importReports;
		var options = byId("targetImportReport").options;

		options.length = 0;
		
		for( var i = 0 ; i < list.length ; i++ )
		{
			options.add( new Option( list[i].name, list[i].id ), null );
		}

		openDialog( dialog2 );
	} );
}

/*
*	Validate copy Export Items to Import Report
*/

function validateCopyExportItemsToImportReport() {

	if ( jQuery( 'input:checked' ).length == 0 )
	{
		setMessage( i18n_no_item );
		return;
	}

	sheetId	= getFieldValue("targetImportReportSheetNo");
	
	if ( sheetId < 1 )
	{
		setMessage( i18n_input_sheet_no );
		return;
	}
	
	if ( getFieldValue("targetImportReport") == -1 )
	{
		setMessage( i18n_choose_import_report );
		return;
	}
	
	itemsCurTarget = null;
	itemsDuplicated = null;
	
	itemsCurTarget = new Array();
	itemsDuplicated = new Array();
	
	jQuery.postJSON( "getImportItemsByImportReport.action", {
		importReportId: getFieldValue( "targetImportReport" )
	}, function( json ) {
		var items = json.importItems;

		for (var i = 0 ; i < items.length ; i ++)
		{
			itemsCurTarget.push( items[i].name );
		}

		splitDuplicatedItems( 'exportItemID', 'exportItemName' );
		saveCopiedExportItemsToImportReport();
	} );
}

function saveCopiedExportItemsToImportReport() {
	
	warningMessages = "";
	
	// If have ExportItem(s) in Duplicating list
	// preparing the warning message
	if ( itemsDuplicated.length > 0 )
	{
		setUpDuplicatedItemsMessage();
	}
	
	// If have also ExportItem(s) in Copying list
	// do copy and prepare the message notes
	if ( ItemsSaved.length > 0 )
	{
		var url = "copyExportItemToImportReport.action";
			url += "?importReportId=" + getFieldValue("targetImportReport");
			url += "&sheetNo=" + sheetId;
			
		for (var i in ItemsSaved)
		{
			url += "&exportItemIds=" + ItemsSaved[i];
		}
	
		executeCopyItems( url );
	}
	// If have no any ExportItem(s) will be copied
	// and also have ExportItem(s) in Duplicating list
	else if ( itemsDuplicated.length > 0 )
	{
		setMessage( warningMessages );
	}
		
	closeDialog( dialog2 );
}

function setUpDuplicatedItemsMessage()
{		
	warningMessages += 
	"<br/><b>[" + (itemsDuplicated.length) + "/" + (NumberOfItemsChecked) + "]</b>:: "
	+ i18n_copy_items_duplicated
	+ "<br/><br/>";
	
	for (var i in itemsDuplicated)
	{
		warningMessages +=
		"<b>(*)</b> "
		+ itemsDuplicated[i] 
		+ "<br/><br/>";
	}
	
	warningMessages += "<br/>";
}

function executeCopyItems( url )
{
	jQuery.postJSON( url, {}, function ( json )
	{
		if ( json.response == "success" )
		{	
			warningMessages +=
			" ======= Sheet [" + sheetId + "] ========"
			+ "<br/><b>[" + (ItemsSaved.length) + "/" + (NumberOfItemsChecked) + "]</b>:: "
			+ i18n_copy_successful
			+ "<br/>=======================<br/><br/>";
		}
		
		setMessage( warningMessages );
	} );
}

/**
* Indicator Export item type
*/
function openIndicatorExpression()
{
	byId("formulaIndicator").value = byId("expression").value;
	
	getIndicatorGroups();
	filterIndicators();	
	enable("indicatorGroups");
	enable("availableIndicators");
	setPositionCenter( 'indicatorForm' );
	
	$("#indicatorForm").show();
}

function getIndicatorGroups()
{
	var list = byId('indicatorGroups');
	
	list.options.length = 0;
	list.add( new Option( "ALL", "ALL" ), null );
	
	var formula = byId("formulaIndicator").value;
	for ( id in indicatorGroups )
	{
		list.add(  new Option( indicatorGroups[id], id ), null );
	}
}

function filterIndicators()
{
	jQuery.postJSON( "../dhis-web-commons-ajax-json/getIndicators.action", {
		id: $("#indicatorGroups").val()
	}, function( json ) {
		var indiatorList = byId( "availableIndicators" );
		indiatorList.options.length = 0;

		var indicators = json.indicators;
		
		for ( var i = 0; i < indicators.length; i++ )
		{
			var id = indicators[ i ].id;
			var name = indicators[ i ].name;
			var option = document.createElement( "option" );
			
			option.value = "[" + id + "]";
			option.text = name;
			indiatorList.add( option, null );	

			if ( byId('formulaIndicator').value == option.value )
			{
				option.selected = true;
				setInnerHTML( "formulaIndicatorDiv", name );
			}
		}
	} );
}

function clearFormula( formulaFieldName )
{
	byId(formulaFieldName).value = '';
	byId(formulaFieldName + "Div").innerHTML = ''
}
