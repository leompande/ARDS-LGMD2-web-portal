$(document).ready(function() {
    $('name').focus();

    validation('updatePatientIdentifierTypeForm');

    $('#updatePatientIdentifierTypeForm').submit(function( e ) {
        e.preventDefault();

        if( $('#type').val() === 'localId' ) {
            var orgunitScope = $('#orgunitScope').is(':checked');
            var programScope = $('#programScope').is(':checked');
            var periodSelected = $('#periodTypeName').val() !== "";

            if( !orgunitScope && !programScope && !periodSelected ) {
                setHeaderDelayMessage(i18n_select_at_least_one_scope);
            } else {
                this.submit();
            }
        } else {
            this.submit();
        }
    });

    checkValueIsExist("name", "validatePatientIdentifierType.action", {id: getFieldValue('id')});
});
