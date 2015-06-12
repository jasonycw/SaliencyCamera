$( document ).ready(function(){
	console.log("Document is ready");
	var testCaseRowHTML = "<tr name='titleRow' class='titleRow'><td></td><td name='title'></td><td></td><td></td><td></td><td></td><td></td></tr><tr name='testCaseRow'><td name='index' class='indexCell' style='min-width:50px'></td><td><div name='inputImages' class='imagePair'><img name='noFlashImage' class='referenceImage'><img name='flashImage' class='lightImage'></div></td><td><div name='lshiifImages' class='imagePair'><img name='noFlash_lshiif' class='referenceImage'><img name='flash_lshiif' class='lightImage'></div></td></td></td><td><img name='opticalFlow'></td><td><img name='differenceImage'></td><td><img name='saliencyWithoutMD'></td><td><img name='saliencyWithMD'></td></tr>";

	for (i=0 ; i<testCases.length ; i++) {
		document.getElementById('data-table').innerHTML += testCaseRowHTML;
	}
	var allTitleRow = document.getElementsByName('titleRow');
	var allTestCaseRow = document.getElementsByName('testCaseRow');
	for(i=0;i<testCases.length;i++){
		// Title and index
		$("td[name*='title']")[i].textContent = testCases[i].category;
		$("td[name*='index']")[i].textContent = i;
		
		// Input Images
		$("tr[name*='testCaseRow'] img[name*='noFlashImage']")[i].src = './ProjectPage/img/'+testCases[i].noFlashImage;
		$("tr[name*='testCaseRow'] img[name*='flashImage']")[i].src = './ProjectPage/img/'+testCases[i].flashImage;

		// Output Images
		$("tr[name*='testCaseRow'] img[name*='flash_lshiif']")[i].src = './ProjectPage/img/'+testCases[i].flash_lshiif;
		$("tr[name*='testCaseRow'] img[name*='noFlash_lshiif']")[i].src = './ProjectPage/img/'+testCases[i].noFlash_lshiif;
		$("tr[name*='testCaseRow'] img[name*='opticalFlow']")[i].src = './ProjectPage/img/'+testCases[i].opticalFlow;
		$("tr[name*='testCaseRow'] img[name*='differenceImage']")[i].src = './ProjectPage/img/'+testCases[i].differenceImage;
		$("tr[name*='testCaseRow'] img[name*='saliencyWithoutMD']")[i].src = './ProjectPage/img/'+testCases[i].saliencyWithoutMD;
		$("tr[name*='testCaseRow'] img[name*='saliencyWithMD']")[i].src = './ProjectPage/img/'+testCases[i].saliencyWithMD;

		console.log("Test case " + i + " are loaded");
	}

	$(".lightImage").fadeTo(0,0.5);
	console.log("Test cases are loaded");
});