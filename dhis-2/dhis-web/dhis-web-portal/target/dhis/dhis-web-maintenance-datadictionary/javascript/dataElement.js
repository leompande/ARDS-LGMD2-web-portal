$(function() {
  $('#aggregationOperatorSelect').change(updateZeroIsSignificant);
  $('#aggregationOperatorSelect').change();

  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

function updateZeroIsSignificant() {
  var $this = $('#aggregationOperatorSelect');

  if( $this.val() == 'sum' ) {
    $('#zeroIsSignificant').removeAttr('disabled');
  }
  else if( $this.val() == 'average' ) {
    $('#zeroIsSignificant').attr('disabled', true);
  }
}

function exportPDF( type ) {
  var params = "type=" + type;
  params += "&dataDictionaryId=" + jQuery('#dataDictionaryList').val();

  exportPdfByType(type, params);
}

function changeValueType( value ) {
  enable('aggregationOperatorSelect');
  if( value == 'int' ) {
    showById('numberTypeTR');
    hideById('textTypeTR');
    enable('zeroIsSignificant');
  } else {
    disable('zeroIsSignificant');
    hideById('numberTypeTR');
    hideById('textTypeTR');
    disable('aggregationOperatorSelect');

    if( value == 'string' ) {
      showById('textTypeTR');
    }
    else if( value == 'bool' ) {
      enable('aggregationOperatorSelect');
    }
  }

  updateAggreationOperation(value);
}

function updateAggreationOperation( value ) {
  if( value == 'string' || value == 'date' || value == 'trueOnly' ) {
    hideById("aggregationOperator");
  } else {
    showById("aggregationOperator");
  }
}

// -----------------------------------------------------------------------------
// Change data element group and data dictionary
// -----------------------------------------------------------------------------

function criteriaChanged() {
  var domainType = getListValue("domainTypeList");

  var url = "dataElement.action?domainType=" + domainType;

  window.location.href = url;
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showDataElementDetails( context ) {
  jQuery.get('../dhis-web-commons-ajax-json/getDataElement.action',
    { "id": context.id }, function( json ) {
      setInnerHTML('nameField', json.dataElement.name);
      setInnerHTML('shortNameField', json.dataElement.shortName);

      var description = json.dataElement.description;
      setInnerHTML('descriptionField', description ? description : '[' + i18n_none + ']');

      var active = json.dataElement.active;
      setInnerHTML('activeField', active == 'true' ? i18n_yes : i18n_no);

      var typeMap = {
        'int': i18n_number,
        'bool': i18n_yes_no,
        'trueOnly': i18n_yes_only,
        'string': i18n_text,
        'date': i18n_date,
        'username': i18n_user_name
      };
      var type = json.dataElement.valueType;
      setInnerHTML('typeField', typeMap[type]);

      var domainTypeMap = {
        'aggregate': i18n_aggregate,
        'patient': i18n_patient
      };
      var domainType = json.dataElement.domainType;
      setInnerHTML('domainTypeField', domainTypeMap[domainType]);

      var aggregationOperator = json.dataElement.aggregationOperator;
      var aggregationOperatorText = i18n_none;
      if( aggregationOperator == 'sum' ) {
        aggregationOperatorText = i18n_sum;
      } else if( aggregationOperator == 'average' ) {
        aggregationOperatorText = i18n_average;
      }
      setInnerHTML('aggregationOperatorField', aggregationOperatorText);

      setInnerHTML('categoryComboField', json.dataElement.categoryCombo);

      var url = json.dataElement.url;
      setInnerHTML('urlField', url ? '<a href="' + url + '">' + url + '</a>' : '[' + i18n_none + ']');

      var lastUpdated = json.dataElement.lastUpdated;
      setInnerHTML('lastUpdatedField', lastUpdated ? lastUpdated : '[' + i18n_none + ']');

      var dataSets = joinNameableObjects(json.dataElement.dataSets);
      setInnerHTML('dataSetsField', dataSets ? dataSets : '[' + i18n_none + ']');

      showDetails();
    });
}

function removeDataElement( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, 'removeDataElement.action');
}

function domainTypeChange( domainType ) {
  if( domainType == 'aggregate' ) {
    enable('selectedCategoryComboId');
  }
  else {
    setFieldValue('selectedCategoryComboId', getFieldValue('defaultCategoryCombo'));
    disable('selectedCategoryComboId');
  }
}

function showUpdateDataElementForm( context ) {
  location.href = 'showUpdateDataElementForm.action?id=' + context.id + '&update=true';
}
