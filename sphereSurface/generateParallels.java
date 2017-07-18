/*
* Author: Grant Storey
* Written: 10/4/14
* Last Updated: 7/15/17
*
* generateParallels
* Compilation: javac generateParallels.java
* Execution: java generateParallels [color]
* Example: java generateParallels > pointData/parallels.txt
* Example: java generateParallels "#FF0000" > pointData/parallelsRed.txt
*
* Prints a list of points needed to properly draw Earth's parallels. Should be
* outputted into a file, otherwise it will just print the data to the console.
* Optionally one can include a command line consisting of a single a color in
* the "#FF3A19" format to set the color of the parallels.
*/


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class generateParallels
{

    public static void main(String[] args)
    {
        // by default, the parallels are black
        String colorHexString = "#000000";
        // if there is a color argument, overwrite the default
        if (args.length > 0) {
          colorHexString = args[0];
        }


        // the number of latitude and longitude degrees
        int totalLat = 360;
        int totalLong = 180;

        // pixel dimensions of the target file, plus a gap
        int pixelWidth = 3000;
        int pixelHeight = 2250;
        int pixelGap = 1;

        int degreeAppearance = 10;

        // generate the color as a long
        String colorString = colorHexString.substring(1);
        long myColor = Long.parseLong(colorString, 16) + Long.parseLong("-1000000", 16);

        // Note: the long value for:
        // black is -16777216, -0x1000000 (alpha 1, rgb #000000)
        // white is -1
        // long colorBlack = Long.parseLong("-16777216");

        // draw all the latitude lines
        for(int i = -(totalLat/(degreeAppearance*2)); i <= (totalLat/(degreeAppearance*2)); i++)
        {
            for(int j = 1; j < pixelHeight/pixelGap; j++)
            {
                double longit = 1.0*j*totalLong/(pixelHeight/pixelGap) - totalLong/2.0;
                System.out.println(longit + " " + (double)(i*degreeAppearance) + " " + myColor);
            }
        }

        // draw all the longitude lines
        for(int i = ((-1*totalLong/(degreeAppearance*2))); i <= (totalLong/(degreeAppearance*2)); i++)
        {
            for(int j = 0; j < pixelWidth/pixelGap; j++)
            {
                double lat = 1.0*j*totalLat/(pixelWidth/pixelGap) - totalLat/2.0;
                System.out.println((double)(i*degreeAppearance) + " " + lat + " " + myColor);
            }
        }

    }
}
