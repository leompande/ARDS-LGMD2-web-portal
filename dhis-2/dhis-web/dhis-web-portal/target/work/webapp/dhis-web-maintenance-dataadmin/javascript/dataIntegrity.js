
$( document ).ready( function()
{
	showLoader();
	
    $.getJSON( "getDataIntegrity.action", {}, populateIntegrityItems );
} );

function populateIntegrityItems( json )
{
	hideLoader();
	
    displayViolationList( json.dataElementsWithoutDataSet, "dataElementsWithoutDataSet" );
    displayViolationList( json.dataElementsWithoutGroups, "dataElementsWithoutGroups" );
	displayViolationList( json.dataElementsViolatingExclusiveGroupSets, "dataElementsViolatingExclusiveGroupSets" );
	displayViolationList( json.dataElementsInDataSetNotInForm, "dataElementsInDataSetNotInForm" );
    displayViolationList( json.dataElementsAssignedToDataSetsWithDifferentPeriodTypes, "dataElementsAssignedToDataSetsWithDifferentPeriodTypes" );
    displayViolationList( json.dataSetsNotAssignedToOrganisationUnits, "dataSetsNotAssignedToOrganisationUnits" );
    displayViolationList( json.sectionsWithInvalidCategoryCombinations, "sectionsWithInvalidCategoryCombinations" );
    displayViolationList( json.indicatorsWithIdenticalFormulas, "indicatorsWithIdenticalFormulas" );
    displayViolationList( json.indicatorsWithoutGroups, "indicatorsWithoutGroups" );
    displayViolationList( json.invalidIndicatorNumerators, "invalidIndicatorNumerators" );
    displayViolationList( json.invalidIndicatorDenominators, "invalidIndicatorDenominators" );
	displayViolationList( json.indicatorsViolatingExclusiveGroupSets, "indicatorsViolatingExclusiveGroupSets" );
    displayViolationList( json.organisationUnitsWithCyclicReferences, "organisationUnitsWithCyclicReferences" );
    displayViolationList( json.orphanedOrganisationUnits, "orphanedOrganisationUnits" );
    displayViolationList( json.organisationUnitsWithoutGroups, "organisationUnitsWithoutGroups" );
    displayViolationList( json.organisationUnitsViolatingExclusiveGroupSets, "organisationUnitsViolatingExclusiveGroupSets" );
    displayViolationList( json.organisationUnitGroupsWithoutGroupSets, "organisationUnitGroupsWithoutGroupSets" );
    displayViolationList( json.validationRulesWithoutGroups, "validationRulesWithoutGroups" );
    displayViolationList( json.invalidValidationRuleLeftSideExpressions, "invalidValidationRuleLeftSideExpressions" );
    displayViolationList( json.invalidValidationRuleRightSideExpressions, "invalidValidationRuleRightSideExpressions" );
}

function displayViolationList( list, id )
{
    if ( list.length > 0 )
    {
    	// Display image "drop-down" button
    	
        $( "#" + id + "Button" )
           .attr({ src: "../images/down.png", title: "View violations" })
           .css({ cursor: "pointer" })
           .click( function() { $( "#" + id + "Div" ).slideToggle( "fast" ); } );

        // Populate violation div

        var violations = "";
        
        for ( var i = 0; i < list.length; i++ )
        {
            violations += list[i] + "<br>";
        }
        
        $( "#" + id + "Div" ).html( violations );
    }
    else
    {
    	// Display image "check" button
    	
        $( "#" + id + "Button" ).attr({ src: "../images/check.png", title: "No violations" });
    }
        
    $( "#" + id + "Div" ).hide();
}