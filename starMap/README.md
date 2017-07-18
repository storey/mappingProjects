# Mapping the Stars

This folder contains code for mapping star data.

## Worflow

For the final assignment in my class, I worked on mapping the sky in an alternate world where all stars were a yellow dwarf like Earth's sun. Essentially, the brightness of a star in the night sky would be a function only of its distance, so the nearby Alpha Centauri stars would be very bright and distant Betelgeuse would be practically invisible. The final version can be viewed in finalPoster.png.

In order to create the gnomonic cube image visible there, take the following steps:

1) Compile readHYGData.java with `javac readHYGData.java`
2) Extract location and magnitude data from the HYG dataset with `java readHYGData rawData/hygfull.csv 1 > pointData/brightestAlternateHYG.txt`
3) Compile createMapStdDraw.java with `javac createMapStdDraw.java`
4) Create the gnomonic cube map of this alternate universe with `java createMapStdDraw gnomonicBG.png 6 alternateGnomonic 1 brightestAlternateHYG.txt`. It will take a few moments to complete the drawing, and you will have to close the draw window yourself.

One could also create a variety of other star images. To look at what the actual night sky looks like, with stars colored (in the light-orange to light-blue range) according to their actual Color Index where possible, take the following steps (assuming everything is compiled):
1) Get the point data for the stars as the appear in real life with (with color data) `java readHYGData rawData/hygfull.csv 0 1 > pointData/brightestHYGWithColor.txt`.
2) Create a set of two stereographic hemispheres of this star data with `java createMapStdDraw blankBlack.png 5 trueSkyWithColor 1 brightestHYGWithColor.txt`.

See comments in createMapStdDraw.java for more specific notes on usage and how to create more images.

## Files
- createMapStdDraw.java - This contains the code for creating a map using a base png and one or more sets of point data. See comments in the file for more specific notes on usage.
- readHYGData.java - This file contains code for parsing a raw CSV of star data into a format usable by the map drawer.
- StdDraw.java - [Princeton's drawing utilities](http://introcs.cs.princeton.edu/java/stdlib/javadoc/StdDraw.html).
- baseImages/blankBlack.png - This is a simple black background.
- baseImages/gnomonicBG.png - This is a black background with outlines for the various sections of the Gnomonic Cube.
- rawData/hygfull.csv - [The full HYG 1.1 star data set](http://www.astronexus.com/hyg), containing information about various stars.
- finalPoster.png - The final poster for my final project.
- outputImages/alternateGnomonic.png - The output of the first workflow above, with a gnomonic cube in the alternate world.
- outputImages/trueSkyWithColor.png - The output of the second workflow above, with the two hemispheres of our night sky and stars colored based on their spectral values.
