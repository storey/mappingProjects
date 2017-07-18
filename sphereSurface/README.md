# Mapping the Surface of a Sphere

This folder contains code for mapping data from the surface of a sphere onto a flat surface


## Worflow

One of the assignments for my class involved mapping the surface of a patterned marble we were given. We had to draw 3 projections, one that preserved the areas, one that preserved the shapes, and one other type. Instead of just using guestimates, I took a variety of pictures of the marble around its equator and meridian, then extracted a single sliver from each marble and strung them all together to create the Lambert equal-area cylindrical images visible in the equalAreaCylinders file. You can see that they are not perfect, but for the purposes of the assignment they would allow me to get far more accurate guides for my three maps. For Github I have added a Lambert equal-area cylindrical projection of Earth so that people can view the projections with a more familiar map.

The following goes through the creation of a two Earth maps using the tools presented here. All commands are to be typed into the command line starting at the top level of this repository.

**Part A: Extract point data from equal-area cylindrical image**
1) Compile readEqualAreaMapData.java with `javac readEqualAreaMapData.java`
2) Use this program to extract point data from the earth map with `java readEqualAreaMapData equalAreaCylinders/earthStandard.png 0 > pointData/earthInColor.txt` (we extract data from the earthStandard file, with is oriented around the equator - type 0 - and write the data to a text file in the pointData folder).

**Part B: Create point data for visualizing parallels**
3) Compile generateParallels.java with `javac generateParallels.java`
4) Use this program to create the parallels using `java generateParallels > pointData/parallels.txt`

**Part C: Create the maps**
5) Compile createMap.java with `javac createMap.java`
6) Use this program to create some maps. First, we draw onto a blank white canvas specified by blank.png, project using the Mercator projection (with index 1), name the output file myMercator, and draw 2 sets of point data onto it, the Earth color data extracted earlier and parallels generated, using the following command: `java createMap blank.png 1 myMercator 2 earthInColor.txt parallels.txt`
7) Create a Kavrayskiy VII map of earth with the command: `java createMap blank.png 4 earthKavrayskiyVII 2 earthInColor.txt parallels.txt`

See comments in createMap.java for more specific notes on usage.

## Files
- createMap.java - This contains the code for creating a map using a base png and one or more sets of point data. See comments in the file for more specific notes on usage.
- generateParallels.java - Generate text files containing the point data necessary to draw parallels for a map. Optionally specify a color for the parallels.
- readEqualAreaMapData.java - Starting with a Lambert equal-area cylindrical projection, extract the data from it to create a text file of point data.
- equalAreaCylinders/marbleStandard.png - A Lambert equal-area cylindrical projection of a marble, in the standard direction (the center line is the sphere's equator).
- equalAreaCylinders/marbleOblique.png - A Lambert equal-area cylindrical projection of a marble, in the oblique direction (the center line is the sphere's meridian).
- equalAreaCylinders/earthStandard.png - A Lambert equal-area cylindrical projection of Earth, in the standard direction. (Raster map data from naturalearthdata.com, map rendered by Uwe Dedering)
- baseImages/blank.png - a blank white background for maps.
- baseImages/blankBlack.png - a blank white background for maps.
- outputImages/myMercator.png - An example of Earth data projected via a standard Mercator projection. This is one of the end results of Workflow 1.
- outputImages/earthKavrayskiyVII.png - An example of Earth data projected to a Kavrayskiy VII projection. This is one of the end results of Workflow 1.
- outputImages/MarbleEckertII.png - An example of the Marble Data projected to an Eckert II projection.
