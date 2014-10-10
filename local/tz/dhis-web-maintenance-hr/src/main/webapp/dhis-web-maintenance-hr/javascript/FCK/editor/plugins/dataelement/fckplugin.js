/*
 * Data element selector plugin for FCK editor.
 * Christian Mikalsen <chrismi@ifi.uio.no>
 */

// Register the command.
var dataSetIdField = window.parent.document.getElementById( 'dataSetIdField' );
var dataSetId = dataSetIdField.value;

var urlLocation = window.parent.location.href;
var urlParts = new Array();
urlParts = urlLocation.split('viewDataEntryForm.action');
var urlPath = urlParts[0]+'selectDataElement.action?dataSetId='+dataSetId

FCKCommands.RegisterCommand( 'InsertDataElement', new FCKDialogCommand( 'Insert data element', 'Data element selector', urlPath, 700, 550 ) ) ;


// Create the "Insert Data element" toolbar button.
var oInsertDataElementItem = new FCKToolbarButton( 'InsertDataElement', FCKLang.PlaceholderBtn ) ;
oInsertDataElementItem.IconPath = FCKPlugins.Items['dataelement'].Path + 'dataElement.gif' ;
FCKToolbarItems.RegisterItem( 'InsertDataElement', oInsertDataElementItem ) ;

// The object used for all operations.
var FCKSelectElement = new Object() ;

// Called by the popup to insert the selected data element.
FCKSelectElement.Add = function( attributeId, attributeName, inputType, dataType ) 
{ 		
	//alert("I passed Through attribute Name = "+ attributeName + " Attribute Id" + attributeId);
	if (inputType == "combo")
	{
		FCK.InsertHtml("<select id=\"" + attributeId + "\" name=\"" + attributeId + "\" onchange=\"saveValue( " + attributeId + ", '" + attributeName + "', '" + dataType + "' )\"" +
				" style=\"width:230px\"/><option id=\"" + 
				attributeId + "\" value=\"\"></option></select><script type='text/javascript'>attributeOptionPopulator(" + attributeId + "); </script>");
	}
	else if (inputType == "date")
		{
		FCK.InsertHtml("<input title=\"" + attributeName + "\" view=\""+attributeName+"\" name=\"entryfield\" id=\"" + 
				attributeId + "\" value=\"\" inputtype=\"" + inputType + "\" onchange=\"saveValue( " + attributeId + ", '" + attributeName + "', '" + dataType + "' )\" datatype=\"" + dataType + "\" value=\"\" style=\"width:230px\"/>" +
						"<script type='text/javascript'>	$(document).ready(function(){ datePicker( '" + attributeId + "' );	});	</script>");
		
		
		}else
			{
		FCK.InsertHtml("<input title=\"" + attributeName + "\" view=\""+attributeName+"\" name=\"entryfield\" id=\"" + 
				attributeId + "\" value=\"\" inputtype=\"" + inputType + "\" onchange=\"saveValue( " + attributeId + ", '" + attributeName + "', '" + dataType + "' )\" datatype=\"" + dataType + "\" style=\"width:230px\"/>");
			}
			 				
}
