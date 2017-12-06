/*
This class defines an image that can have triangle morphs applied to it.
 */

import Jama.Matrix;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

public class MorphableImage extends JLabel {

    BufferedImage bim, warpedbim;
    boolean showWarped;

    // Default constructor
    public MorphableImage()
    {

    }

    // Constructor 1: Morphable image that uses the source image X and Y resolution
    public MorphableImage(String location)
    {
        bim = readImage(location);
        int oldWidth = bim.getWidth();
        int oldHeight = bim.getHeight();
        //bim = readImage(location, oldWidth*8, oldHeight*8);
        warpedbim = new BufferedImage(bim.getWidth(), bim.getHeight(), BufferedImage.TYPE_INT_RGB);
        showWarped = false;
        repaint();
    }

    // Constructor 2: Morphable image that uses a custom X and Y resolution
    public MorphableImage(String location, int xRes, int yRes)
    {
        bim = readImage(location, xRes, yRes);
        warpedbim = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_RGB);
        showWarped = false;
        repaint();
    }

    // Constructor 2: Morphable image that uses a Buffered Image that was already created
    public MorphableImage(BufferedImage bim)
    {
        this.bim = bim;
        warpedbim = new BufferedImage(bim.getWidth(), bim.getHeight(), BufferedImage.TYPE_INT_RGB);
        showWarped = false;
        repaint();
    }

    // Getters/Setters
    public BufferedImage getBufferedImage() { return showWarped ? warpedbim : bim; }
    public int getBimWidth() { return bim.getWidth(); }
    public int getBimHeight() { return bim.getHeight(); }

    public void setBufferedImage(BufferedImage bim) { this.bim = bim;}

    /*
    This function reads in an image into a BufferedImage object.
    It maintains the resolution of the source image.
     */
    public BufferedImage readImage (String file) {

        Image image = Toolkit.getDefaultToolkit().getImage(file);
        MediaTracker tracker = new MediaTracker (new Component () {});
        tracker.addImage(image, 0);
        try { tracker.waitForID (0); }
        catch (InterruptedException e) {}
        BufferedImage bim = new BufferedImage
                (image.getWidth(this), image.getHeight(this),
                        BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bim.createGraphics();
        big.drawImage (image, 0, 0, this);
        return bim;
    }

    /*
    This function reads in a particular BufferedImage object and scales it to a certain height
    and width, in pixels.
    It returns the generated BufferedImage.
     */
    public BufferedImage readImage (String file, int width, int height) {

        Image image = Toolkit.getDefaultToolkit().getImage(file);
        MediaTracker tracker = new MediaTracker (new Component () {});
        tracker.addImage(image, 0);
        try { tracker.waitForID (0); }
        catch (InterruptedException e) {}
        BufferedImage bim = new BufferedImage
                (image.getWidth(this), image.getHeight(this),
                        BufferedImage.TYPE_INT_RGB);

        // Scale the image to match the specified width and height
        BufferedImage afterBim = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        AffineTransform at = new AffineTransform();
        at.scale((double)width / (double)bim.getWidth(), (double)height / (double)bim.getHeight());
        Graphics2D big = afterBim.createGraphics();
        big.setTransform(at);
        big.drawImage (image, 0, 0, this);
        return afterBim;
    }

    /*
    This function takes in a source image and a dest image and warps it using two triangles.
    It is based off the code provided in the slides.
     */
    public void warpTriangle(BufferedImage src,
                             BufferedImage dest,
                             Triangle S,
                             Triangle D,
                             Object ALIASING,
                             Object INTERPOLATION)
    {
        AffineTransform af;

        if (ALIASING == null)
            ALIASING = RenderingHints.VALUE_ANTIALIAS_ON;
        if (INTERPOLATION == null)
            INTERPOLATION = RenderingHints.VALUE_INTERPOLATION_BICUBIC;

        // If the two triangles are not the same as each other, morph
        if (!S.equals(D)) {
            // Create matrices that will be used in solving and fill them with the necessary values
            double[][] a = new double[3][3];
            double[][] b = new double[3][3];
            double[] destx = new double[3];
            double[] desty = new double[3];

            for (int i = 0; i < 3; ++i) {
                a[i][0] = S.getX(i);
                a[i][1] = S.getY(i);
                a[i][2] = 1.0;
                b[i][0] = S.getX(i);
                b[i][1] = S.getY(i);
                b[i][2] = 1.0;
                destx[i] = D.getX(i);
                desty[i] = D.getY(i);
            }

            // Create the matrices that will have the solutions to the previously created matrices
            Matrix aMat = new Matrix(a);
            Matrix bMat = new Matrix(b);
            Matrix xVec = new Matrix(destx, 3);
            Matrix yVec = new Matrix(desty, 3);

            Matrix aVec = aMat.solve(xVec);
            Matrix bVec = bMat.solve(yVec);

            double[] aArray = aVec.getColumnPackedCopy();
            double[] bArray = bVec.getColumnPackedCopy();

            // Apply the affine transform
            af = new AffineTransform(aArray[0], bArray[0], aArray[1],
                    bArray[1], aArray[2], bArray[2]);

        }
        else
        {
            af = new AffineTransform(1,0,0,1,0,0);
        }

        GeneralPath destPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

        destPath.moveTo((float) D.getX(0), (float) D.getY(0));
        destPath.lineTo((float) D.getX(1), (float) D.getY(1));
        destPath.lineTo((float) D.getX(2), (float) D.getY(2));
        destPath.lineTo((float) D.getX(0), (float) D.getY(0));
        Graphics2D g2 = dest.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, ALIASING);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, INTERPOLATION);

        g2.clip(destPath);
        g2.setTransform(af);
        g2.drawImage(src, 0, 0, null);
        g2.dispose();

    }

    /*
    This function morphs using four different triangles.
     */
    public void warpBim(Point srcPoint, Point destPoint, int minX, int minY, int maxX, int maxY)
    {
        Triangle ta1 = new Triangle(new Point(minX,minY), srcPoint, new Point(maxX,minY));
        Triangle ta2 = new Triangle(new Point(minX,minY), destPoint, new Point(maxX,minY));
        Triangle tb1 = new Triangle(new Point(minX,maxY), srcPoint, new Point(maxX, maxY));
        Triangle tb2 = new Triangle(new Point(minX,maxY), destPoint, new Point(maxX,maxY));
        Triangle tc1 = new Triangle(new Point(minX,minY), srcPoint, new Point(minX, maxY));
        Triangle tc2 = new Triangle(new Point(minX,minY), destPoint, new Point(minX,maxY));
        Triangle td1 = new Triangle(new Point(maxX,minY), srcPoint, new Point(maxX, maxY));
        Triangle td2 = new Triangle(new Point(maxX,minY), destPoint, new Point(maxX,maxY));

        warpTriangle(bim, warpedbim, ta1, ta2, null, null);
        warpTriangle(bim, warpedbim, tb1, tb2, null, null);
        warpTriangle(bim, warpedbim, tc1, tc2, null, null);
        warpTriangle(bim, warpedbim, td1, td2, null, null);
        showWarped = true;
        repaint();
    }

    public void paintComponent(Graphics g)
    {
        Graphics2D big = (Graphics2D) g;
        if (showWarped)
            big.drawImage(warpedbim, 0, 0, this);
        else
            big.drawImage(bim, 0, 0, this);
    }
}
