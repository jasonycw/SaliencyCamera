// var tableOffset = $("#data-table").offset().top;
// var $header = $("#data-table > thead").clone();
// var $fixedHeader = $("#header-fixed").append($header);

// $(window).bind("scroll", function() {
//     var offset = $(this).scrollTop();

//     if (offset >= tableOffset && $fixedHeader.is(":hidden")) {
//         $fixedHeader.show();
//     }
//     else if (offset < tableOffset) {
//         $fixedHeader.hide();
//     }
// });

$(".inputImages").mouseenter(function(){
	$(".lightImage").fadeTo(0,0);
	$(".inputImagesHeader").text("No flash Image");
});

$(".inputImages").mouseleave(function(){
	$(".lightImage").fadeTo(0,1);
	$(".inputImagesHeader").text("Flash Image");
});

