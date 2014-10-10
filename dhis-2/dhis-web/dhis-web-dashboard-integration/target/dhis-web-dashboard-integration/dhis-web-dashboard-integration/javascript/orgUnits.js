 /**
 * Created by kelvin Mbwilo on 8/17/14.
 */

 function prepareOrganisationUnit(){
     //creating organization unit multselect
     var district = [];
     var districtOptions="<optgroup label='Districts'>";
     var regions = [];
     var regionsOptions = "<optgroup label='Regions'>";
     var allunits = [];
     var allunitsOptions ="";
     $.getJSON( "organisationUnits.json", function( data ) {
         $.each( data.organisationUnits, function( key, value ) {
             //populate regions
             if(value.level == 2){
                 var temp ={ name: value.name, id: value.id };
                 regions.push(temp);
                 regionsOptions += '<option value="'+value.id+'">'+value.name+' Region</option>';
             }
             //populate districts
             if(value.level == 3){
                 var temp ={ name: value.name, id: value.id };
                 district.push(temp);
                 districtOptions += '<option value="'+value.id+'">'+value.name+'</option>';
             }


         })
         regionsOptions += "<optgroup/>"
         districtOptions += "<optgroup/>"
         allunitsOptions = regionsOptions + districtOptions
         $('.adminUnit').multiselect().multiselectfilter();
         $('#allunits').click(function(){
             $('.unitfilter button').removeClass('btn-primary').addClass('btn-default');
             $(this).removeClass('btn-default').addClass('btn-primary');
             $(".adminUnit").multiselectfilter("destroy");
             $(".adminUnit").multiselect("destroy");
             $('.adminUnit').html(allunitsOptions);
             $('.adminUnit').multiselect().multiselectfilter();
         })
         $('#district').click(function(){
             $('.unitfilter button').removeClass('btn-primary').addClass('btn-default');
             $(this).removeClass('btn-default').addClass('btn-primary');
             $(".adminUnit").multiselectfilter("destroy");
             $(".adminUnit").multiselect("destroy");
             $('.adminUnit').html(districtOptions);
             $('.adminUnit').multiselect().multiselectfilter();
         })
         $('#regions').click(function(){
             $('.unitfilter button').removeClass('btn-primary').addClass('btn-default');
             $(this).removeClass('btn-default').addClass('btn-primary');
             $(".adminUnit").multiselectfilter("destroy");
             $(".adminUnit").multiselect("destroy");
             $('.adminUnit').html(regionsOptions);
             $('.adminUnit').multiselect().multiselectfilter();
         })
         $('#allunits').trigger('click')
     });
 }