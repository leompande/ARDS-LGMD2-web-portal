//@author Ovidiu Rosu <rosu.ovi@gmail.com>

// Global Variables
var metaDataArray = [ "AttributeTypes", "Dimensions", "Charts", "Concepts", "Constants", "DataDictionaries", "DataElementGroupSets",
    "DataElementGroups", "DataElements", "DataSets", "Documents", "IndicatorGroupSets", "IndicatorGroups", "Indicators",
    "IndicatorTypes", "MapLegendSets", "Maps", "OptionSets", "OrganisationUnitGroupSets", "OrganisationUnitGroups",
    "OrganisationUnitLevels", "OrganisationUnits", "ReportTables", "Reports", "SqlViews", "UserGroups", "UserRoles",
    "Users", "ValidationRuleGroups", "ValidationRules" ];

// -----------------------------------------------------------------------------
// MetaData Category Accordion
// -----------------------------------------------------------------------------
jQuery( function ()
{
    if ( !jQuery.isEmptyObject( metaDataJson ) )
    {
        loadMetaDataCategories( metaDataJson );
        $( "#mainDivAccordion" ).accordion(
            {
                active: false,
                collapsible: true,
                clearStyle: true,
                autoHeight: false
            } );

        selectAllMetaDataCategories();
        selectAllValues();
        loadMetaDataAccordionEvents( metaDataJson );
    }

    loadFile();
} );

// Collapsed MetaData Category information
function loadMetaDataCategories( metaData )
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        if ( metaData.hasOwnProperty( lowercaseFirstLetter( metaDataArray[i] ) ) )
        {
            $( "#heading" + metaDataArray[i] ).show();
            $( "#div" + metaDataArray[i] ).show();
            $( "#metaDataCommands" ).show();

            insertMetaDataCategoryHeadingDesign( metaDataArray[i] );
            preventAccordionCollapse( metaDataArray[i] );

            $( "#checkboxSelectAll" + metaDataArray[i] ).change( function ()
            {
                var metaDataCategoryName = $( this ).attr( "name" );

                if ( $( this ).is( ":checked" ) )
                {
                    selectAllValuesByCategory( metaDataCategoryName );
                } else
                {
                    deselectValuesByCategory( metaDataCategoryName );
                }
            } );
        } else
        {
            metaDataArray.splice( i, 1 );
            i--;
        }
    }
}

// Insert MetaData HTML & CSS heading design
function insertMetaDataCategoryHeadingDesign( metaDataCategoryName )
{
    var design = generateMetaDataHeadingDesign( metaDataCategoryName );
    $( "#heading" + metaDataCategoryName ).append( design );
}

// Insert MetaData HTML & CSS for a Category
function insertMetaDataCategoryDesign( metaDataCategoryName )
{
    if ( $( "#mainDiv" + metaDataCategoryName ).is( ":empty" ) )
    {
        var design = generateMetaDataCategoryDesign( metaDataCategoryName );
        $( "#mainDiv" + metaDataCategoryName ).append( design );
        loadMetaData( metaDataCategoryName );
    } else
    {
        $( "#mainDiv" + metaDataCategoryName ).show();
        deselectAllValues();
    }
}

// Generate MetaData Heading design
function generateMetaDataHeadingDesign( metaDataCategoryName )
{
    var design =
            '<div id="divSelectAll' + metaDataCategoryName + '" style="float: right;">'
          +     '<input id="checkboxSelectAll' + metaDataCategoryName + '" name="' + metaDataCategoryName + '" type="checkbox"/>'
          +     '<label id="labelSelectAll' + metaDataCategoryName + '" for="' + metaDataCategoryName + '" style="font-size: 10pt;">' + i18n_select_all + '</label>'
          + '</div>'
        ;

    return design;
}

// Generate MetaData HTML & CSS for a Category
function generateMetaDataCategoryDesign( metaDataCategoryName )
{
    var i18n_available_metadata = getI18nAvailableMetaData( metaDataCategoryName );
    var i18n_selected_metadata = getI18nSelectedMetaData( metaDataCategoryName );
    var design =
                  '<table id="selectionArea'+metaDataCategoryName + '" style="border: 1px solid #ccc; padding: 15px;  margin-top: 10px; margin-bottom: 10px;">'
                + '<colgroup>'
                +    '<col style="width: 500px;"/>'
                +    '<col/>'
                +    '<col style="width: 500px"/>'
                + '</colgroup>'
                + '<thead>'
                +    '<tr>'
                +        '<th>' + i18n_available_metadata + '</th>'
                +        '<th>' + i18n_filter + '</th>'
                +        '<th>' + i18n_selected_metadata + '</th>'
                +    '</tr>'
                + '</thead>'
                +        '<tbody>'
                +            '<tr>'
                +                '<td>'
                +                   '<select id="available' + metaDataCategoryName + '" multiple="multiple" style="height: 200px; width: 100%; margin-top: 10px;"></select>'
                +               '</td>'
                +               '<td>'
                +                   '<input id="moveSelected' + metaDataCategoryName +'" type="button" value="&gt;" title="' + i18n_move_selected + '" style="width:50px"'
                +                       'onclick="moveSelected( \'' + metaDataCategoryName + '\' );"/><br/>'
                +                   '<input id="removeSelected' + metaDataCategoryName + '" type="button" value="&lt;" title="' + i18n_remove_selected + '" style="width:50px"'
                +                       'onclick="removeSelected( \'' + metaDataCategoryName + '\' );"/><br/>'
                +                   '<input id="select' + metaDataCategoryName + '" type="button" value="&gt;&gt;" title="' + i18n_move_all + '" style="width:50px"'
                +                       'onclick="moveAll( \'' + metaDataCategoryName + '\' );"/><br/>'
                +                   '<input id="deselect' + metaDataCategoryName + '" type="button" value="&lt;&lt;" title="' + i18n_remove_all +  '" style="width:50px"'
                +                       'onclick="removeAll( \'' + metaDataCategoryName + '\' );"/><br/>'
                +               '</td>'
                +               '<td>'
                +                   '<select id="selected' + metaDataCategoryName + '" name="selected' + metaDataCategoryName + '" multiple="multiple" style="height: 200px; width: 100%; margin-top: 10px;"></select>'
                +               '</td>'
                +           '</tr>'
                +       '</tbody>'
                + '</table>'
        ;

    return design;
}

// Move all selected items
function moveSelected( metaDataCategoryName )
{
    $( "#available" + metaDataCategoryName + " option:selected" ).each( function ()
    {
        $( "#checkboxSelectAll" + metaDataCategoryName ).attr( "checked", true );

        var option = jQuery( "<option/>" );
        option.text( $( this ).attr( "name" ) );
        option.attr( "name", $( this ).attr( "name" ) );
        option.attr( "value", $( this ).attr( "value" ) );
        option.attr( "selected", "selected" );

        $( "#selected" + metaDataCategoryName ).append( option );
        $( this ).remove();
    } );

    if ( $( "#selected" + metaDataCategoryName + " option" ).length > 0 )
    {
        $( "#heading" + metaDataCategoryName ).css( "background", "#CFFFB3 50% 50% repeat-x" );
    }
}

// Remove all selected items
function removeSelected( metaDataCategoryName )
{
    $( "#selected" + metaDataCategoryName + " option:selected" ).each( function ()
    {
        var option = jQuery( "<option/>" );
        option.text( $( this ).attr( "name" ) );
        option.attr( "name", $( this ).attr( "name" ) );
        option.attr( "value", $( this ).attr( "value" ) );
        option.attr( "selected", "selected" );

        $( "#available" + metaDataCategoryName ).append( option );
        $( this ).remove();
    } );

    if ( $( "#selected" + metaDataCategoryName + " option" ).length == 0 )
    {
        $( "#heading" + metaDataCategoryName ).css( "background", "" );
    }

    if ( $( "#available" + metaDataCategoryName + " option" ).length > 0 )
    {
        $( "#checkboxSelectAll" + metaDataCategoryName ).attr( "checked", false );
    }
}

// Move all items
function moveAll( metaDataCategoryName )
{
    $( "#available" + metaDataCategoryName + " option" ).each( function ()
    {
        var option = jQuery( "<option/>" );
        option.text( $( this ).attr( "name" ) );
        option.attr( "name", $( this ).attr( "name" ) );
        option.attr( "value", $( this ).attr( "value" ) );
        option.attr( "selected", "selected" );

        $( "#selected" + metaDataCategoryName ).append( option );
        $( this ).remove();
    } );

    $( "#checkboxSelectAll" + metaDataCategoryName ).attr( "checked", true );
    $( "#heading" + metaDataCategoryName ).css( "background", "#CFFFB3 50% 50% repeat-x" );
}

// Remove all items
function removeAll( metaDataCategoryName )
{
    $( "#selected" + metaDataCategoryName + " option" ).each( function ()
    {
        var option = jQuery( "<option/>" );
        option.text( $( this ).attr( "name" ) );
        option.attr( "name", $( this ).attr( "name" ) );
        option.attr( "value", $( this ).attr( "value" ) );
        option.attr( "selected", "selected" );

        $( "#available" + metaDataCategoryName ).append( option );
        $( this ).remove();
    } );

    $( "#checkboxSelectAll" + metaDataCategoryName ).attr( "checked", false );
    $( "#heading" + metaDataCategoryName ).css( "background", "" );
}

// -----------------------------------------------------------------------------
// Load MetaData by Category
// -----------------------------------------------------------------------------

// Load MetaData by Category
function loadMetaData( metaDataCategoryName )
{
    var metaDataCategoryProperty = lowercaseFirstLetter( metaDataCategoryName );
    if ( metaDataJson.hasOwnProperty( metaDataCategoryProperty ) )
    {
        var metaDataCategoryArray = metaDataJson[metaDataCategoryProperty];
        for ( var i = 0; i < metaDataCategoryArray.length; i++ )
        {
            var option = jQuery( "<option/>" );
            option.text( metaDataCategoryArray[i].name );
            option.attr( "name", metaDataCategoryArray[i].name );
            option.attr( "value", metaDataCategoryArray[i].id );
            option.attr( "selected", "selected" );

            $( "#selected" + metaDataCategoryName ).append( option );
            $( "#heading" + metaDataCategoryName ).css( "background", "#CFFFB3 50% 50% repeat-x" );
        }
    }
}

// -----------------------------------------------------------------------------
// MetaData Category Accordion Commands
// -----------------------------------------------------------------------------

// Load MetaData Category Accordion Events
function loadMetaDataAccordionEvents( metaData )
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        if ( metaData.hasOwnProperty( lowercaseFirstLetter( metaDataArray[i] ) ) )
        {
            $( "#heading" + metaDataArray[i] ).click( {metaDataCategoryName: metaDataArray[i]}, selectMetaDataCategoryByHeading );
        }
    }
}

// Select a MetaData Category from the MetaData accordion by heading
function selectMetaDataCategoryByHeading( categoryName )
{
    var metaDataCategoryName = categoryName.data.metaDataCategoryName;
    if ( $( "#mainDiv" + metaDataCategoryName ).children().length == 0 )
    {
        insertMetaDataCategoryDesign( metaDataCategoryName );
    }
}

// Select a MetaData Category from the MetaData accordion
function selectMetaDataCategory( metaDataCategoryName )
{
    if ( $( "#mainDiv" + metaDataCategoryName ).children().length == 0 )
    {
        insertMetaDataCategoryDesign( metaDataCategoryName );
    }
}

// Select all MetaData categories from the accordion
function selectAllMetaDataCategories()
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        selectMetaDataCategory( metaDataArray[i] );
    }
}

// Select all values
function selectAllValues()
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        $( "#select" + metaDataArray[i] ).click();
    }
}

// Deselect all values
function deselectAllValues()
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        $( "#deselect" + metaDataArray[i] ).click();
        $( "#available" + metaDataArray[i] ).find( "option" ).each( function ()
        {
            $( this ).prop( "selected", false );
        } );
    }
}

// Select all values by category
function selectAllValuesByCategory( metaDataCategoryName )
{
    $( "#select" + metaDataCategoryName ).click();
}

// Deselect all values by category
function deselectValuesByCategory( metaDataCategoryName )
{
    $( "#deselect" + metaDataCategoryName ).click();

    $( "#available" + metaDataCategoryName ).find( "option" ).each( function ()
    {
        $( this ).prop( "selected", false );
    } );
}

// -----------------------------------------------------------------------------
// Process file
// -----------------------------------------------------------------------------

// Load file
function loadFile()
{
    $( "#upload" ).change( function ()
    {
        $( "#loadFile" ).submit();
    } );
}

// -----------------------------------------------------------------------------
// Process import MetaData Json
// -----------------------------------------------------------------------------

// Process import MetaData Json
function processImportMetaDataJson()
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        var metaDataCategoryProperty = lowercaseFirstLetter( metaDataArray[i] );

        if ( metaDataJson.hasOwnProperty( metaDataCategoryProperty ) )
        {
            var metaDataCategoryArray = metaDataJson[metaDataCategoryProperty];
            for ( var j = 0; j < metaDataCategoryArray.length; j++ )
            {
                var existsMetaDataCategoryOption = $( "#selected" + metaDataArray[i] + " option[value='" + metaDataCategoryArray[j].id + "']" ).length !== 0;

                if ( !existsMetaDataCategoryOption )
                {
                    (metaDataJson[metaDataCategoryProperty]).splice( j, 1 );
                    j--;
                }
            }

            if ( metaDataJson[metaDataCategoryProperty].length == 0 )
            {
                delete metaDataJson[metaDataCategoryProperty];
            }
        }
    }

    return metaDataJson;
}

// -----------------------------------------------------------------------------
// Import
// -----------------------------------------------------------------------------

// Import detailed MetaData
function importDetailedMetaData()
{
    var importMetaDataJson = {};

    importMetaDataJson.metaData = processImportMetaDataJson();
    importMetaDataJson.dryRun = $("#dryRun" ).find(":selected" ).val();
    importMetaDataJson.strategy = $("#strategy" ).find(":selected" ).val();

    $.ajax(
        {
            type: "POST",
            url: "../api/filteredMetaData/importDetailedMetaData",
            data: JSON.stringify( importMetaDataJson ),
            contentType: "application/json",
            success: function ()
            {
                console.log( "Exported JSON: " + JSON.stringify( importMetaDataJson ) );
            },
            error: function ( request, status, error )
            {
                console.log( request.responseText );
                console.log( arguments );
                alert( "Import process failed." );
            }
        } );
}

// -----------------------------------------------------------------------------
// Utils
// -----------------------------------------------------------------------------

// Stop accordion collapse
function preventAccordionCollapse( metaDataCategoryName )
{
    $( "#heading" + metaDataCategoryName ).find( "input" ).click( function ( e )
    {
        e.stopPropagation();
    } );
}
