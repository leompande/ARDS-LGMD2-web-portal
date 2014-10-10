
// -----------------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------------

var selectedOrganisationUnitIds = null;

function setSelectedOrganisationUnitIds( ids )
{
    selectedOrganisationUnitIds = ids;
}

if ( selectionTreeSelection )
{
    selectionTreeSelection.setListenerFunction( setSelectedOrganisationUnitIds );
}

function validateAggregatedReport()
{
    if ( !getListValue( "hrDataSetId" ) )
    {
        setMessage( i18n_select_data_set );
        return false;
    }
    if ( !getListValue( "attributeId" ) )
    {
        setMessage( i18n_select_attribute );
        return false;
    }
    if ( selectedOrganisationUnitIds == null || selectedOrganisationUnitIds.length == 0 )
    {
        setMessage( i18n_select_organisation_unit );
        return false;
    }
    
    document.getElementById( "reportForm" ).submit();
}
