//-----------------------------------------------------------------------------
//Add Patient
//-----------------------------------------------------------------------------

function validateAddRepresentative()
{	
	$.postUTF8("validatePatient.action?" + getIdentifierTypeIdParams(),
		{
		}, addValidationRepresentativeCompleted, "xml" );
}

function addValidationRepresentativeCompleted( messageElement )
{
	var type = $(messageElement).find('message').attr('type');
	var message = $(messageElement).find('message').text();
    
	 if ( type == 'success' )
	 {
		if( message == 0 ){
			jQuery.ajax({
				type: "POST"
				,url: "addRepresentative.action"
				,data: jQuery("#addRepresentativeForm").serialize()
				,dataType : "xml"
				,success: function(xml){ 
					autoChoosePerson( xml );
				}
				,error: function()
				{
					alert(i18n_error_connect_to_server);
				}
			});
		}
		else if( message == 1 ){
			showErrorMessage( i18n_adding_patient_failed + ':' + '\n' + i18n_duplicate_identifier );
		}
		else if( message == 2 ){
			showErrorMessage( i18n_adding_patient_failed + ':' + '\n' + i18n_this_patient_could_not_be_enrolled_please_check_validation_criteria );
		}
	 }
	 else if ( type == 'error' )
	 {
	     showErrorMessage( i18n_adding_patient_failed + ':' + '\n' + message );
	 }
	 else if ( type == 'input' )
	 {
	     showWarningMessage( message );
	 }
	 else if( type == 'duplicate' )
	 {
		 jQuery("#formContainer").hide();
		 showPersons("listPersonsDuplicate", messageElement);
	 }
}

//get and build a param String of all the identifierType id and its value
//excluding  identifiers which related is False
function getIdentifierTypeIdParams()
{
	var params = "";
	jQuery("#addRepresentativeForm :input.idfield").each(
		function()
		{
			if( jQuery(this).val() && !jQuery(this).is(":disabled") )
				params += "&" + jQuery(this).attr("name") +"="+ jQuery(this).val();
		}
	);
	return params;
}

function searchPerson()
{
	jQuery.ajax({
		   type: "POST"
		   ,url: "searchPerson.action"
		   ,data: jQuery("#searchForm").serialize()
		   ,dataType : "xml"
		   ,success: function(xmlObject){
				showPersons( "searchForm div[id=listPersons]", xmlObject );
			}
		   ,error: function(request,status,errorThrown)
		   {
				alert(i18n_error_connect_to_server);
		   }
		 });
}

function showPersons( divContainer, xmlElement )
{
	var container = jQuery("#"+divContainer);
	container.html("");
	var patients = $(xmlElement).find('patient');
	var sPatient = "";
	
	if ( patients.length == 0 )
	{
		var message = "<p>" + i18n_no_result + "</p>";
		container.html(message);
	}
	
	$( patients ).each( function( i, patient )
    {
		sPatient += "<hr style='margin:5px 0px;'><table>";
		sPatient += "<tr><td class='bold'>" + i18n_patient_system_id + "</td><td>" + $( patient ).find('systemIdentifier').text() + "</td></tr>" ;
		
		var identifiers =  $( patient ).find('identifier');
		$( identifiers ).each( function( i, identifier )
		{
			sPatient +="<tr class='identifierRow" + $(identifier).find('id').text() + "' id='iden" + $(identifier).find('id' ).text() + "'>"
				+"<td class='bold'>" + $(identifier).find('name').text() + "</td>"
				+"<td class='value'>" + $(identifier).find('value').text() + "</td>	"	
				+"</tr>";
		});
		
		var attributes = $( patient ).find('attribute');
		$( attributes ).each( function( i, attribute )
		{
				sPatient += "<tr class='attributeRow'>"
					+ "<td class='bold'>" + $(attribute).find('name').text() + "</td>"
					+ "<td>" + $(attribute).find('value').text() + "</td>	"	
					+ "</tr>";
		});
		
		sPatient += "<tr><td colspan='2'><input type='button' id='" + $(patient).find('id' ).first().text() +"' value='" + i18n_choose_this_person + "' onclick='choosePerson(this)'/></td></tr>";
		sPatient += "</table>";
		
		container.append(i18n_duplicate_warning + "<br>" + sPatient);
	 } );
}

// Will be call after save new person successfully
function autoChoosePerson( xmlElement )
{
	jQuery("#tab-2").html("<center><span class='bold'>" + i18n_add_person_successfully + "</span></center>");
	var root = jQuery(xmlElement);
	jQuery("#patientForm [id=representativeId]").val( root.find("id").text() );
	jQuery("#patientForm [id=relationshipTypeId]").val( root.find("relationshipTypeId").text() );
	root.find("identifier").each(
			function(){
				var inputField = jQuery("#patientForm iden" + jQuery(this).find("identifierTypeId").text());
				inputField.val( jQuery(this).find("identifierText").text() );
				inputField.attr({"disabled":"disabled"});
			}
	);
}

//------------------------------------------------------------------------------
// Set Representative information to parent page.
//------------------------------------------------------------------------------

function choosePerson(this_)
{
	var relationshipTypeId = jQuery("#searchForm [id=relationshipTypeId]").val();
	if( isBlank( relationshipTypeId ))
	{
		alert(i18n_please_select_relationshipType);
		return;
	}
	
	var id = jQuery(this_).attr("id");
	jQuery("#patientForm [id=representativeId]").val(id);
	jQuery("#patientForm [id=relationshipTypeId]").val(relationshipTypeId);
	jQuery(".identifierRow"+id).each(function(){
		var inputField = window.parent.jQuery("#"+jQuery(this).attr("id"));
		if( inputField.metadata({type:"attr",name:"data"}).related  )
		{
			// only inherit identifierType which related is true
			inputField.val(jQuery(this).find("td.value").text());
			inputField.attr({"disabled":"disabled"});
		}
	});
	
	jQuery('#representativeDiv').dialog('close');
}

function toggleSearchType(this_)
{
	var type = jQuery(this_).val();
	if( "identifier" == type )
	{
		jQuery("#searchForm [id=rowIdentifier]").show().find("identifierTypeId").addClass('required:true');
		jQuery("#searchForm [id=rowAttribute]").hide().find("id=attributeId").removeClass("required");
		jQuery("#searchForm [id=searchValue]").val("");
	}
	else if( "attribute" == type )
	{
		jQuery("#searchForm [id=rowIdentifier]").hide().find("#identifierTypeId").removeClass("required");
		jQuery("#searchForm [id=rowAttribute]").show().find("#attributeId").addClass("required:true");
		jQuery("#searchForm [id=searchValue]").val("");
	}
	else if( "name" == type || "" == type )
	{
		jQuery("#searchForm [id=rowIdentifier]").hide().find("#identifierTypeId").removeClass("required");
		jQuery("#searchForm [id=rowAttribute]").hide().find("#attributeId").removeClass("required");
		jQuery("#searchForm [id=searchValue]").val("");
	}
}

function isBlank(text)
{
	return !text ||  /^\s*$/.test(text);
}

