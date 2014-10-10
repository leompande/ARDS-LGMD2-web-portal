
// -----------------------------------------------------------------------------
// Global variables
// -----------------------------------------------------------------------------

var indicators = []; // Array->associative array (id,name)
var periods = []; // Array->associative array (id,name)
var orgunits = []; // Array->associative array (id,name)
var data = []; // Associative array [(indicator-period-orgunit), (indicatorValue)]

var sizes = []; // Associative array (dimension, size)

var pivotIndicator = true; // Should correspond to init value in form
var pivotPeriod = false;
var pivotOrgunit = false;

var currentIndicator = 0;
var currentPeriod = 0;
var currentOrgunit = 0;

// -----------------------------------------------------------------------------
// Public methods
// -----------------------------------------------------------------------------

/**
 * This method is called from the UI and is responsible for retrieving data from 
 * the server and setting the global variables.
 */
function getData()
{
  clearGlobalVariables();
  
  var indicatorGroupList = document.getElementById( "indicatorGroup" );
  var periodTypeList = document.getElementById( "periodType" );
  var levelList = document.getElementById( "level" );
  
  var indicatorGroupId = indicatorGroupList.options[ indicatorGroupList.selectedIndex ].value;
  var indicatorGroupName = indicatorGroupList.options[ indicatorGroupList.selectedIndex ].text;
  var startDate = document.getElementById( "startDate" ).value;
  var endDate = document.getElementById( "endDate" ).value;
  var periodTypeName = periodTypeList.options[ periodTypeList.selectedIndex ].value;
  var level = levelList.options[ levelList.selectedIndex ].value;
  
  /* DEBUG 
  indicatorGroupId = "1152";
  periodTypeName = "Monthly";
  startDate = "2006-01-01";
  endDate = "2007-01-01";
  level = "4";  
  /* DEBUG */
  
  document.getElementById( "dataLabel" ).innerHTML = 
    i18n_indicator_group + ": " + indicatorGroupName +
    ", " + i18n_start_date + ": " + startDate + 
    ", " + i18n_end_date + ": " + endDate + 
    ", " + i18n_period_type + ": " + periodTypeName +
    ", " + i18n_organisation_unit_level + ": " + level;
  
  var url = "getPivotTable.action";
  
  $.getJSON(
    url,
    {
      "indicatorGroupId": indicatorGroupId, 
      "periodTypeName": periodTypeName, 
      "startDate": startDate, 
      "endDate": endDate, 
      "level": level 
    },
    function( json ) 
    {
      var pivot = json.pivotTable;
      
      indicators = pivot.indicators;
      periods = pivot.periods;
      orgunits = pivot.organisationUnits;
      
      sizes["indicator"] = pivot.sizeIndicators;
      sizes["period"] = pivot.sizePeriods;
      sizes["orgunit"] = pivot.sizeOrganisationUnits;
      
      data = pivot.indicatorValues[0];
      
      generateTable();
    }
  );
}

/**
 * This method is called from the UI and is responsible for pivoting the table.
 */
function pivotData()
{
  pivotIndicator = document.getElementById( "indicatorBox" ).checked;
  pivotPeriod = document.getElementById( "periodBox" ).checked;
  pivotOrgunit = document.getElementById( "orgunitBox" ).checked;
  
  generateTable();
}

/**
 * This method is called from the UI and shows a chart with the selected data.
 */
function viewChart( chartIndicators, chartDimension )
{
  var chartWidth = 750;
    
  var url = "generateChart.action";
    
  if ( chartIndicators == "single" && chartDimension == "period" )
  {
    url += "?indicatorId=" + currentIndicator + "&organisationUnitId=" + currentOrgunit + "&dimension=period&regression=true";
    
    for ( p in periods )
    {
      url += "&periodId=" + periods[p].id;
    }
  }
  else if ( chartIndicators == "single" && chartDimension == "orgunit" )
  {
    url += "?indicatorId=" + currentIndicator + "&periodId=" + currentPeriod + "&dimension=organisationUnit&regression=false";
    
    for ( o in orgunits )
    {
      url += "&organisationUnitId=" + orgunits[o].id;
    }
  }
  else if ( chartIndicators == "all" && chartDimension == "period" )
  {
    url += "?organisationUnitId=" + currentOrgunit + "&dimension=period&regression=false&chartWidth=950";
    
    for ( i in indicators )
    {
      url += "&indicatorId=" + indicators[i].id;
    }
    
    for ( p in periods )
    {
      url += "&periodId=" + periods[p].id;
    }
    
    chartWidth = 1000;
  }
  else if ( chartIndicators == "all" && chartDimension == "orgunit" )
  {
    url += "?periodId=" + currentPeriod + "&dimension=organisationUnit&regression=false&chartWidth=950";
    
    for ( i in indicators )
    {
      url += "&indicatorId=" + indicators[i].id;
    }
    
    for ( o in orgunits )
    {
      url += "&organisationUnitId=" + orgunits[o].id;
    }
    
    chartWidth = 1000;
  }
  
  hideDropDown();
  
  window.open( url, "_blank", "directories=no, height=550, width=" + chartWidth + ", location=no, menubar=no, status=no, toolbar=no, scrollbars=no" );
}

/**
 * This method is called from the UI and will display the chart menu.
 * 
 * @param indicatorId the indicator identifier
 * @param periodId the period identifier
 * @param orgunitId the organisation unit identifier
 */
function viewChartMenu( indicatorId, periodId, orgunitId )
{
  currentIndicator = indicatorId;
  currentPeriod = periodId;
  currentOrgunit = orgunitId;
  
  showDropDown( "pivotMenu" );
}

/**
 * Loads the event listeners for the pivot table. Called after page is loaded.
 */
function loadListeners()
{
  var table = document.getElementById( "pivotTable" );
  
  table.addEventListener( "click", setPosition, false );
}

// -----------------------------------------------------------------------------
// Supportive methods
// -----------------------------------------------------------------------------

/**
* This method sets the position of the pivot menu, and is registered as a 
* callback function for mouse click events.
*/
function setPosition( e )
{
  var left = e.pageX + "px";
  var top = e.pageY + "px";
  
  var pivotMenu = document.getElementById( "pivotMenu" );
  
  pivotMenu.style.left = left;
  pivotMenu.style.top = top;
}

/**
 * This method is responsible for generating the pivot table.
 */
function generateTable()
{
  hideDivs();
   
  var columnIndicators = pivotIndicator ? indicators : [null];
  var columnPeriods = pivotPeriod ? periods : [null];
  var columnOrgunits = pivotOrgunit ? orgunits : [null];
  
  var rowIndicators = pivotIndicator ? [null] : indicators;
  var rowPeriods = pivotPeriod ? [null] : periods;
  var rowOrgunits = pivotOrgunit ? [null] : orgunits;

  var table = document.getElementById( "pivotTable" );

  clearTable( table );
  
  var columns = getColumns( columnIndicators, columnPeriods, columnOrgunits );
  var rows = getRows( rowIndicators, rowPeriods, rowOrgunits );
  
  var columnDimensions = getColumnDimensions();
  var rowDimensions = getRowDimensions();
  
  var colSpans = getSpans( columnDimensions );
  var rowSpans = getSpans( rowDimensions );

  var html = "<tr>";

  // ---------------------------------------------------------------------------
  // Column headers
  // ---------------------------------------------------------------------------

  for ( d in columnDimensions )
  {
    for ( rowDimension in rowDimensions ) // Make space for row header
    {
      html += "<td class='row'></td>"; 
    }
    
    var dimension = columnDimensions[d];
    var colSpan = colSpans[dimension];
    
    for ( c in columns )
    {
      var modulus = c % colSpan;
      
      if ( modulus == 0 )
      {
        html += "<td class='column' colspan='" + colSpan + "'>" + columns[c][dimension]  + "</td>";
      }
    }
    
    html += "</tr>";
  }
  
  // ---------------------------------------------------------------------------
  // Rows
  // ---------------------------------------------------------------------------

  for ( r in rows )
  {
    html += "<tr>";    
    
    for ( d in rowDimensions ) // Row headers
    {
      var dimension = rowDimensions[d];
      var rowSpan = rowSpans[dimension];
      var modulus = r % rowSpan;
      
      if ( modulus == 0 )
      {
        html += "<td class='row' rowspan='" + rowSpan + "'>" + rows[r][dimension] + "</td>";
      }
    }
    
    for ( c in columns ) // Values
    {
      var value = getValue( columns[c], rows[r] );
      
      var ids = mergeArrays( columns[c], rows[r] );
      
      html += "<td class='cell' onclick='viewChartMenu( \"" + ids.indicatorId + "\", \"" + ids.periodId + "\", \"" + ids.orgunitId + "\" )'>" + value + "</td>";
    }
    
    html += "</tr>";
  }
  
  table.innerHTML = html;
  
  hidePivot();
}

/**
* @param dimensions array -> dimensions
*
* @return associative array ( dimension, span )
*/
function getSpans( dimensions )
{
  var spans = [];
  
  var lastIndex = ( dimensions.length - 1 );
  
  var span = 1;
  
  for ( var i=lastIndex; i>=0; i-- )
  {
    var dimension = dimensions[i];
    
    spans[dimension] = span;
    
    var dimensionSize = sizes[dimension];
    
    span = ( span * dimensionSize );
  }
  
  return spans;
}

/**
* @param columnIndicators array -> associative array ( indicatorId, indicatorName )
* @param columnPeriods array -> associative array ( periodId, periodName )
* @param columnOrgunits array -> associative array ( orgunitId, orgunitName )
*
* @return array -> associative array ( indicatorId, indicator, periodId, period, orgunitId, orgunit )
*/
function getColumns( columnIndicators, columnPeriods, columnOrgunits )
{
  var columns = [];
  var columnsIndex = 0;

  for ( var i=0; i<columnIndicators.length; i++ )
  {
    for ( var j=0; j<columnPeriods.length; j++ )
    {
      for ( var k=0; k<columnOrgunits.length; k++ )
      {
        var column = [];
        
        if ( columnIndicators[i] != null )
        {
          column["indicatorId"] = columnIndicators[i].id;
          column["indicator"] = columnIndicators[i].name;
        }
        
        if ( columnPeriods[j] != null )
        {
          column["periodId"] = columnPeriods[j].id;
          column["period"] = columnPeriods[j].name;
        }
        
        if ( columnOrgunits[k] != null )
        {
          column["orgunitId"] = columnOrgunits[k].id;
          column["orgunit"] = columnOrgunits[k].name;
        }
        
        columns[columnsIndex++] = column;     
      }
    }
  }
  
  return columns;
}

/**
* @param rowIndicators array -> associative array ( indicatorId, indicatorName )
* @param rowPeriods array -> associative array ( periodId, periodName )
* @param rowOrgunits array -> associative array ( orgunitId, orgunitName )
*
* @return array -> associative array ( indicatorId, indicator, periodId, period, orgunitId, orgunit )
*/
function getRows( rowIndicators, rowPeriods, rowOrgunits )
{
  var rows = [];
  var rowsIndex = 0;

  for ( var i=0; i<rowIndicators.length; i++ )
  {
    for ( var j=0; j<rowPeriods.length; j++ )
    {
      for ( var k=0; k<rowOrgunits.length; k++ )
      {
        var row = [];
        
        if ( rowIndicators[i] != null )
        {
          row["indicatorId"] = rowIndicators[i].id;
          row["indicator"] = rowIndicators[i].name;
        }
        
        if ( rowPeriods[j] != null )
        {
          row["periodId"] = rowPeriods[j].id;
          row["period"] = rowPeriods[j].name;
        }
        
        if ( rowOrgunits[k] != null )
        {
          row["orgunitId"] = rowOrgunits[k].id;
          row["orgunit"] = rowOrgunits[k].name;
        }
        
        rows[rowsIndex++] = row;
      }
    }
  }
  
  return rows;
}

/**
* @return array -> dimension
*/
function getColumnDimensions()
{
  var dimensions = [];
   
  if ( pivotIndicator )
  {
    dimensions[dimensions.length] = "indicator";
  }
  
  if ( pivotPeriod )
  {
    dimensions[dimensions.length] = "period";
  }
  
  if ( pivotOrgunit )
  {
    dimensions[dimensions.length] = "orgunit";
  }
  
  return dimensions;
}

/**
* @return array -> dimension
*/
function getRowDimensions()
{
  var dimensions = [];
   
  if ( !pivotIndicator )
  {
    dimensions[dimensions.length] = "indicator";
  }
  
  if ( !pivotPeriod )
  {
    dimensions[dimensions.length] = "period";
  }
  
  if ( !pivotOrgunit )
  {
    dimensions[dimensions.length] = "orgunit";
  }
  
  return dimensions;
}

/**
 * @param array1 the first associative array.
 * @param array2 the second associative array.
 * 
 * @return an associative array with the merged contents of the input arrays.
 */
function mergeArrays( array1, array2 )
{
  for ( a2 in array2 )
  {
    array1[a2] = array2[a2];
  }
  
  return array1;
}

/**
 * @param column associative array ( columnId, columnName )
 * @param row associative array ( rowId, rowName )
 * 
 * @return the value for the given combination of dimension identifiers.
 */
function getValue( column, row )
{
  var key = mergeArrays( column, row );
  
  var keyString = key.indicatorId + "-" + key.periodId + "-" + key.orgunitId;
  
  var value = data[keyString];
   
  return value != null ? value : "";
}

/**
 * Clears the table.
 */
function clearTable( table )
{
  while ( table.rows.length >  0 )
  {
    table.deleteRow( 0 );
  }
}

/**
 * Clears the global variables.
 */
function clearGlobalVariables()
{
  indicators.length = 0;
  periods.length = 0;
  orgunits.length = 0;
  data.length = 0;
  sizes.length = 0;
}
