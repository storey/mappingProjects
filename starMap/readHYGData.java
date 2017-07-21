/*
* Author: Grant Storey
* Written: 10/4/14
* Last Updated: 7/18/17
*
* Compilation: javac readHYGData.java
* Execution: java readHYGData SourceFilename.csv magnitudeType includeColor
* Example: java readHYGData rawData/hygfull.csv 1 0 > pointData/brightestAlternateHYG.txt
* Example: java readHYGData rawData/hygfull.csv 0 1 > pointData/brightestHYGWithColor.txt
*
* Read and process the data from the HYG 1.1 star dataset into a format
* compatible with createMapStdDraw.java. Output position info, color info, and
* the visual magnitude of the star.
*
* If magnitudeType is 0, output the true magnitude. If it is 1, output the
* apparent magnitude from Earth if the star's absolute magnitude were the same
* as Earth's sun (that is, star brightness is a function of distance from
* Earth).
*
* If includeColor is 0, all stars will be white. Otherwise, they will be based
* on the color of the star (without atmospheric scattering accounted for).
*/


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Comparator;

public class readHYGData
{
    // Class for storing information about star data.
    private static class Star implements Comparable<Star>
    {
        private int StarID;
        private int Hip;
        private int HD;
        private String HR;
        private String Gliese;
        private String BayerFlamsteed;
        private String ProperName;
        private double RA;
        private double Dec;
        private double Distance; // in parsecs
        private double Mag;
        private double AbsMag;
        private String Spectrum;
        private double ColorIndex;
        private double NormalizedApparentMag;

        // given a CSV input line, split it and parse each of the values.
        public Star(String input)
        {
            String[] items = input.split(",");
            StarID = Integer.parseInt(items[0].trim());
            Hip = valOrNeg1(items[1].trim());
            HD = valOrNeg1(items[2].trim());
            HR = items[3].trim();
            Gliese = items[4].trim();
            BayerFlamsteed = items[5].trim();
            ProperName = items[6].trim();
            RA = valOrNAN(items[7].trim());
            Dec = valOrNAN(items[8].trim());
            Distance = valOrNAN(items[9].trim());
            Mag = valOrNAN(items[10].trim());
            AbsMag = valOrNAN(items[11].trim());
            Spectrum = items[12].trim();
            ColorIndex = valOrNAN(items[13].trim());

            NormalizedApparentMag = 4.83 + 5*((Math.log10(Distance))-1);//4.83 is sun's mag
        }

        private int valOrNeg1(String input)
        {
            if (input.equals("")) return -1;
            else return Integer.parseInt(input);
        }

        private double valOrNAN(String input)
        {
            if (input.equals("")) return Double.NaN;
            else return Double.parseDouble(input);
        }


        // comparison functions
        public int compareTo(Star that)
        {
            return compareMag(that);
        }

        public int compareMag(Star that)
        {
            if (this.Mag < that.Mag)      return -1;
            else if (this.Mag > that.Mag) return  1;
            else                          return  0;
        }

        public int compareNormMag(Star that)
        {
            if (this.NormalizedApparentMag < that.NormalizedApparentMag)      return -1;
            else if (this.NormalizedApparentMag > that.NormalizedApparentMag) return  1;
            else                          return  0;
        }

        public int compareDist(Star that)
        {
            if (this.Distance < that.Distance)      return -1;
            else if (this.Distance > that.Distance) return  1;
            else                                    return  0;
        }

        public int compareRA(Star that)
        {
            if (this.RA < that.RA)      return -1;
            else if (this.RA > that.RA) return  1;
            else                          return  0;
        }


        // getters

        public double getMag()
        {
            return Mag;
        }

        public double getNormMag()
        {
            return NormalizedApparentMag;
        }

        public double getColorIndex()
        {
            return ColorIndex;
        }

        public double getRA()
        {
            return RA;
        }

        public double getDec()
        {
            return Dec;
        }
    }

    // comparators
    private static class magComp implements Comparator<Star>
    {
        public int compare(Star a, Star b)
        {
            return a.compareMag(b);
        }
    }
    private static class normMagComp implements Comparator<Star>
    {
        public int compare(Star a, Star b)
        {
            return a.compareNormMag(b);
        }
    }
    private static class distComp implements Comparator<Star>
    {
        public int compare(Star a, Star b)
        {
            return a.compareDist(b);
        }
    }
    private static class RAComp implements Comparator<Star>
    {
        public int compare(Star a, Star b)
        {
            return a.compareRA(b);
        }
    }

    // Given a star's color index, return the visible color in RGB.
    // Some code adapted from https://stackoverflow.com/questions/21977786/star-b-v-color-index-to-apparent-rgb-color
    private static String getColorString(double colorIndex)
    {
        double bv = colorIndex;
        if (bv < -0.40) {
            bv = -0.40;
        }
        if (bv > 2.00) {
            bv = 2.00;
        }

        double r = 0.0;
        double g = 0.0;
        double b = 0.0;
        double t; // temp variable

        // red
        if (bv < 0.00) {
            t = (bv + 0.40) / (0.00 + 0.40);
            r = 0.61 + (0.11 * t) + (0.1 * t * t);
        } else if (0.00 <= bv && bv < 0.40 ) {
            t = (bv - 0.00) / (0.40 - 0.00);
            r = 0.83 + (0.17 * t);
        } else if (0.40 <= bv) {
            t = (bv - 0.40) / (2.10 - 0.40);
            r = 1.00;
        }

        // green
        if (bv < 0.00) {
            t = (bv + 0.40) / (0.00 + 0.40);
            g = 0.70 + (0.07 * t) + (0.1 * t * t);
        } else if (0.00 <= bv && bv < 0.40) {
            t = (bv - 0.00) / (0.40 - 0.00);
            g = 0.87 + (0.11 * t);
        } else if (0.40 <= bv && bv < 1.60) {
            t = (bv - 0.40) / (1.60 - 0.40);
            g = 0.98 - (0.16 * t);
        } else if (1.60 <= bv) {
            t = (bv - 1.60) / (2.00 - 1.60);
            g = 0.82 - (0.5 * t * t);
        }

        // blue
        if (bv < 0.40) {
            t = (bv + 0.40) / (0.40 + 0.40);
            b = 1.00;
        } else if (0.40 <= bv && bv < 1.50) {
            t = (bv - 0.40) / (1.50 - 0.40);
            b = 1.00 - (0.47 * t) + (0.1 * t * t);
        } else if (1.50 <=  bv) {
            t = (bv - 1.50) / (1.94 - 1.50);
            b = 0.63 - (0.6 * t * t);
        }

        // prevent some issues with the lower bound.
        r = Math.max(r, 0.0);
        g = Math.max(g, 0.0);
        b = Math.max(b, 0.0);


        int red = (int)(255.0*r);
        int green = (int)(255.0*g);
        int blue = (int)(255.0*b);
        String colorString = red + " " + green + " " + blue;

        return colorString;
    }

    // writes from k input files onto the blank file, then saves it as the final name
    public static void main(String[] args)
    {
        // get inputs
        String inputFilename = args[0];
        int magnitudeType = Integer.parseInt(args[1]);
        int includeColorInt = Integer.parseInt(args[2]);
        boolean includeColor = false;
        if (includeColorInt == 1) {
          includeColor = true;
        }

        Scanner myScanner;
        int count = 0;
        File myFile = new File(inputFilename);
        // the number of stars in the HYG catalog
        int size = 87475;

        // the threshold of star magnitude to include (weak stars are ignored)
        double magnitudeThreshold = 6.5;

        Star[] myStars = new Star[size];
        boolean[] marked = new boolean[size];
        int i = 0;


        // get the list of stars from the data file
        Star temp;
        try {
            myScanner = new Scanner(myFile);
            // get first line
            String line = myScanner.nextLine();

            // create a star for each line
            while(myScanner.hasNextLine())
            {
                line = myScanner.nextLine();
                temp = new Star(line);
                myStars[i] = temp;
                i++;

            }
            myScanner.close();
        } catch (FileNotFoundException e) {
        }

        // sort start by right ascension
        Arrays.sort(myStars, new RAComp());

        // for each star
        i = 0;

        while (i < size)
        {
            // if this star has already been handled (marked), skip it
            if (marked[i] == false)
            {
                int n = 1;
                double magToDraw;
                if (magnitudeType == 1) {
                  magToDraw = myStars[i].getNormMag();
                } else {
                  magToDraw = myStars[i].getMag();
                }
                // if there are any stars that are right next to this one
                // (i.e. within 0.1 Right Ascension and Declination) then we
                // combine their magnitudes into what appears to be one super
                // star.
                while ((i+n) < size-1 && Math.abs(myStars[i+n].getRA() - myStars[i].getRA()) < 0.01)
                {
                    if (Math.abs(myStars[i+n].getDec() - myStars[i].getDec()) < 0.01)
                    {
                        // uncomment to view which stars are close to eachother.
                        // System.err.println(i+" ; "+(i+n));
                        double newMag;
                        if (magnitudeType == 1) {
                          newMag = myStars[i+n].getNormMag();
                        } else {
                          newMag = myStars[i+n].getMag();
                        }
                        magToDraw = Math.log10(Math.pow(10,(-0.4*magToDraw))+Math.pow(10,(-0.4*newMag)))/-0.4;
                        marked[n+i] = true;
                    }
                    n++;
                }

                // if the magnitude is below a threshold, aka the star is bright
                // enough, then print the declination, a modified right
                // ascension (to be more like longitude), and the magnitude.
                if (magToDraw <= magnitudeThreshold)
                {
                    double modifiedRA = -360*(myStars[i].getRA()/24.0) + 180.0;
                    int red;
                    int green;
                    int blue;
                    double colorIndex = myStars[i].getColorIndex();
                    String colorString;
                    // if we include color and there is a color value,
                    // determine the appropriate color.
                    if (includeColor && (colorIndex != Double.NaN)) {
                      colorString = getColorString(colorIndex);
                    } else { // otherwise, just make the star white
                      red = 255;
                      green = 255;
                      blue = 255;
                      colorString = red + " " + green + " " + blue;
                    }
                    System.out.println(myStars[i].getDec() + " " + modifiedRA + " " + colorString + " " + magToDraw);
                }
            }
            i++;
        }
    }
}
