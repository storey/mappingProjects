/*
* Author: Grant Storey
* Written: 10/4/14
* Last Updated: 7/15/17
*
* readEqualAreaMapData
* Compilation: javac readEqualAreaMapData.java
* Execution: java readEqualAreaMapData inputFileName standardOrOblique
* Example: java readEqualAreaMapData equalAreaCylinders/earthStandard.png 0 > pointData/earthInColor.txt
* Example: java readEqualAreaMapData equalAreaCylinders/marbleOblique.png 1 > pointData/marbleDataOblique.txt
*
* Given an Lambert equal-area cylindrical map, extract all the pixels as points
* on the sphere and store them.
*
*/

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class readEqualAreaMapData
{

    // converts from pixel location to longitude
    // type = 0 for equator
    // 1 for meridian
    private static double getLong(int x, int y, int w, int h, int type)
    {
        if (type == 0)
        {
            double newY = (y+0.5) - (h/2.0);
            double scaledY = newY/((h-1)/2.0);
            double phi = Math.asin(scaledY)*180/Math.PI;
            return -phi;
        }
        else if (type == 1)
        {
            double newX = (x+0.5) - (w/2.0);
            double scaledX = newX/((w-1)/2.0);
            scaledX += 0.5;
            if (scaledX > 1)
            {
                scaledX = scaledX - 2;
            }
            double newY = (y+0.5) - (h/2.0);
            double scaledY = newY/((h-1)/2.0);
            double phi = -1*Math.asin(Math.sqrt(1-(scaledY*scaledY))*Math.sin(scaledX*Math.PI))*180/Math.PI;
            return -phi;
        }
        return 0;
    }

    // converts from pixel location to latitude
    // type = 0 for equator
    // 1 for meridian
    private static double getLat(int x, int y, int w, int h, int type)
    {
        if (type == 0)
        {
            double newX = (x+0.5) - (w/2.0);
            double scaledX = newX/((w-1)/2.0);
            double lambda = -1*scaledX*180;
            return -lambda;
        }
        else if (type == 1)
        {
            double newX = (x+0.5) - (w/2.0);
            double scaledX = newX/((w-1)/2.0);
            scaledX += 0.5;
            if (scaledX > 1)
            {
                scaledX = scaledX - 2;
            }
            double newY = (y+0.5) - (h/2.0);
            double scaledY = newY/((h-1)/2.0);
            double numerator = -1 * scaledY;
            double denominator = Math.sqrt(1-(scaledY*scaledY))*Math.cos(scaledX*Math.PI);
            double lambda = -1*Math.atan(numerator/denominator)*180/Math.PI;
            if (scaledX < -0.5)
            {
                lambda = -180 + -1*lambda;
            }
            else if(scaledX > 0.5)
            {
                lambda = 180 - lambda;
            }
            else
            {
                lambda *= -1;
            }
            lambda += 180;
            if (lambda >= 180)
            {
                lambda -= 360;
            }
            return -lambda;
        }
        return 0;
    }

    // convert the result of getRGB to a hex color
    private static String getHexColor(int rgba)
    {
        String fullRGBA = Integer.toHexString(rgba);
        return fullRGBA.substring(2,8);
    }

    public static void main(String[] args)
    {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(args[0]));
        } catch (IOException e)
        {
            System.out.println("Bad File");
        }

        int height = img.getHeight();
        int width = img.getWidth();

        // grab the orientation, either along the equator (standard) or
        // meridian (oblique)
        int orientation = Integer.parseInt(args[1]);


        // extract the pixel data.
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                double longit = getLong(i, j, width, height, orientation);

                double lat = getLat(i, j, width, height, orientation);
                int rgb = img.getRGB(i,j);

                System.out.println(longit + " " + lat + " " + rgb);
            }
        }

    }
}
