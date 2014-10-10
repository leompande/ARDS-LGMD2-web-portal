// -----------------------------------------------------------------------------
// Section details form
// -----------------------------------------------------------------------------

function showSectionDetails( sectionId )
{
  var request = new Request();
  request.setResponseTypeXML( 'section' );
  request.setCallbackSuccess( sectionReceived );
  request.send( 'getSection.action?sectionId=' + sectionId );
}

function sectionReceived( sectionElement )
{
  setFieldValue( 'idField', getElementValue( sectionElement, 'id' ) );
  setFieldValue( 'nameField', getElementValue( sectionElement, 'name' ) );
  setFieldValue( 'titleField', getElementValue( sectionElement, 'title' ) );
  setFieldValue( 'dataSetField', getElementValue( sectionElement, 'dataSet' ) );
  setFieldValue( 'categoryComboField', getElementValue( sectionElement, 'categoryCombo' ) );
  setFieldValue( 'dataElementCountField', getElementValue( sectionElement, 'dataElementCount' ) );  

  showDetails();
}

function sortOrderSubmit() {
	var datasetId = document.getElementById('dataSetId').value;

	if( datasetId == "null" ) {
		window.alert( i18n_please_select_dataset );
	} else {
		window.location = "sortOrderSection.action?dataSetId=" + datasetId;
	}
}

function getSectionByDataSet(dataSetId) {
	window.location = "section.action?dataSetId=" + dataSetId;
}
function removeSection(sectionId, sectionName) {
	removeItem(sectionId, sectionName, i18n_confirm_delete,
			"removeSection.action");
}

function addSectionSubmit() {
	var dataSetId = document.getElementById('dataSetId').value;
	var categoryComboId = document.getElementById('categoryComboId').value;

	if (dataSetId == "null" || dataSetId == "" || categoryComboId == "null"
			|| categoryComboId == "") {
		window.alert("Please select a dataset/categorycombo");
	} else {
		window.location.href = "addSectionAction.action?dataSetId=" + dataSetId
				+ "&categoryComboId=" + categoryComboId;
	}
}

function getDataElementByDataSet(dataSetId) {
	var request = new Request();

	var requestString = 'filterAvailableDataElementsByDataSet.action';

	var params = 'dataSetId=' + dataSetId;

	var selectedList = document.getElementById('selectedList');

	for ( var i = 0; i < selectedList.options.length; ++i) {
		params += '&selectedDataElements=' + selectedList.options[i].value;
		// process list.options[i].value / list.options[i].text
	}

	var availableList = document.getElementById('availableList');
	availableList.options.length = 0;

	request.setResponseTypeXML('dataElementGroup');
	request.setCallbackSuccess(filterByDataElementGroupCompleted);
	request.sendAsPost(params);
	request.send(requestString);
}

function addSectionValidationCompleted(messageElement) {
	var type = messageElement.getAttribute('type');
	var message = messageElement.firstChild.nodeValue;

	if (type == 'success') {
		document.forms['addSectionForm'].submit();
	}
	/**
	 * else if ( type == 'error' ) { window.alert( 'Adding the organisation unit
	 * failed with the following message:\n' + message ); }
	 */
	else if (type == 'input') {
		document.getElementById('message').innerHTML = message;
		document.getElementById('message').style.display = 'block';
	}
}

function validateAddSection() {
	var request = new Request();
	request.setResponseTypeXML('message');
	request.setCallbackSuccess(addSectionValidationCompleted);

	var requestString = 'validateSection.action?name='
			+ getFieldValue('sectionName') + '&title='
			+ getFieldValue('sectionTitle');

	request.send(requestString);

	return false;

}

function toggle(dataElementId, optionComboId) {
	var elementId = '[' + dataElementId;

	if (optionComboId != '') {
		elementId = elementId + ']_[' + optionComboId;
	}

	elementId = elementId + ']';

	if (document.getElementById(elementId + '.text').disabled == true) {
		document.getElementById(elementId + '.text').disabled = false;
		document.getElementById(elementId + '.button').value = i18n_disable;
	} else {
		document.getElementById(elementId + '.text').disabled = true;
		document.getElementById(elementId + '.button').value = i18n_enable;
	}
}

// -----------------------------------------------------------------------------
// Grey/Ungrey Fields
// -----------------------------------------------------------------------------

function saveGreyStatus( sectionId_, dataElementId_, optionComboId_ ) 
{
	var sectionId = sectionId_
	var dataElementId = dataElementId_;
	var optionComboId = optionComboId_;
	var isGreyed;

	var elementId = '[' + dataElementId;	

	if ( optionComboId != '') {
		elementId = elementId + ']_[' + optionComboId;
	}

	elementId = elementId + ']';
	
	var txtElementId = elementId + '.txt';
	var btnElementId = elementId + '.btn';

	if (document.getElementById( txtElementId ).disabled == true) 
	{
		document.getElementById( txtElementId ).disabled = false;
		document.getElementById( btnElementId ).value = i18n_disable;

		isGreyed = false;		
	} 
	else 
	{
		document.getElementById( txtElementId ).disabled = true;
		document.getElementById( btnElementId ).value = i18n_enable;

		isGreyed = true;
	}
	
	var request = new Request();
	request.setCallbackSuccess(handleResponse);
	request.setCallbackError(handleHttpError);
	request.setResponseTypeXML('status');
	
	var requestString = 'saveSectionGreyStatus.action?sectionId=' + sectionId
			+ '&dataElementId=' + dataElementId + '&optionComboId='
			+ optionComboId + '&isGreyed=' + isGreyed;

	request.send(requestString);
}

function handleResponse(rootElement) {
}

function handleHttpError(errorCode) {
}

function markValue(color) {
}