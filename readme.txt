Joshua Baunach & Clay Chaffins
CS 335
Fall 2017

Final Project

Table of Contents
1. Overview
2. Features
3. Known Limitations & Glitches
4. External Libraries Used

1. Overview
This is an implementation of the final project for CS335. This project allows you to select two images, move "control points" over the two images, and create a morph of the two images.


2. Features
You can move each of the control points onto defining areas of the source image and the destination image. Once you have specified where you want the control points, you can
preview the animation by going to Animation -> Preview Animation. There, you will see what the animation will look like as well as the control points.

If you want to change the parameters of the animation, youcan do so by going to Animation -> Adjust Animation. There, you can specify the FPS of the animation as well as
duration of the animation.

If you want to increase or decrease the number of control points you can manipulate, you can go to Animation -> Control Grid Resolution. There, you can adjust the number of control
points in both the X and Y directions. These values are clamped between 5 and 25.

If you want to import a new image to be used as a source or destination image, you can do so by using File -> Import new Source/Destination image. From there, a file prompt will open.
There, you can select the image file you want to import.

If you want to save your morph file to work on later, you can do so by going to File -> Save Morph File. There, you will see a file prompt under which you can save your morph to work
on later. The file will be saved as a .mph file. Later, you can reopen it by going to File -> Open Morph File. You can try this out by opening the sampleMorph.mph file
located in the project's directory.

If you want to export your morph, you can do so by going to File -> Export. There, you can export it as either a series of JPEG images or a MP4 video. If you choose the former,
the program will ask for a directory to save the images under. Once you have selected that, the program will save the images as frame0.jpeg to frameN.jpeg. If you choose the latter,
you will also choose a save directory. The output will be named output.mp4. An example of the output has been included in the project directory.


3. Known Limitations & Glitches
There are a few known limitations and glitches.

- If you drag a control point all the way to a line, it will cause one (or both) of the morph matrices to be singular, causing the program to perform unexpectedly.
- Although it is not possible to drag a control point beyond a line, it is possible to drag a control point such that another control point goes out-of-bounds. If this happens,
the control point that went out-of-bounds will not be draggable until it returns within bounds.
- The FPS and/or duration of the preview will almost certainly not match the final FPS/duration (unless you happen to be running this on a really beefy computer).


4. External Libraries Used
JAMA (http://math.nist.gov/javanumerics/jama/) was used to handle matrix math. 
The Apache Commons IO Library (https://commons.apache.org/proper/commons-io/) was used to help out with file saving/writing. 
Xuggler (http://www.xuggle.com/xuggler/) and SLF4J (https://www.slf4j.org/) were used to export the images to MP4.