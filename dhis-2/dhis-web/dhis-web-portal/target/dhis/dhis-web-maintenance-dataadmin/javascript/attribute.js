function onValueTypeChange( e ) {
  var val = $(this).find(":selected").val();

  if( val == "multiple_choice" ) {
    $("#memberValidator").addClass("required");
    $("#multipleChoice").show();
  }
  else {
    $("#memberValidator").removeClass("required");
    $("#multipleChoice").hide();
  }
}

function showAttributeDetails( context ) {
  jQuery.post('getAttribute.action', { id: context.id },
    function( json ) {
      setInnerHTML('nameField', json.attribute.name);
      setInnerHTML('mandatoryField', json.attribute.mandatory);
      setInnerHTML('dataelementField', json.attribute.dataelement);
      setInnerHTML('indicatorField', json.attribute.indicator);
      setInnerHTML('organisationunitField', json.attribute.organisationunit);
      setInnerHTML('userField', json.attribute.user);
      setInnerHTML('valuetypeField', json.attribute.valueType);
      showDetails();
    });
}

function removeAttribute( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, 'removeAttribute.action');
}

function showUpdateAttributeForm( context ) {
  location.href = 'showUpdateAttributeForm.action?id=' + context.id;
}