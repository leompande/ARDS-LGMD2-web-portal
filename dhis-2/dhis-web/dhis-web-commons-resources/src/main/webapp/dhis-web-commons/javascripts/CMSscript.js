
window.imageArray = new Array();
window.documentArray = new Array();
window.hiddendocumentArray = new Array();
window.hiddenimageArray = new Array();
$(document).ready(function(){
    $(".text_inputs").hide();
    $(".save").hide();
    ///OPerate CMS switching

    $("div.onoffswitch input.onoffswitch-checkbox").on("click",function(){

        if($(this).is(':checked')){
            window.location.replace("../dhis-web-dashboard-integration/home.action");
        }else{
            window.location.replace("../dhis-web-dashboard-integration/cms.action");
        }
    });

    $("#Slide_manager_pane").hide();
    $("#other_pages").hide();
    $("#article_menus").hide();
    $("#add_div").show();

    $("div#cms_menu_bar a#manage_articles").addClass("active");
    // CMS main menus //
    $("div#cms_menu_bar a").on("click",function(){
        $("div#cms_menu_bar a.active").removeClass("active");
        $(this).addClass("active");
        var menu_name = $(this).attr("id");
        if(menu_name =="manage_pages"){
            $("#add_div").hide();
            $("#Slide_manager_pane").hide();
            $("#article_menus").hide();
            $("#add_div").hide();
            $("#other_pages").show();
            $("div.title_pages").show();
            $("#bs-docs-pages").show();
            $("#pagination-docs").show();
            $("#add_new_article_form").hide();

            $("textarea#newpage").ckeditor();

            $("#add_new_page").on("click",function(){
                $("div.display_article_for_cms").hide();
                $("form#add_new_article_form").show();

                        $('#add_new_article_form').on('submit', function(e) {
                            e.preventDefault();
                            var sendOnce = false;
                            var data = CKEDITOR.instances.newpage.getData();
                            $("#imposter_newpage").val(data);

                            if(!$("input#page_name").val()){
                                $("#page_label").css({"color":"red","font-weight":"bold"});
                                $('#page_label').blink();
                            }else{


                                $("#output_article_pane").html("<h3 ><i class='fa fa-spin fa-spinner '></i><span>Making changes please wait...</span><h3>");
                               if(!sendOnce){
                                   $.post( "addArticle.action",$(this).serialize())
                                       .done(function() {
                                           $('#add_new_article_form').find("input[type=text], textarea").val("");
                                           $("#output_article_pane").html(" ");
                                           sendOnce = true;

                                       })
                                       .fail(function() {
                                           $("#output_article_pane").html("<h3 style='color:red;'><i class='fa fa-spin fa-spinner '></i><span>Adding failure ... </span><h3>");
                                       });
                               }

                            }

                        });
                        $("#cancel_add_new_article_button").on("click",function(){
                            $('#add_new_article_form').find("input[type=text], textarea").val("");
                            $("#add_new_article_form").hide();
                            $("div.display_article_for_cms").show();
                        });

            });



                /////////////// CMS ARTICLES PAGINATION //////////


                var items = $("ul#article_list_pages li");

                var numItems = items.length;
                var perPage = 3;

                // only show the first 3 (or "first per_page") items initially
                items.slice(perPage).hide();
                // now setup your pagination
                // you need that .pagination-page div before/after your table
                $("#cmsArticlePaginataionDiv").pagination({
                    items: numItems,
                    itemsOnPage: perPage,
                    cssStyle: "compact-theme",
                    onPageClick: function(pageNumber) { // this is where the magic happens
                        // someone changed page, lets hide/show trs appropriately
                        var showFrom = perPage * (pageNumber - 1);
                        var showTo = showFrom + perPage;

                        items.hide().slice(showFrom, showTo).show();
                    }
                });



                /// CMS ARTICLES VIEWING AND MANIPULATION
                $("div.article_conteiner_pages").hide();
                $("div.available_articles_pages").hide();
                $("div#editorArticleFormContainer").hide();
                $("div.back_to_list").hide();
                $("ul#article_list_pages li a").on("click",function(e){
                    e.preventDefault();
                    $("div.available_articles_pages").hide();
                    $("div#article_title").hide();
                    $("div.title_pages").hide();
                    $("#bs-docs-pages").hide();
                    $("#pagination-docs").hide();
                    $("div.back_to_list").hide();
                    $("div#"+$(this).attr("redirect_to")).show();
                    var prevRedirect = $(this).attr("redirect_to");
                    var buttonArray = prevRedirect.split("_");
                    $("div#button_group_"+buttonArray[1]).show();
                    $("a#backArticle_"+buttonArray[1]).on("click",function(){
                        $("div#editorArticleFormContainer").hide();
                        $("div#"+prevRedirect).hide();
                        $("div.back_to_list").hide();
                        $("div.article_conteiner_pages").hide();
                        $("div.available_articles_pages").hide();
                        $("div.title_pages").show();
                        $("#bs-docs-pages").show();
                        $("#pagination-docs").show();
                    });
                    $("a.edit_article_button").on("click",function(){
                        var title   = $("div.title_"+buttonArray[1]+"_pages").html();
                        var content = $("div.body_"+buttonArray[1]+"_pages").html();
                        $("div#article_"+buttonArray[1]+"_pages").hide();
                        $("input#edit_page_name").val(title.trim());
                        $("input#imposter_article_id").val(buttonArray[1]);
                        $("textarea#article_edit").val(content.trim());
                        $("div#editorArticleFormContainer").show();
                        $("div.article_conteiner_pages").hide();
                        $("div#button_group_"+buttonArray[1]).hide();
                        $("button#cancel_article_editor").on("click",function(){
                            $("input#edit_page_name").val("");
                            $("input#imposter_article_id").val("");
                            $("input#imposter_article_edit").val("");
                            $("div#editorArticleFormContainer").hide();
                            $("div#article_"+buttonArray[1]+"_pages").show();
                            $("div#button_group_"+buttonArray[1]).show();

                        });

                        ///// process article edit
                        allowSubmit = true;
                        $("form#edit_article_form").on("submit",function(e){
                            $("input#imposter_article_edit").val(CKEDITOR.instances.article_edit.getData());
                            var formValues = $(this).serialize();
                            e.preventDefault();
                                if (!allowSubmit) return false;

                                $.ajax({
                                    type: "POST",
                                    url: 'editArticle.action',
                                    data: formValues,
                                    success: whenSucceed
                                });

                                function whenSucceed(){
                                    location.reload(true);
                                    allowSubmit = false;

                                }
                                setTimeout(function(){ allowSubmit = true; }, 5000);

                        });
                    });
                    $("a.delete_article_button").on("click",function(){
                        $("div#editorArticleFormContainer").hide();

                        var deleteArray = $(this).attr("id").split("_");

                        if (!allowSubmit) return false;

                        $.ajax({
                            type: "POST",
                            url: 'removeArticle.action',
                            data: "&item="+deleteArray[1],
                            success: whenSucceed
                        });

                        function whenSucceed(){
                            allowSubmit = false;
                            $("article_"+deleteArray[1]+"_pages").hide();
                            $("button_group_"+deleteArray[1]).hide();
                            $("a[redirect_to='article_"+deleteArray[1]+"_pages']").hide();
                            $("div#editorArticleFormContainer").hide();
                            $("div#"+prevRedirect).hide();
                            $("div.back_to_list").hide();
                            $("div.article_conteiner_pages").hide();
                            $("div.available_articles_pages").hide();
                            $("div.title_pages").show();
                            $("#bs-docs-pages").show();
                            $("#pagination-docs").show();
                        }
                        setTimeout(function(){ allowSubmit = true; }, 5000);

                    });
                });




        }
        if(menu_name =="manage_images"){
            $("#Slide_manager_pane").show();
            $("#other_pages").hide();
            $("#article_menus").hide();
            $("#add_div").hide();
        }
        if(menu_name =="manage_articles_menus"){
            $("#add_div").hide();
            $("#Slide_manager_pane").hide();
            $("#other_pages").hide();
            $("#article_menus").show();
            $("#tab_menu_table tr").each(function(){

                $(this).find("td:last a").on("click",function(){
                    var IdArray =  $(this).attr("id").split("_");
                    var menu_id = IdArray[1];
                    if($(this).attr("class").indexOf("edit")>=0){
                    var textToEdited = $("tr#rowspecific_"+menu_id+" td:first span").html();
                    $("tr#rowspecific_"+menu_id+" td:first input").val(textToEdited);
                    $("tr#rowspecific_"+menu_id+" td:first input").show();
                    $("tr#rowspecific_"+menu_id+" td:first span").hide();
                    $("tr#rowspecific_"+menu_id+" td:last a#save_"+menu_id).show();
                    $("tr#rowspecific_"+menu_id+" td:last a#edit_"+menu_id).hide();
                    $("a.save").on("click",function(){
                            var menu_id = IdArray[1];
                            var textEdited =  $("tr#rowspecific_"+menu_id+" td:first input.text_inputs").val();
                            $("tr#rowspecific_"+menu_id+" td:first span").html(textEdited);
                            $("tr#rowspecific_"+menu_id+" td:last span.signal").html("");
                            var url = "editTabMenu.action";
                            $.post( url,"&menu="+textEdited+"&menu_id="+menu_id)
                                .done(function(data){
                                    $("tr#rowspecific_"+menu_id+" td:first span").show();
                                    $("tr#rowspecific_"+menu_id+" td:first input").hide();
                                    $("tr#rowspecific_"+menu_id+" td:last ").append("<span class='alert-success signal'>&nbsp;success</span>");

                                    setTimeout(function() {
                                        $("tr#rowspecific_"+menu_id+" td:last span.signal").html("");
                                        $("tr#rowspecific_"+menu_id+" td:last a#edit_"+menu_id).show();
                                        $("tr#rowspecific_"+menu_id+" td:last a#save_"+menu_id).hide();
                                    }, 2000);
                                })
                                .fail(function(data,error){
                                    console.log(error);
                                });
                    });

                    }
                    if($(this).attr("class").indexOf("delete")>=0){
                        var menu_id = IdArray[1];
                        var url = "deleteTabMenu.action";
                        $.post( url,"&item="+menu_id)
                            .done(function(data){
                                $("tr#rowspecific_"+menu_id).remove();
                                setTimeout(function () {
                                    location.reload(true);
                                }, 2000);
                            })
                            .fail(function(data,error){
                                console.log(error);
                            });
                    }
                });
            });
            var isProcessing = false;
            $('#menu_name_form').submit(function(event) {

                event.preventDefault();
                if (!isProcessing) {
                    $("#menu_name_form").ajaxSubmit({
                        url: 'addTabMenu.action',
                        type: 'post',
                        data: $("#menu_name_form").serialize(),
                        success: function (data) {
                            isProcessing = true;
                            $("div#report").html('<div class="alert alert-success alert-dismissible" role="alert"><button type="button" id="alert_message" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button><strong>Congratulations!</strong> new menu added successfully.</div>');
                            setTimeout(function () {
                                $("#alert_message").trigger("click");
                                location.reload(true);
                            }, 2000);

                        }
                    })
                }
            });
        }
        if(menu_name =="manage_articles"){
            $("#Slide_manager_pane").hide();
            $("#other_pages").hide();
            $("#article_menus").hide();
            $("#add_div").show();
            $("#display_divs").show();
            $("#new_article").on("click",function(){
                $("#creator").show();
                $("#editor").hide();



            });
        }
    });

    /////////////// CMS ARTICLES PAGINATION //////////


        var items = $("ul#article_list li");

        var numItems = items.length;
        var perPage = 10;

        // only show the first 2 (or "first per_page") items initially
        items.slice(perPage).hide();

        // now setup your pagination
        // you need that .pagination-page div before/after your table
        $("#ArticlePaginataionDiv").pagination({
            items: numItems,
            itemsOnPage: perPage,
            cssStyle: "compact-theme",
            onPageClick: function(pageNumber) { // this is where the magic happens
                // someone changed page, lets hide/show trs appropriately
                var showFrom = perPage * (pageNumber - 1);
                var showTo = showFrom + perPage;

                items.hide() // first hide everything, then show for the new page
                    .slice(showFrom, showTo).show();
            }
        });



   ///////////// SLIDE SHOW////////////////////


    $.ajax({
        url: "../api/documents.json",
        dataType: 'json'
    }).done(function(dataDocs) {
        docIds = new Array();
        imageIds = new Array();
        Image_grid ='';
        Doc_grid = '';
        Doc_menu = '';
        Doc_menu_cms = '';
        Doc_menu_cms_hidden = '';

        jQuery.when($.getJSON("listDocuments.action",function( data ){
            var imageCounter = 0;
            var documentCounter = 0;
            var hiddenimageCounter = 0;
            var hiddendocumentCounter = 0;
            $.each(data,function(index,vals){

                if(vals.file_type=="doc"){
                    if(vals.status == "enabled"){
                        docIds[vals.file_name] = vals.id;
                        window.documentArray[documentCounter] = vals.file_name;
                        documentCounter++;
                    }
                    if(vals.status == "disabled"){
                        docIds[vals.file_name] = vals.id;
                        window.hiddendocumentArray[hiddendocumentCounter] = vals.file_name;
                        hiddendocumentCounter++;
                    }
                }


                if(vals.file_type=="image"){
                    if(vals.status == "enabled"){
                        imageIds[vals.file_name] = vals.id;
                        window.imageArray[imageCounter] = vals.file_name;
                        imageCounter++;
                    }else{
                        imageIds[vals.file_name] = vals.id;
                        window.hiddenimageArray[hiddenimageCounter] = vals.file_name;
                        hiddenimageCounter++;
                    }
                }


            });

             })).done(function () {

            $.each(dataDocs.documents,function(index,val){
                if(window.imageArray.indexOf(val['name'])>-1){
                    Image_grid +='<img class="slideProperty img-rounded img-responsive" src="'+val['href']+'/data" alt="" title="" />';
                    $(".SliderName_3").html(Image_grid).promise().done(function(){
                        demo3Effect1 = {name: 'myEffect31', top: true, move: true, duration: 400};
                        demo3Effect2 = {name: 'myEffect32', right: true, move: true, duration: 400};
                        demo3Effect3 = {name: 'myEffect33', bottom: true, move: true, duration: 400};
                        demo3Effect4 = {name: 'myEffect34', left: true, move: true, duration: 400};
                        demo3Effect5 = {name: 'myEffect35', rows: 3, cols: 9, delay: 50, duration: 100, order: 'random', fade: true};
                        demo3Effect6 = {name: 'myEffect36', rows: 2, cols: 4, delay: 100, duration: 400, order: 'random', fade: true, chess: true};
                        effectsDemo3 = [demo3Effect1,demo3Effect2,demo3Effect3,demo3Effect4,demo3Effect5,demo3Effect6,'blinds'];
                        var demoSlider_3 = Sliderman.slider({container: 'SliderName_3', width: 210, height: 130, effects: effectsDemo3, display: {autoplay: 6000}});
                    });
                }


                if(window.documentArray.indexOf(val['name'])>-1){
                        //left menu document download
                        Doc_grid += '<tr class="list-group-item">';
                        Doc_grid += '<td><a title="View Document(Download)" target="_blank" href="'+val['href']+'/data" class="text-success">';
                        Doc_grid += '<span class="fa fa-globe"></span>&nbsp;&nbsp;'+ val['name']+'</a></td>';
                        Doc_grid += '</tr>';

                        //navbar download
                        Doc_menu +='<li style="color:#ffffff!important;">';
                        Doc_menu +='<a title="View Document(Download)" target="_blank" href="'+val['href']+'/data">';
                        Doc_menu += val['name'];
                        Doc_menu +='</a>';
                        Doc_menu +='</li>';

                        //cms left menu
                        Doc_menu_cms +='<tr >';
                        Doc_menu_cms +='<td>';
                        Doc_menu_cms +='<a title="View Document(Download)" target="_blank" href="'+val['href']+'/data" class="text-success">';
                        Doc_menu_cms +='<span class="fa fa-globe"></span>&nbsp;&nbsp;'+ val['name']+'</a>';
                        Doc_menu_cms +='</td>';
                        Doc_menu_cms +='<td>';

                        Doc_menu_cms += '<a  href="#" class="hide_document" id="document_hides,'+docIds[val['name']]+'">';
                        Doc_menu_cms += '<i  style="color:orange" title="Hide Document " class="fa fa-minus pull-right"></i>';
                        Doc_menu_cms += '</a>';
                        Doc_menu_cms +='<a  class="delete_document" href="#" id="deleteDocument_'+docIds[val['name']]+'">';
                        Doc_menu_cms +='<i style="color:red;" title="Delete Document" class="fa fa-trash-o pull-right"></i>';
                        Doc_menu_cms +='</a>';
                        Doc_menu_cms +='</td>';
                        Doc_menu_cms +='</tr>';

                        $(".cms_document").html(Doc_menu_cms);
                        $(".document_menu").html(Doc_menu);
                        $(".document_panel").html(Doc_grid);
                }


                if(window.hiddendocumentArray.indexOf(val['name'])>-1){

                    Doc_menu_cms_hidden +='<tr >';
                    Doc_menu_cms_hidden +='<td>';
                    Doc_menu_cms_hidden +='<a title="View Document(Download)" target="_blank" href="'+val['href']+'/data" class="text-success">';
                    Doc_menu_cms_hidden +='<span class="fa fa-globe">&nbsp;&nbsp;</span>&nbsp;'+ val['name']+'</a>';
                    Doc_menu_cms_hidden +='</td>';
                    Doc_menu_cms_hidden +='<td>';

                    Doc_menu_cms_hidden += '<a  href="#" class="unhide_document" id="document_unhides,'+docIds[val['name']]+'">';
                    Doc_menu_cms_hidden += '<i  style="color:orange" title="Un Hide Document " class="fa fa-plus pull-right"></i>';
                    Doc_menu_cms_hidden += '</a>';
                    Doc_menu_cms_hidden +='<a  class="delete_document" href="#" id="deleteDocument_'+docIds[val['name']]+'">';
                    Doc_menu_cms_hidden +='<i style="color:red;" title="Delete Document" class="fa fa-trash-o pull-right"></i>';
                    Doc_menu_cms_hidden +='</a>';
                    Doc_menu_cms_hidden +='</td>';
                    Doc_menu_cms_hidden +='</tr>';

                    $(".cms_hidden_document").html(Doc_menu_cms_hidden);
                }



                $(".cms_document tr").each(function(){
                    $(this).find("a.delete_document").on("click",function(e){
                        e.preventDefault();
                        var unique_id = $(this).attr("id");
                        var ary = unique_id.split("_");

                        $("#dialog").html("Are you sure to Delete? &nbsp;&nbsp;<span style='color:red;'>Irriversible action</span></br><span class='btn-group'><a class='btn btn-xs btn-success' id='yes' onclick='deleteIt("+ary[1]+","+unique_id+")'>yes</a><a class='btn btn-xs btn-danger' id='no' onclick='no()'>no</a></span>");
                        $("#dialog").dialog({title: "Delete Document" ,
                            show: {
                                effect: 'slide',
                                complete: function() {

                                }
                            },
                            open: function(event, ui) {

                            }
                        });
                    });
                });
                $(".cms_hidden_document tr").each(function(){
                    $(this).find("a.delete_document").on("click",function(e){
                        e.preventDefault();
                        var unique_id = $(this).attr("id");
                        var ary = unique_id.split("_");
                        $("#dialog").html("Are you sure to Delete? &nbsp;&nbsp;<span style='color:red;'>Irriversible action</span></br><span class='btn-group'><a class='btn btn-xs btn-success yes' onclick='deleteIt("+ary[1]+","+unique_id+")'>yes</a><a class='btn btn-xs btn-danger no' onclick='no()'>no</a></span>");
                        $("#dialog").dialog({ title: "Deleting Document" ,
                            show: {
                                effect: 'slide',
                                complete: function() {
                                    console.log('animation complete');
                                }
                            },
                            open: function(event, ui) {

                            }
                        });
                    });
                });
                $(".hide_document").on("click",function(e){
                    e.preventDefault();
                    var unique_id = $(this).attr("id");
                    var ary = unique_id.split(",");

                    $.post( "hideDocument.action","docId="+ary[1])
                        .done(function() {
                            location.reload(true);
                        })
                        .fail(function() {

                        });
                });
                $(".cms_hidden_document").hide();
                $("#hidden_docs").on("click",function(){
                    $(".cms_hidden_document tr:last td a#hide_hidden").parent("td").parent("tr").remove();
                    $(".cms_hidden_document").append("<tr><td colspan='2'><a class='btn btn-sm btn-default form-control' id='hide_hidden'>hide</a></td></tr>");
                    $(".cms_hidden_document").show();

                    $(".unhide_document").on("click",function(e){
                        e.preventDefault();
                        var unique_id = $(this).attr("id");
                        var ary = unique_id.split(",");

                        $.post( "unHideDocument.action","docid="+ary[1])
                            .done(function(){
                                $(this).parent().parent().hide("slow");
                                location.reload(true);
                            })
                            .fail(function(){

                            });


                    });
                    $(".cms_hidden_document tr:last td a#hide_hidden").on("click",function(){
                        $(".cms_hidden_document").hide();
                    });
                });
            });

            });

    });


    /////////////////////////////////FILTER HTML CONTENT ///////////////////////
    (function(){


        // $("#content div.all").hide(); // Initially hide all content
        $("#tabs li:first").attr("id","current"); // Activate first tab
        $("#content div.all").fadeIn(); // Show first tab content

        $('#tabs a').on("click",function(e) {
            e.preventDefault();
            $("#content div.all").hide(); //Hide all content
            $("#tabs li ").attr("id",""); //Reset id's
            $(this).parent().attr("id","current"); // Activate this

            var selected_option = $(this).attr("id")

            if(selected_option === "all"){
                $("div.all").hide();
                current_option = selected_option;
                $("div."+current_option).fadeIn(); // Show content for current tab
                $(".disabled").hide();
            }else{
                if(selected_option === "disabled"){
                    $("div.all").hide();
                    $("div.disabled").fadeIn(); // Show content for current tab
                }else{
                    $("div.all").hide();
                    current_option = selected_option;
                    $("div."+current_option).fadeIn(); // Show content for current tab
                    $(".disabled").hide();
                }
            }
        });


    })();
    ///////////////////////////////// SUMO UTILIZATION //////////////////////////

    (function(){
        var warning = $(".warning_favourites");
        $.getJSON( "../api/charts.json", function( data ) {
            var MySelect = $('select.favourite_charts');
            var Options = "";
            $.each( data.charts, function(key,val) {
                Options += "<option value='"+val.id+"'>"+val.name+"</option>";
            });
            MySelect.html(Options);
            MySelect.multiselect({
                header: "Choose only THREE statistics!",
                click: function(e){
                    if( $(this).multiselect("widget").find("input:checked").length > 3 ){
                        warning.addClass("error").removeClass("success").html("You can only check statistics!");
                        return false;
                    }else {
                        warning.addClass("success").removeClass("error").html("Check a few statistics.");
                    }
                }
            }).multiselectfilter();
        });


    })();
    //////////////// FAVOURITES SUBMIT/////
    //validate selections

    $("#favourites").on("submit",function(e){
        e.preventDefault();
        var selected = $(this).serialize();
        if ($(".favourite_charts option:selected").length>3){
        }else if(!$(".favourite_charts option:selected").length){
            alert("No favourite selected");
        }else{
            $("#status_box").html("<h4 style='color:green;font-size:12px;'><i class='fa fa-spinner fa-spin'></i>&nbsp;favourites adding in progress..</h4>");

            $.post( "addFavourite.action",selected)
                .done(function(data) {
                    $("#status_box").html("<h4 style='color:green;font-size:12px;'><i class='fa fa-check'>&nbsp;favourites adding succeed..</h4>");
                })
                .fail(function() {
                    $("#status_box").html("<h4 style='color:red;font-size:12px;'>favourites adding failed..</h4>");

                });

        }
    });

    ////////////////////////////// CKEDITOR UTILIZATION//////////////

    $( 'textarea#content_new' ).ckeditor();
    $( 'textarea#content_edit' ).ckeditor();
    $( 'textarea#article_edit' ).ckeditor();

    $("#editor").hide();
    $("#creator").hide();
    $("#new_article").on("click",function(){
        $("#editor").hide();
        $("#creator").show("slow",function(){// show tinymcee creaor
            $("#output").html("");
            $('#add_item_form').on('submit', function(e) {
                var data = CKEDITOR.instances.content_new.getData();
                $("#imposter").val(data);
                e.preventDefault();
                $("#output").html("<h3 ><i class='fa fa-spin fa-spinner '></i><span>Making changes please wait...</span><h3>");
                var og = ""; if($("select#add_picked_cat").val() == "Select Category"){}else{og = $("select#add_picked_cat").val();}
                $.post( "addHtml.action",$(this).serialize()+"&origin="+og)
                    .done(function() {
                        $("#output").html(" ");
                        location.reload(true);
                    })
                    .fail(function() {
                        $("#output").html("<h3 style='color:red;'><i class='fa fa-spin fa-spinner '></i><span>Adding failure ... </span><h3>");
                    });

            });
            $("#cancel_creator").on("click",function(){
                $("#creator").hide();
            });

        });
    });

    ///  div contents manipulations
    //EDIT
    $("a.edit_content").on("click",function(){
        var unique_id = $(this).attr("id");
        var ary = unique_id.split(",");
        $("#creator").hide();
//        var bd = $("div#"+ary[1]+" div.conteiner").html();
        var bd = jQuery( "div[uniqueness='"+ary[1]+"'] div.conteiner" ).html();
        $.when($( 'textarea#content_edit' ).val(bd)).then(function(){
            $("#editor").show("slow",function(){
                $('#edit_item_form').on('submit', function(e) {
                    e.preventDefault();
                    var text = $( 'textarea#content_edit' ).val();
                    $("#imposter_edit").val(text);
                    $("#imposter_id").val(ary[1]);

                    $("#output_edit").html("<h3><i class='fa fa-spin fa-spinner '></i><span>editing changes please wait...</span><h3>");
                    var og = ""; if($("select#edit_picked_cat").val() == "Select Category"){}else{og = $("select#edit_picked_cat").val();}
                    $.post( "editHtml.action",$(this).serialize()+"&origin="+og)
                        .done(function() {
                            CKEDITOR.instances.content_edit.setData(" ");
                            $("#output_edit").html(" ");
                            location.reload(true);
                        })
                        .fail(function() {
                            $("#output_edit").html("<h3 style='color:red;'><i class='fa fa-spin fa-spinner '></i><span>editing failure ... </span><h3>");
                        });
                });


                $("#cancel_editor").on("click",function(){
                    $("#editor").hide();
                });
            });
        });

    });

    ///  div contents manipulations
    //EDIT
    $("a.enable_content").on("click",function(){
        var unique_id = $(this).attr("id");
        var ary = unique_id.split(",");

        $.post( "unhideHtml.action","item="+ary[1])
            .done(function() {
                $("div#"+ary[1]).removeClass("disabled").addClass("enabled").hide();
                location.reload(true);
            })
            .fail(function(){
                alert("unhide failed");
            });

    });
    $("a.top_content").on("click",function(){
        var unique_id = $(this).attr("id");
        var ary = unique_id.split(",");

        $.post( "makeTopHtml.action","item="+ary[1])
            .done(function() {
                location.reload(true);
            })
            .fail(function(){

            });

    });

    ///DISABLE
    $("a.disable_content").on("click",function(){
        var unique_id = $(this).attr("id");
        var ary = unique_id.split(",");
        $("#dialog").html("Are you sure?</br><span class='btn-group'><a class='btn btn-xs btn-success' id='yes'>yes</a><a class='btn btn-xs btn-danger' id='no'>no</a></span>");
        $("#dialog").dialog({title: "Hiding Content" ,
            show: {
                effect: 'slide',
                complete: function() {

                }
            },
            open: function(event, ui) {
                $("#yes").click(function(){
                    $.post("hideHtml.action","item="+ary[1])
                        .done(function() {
                            $( "#dialog" ).dialog( "close" );
                            $("div#"+ary[1]).hide("slow");
                            location.reload(true);
                        })
                        .fail(function() {
                            $("#output").html("<p style='color:red;'> failure </p>");
                        });
                });
                $("#no").click(function(){
                    $( "#dialog" ).dialog( "close" );
                });
            }
        });
    });
    ///REMOVE
    $("a.remove_content").on("click",function(){
        var unique_id = $(this).attr("id");
        var ary = unique_id.split(",");
        $("#dialog").html("Are you sure to delete? &nbsp;&nbsp;<span style='color:red;'>Irriversible action</span></br><span class='btn-group'><a class='btn btn-xs btn-success' id='yes'>yes</a><a class='btn btn-xs btn-danger' id='no'>no</a></span>");
        $("#dialog").dialog({ title: "Deleting Content" ,
            show: {
                effect: 'slide',
                complete: function() {

                }
            },
            open: function(event, ui) {
                $("#yes").click(function(){
                    $.post("removeHtml.action","item="+ary[1])
                        .done(function() {
                            $( "#dialog" ).dialog("close");
                            $("div#"+ary[1]).hide("slow");
                            location.reload(true);
                        })
                        .fail(function() {
                            $("#output").html("<p style='color:red;'> failure </p>");
                        });
                });
                $("#no").click(function(){
                    $( "#dialog" ).dialog( "close" );
                });
            }
        });
    });


    ///// process link
    allowSubmit = true;
    $('#addImportantLink').on('submit', function(e) {
        e.preventDefault();

        if (!allowSubmit) return false;
        allowSubmit = false;

            $.ajax({
                type: "POST",
                url: 'addLink.action',
                data: $(this).serialize(),
                success: whenSucceed
            });

        function whenSucceed(){
            location.reload(true);
        }
        setTimeout(function(){ allowSubmit = true; }, 5000);
    });

    //// link operation
    $(".remove").on("click",function(e){
        e.preventDefault()
        var unique_id = $(this).attr("id");
        var ary = unique_id.split(",");
        $("#dialog").html("Are you sure to delete? &nbsp;&nbsp;<span style='color:red;'>Irriversible action</span></br><span class='btn-group'><a class='btn btn-xs btn-success' id='yes' >yes</a><a class='btn btn-xs btn-danger' id='no' >no</a></span>");
        $("#dialog").dialog({ title: "Deleting Link" ,
            show: {
                effect: 'slide',
                complete: function() {
                    console.log('animation complete');
                }
            },
            open: function(event, ui) {
                $("#yes").on("click",function(){

                    $.post("removeLink.action","linkid="+ary[1])
                        .done(function() {
                            $( "#dialog" ).dialog( "close" );
                            $(this).parent().parent().remove("slow");
                            location.reload(true);
                        })
                        .fail(function() {
                            alert("delete failed");
                        });



                });

                $("#no").on("click",function(){
                    $( "#dialog" ).dialog( "close" );
                });
            }
        });
    });

    $(".hides").on("click",function(e){
        e.preventDefault();
        var unique_id = $(this).attr("id");
        var ary = unique_id.split(",");

        $.post( "hideLink.action","linkid="+ary[1])
            .done(function(){
                $(this).parent().parent().hide("slow");
                location.reload(true);
            })
            .fail(function(){

            });


    });


    $("#hidden_links").hide();
    $("#hidden_links_button").on("click",function(ev){
        ev.preventDefault();
        $("#hidden_links").show();
        $("#_hide").on("click",function(e){
            e.preventDefault();
            $("#hidden_links").hide();
        });
        $(".unhide").on("click",function(e){
            e.preventDefault();
            var unique_id = $(this).attr("id");
            var ary = unique_id.split(",");

            $.post( "unHideLink.action","linkid="+ary[1])
                .done(function(){
                    $(this).parent().parent().hide("slow");
                    location.reload(true);
                })
                .fail(function(){

                });


        });
    });

    ///// process docs
    $('#documentForm').on('submit', function(e) {

        var docname = $("#documentForm input#document_name").val();
        $.ajax({
            type: "POST",
            url: 'addDatabaseDocs.action',
            data: "&docname="+docname,
            success: function(){
//                location.reload(true);
            }
        });

        $.ajax({
            type: "POST",
            url: 'addDocs.action',
            data: new FormData( this ),
            success: whenSucceed
        });
        e.preventDefault();

        function whenSucceed(){
            console.log(docname);

        }

    });









});

function in_array(id, array) {
    for(var i=0;i<array.length;i++) {
        return (array[i] === id)
    }
    return false;
}

function deleteIt(id,unique_id){
    $.post("deleteDocument.action","docId="+id)
        .done(function() {
            location.reload(true);
        })
        .fail(function() {
            alert("not deleted");
        });
}
function no(){
    $("#dialog").dialog("close");
}