
$(document).ready(function(){
    $("#manage_images").on("click",function(){

        $.ajax({
            url: "/api/documents.json",
            dataType: 'json'
        }).done(function(data) {
            // menu displays
            Image_grid = "<div class='row' id='menu_container'>";
            Image_grid +="<div class='btn-group'>";
            Image_grid +="<a class='btn btn-success btn-sm' id='upload_image_file' data-toggle='modal' data-target='' title='upload image'><i class='fa fa-picture-o'></i></a>"
            Image_grid +="<a class='btn btn-success btn-sm' id='hide_all_image_files' title='hide all'><i class='fa fa-minus-circle'></i></a>"
            Image_grid +="<a class='btn btn-success btn-sm' id='delete_all_image_files' title='delete all'><i class='fa fa-times-circle-o'></i></a>"
            Image_grid +="<a class='btn btn-success btn-sm' id='hidden_image_files' title='hidden'><i class='fa fa-unlock-alt'></i></a>"
            Image_grid +="<a class='btn btn-success btn-sm' id='unhide_all_image_files' title='un hide all'><i class='fa fa-unlock'></i></a>"
            Image_grid +="</div>";
            Image_grid +="<div class='pull-right'>";
            Image_grid +="<a href='#' title='close' id='closing-image-manager'><i class='fa fa-times fa-2x alert-success'></i></a>"
            Image_grid +="</div>";
            Image_grid +="</div>";

            ///image displays
            Image_grid += "<div class='row' id='table_container' ><table class='col-md-10' id='image_table'>";

            var tdCounter = 0;
            $.each(data.documents,function(index,val){
                if(val['name']==="image"){
                    if(tdCounter>4){

                        Image_grid += "</tr>";
                        Image_grid += "<tr>";
                    }else{
                        Image_grid += "<td>";
                        Image_grid += "<a>";
                        Image_grid += "<a href='#' class=' preview-image-button' title='preview' data-toggle='modal' data-target=''><img class='thumbnail ' src='"+val['href']+"/data'/></a>";
                        Image_grid += "</a>";
                        Image_grid += "<span class='btn-group ' style='width:133px;'>";
                        Image_grid += "<a class='btn btn-xs btn-danger delete-image-button' title='delete' id='"+val['id']+"'><i class='fa fa-times'></i></a>";
                        if(val['id'] ==="disabled" || $("#"+val['id']).parent().parent().attr("class")==="disabled"){
                            Image_grid += "<a class='btn btn-xs btn-default unhide-image-button' title='un hide' id='"+val['id']+"'><i class='fa fa-lock'></i></a>";

                        }else{
                            Image_grid += "<a class='btn btn-xs btn-warning hide-image-button' title='hide' id='"+val['id']+"'><i class='fa fa-lock'></i></a>";

                        }
                        Image_grid += "<a class='btn btn-xs btn-success preview-image-button' title='preview' data-toggle='modal' data-target='' id='"+val['id']+"'><i class='fa fa-folder-open'></i></a>";
                        Image_grid += "</span></br>";
                        Image_grid += "<td>";
                        tdCounter++;
                    }

                }
            });

            Image_grid += "</table></div>";
            $(".Image-manager").html(Image_grid);
            $("#add_div").hide();
            $("#display_divs").hide().promise().done(function(){
                $("#Slide_manager_pane").show("slow",function(){
                    $(".disabled").hide();
                    manageImageButtons();
                });
            });

        });

    });


    function manageImageButtons(){

        // Main menus
        $("div#menu_container a").on("click",function(){
            var link_id = $(this).attr("id");
            if(link_id === "upload_image_file"){
                $(this).attr("data-target","#myModal");

                $("#myModal .modal-body").load("/dhis-web-dashboard-integration/imageform.vm").promise().done(function(){
                });



            }


            if(link_id === "hide_all_image_files"){
                //////// dialog prompt to confirm deleting image
                $("#dialog").html("Are you sure to hide?</br><span class='btn-group'><a class='btn btn-xs btn-success yes' id='yes'>yes</a><a class='btn btn-xs btn-danger no' id='no'>no</a></span>");
                $("#dialog").dialog({ title: "Hiding All Images" ,
                    show: {
                        effect: 'slide',
                        complete: function() {
                        }
                    },
                    open: function(event, ui) {
                        $(".yes").click(function(){
                            $.ajax({url:"hideAllImages.action"}).done(function(data){
                                $( "#dialog" ).dialog( "close" );
                                $("#slide_show_manager").trigger("click");
                            });

                        });

                        $(".no").click(function(){
                            $( "#dialog" ).dialog( "close" );
                        });
                    }
                });



            }
            if(link_id === "delete_all_image_files"){
                //////// dialog prompt to confirm deleting image
                $("#dialog").html("Are you sure to delete? &nbsp;&nbsp;<span style='color:red;'>Irriversible action</span></br><span class='btn-group'><a class='btn btn-xs btn-success yes' id='yes'>yes</a><a class='btn btn-xs btn-danger no' id='no'>no</a></span>");
                $("#dialog").dialog({ title: "Deleing All Images" ,
                    show: {
                        effect: 'slide',
                        complete: function() {
                        }
                    },
                    open: function(event, ui) {
                        $(".yes").click(function(){
                            $.ajax({url:"deleteAllImages.action"}).done(function(data){
                                $( "#dialog" ).dialog( "close" );
                                $("#slide_show_manager").trigger("click");
                            });

                        });

                        $(".no").click(function(){
                            $( "#dialog" ).dialog( "close" );
                        });
                    }
                });



            }

            if(link_id === "hidden_image_files"){
                $(".enabled").hide();
                $(".disabled").show("slow");
            }

            if(link_id === "unhide_all_image_files"){
                $.ajax({url:"unHideAllImages.action"}).done(function(data){
                    $(".disabled").show();
                });
            }


        });


        // Grid Menus;
        $(".delete-image-button").on("click",function(){
            var image_id = $(this).attr("id");

            //////// dialog prompt to confirm deleting image
            $("#dialog").html("Are you sure to remove? &nbsp;&nbsp;<span style='color:red;'>Irriversible action</span></br><span class='btn-group'><a class='btn btn-xs btn-success yes' id='yes'>yes</a><a class='btn btn-xs btn-danger no' id='no'>no</a></span>");
            $("#dialog").dialog({ title: "Deleting Image" ,
                show: {
                    effect: 'slide',
                    complete: function() {
                    }
                },
                open: function(event, ui) {
                    $(".yes").click(function(){

                        $.ajax({
                            type: "POST",
                            url:"deleteImage.action",
                            data: "image="+image_id,
                            success: function(){
                                $( "#dialog" ).dialog( "close" );
                                $("#slide_show_manager").trigger("click");
                            },
                            dataType: "text"
                        });

                    });

                    $(".no").click(function(){
                        $( "#dialog" ).dialog( "close" );
                    });
                }
            });




        });


        $(".hide-image-button").on("click",function(){
            var image_id = $(this).attr("id");

            /////// dialog prompt to confirm deleting image
            $("#dialog").html("Are you sure to hide?</br><span class='btn-group'><a class='btn btn-xs btn-success accept'>yes</a><a class='btn btn-xs btn-danger deny' id='deny'>no</a></span>");
            $("#dialog").dialog({ title: "Hiding Image" ,
                show: {
                    effect: 'slide',
                    complete: function() {
                    }
                },
                open: function(event, ui) {
                    $("a.accept").on("click",function(){
                        console.log("abc");
                        $.ajax({
                            type: "POST",
                            url:"hideImage.action",
                            data: "image="+image_id,
                            success: function(){
                                $( "#dialog" ).dialog( "close" );
                                $("#slide_show_manager").trigger("click");
                            },
                            dataType: "text"
                        });

                    });

                    $("a.deny").on("click",function(){
                        $( "#dialog" ).dialog( "close" );
                    });
                }
            });


            /////////////////////

        });

        $(".unhide-image-button").on("click",function(){
            var image_id = $(this).attr("id");




            $("#dialog").html("Are you sure to hide?</br><span class='btn-group'><a class='btn btn-xs btn-success accept' >yes</a><a class='btn btn-xs btn-danger deny' >no</a></span>");
            $("#dialog").dialog({ title: "Un Hiding Image" ,
                show: {
                    effect: 'slide',
                    complete: function() {
                    }
                },
                open: function(event, ui) {
                    $("a.accept").on("click",function(){

                        $.ajax({
                            type: "POST",
                            url:"unHideImage.action",
                            data: "image="+image_id,
                            success: function(){
                                $( "#dialog" ).dialog( "close" );
                                $("#slide_show_manager").trigger("click");
                            },
                            dataType: "text"
                        });


                    });

                    $("a.deny").on("click",function(){
                        $( "#dialog" ).dialog( "close" );
                    });
                }
            });


        });


        $(".preview-image-button").on("click",function(){
            var image_id = $(this).attr("id");
            var image = "<div><img src='"+$(this).parent().parent().find("img").attr("src")+"'></div>";
            $(".modal-body").html(image);
            $(this).attr("data-target","#myModal");
        });




        $("#closing-image-manager").on("click",function(){
            $( "#Slide_manager_pane" ).hide( "slow", function() {
                $("#add_div").show("slow",function(){
                    $("#display_divs").show("slow")
                });

            });
//

        });

    }
});
                    