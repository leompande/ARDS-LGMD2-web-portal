isAjax = true;

function validateAddUpdateSqlView( mode ) {
  var name = $("#name").val();
  var sqlquery = $("#sqlquery").val();

  $.getJSON(
    "validateAddUpdateSqlView.action",
    {
      "name": name,
      "sqlquery": sqlquery,
      "mode": mode
    },
    function( json ) {
      if( json.response == "success" ) {
        if( mode == "add" ) {
          byId("addSqlViewForm").submit();
          return;
        }
        byId("updateSqlViewForm").submit();
      }
      else if( json.response == "input" ) {
        setMessage(json.message);
      }
    }
  );
}

function removeSqlViewObject( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, 'removeSqlViewObject.action');
}

function showSqlViewDetails( context ) {
  jQuery.postJSON('getSqlView.action', { id: context.id }, function( json ) {

    setInnerHTML('nameField', json.sqlView.name);

    var description = json.sqlView.description;
    setInnerHTML('descriptionField', description ? description : '[' + i18n_none + ']');
    setInnerHTML('sqlQueryField', json.sqlView.sqlquery);

    showDetails();
  });
}

/**
 * Execute query to create a new view table
 *
 * @param viewId the item identifier.
 */
function runSqlViewQuery( context ) {
  $.getJSON(
    "executeSqlViewQuery.action", { "id": context.id },
    function( json ) {
      if( json.response == "success" ) {
        setHeaderDelayMessage(json.message);
      } else {
        setMessage(json.message);
      }
    }
  );
}

function showUpdateSqlViewForm(context) {
  location.href = 'showUpdateSqlViewForm.action?id=' + context.id;
}
// -----------------------------------------------------------------------
// View data from the specified view table
// -----------------------------------------------------------------------

function showDataSqlViewForm( context ) {
  $.getJSON(
    "checkViewTableExistence.action",
    {
      "id": context.id
    },
    function( json ) {
      if( json.response == "success" ) {
        window.location.href = "exportSqlView.action?id=" + context.id;
      }
      else if( json.response == "error" ) {
        setHeaderDelayMessage(json.message);
      }
    }
  );
}
