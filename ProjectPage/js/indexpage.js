var showLightImage = false;

$(".imagePair").mouseenter(function(){
	$(".lightImage").fadeTo(0,1);
	$(".inputImagesHeader").text("Flash Images");
	$(".lshiifImagesHeader").text("Flash LSHIIF");
	showLightImage = true;
});

$(".imagePair").mouseleave(function(){
	$(".lightImage").fadeTo(0,0.5);
	$(".inputImagesHeader").text("Image Pairs");
	$(".lshiifImagesHeader").text("LSHIIF");
});

$(".imagePair").click(function() {
	if(showLightImage){
		$(".lightImage").fadeTo(0,0);
		$(".inputImagesHeader").text("No flash Images");
		$(".lshiifImagesHeader").text("No flash LSHIIF");
		showLightImage = false;
	}
	else{
		$(".lightImage").fadeTo(0,1);
		$(".inputImagesHeader").text("Flash Images");
		$(".lshiifImagesHeader").text("Flash LSHIIF");
		showLightImage = true;
	}
});