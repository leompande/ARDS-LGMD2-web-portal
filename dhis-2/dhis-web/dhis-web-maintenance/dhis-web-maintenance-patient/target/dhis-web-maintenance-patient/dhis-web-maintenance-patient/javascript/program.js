$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function programIndicatorManagementForm( context ) {
  location.href = 'programIndicator.action?programId=' + context.id;
}

function showProgramUserRoleForm( context ) {
  location.href = 'showProgramUserroleForm.action?id=' + context.id;
}

function showUpdateProgramForm( context ) {
  location.href = 'showUpdateProgramForm.action?id=' + context.id;
}

function programStageManagement( context ) {
  location.href = 'programStage.action?id=' + context.id;
}

function programValidationManagement( context ) {
  location.href = 'programValidation.action?programId=' + context.id;
}

function defineProgramAssociationsForm( context ) {
  location.href = 'defineProgramAssociationsForm.action?id=' + context.id;
}

function validationCriteria( context ) {
  location.href = 'validationCriteria.action?id=' + context.id;
}

function programPatientReminder( context ){
  location.href = 'programPatientReminder.action?id=' + context.id;
}

function showProgramDetails( context ) {
  jQuery.getJSON("getProgram.action", {
    id: context.id
  }, function( json ) {
    setInnerHTML('nameField', json.program.name);
    setInnerHTML('descriptionField', json.program.description);

    var type = i18n_multiple_events_with_registration;
    if( json.program.type == "2" )
      type = i18n_single_event_with_registration;
    else if( json.program.type == "3" )
      type = i18n_single_event_without_registration;
    setInnerHTML('typeField', type);

    var displayIncidentDate = ( json.program.displayIncidentDate == 'true') ? i18n_yes : i18n_no;
    setInnerHTML('displayIncidentDateField', displayIncidentDate);

    var ignoreOverdueEvents = ( json.program.ignoreOverdueEvents == 'true') ? i18n_yes : i18n_no;
    setInnerHTML('ignoreOverdueEventsField', ignoreOverdueEvents);

    var onlyEnrollOnce = ( json.program.onlyEnrollOnce == 'true') ? i18n_yes : i18n_no;
    setInnerHTML('onlyEnrollOnceField', onlyEnrollOnce);

    var displayOnAllOrgunit = ( json.program.displayOnAllOrgunit == 'true') ? i18n_yes : i18n_no;
    setInnerHTML('displayOnAllOrgunitField', displayOnAllOrgunit);

    var selectEnrollmentDatesInFuture = ( json.program.selectEnrollmentDatesInFuture == 'true') ? i18n_yes : i18n_no;
    setInnerHTML('selectEnrollmentDatesInFutureField', selectEnrollmentDatesInFuture);

    var selectIncidentDatesInFuture = ( json.program.selectIncidentDatesInFuture == 'true') ? i18n_yes : i18n_no;
    setInnerHTML('selectIncidentDatesInFutureField', selectIncidentDatesInFuture);

    var dataEntryMethod = ( json.program.dataEntryMethod == 'true') ? i18n_yes : i18n_no;
    setInnerHTML('dataEntryMethodField', dataEntryMethod);

    setInnerHTML('dateOfEnrollmentDescriptionField', json.program.dateOfEnrollmentDescription);
    setInnerHTML('dateOfIncidentDescriptionField', json.program.dateOfIncidentDescription);
    setInnerHTML('programStageCountField', json.program.programStageCount);
    setInnerHTML('noAttributesField', json.program.noAttributes);
    setInnerHTML('noIdentifierTypesField', json.program.noIdentifierTypes);

    showDetails();
  });
}

// -----------------------------------------------------------------------------
// Remove Program
// -----------------------------------------------------------------------------

function removeProgram( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, 'removeProgram.action');
}

function relationshipTypeOnchange() {
  clearListById('relationshipSide');
  var relationshipType = jQuery('#relationshipTypeId option:selected');
  if( relationshipType.val() != "" ) {
    var aIsToB = relationshipType.attr('aIsToB');
    var bIsToA = relationshipType.attr('bIsToA');

    var relationshipSide = jQuery("#relationshipFromA");
    relationshipSide.append('<option value="false">' + aIsToB + '</option>');
    relationshipSide.append('<option value="true">' + bIsToA + '</option>');
  }
}

function programOnChange() {
  var type = getFieldValue('type');

  // anonymous
  if( type == "3" ) {
    disable('onlyEnrollOnce');
    disable('dateOfEnrollmentDescription');
    disable("displayIncidentDate");
    disable("dateOfIncidentDescription");
    disable("generatedByEnrollmentDate");
    disable("availablePropertyIds");
    disable('ignoreOverdueEvents');
    hideById('selectedList');
    hideById('programMessageTB');

    jQuery("[name=displayed]").attr("disabled", true);
    jQuery("[name=displayed]").removeAttr("checked");

    jQuery("[name=nonAnonymous]").hide();
  }
  else {
    enable('onlyEnrollOnce');
    jQuery("[name=displayed]").prop("disabled", false);
    enable("availablePropertyIds");
    enable("generatedByEnrollmentDate");
    enable('dateOfEnrollmentDescription');
    enable("displayIncidentDate");
    enable('ignoreOverdueEvents');
    showById('programMessageTB');
    showById("selectedList");

    jQuery("[name=nonAnonymous]").show();
    if( type == 2 ) {
      disable('ignoreOverdueEvents');
      disable('onlyEnrollOnce');
      disable('generatedByEnrollmentDate');
    }

    if( byId('displayIncidentDate').checked ) {
      enable("dateOfIncidentDescription");
    }
    else {
      disable("dateOfIncidentDescription");
    }
  }
}

// -----------------------------------------------------------------------------
// select identifiers / attributes
// -----------------------------------------------------------------------------

function selectProperties() {
  var selectedList = jQuery("#selectedList");
  jQuery("#availablePropertyIds").children().each(function( i, item ) {
    if( item.selected ) {
      html = "<tr class='selected' id='" + item.value + "' ondblclick='unSelectProperties( this )'><td onmousedown='select(event,this)'>" + item.text + "</td>";
      html += "<td align='center'><input type='checkbox' name='displayed' value='" + item.value + "'";
      html += "></td></tr>";
      selectedList.append(html);
      jQuery(item).remove();
    }
  });

  if( getFieldValue('type') == "3" ) {
    jQuery("[name=displayed]").attr("disabled", true);
  }
}

function selectAllProperties() {
  var selectedList = jQuery("#selectedList");
  jQuery("#availablePropertyIds").children().each(function( i, item ) {
    html = "<tr class='selected' id='" + item.value + "' ondblclick='unSelectDataElement( this )'><td onmousedown='select(this)'>" + item.text + "</td>";
    html += "<td align='center'><input type='checkbox' name='displayed' value='" + item.value + "'";
    if( item.value.match("^attr_") == "attr_" ) {
      html += " style='display:none' ";
    }
    html += "'></td></tr>";
    selectedList.append(html);
    jQuery(item).remove();
  });
}

function unSelectProperties() {
  var availableList = jQuery("#availablePropertyIds");
  jQuery("#selectedList").find("tr").each(function( i, item ) {
    item = jQuery(item);
    if( item.hasClass("selected") ) {
      availableList.append("<option value='" + item.attr("id") + "' selected='true'>" + item.find("td:first").text() + "</option>");
      item.remove();
    }
  });
}

function unSelectAllProperties() {
  var availableList = jQuery("#availablePropertyIds");
  jQuery("#selectedList").find("tr").each(function( i, item ) {
    item = jQuery(item);
    availableList.append("<option value='" + item.attr("id") + "' selected='true'>" + item.find("td:first").text() + "</option>");
    item.remove();
  });
}

function select( event, element ) {
  if( !getKeyCode(event) )// Ctrl
  {
    jQuery("#selectedList .selected").removeClass('selected');
  }

  element = jQuery(element).parent();
  if( element.hasClass('selected') ) element.removeClass('selected');
  else element.addClass('selected');
}

function getKeyCode( e ) {
  var ctrlPressed = 0;

  if( parseInt(navigator.appVersion) > 3 ) {

    var evt = e ? e : window.event;

    if( document.layers && navigator.appName == "Netscape"
      && parseInt(navigator.appVersion) == 4 ) {
      // NETSCAPE 4 CODE
      var mString = (e.modifiers + 32).toString(2).substring(3, 6);
      ctrlPressed = (mString.charAt(1) == "1");
    }
    else {
      // NEWER BROWSERS [CROSS-PLATFORM]
      ctrlPressed = evt.ctrlKey;
    }
  }
  return ctrlPressed;
}

//-----------------------------------------------------------------------------
//Move Table Row Up and Down
//-----------------------------------------------------------------------------

function moveUpPropertyList() {
  var selectedList = jQuery("#selectedList");

  jQuery("#selectedList").find("tr").each(function( i, item ) {
    item = jQuery(item);
    if( item.hasClass("selected") ) {
      var prev = item.prev('#selectedList tr');
      if( prev.length == 1 ) {
        prev.before(item);
      }
    }
  });
}

function moveDownPropertyList() {
  var selectedList = jQuery("#selectedList");
  var items = new Array();
  jQuery("#selectedList").find("tr").each(function( i, item ) {
    items.push(jQuery(item));
  });

  for( var i = items.length - 1; i >= 0; i-- ) {
    var item = items[i];
    if( item.hasClass("selected") ) {
      var next = item.next('#selectedList tr');
      if( next.length == 1 ) {
        next.after(item);
      }
    }
  }
}
