$(document).ready(function(){

//    $("#content div.all").hide(); // Initially hide all content
    $("#tabs li:first").attr("id","current"); // Activate first tab
    $("#content div.all").fadeIn(); // Show first tab content

    $('#tabs a').on("click",function(e) {
        e.preventDefault();
        $("#content div.all").hide(); //Hide all content
        $("#tabs li ").attr("id",""); //Reset id's
        $(this).parent().attr("id","current"); // Activate this

        var selected_option = $(this).attr("id");

        if(selected_option === "all"){
            $("div.all").hide();
            current_option = selected_option;
            $("div."+current_option).fadeIn(); // Show content for current tab
            $(".disabled").hide();
        }else if(selected_option === "kilimo"){
            $("div.all").hide();
            current_option = selected_option;
            $("div."+current_option).fadeIn(); // Show content for current tab
            $(".disabled").hide();
        }else if(selected_option === "mifugo"){
            $("div.all").hide();
            current_option = selected_option;
            $("div."+current_option).fadeIn(); // Show content for current tab
            $(".disabled").hide();
        }else if(selected_option === "uvuvi"){
            $("div.all").hide();
            current_option = selected_option;
            $("div."+current_option).fadeIn(); // Show content for current tab
            $(".disabled").hide();
        }
    });
});


