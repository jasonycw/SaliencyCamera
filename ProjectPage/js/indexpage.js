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

var showLightImage = false;

$(".inputImages").mouseenter(function(){
	$(".lightImage").fadeTo(0,1);
	$(".inputImagesHeader").text("Flash Images");
	showLightImage = true;
});

$(".inputImages").mouseleave(function(){
	$(".lightImage").fadeTo(0,0.5);
	$(".inputImagesHeader").text("Image Pairs");
});

$(".inputImages").click(function() {
	if(showLightImage){
		$(".lightImage").fadeTo(0,0);
		$(".inputImagesHeader").text("No flash Images");
		showLightImage = false
	}
	else{
		$(".lightImage").fadeTo(0,1);
		$(".inputImagesHeader").text("Flash Images");
		showLightImage = true;
	}
});