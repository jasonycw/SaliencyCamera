# SaliencyCamera
This is an Android application for implementing the flash no-flash image saliency detection algorithm posted by Shengfeng He.

The original project page can be found here 
http://www.shengfenghe.com/saliency-detection-with-flash-and-no-flash-image-pairs.html

To improve the saliency detection for day-to-day useage, motion detection is required for improving the accuracy for saliency detection algorithm.
So, Locality Sensitive Histograms is used to for removing the illumination factor to help motion compensation.

Detail for Locality Sensitive Histograms can be found here
http://www.shengfenghe.com/visual-tracking-via-locality-sensitive-histograms.html

This application also utilizes the OpenCV library for Android to do the image calculation and Optical Flow detection

The testing result will be posted here http://jasonycw.github.io/SaliencyCamera/
