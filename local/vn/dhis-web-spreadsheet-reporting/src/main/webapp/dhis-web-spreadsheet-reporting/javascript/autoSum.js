function getValue( fieldId )
{
	var value = '';
	var element = byId( fieldId );

	if ( element ) {
		value = element.getElementsByTagName('input')[0].value;
	}
	else {
		value = jQuery( '#' + fieldId.split('.')[0] + '-' + fieldId.split('.')[1] + '-val').val();
	}
	
	if ( value == '' ) {
		return 0;
	}
	
	return value;
}

// Assign value and save to database
function assignValue( fieldId, value )
{	
	value = (value == 0) ? "" : value;
	
	var element = byId( fieldId );

	if ( element ) {
		element.getElementsByTagName('input')[0].value = value;
	}
	else {
		setFieldValue( fieldId.split('.')[0] + '-' + fieldId.split('.')[1] + '-val', value );
	}
	
	saveVal( fieldId.split('.')[0], fieldId.split('.')[1] );
}

// Sum for two
function sum2( targetField, field1, field2 )
{
	var sumValue = eval( getValue(field1) ) + eval( getValue(field2) );
	
	assignValue( targetField, sumValue );
}

// Sum for three
function sum( targetField, field1, field2, field3 )
{
	var sumValue = eval( getValue(field1) ) + eval( getValue(field2) ) + eval( getValue(field3) );
	
	assignValue( targetField, sumValue );
}