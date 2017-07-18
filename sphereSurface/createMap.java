/*
* Author: Grant Storey
* Written: 10/4/14
* Last Updated: 7/15/17
*
* createMap
* Compilation: javac createMap.java
* Execution: java createMap SourceFilename MapType OutputFilename NumInputs [Input Files]
* Example: java createMap blank.png 1 myMercator 2 earthInColor.txt parallels.txt
*
* For small-scale use, this long string of command line inputs is perfectly
* fine, but if one wanted to make this more accessible it would be best to
* use something like Apache's Commons CLI to make most of these into optional
* command line flags.
*
* Creates a map image saved at location outputImages/[OutputFilename].png based
* on the command line argument. The type of map created is based on the integer
* input MapType, with the maps corresponding to each integer outlined below.
* The image is created using input text files (which must be located in the
* folder "pointData"). Each line in the file has the longitude, latitude, and
* color of a point on the earth, and this point is drawn onto the base png
* image located at baseImages/[SourceFilename] (note that the source filename
* includes the extension). Points in earlier files are drawn first, so in the
* example above the parallels are drawn on top of the world countries.
*
*/

// Map Types:
// 0 is Equirectangular
// 1 is Mercator
// 2 is Lagrange Conformal
// 3 is Eckert II
// 4 is Kavrayskiy VII
// 5 is two Stereographic hemispheres (centered at the poles) placed together.
// 6 is the gnomonic cube


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class createMap
{

    // calculates great circle distance between points (long1, lat1) and (long2, lat2)
    private static double distance(double long1, double lat1, double long2, double lat2)
    {
        double a = Math.sin((long2-long1)*(Math.PI/180)/2);
        double b = Math.sin((lat2-lat1)*(Math.PI/180)/2);
        double c = b*b*Math.cos(long1*(Math.PI/180))*Math.cos(long2*(Math.PI/180));
        return 2*Math.asin(Math.sqrt(a*a + c));
    }

    // calculates the desired x position of the point at longitude longit and
    // latitude lat on a map of size width by height, given that the map
    // projection is of type type
    private static double getX(double longit, double lat, int width, int height, int type)
    {
        switch (type)
        {
            case 6:
                int latOffset = 225;
                lat += latOffset;
                int sideLength = Math.min(((width-1)/4),((height-1)/3));
                double xCenter = 0.0;
                double yCenter = 0.0;
                double longCenter;
                double latCenter;
                double normalLat = lat;
                while (normalLat < 0)
                {
                    normalLat += 360;
                }
                double targetLat = (normalLat)-(normalLat%90) + 45;
                int i;
                //-135 puts it above the second square
                // 45 above the 4th square
                double poleLatChange = 45;
                if (distance(-45,targetLat,longit,lat) > Math.PI/2) // north
                {
                    lat += poleLatChange;
                    i = -1;
                    xCenter = 3.5*sideLength;
                    yCenter = 0.5*sideLength;
                    longCenter = 90.0;
                    latCenter = 0.0;
                }
                else if (distance(45,targetLat,longit,lat) > Math.PI/2) // south
                {
                    lat += poleLatChange;
                    i = -2;
                    xCenter = 3.5*sideLength;
                    yCenter = 2.5*sideLength;
                    longCenter = -90.0;
                    latCenter = 0.0;
                }
                else
                {
                    i = ((int) Math.floor(normalLat/90))%4;
                    longCenter = 0.0;
                    latCenter = 90*i + 45;
                    xCenter = (i+0.5)*sideLength;
                    yCenter = 1.5*sideLength;
                }
                double cosC = Math.sin(longCenter*(Math.PI/180))*Math.sin(longit*(Math.PI/180))
                            + Math.cos(longCenter*(Math.PI/180))*Math.cos(longit*(Math.PI/180))*Math.cos((lat-latCenter)*(Math.PI/180));
                //System.out.println(Math.cos(lat*(Math.PI/180))*Math.sin((longit-longCenter)*(Math.PI/180))+","+cosC+","+i);
                //if (i != -1) return 0;
                return xCenter + sideLength/2*Math.cos(longit*(Math.PI/180))*Math.sin((lat-latCenter)*(Math.PI/180))/cosC;
            case 5:
                double phi;
                if (longit < 0) phi = -longit;
                else phi = longit;
                phi += 90;
                phi *= (Math.PI/180);
                double theta = lat * (Math.PI/180);
                double scale5 = Math.min((height-1)/2.0, (width-1)/4.0);
                double r = scale5 * (1/Math.tan(phi/2.0));
                if (longit > 0)
                {
                    return (int) (-1*r*Math.cos(theta) + (width-1)/2 +-1*scale5);
                }
                else
                {
                    return (int) (r*Math.cos(theta) + (width-1)/2 + scale5);
                }
            case 4:
                longit *= (Math.PI/180);
                lat *= (Math.PI/180);
                double scale4 = ((width-2)/2.0)*Math.sqrt(3)/(Math.PI*1.5);
                return scale4*1.5*lat*Math.sqrt((Math.PI*Math.PI)/3 - longit*longit)/Math.PI+width/2.0;

            case 3:
                longit *= (Math.PI/180);
                lat *= (Math.PI/180);
                double scale3 = ((width-2)/2.0)/(2*Math.PI);
                return scale3*lat*Math.sqrt((4 - 3*Math.sin(Math.abs(longit)))) + width/2.0;

            case 2:
                longit *= (Math.PI/180);
                lat *= (Math.PI/180);
                double sinPhiPrime = Math.tan(longit/2);
                double cosPhiPrime = Math.sqrt(1-sinPhiPrime*sinPhiPrime);//Math.cos(Math.asin(sinPhiPrime));
                return ((height-1)/2.0)*cosPhiPrime*Math.sin(lat/2)/(1
                            + cosPhiPrime*Math.cos(lat/2)) + (width/2);

            case 1:
                return ((lat/360.0)*(width-1)) + (width-1)/2.0;

            case 0:
                return (lat/360.0)*(width-1) + (width-1)/2.0;

            default:
                throw new IllegalArgumentException("Invalid Map Type");
        }
    }

    // calculates the desired y position of the point at longitude longit and
    // latitude lat on a map of size width by height, given that the map
    // projection is of type type
    private static double getY(double longit, double lat, int width, int height, int type)
    {

        switch (type)
        {
            case 6:
                int latOffset = 225;
                lat += latOffset;
                int sideLength = Math.min(((width-1)/4),((height-1)/3));
                double xCenter = 0.0;
                double yCenter = 0.0;
                double longCenter;
                double latCenter;
                double normalLat = lat;
                while (normalLat < 0)
                {
                    normalLat += 360;
                }
                double targetLat = (normalLat)-(normalLat%90) + 45;
                int i;
                //-135 puts it above the second square
                // 45 above the 4th square
                double poleLatChange = 45;
                if (distance(-45,targetLat,longit,lat) > Math.PI/2) // north
                {
                    lat += poleLatChange;
                    i = -1;
                    xCenter = 3.5*sideLength;
                    yCenter = 0.5*sideLength;
                    longCenter = 90.0;
                    latCenter = 0.0;
                }
                else if (distance(45,targetLat,longit,lat) > Math.PI/2) // south
                {
                    lat += poleLatChange;
                    i = -2;
                    xCenter = 3.5*sideLength;
                    yCenter = 2.5*sideLength;
                    longCenter = -90.0;
                    latCenter = 0.0;
                }
                else
                {
                    i = ((int) Math.floor(normalLat/90))%4;
                    longCenter = 0.0;
                    latCenter = 90*i + 45;
                    xCenter = (i+0.5)*sideLength;
                    yCenter = 1.5*sideLength;
                }
                double cosC = Math.sin(longCenter*(Math.PI/180))*Math.sin(longit*(Math.PI/180))
                              + Math.cos(longCenter*(Math.PI/180))*Math.cos(longit*(Math.PI/180))*Math.cos((lat-latCenter)*(Math.PI/180));
                //if (i != -1) return 0;
                return yCenter - sideLength/(2)*(Math.cos(longCenter*(Math.PI/180))*Math.sin(longit*(Math.PI/180))
                       - Math.sin(longCenter*(Math.PI/180))*Math.cos(longit*(Math.PI/180))*Math.cos((lat-latCenter)*(Math.PI/180)))/cosC;
            case 5:
                double phi;
                if (longit < 0) phi = -longit;
                else phi = longit;
                phi += 90;
                phi *= (Math.PI/180);
                double theta = lat * (Math.PI/180);
                double scale5 = Math.min((height-1)/2.0, (width-1)/4.0);
                double r = scale5 * (1/Math.tan(phi/2.0));
                return (int) (r*Math.sin(theta) + (height-1)/2);
            case 4:
                longit *= (Math.PI/180);
                double scale4 = ((width-2)/2.0)*Math.sqrt(3)/(Math.PI*1.5);
                return -1*scale4*longit+height/2.0;

            case 3:
                if(longit == 0)
                {
                    return height/2.0;
                }
                longit *= (Math.PI/180);
                lat *= (Math.PI/180);
                double scale3 = ((width-1)/2.0)/2;
                double y3 = (2 - Math.sqrt(4 - 3*Math.sin(Math.abs(longit))))*(longit/Math.abs(longit));
                return -1*scale3 * y3 + height/2.0;

            case 2:
                longit *= (Math.PI/180);
                lat *= (Math.PI/180);
                double sinPhiPrime = Math.tan(longit/2);
                double cosPhiPrime = Math.sqrt(1-sinPhiPrime*sinPhiPrime);//Math.cos(Math.asin(sinPhiPrime));
                return -1*((height/2.0-1))*sinPhiPrime/(1 + cosPhiPrime*Math.cos(lat/2)) + (height/2);

            case 1:
                longit *= (Math.PI/180);
                double y1 = Math.log(Math.tan(0.25*Math.PI + 0.5*longit));
                y1 *= -1*height/4.0;
                y1 += height/2.0;
                y1 = Math.max(Math.min(y1,height-1),0);
                return y1;

            case 0:
                return -1*(longit/180)*(height-1) + (height-1)/2.0;

            default:
                throw new IllegalArgumentException("Invalid Map Type");
        }
    }

    // draw the point (3x3 pixels) at position x, y in BufferedImage image
    // and make the point have the rgb color given by color.
    // draw a square with sides of len
    private static void draw3by3Point(int x, int y, int color, BufferedImage image)
    {

        int width = image.getWidth();
        int height = image.getHeight();
        image.setRGB(x, y, color);

        if(y > 0)
        {
            image.setRGB(x, y-1, color);
        }
        if(y < height-1)
        {
            image.setRGB(x, y+1, color);
        }

        if(x > 0)
        {
            if(y > 0)
            {
                image.setRGB(x-1, y-1, color);
            }
            image.setRGB(x-1, y, color);
            if(y < height-1)
            {
                image.setRGB(x-1, y+1, color);
            }
        }
        if(x < width-1)
        {
            if(y > 0)
            {
                image.setRGB(x+1, y-1, color);
            }
            image.setRGB(x+1, y, color);
            if(y < height-1)
            {
                image.setRGB(x+1, y+1, color);
            }
        }
    }

    // writes from k input files onto the blank file, then saves it as the final name
    public static void main(String[] args)
    {
        // parse arguments
        String sourceImageFilename = "baseImages/" + args[0];
        String outputFileName = "outputImages/" + args[2] + ".png";
        int myType = Integer.parseInt(args[1]);
        int numFiles = Integer.parseInt(args[3]);

        // load source file
        BufferedImage img = null;
        File sourceImageFile = null;
        sourceImageFile = new File(sourceImageFilename);
        try
        {
            img = ImageIO.read(sourceImageFile);
        } catch (IOException e)
        {
            throw new IllegalArgumentException("Invalid Image File");
        }

        // draw datapoints onto the image
        Scanner myScanner;
        int width = img.getWidth();
        int height = img.getHeight();

        int count = 0;
        for(int i = 0; i < numFiles; i++)
        {
            String myFileName = "pointData/" + args[i+4];
            File myFile = new File(myFileName);
            try {
                myScanner = new Scanner(myFile);
                while(myScanner.hasNextDouble())
                {
                    double longit = myScanner.nextDouble();
                    double lat = myScanner.nextDouble();
                    int color = myScanner.nextInt();
                    int x = (int)Math.round(getX(longit,lat,width,height, myType));
                    int y = (int)Math.round(getY(longit,lat,width,height, myType));

                    ///* Error Checking
                    if( x >= width)
                    {
                        System.out.println("x:"+x+";y:"+y+";longit:"+longit+";lat:"+lat);
                    }
                    if( y >= height)
                    {
                        System.out.println("x:"+x+";y:"+(y)+";longit:"+longit+";lat:"+lat);
                    }
                    if(x < 0)
                    {
                        System.out.println("x:"+x+";y:"+y+";longit:"+longit+";lat:"+lat);
                    }
                    if( y < 0)
                    {
                        System.out.println("x:"+x+";y:"+y+";longit:"+longit+";lat:"+lat);
                    }
                    //*/

                    draw3by3Point(x, y, color, img);

                }
                myScanner.close();
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("File " + myFileName
                                                   + " is invalid");
            }
        }

        // Write the image to the specified output
        try
        {
            File outputfile = new File(outputFileName);
            ImageIO.write(img, "png", outputfile);
        } catch (IOException e)
        {
            System.err.println(e);
        }


    }
}
