
var significantZeros = []; // Identifiers for which zero values are insignificant, also used in entry.js

function addEventListeners() 
{
	var inputs = document.getElementsByName( "entryfield" ) 

	for ( var i = 0, input; input = inputs[i]; i++ )
	{
		input.addEventListener('focus', valueFocus, false);
	}

    var selects = document.getElementsByName( "entryselect" );

	for ( var i = 0, select; select = selects[i]; i++ )
	{
		select.addEventListener('focus', valueFocus, false);
	}
}

function clearPeriod()
{	
	clearList( document.getElementById( 'selectedPeriodIndex' ) );	
	clearEntryForm();
}

function clearEntryForm()
{
	$( '#contentDiv' ).html( '' );
}

// -----------------------------------------------------------------------------
// HrDataSet Selection
// -----------------------------------------------------------------------------

function hrDataSetSelected()
{
	var hrDataSetId = $( '#hrDataSetId' ).val();
	if ( hrDataSetId && hrDataSetId != -1 )
	{
		var url = 'loadAttributes.action?hrDataSetId=' + hrDataSetId;

		var list = document.getElementById( 'attributeId' );
		//alert( list );
	    clearList( list );
	    $.getJSON( url, function( json ) {
	    	//addOptionToList( list, '-1', '[ ' + i18n_select_attribute + ' ]' );
	    	for ( i in json.attributes ) {
	    		addOptionToList( list, json.attributes[i].id, json.attributes[i].caption );
	    	}
	    } );
	}
}

function attributeOptionPopulator( attributeId ){
	
	//var CurrentAttributeId = $( '#' + attributeId ).val();
	
	if ( attributeId && attributeId != -1 )
	{
		
		var url = 'loadAttributeOption.action?attributeId=' + attributeId;
		
		var list = document.getElementById( attributeId );
		//alert( list );
		
	    clearList( list );
	    
	    
	    $.getJSON( url, function( json ) {
	    	addOptionToList( list, '-1', '[ Select Options ]' );
	    	for ( i in json.attributesOptions ) {
	    		addOptionToList( list, json.attributesOptions[i].id, json.attributesOptions[i].value );
	    	}
	    } );
	}
}

function editAttributeOptionPopulator( attributeId, attributeOptionId, attributeOption ){
	
	//var CurrentAttributeId = $( '#' + attributeId ).val();
	
	if ( attributeId && attributeId != -1 )
	{
		
		var url = 'loadAttributeOption.action?attributeId=' + attributeId;
		
		var list = document.getElementById( attributeId );
		//alert( list );
		
	    clearList( list );
	    
	    
	    $.getJSON( url, function( json ) {
	    	if (attributeOption == null)
		    	{
		    		addOptionToList( list, '-1', '[ Select Options ]' );
		    	}
	    	else
	    		{
	    			addOptionToList( list, attributeOptionId, '[ ' + attributeOption + ' ]' );
	    		}
	    	for ( i in json.attributesOptions ) {
	    		addOptionToList( list, json.attributesOptions[i].id, json.attributesOptions[i].value );
	    	}
	    } );
	}
}

