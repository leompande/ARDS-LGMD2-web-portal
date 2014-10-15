$(document).ready(function(){

    /////////////// CMS ARTICLES PAGINATION //////////


    var items = $("ul#article_list_pages li");

    var numItems = items.length;
    var perPage = 10;

    // only show the first 2 (or "first per_page") items initially
    items.slice(perPage).hide();

    // now setup your pagination
    // you need that .pagination-page div before/after your table
    $("#ArticlePaginataionDiv_pages").pagination({
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



    /// CMS ARTICLES VIEWING AND MANIPULATION
    $("div.article_conteiner_pages").hide();
    $("div.available_articles_pages").hide();
    $("div#back_to_list").hide();
    $("ul#article_list_pages li a").on("click",function(e){
        e.preventDefault();
        $("div.available_articles_pages").hide();
        $("div#article_title").hide();
        $("div.title_pages").hide();
        $("#bs-docs-pages").hide();
        $("#pagination-docs").hide();
        $("div.article_conteiner_pages").show();
        $("div#"+$(this).attr("redirect_to")).show();
        $("div#back_to_list").show();
        var prevRedirect = $(this).attr("redirect_to");
        $("a#back_to_list_button").on("click",function(){
            $("div#"+prevRedirect).hide();
            $("div#back_to_list").hide();
            $("div.article_conteiner_pages").hide();
            $("div.available_articles_pages").hide();
            $("div.title_pages").show();
            $("#bs-docs-pages").show();
            $("#pagination-docs").show();
        });
    });

});