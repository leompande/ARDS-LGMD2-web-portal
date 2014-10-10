
function showUpdateValidationRuleGroupForm( context ) {
  location.href = 'showUpdateValidationRuleGroupForm.action?id=' + context.id;
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showValidationRuleGroupDetails( context ) {
  jQuery.post('getValidationRuleGroup.action', { 'id': context.id }, function( json ) {
    setInnerHTML('nameField', json.validationRuleGroup.name);
    setInnerHTML('descriptionField', json.validationRuleGroup.description);
    setInnerHTML('memberCountField', json.validationRuleGroup.memberCount);
    setInnerHTML('userRolesToAlertCountField', json.validationRuleGroup.userRolesToAlertCount);

    showDetails();
  });
}

// -----------------------------------------------------------------------------
// Remove data element group
// -----------------------------------------------------------------------------

function removeValidationRuleGroup( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, 'removeValidationRuleGroup.action');
}

// -----------------------------------------------------------------------------
// Select lists
// -----------------------------------------------------------------------------

function initLists() {
  for( var id in groupMembers ) {
    $("#groupMembers").append($("<option></option>").attr("value", id).text(groupMembers[id]));
  }

  for( var id in availableValidationRules ) {
    $("#availableValidationRules").append(
      $("<option></option>").attr("value", id).text(availableValidationRules[id]));
  }

  for( var id in availableUserRolesToAlert ) {
    $("#availableUserRolesToAlert").append($("<option></option>").attr("value", id).text(availableUserRolesToAlert[id]));
  }

  for( var id in selectedUserRolesToAlert ) {
    $("#availableValidationRules").append(
      $("<option></option>").attr("value", id).text(selectedUserRolesToAlert[id]));
  }
}

function filterGroupMembers() {
  var filter = document.getElementById('groupMembersFilter').value;
  var list = document.getElementById('groupMembers');

  list.options.length = 0;

  for( var id in groupMembers ) {
    var value = groupMembers[id];

    if( value.toLowerCase().indexOf(filter.toLowerCase()) != -1 ) {
      list.add(new Option(value, id), null);
    }
  }
}

function filterAvailableValidationRules() {
  var filter = document.getElementById('availableValidationRulesFilter').value;
  var list = document.getElementById('availableValidationRules');

  list.options.length = 0;

  for( var id in availableValidationRules ) {
    var value = availableValidationRules[id];

    if( value.toLowerCase().indexOf(filter.toLowerCase()) != -1 ) {
      list.add(new Option(value, id), null);
    }
  }
}

function addGroupMembers() {
  var list = document.getElementById('availableValidationRules');

  while( list.selectedIndex != -1 ) {
    var id = list.options[list.selectedIndex].value;

    list.options[list.selectedIndex].selected = false;

    groupMembers[id] = availableValidationRules[id];

    delete availableValidationRules[id];
  }

  filterGroupMembers();
  filterAvailableValidationRules();
}

function removeGroupMembers() {
  var list = document.getElementById('groupMembers');

  while( list.selectedIndex != -1 ) {
    var id = list.options[list.selectedIndex].value;

    list.options[list.selectedIndex].selected = false;

    availableValidationRules[id] = groupMembers[id];

    delete groupMembers[id];
  }

  filterGroupMembers();
  filterAvailableValidationRules();
}

function filterAvailableUserRolesToAlert() {
  var filter = document.getElementById('availableUserRolesToAlertFilter').value;
  var list = document.getElementById('availableUserRolesToAlert');

  list.options.length = 0;

  for( var id in availableUserRolesToAlert ) {
    var value = availableUserRolesToAlert[id];

    if( value.toLowerCase().indexOf(filter.toLowerCase()) != -1 ) {
      list.add(new Option(value, id), null);
    }
  }
}

function filterSelectedUserRolesToAlert() {
  var filter = document.getElementById('selectedUserRolesToAlertFilter').value;
  var list = document.getElementById('selectedUserRolesToAlert');

  list.options.length = 0;

  for( var id in selectedUserRolesToAlert ) {
    var value = selectedUserRolesToAlert[id];

    if( value.toLowerCase().indexOf(filter.toLowerCase()) != -1 ) {
      list.add(new Option(value, id), null);
    }
  }
}

function addSelectedUserRolesToAlert() {
  var list = document.getElementById('selectedUserRolesToAlert');

  while( list.selectedIndex != -1 ) {
    var id = list.options[list.selectedIndex].value;

    list.options[list.selectedIndex].selected = false;

    selectedUserRolesToAlert[id] = availableUserRolesToAlert[id];

    delete availableUserRolesToAlert[id];
  }

  filterAvailableUserRolesToAlert();
  filterSelectedUserRolesToAlert();
}

function removeSelectedUserRolesToAlert() {
  var list = document.getElementById('selectedUserRolesToAlert');

  while( list.selectedIndex != -1 ) {
    var id = list.options[list.selectedIndex].value;

    list.options[list.selectedIndex].selected = false;

    availableUserRolesToAlert[id] = selectedUserRolesToAlert[id];

    delete selectedUserRolesToAlert[id];
  }

  filterAvailableUserRolesToAlert();
  filterSelectedUserRolesToAlert();
}
