function testCase(category, noFlashImage, flashImage, differenceImage, lsiif, opticalFlow, saliencyWithMD, saliencyWithoutMD)
{
    this.category = category;
    this.noFlashImage = noFlashImage;
    this.flashImage = flashImage;
    this.lsiif = lsiif;
    this.opticalFlow = opticalFlow;
    this.differenceImage = differenceImage;
    this.saliencyWithoutMD = saliencyWithoutMD;
    this.saliencyWithMD = saliencyWithMD;
}
var testCases = [];