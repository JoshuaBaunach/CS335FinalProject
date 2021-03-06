/*
This class defines an image that can have triangle morphs applied to it.
 */

import Jama.Matrix;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

public class MorphableImage extends JLabel {

    BufferedImage bim, warpedbim, basebim;
    boolean showWarped;
    int intensity;

    // Default constructor
    public MorphableImage()
    {

    }

    // Constructor 1: Morphable image that uses the source image X and Y resolution
    public MorphableImage(String location)
    {
        //bim = readImage(location);
        basebim = readImage(location);
        bim = new BufferedImage(basebim.getWidth(), basebim.getHeight(), BufferedImage.TYPE_INT_RGB);
        applyIntensity(1000);
        intensity = 1000;
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
        //bim = readImage(location, xRes, yRes);
        basebim = readImage(location, xRes, yRes);
        bim = new BufferedImage(basebim.getWidth(), basebim.getHeight(), BufferedImage.TYPE_INT_RGB);
        applyIntensity(1000);
        intensity = 1000;
        warpedbim = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_RGB);
        showWarped = false;
        repaint();
    }

    // Constructor 3: Morphable image that uses a Buffered Image that was already created
    public MorphableImage(BufferedImage bim)
    {
        //this.bim = bim;
        this.basebim = bim;
        this.bim = new BufferedImage(basebim.getWidth(), basebim.getHeight(), BufferedImage.TYPE_INT_RGB);
        applyIntensity(1000);
        intensity = 1000;
        warpedbim = new BufferedImage(bim.getWidth(), bim.getHeight(), BufferedImage.TYPE_INT_RGB);
        showWarped = false;
        repaint();
    }

    // Getters/Setters
    public BufferedImage getBaseBufferedImage() { return basebim; }
    public BufferedImage getBufferedImage() { return showWarped ? warpedbim : bim; }
    public int getBimWidth() { return bim.getWidth(); }
    public int getBimHeight() { return bim.getHeight(); }
    public int getIntensity() { return intensity; }

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

        BufferedImage afterBim = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        try {
            BufferedImage bim = ImageIO.read(new File(file));

            // Scale the image to match the specified width and height
            afterBim = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            AffineTransform at = new AffineTransform();
            at.scale((double) width / (double) bim.getWidth(), (double) height / (double) bim.getHeight());
            Graphics2D big = afterBim.createGraphics();
            big.setTransform(at);
            big.drawImage(bim, 0, 0, this);
            return afterBim;
        }
        catch (IOException e)
        {

        }
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
    public void warpBim(Point vs1, Point vs2, Point vs3, Point vs4, Point vs5, Point vs6, Point vs7, Point vs8, Point vs9,
                        Point vd1, Point vd2, Point vd3, Point vd4, Point vd5, Point vd6, Point vd7, Point vd8, Point vd9)
    {
        Triangle tris1 = new Triangle(vs1, vs2, vs5);
        Triangle tris2 = new Triangle(vs1, vs4, vs5);
        Triangle tris3 = new Triangle(vs2, vs3, vs6);
        Triangle tris4 = new Triangle(vs2, vs5, vs6);
        Triangle tris5 = new Triangle(vs4, vs8, vs5);
        Triangle tris6 = new Triangle(vs4, vs7, vs8);
        Triangle tris7 = new Triangle(vs5, vs6, vs9);
        Triangle tris8 = new Triangle(vs5, vs8, vs9);
        Triangle trid1 = new Triangle(vd1, vd2, vd5);
        Triangle trid2 = new Triangle(vd1, vd4, vd5);
        Triangle trid3 = new Triangle(vd2, vd3, vd6);
        Triangle trid4 = new Triangle(vd2, vd5, vd6);
        Triangle trid5 = new Triangle(vd4, vd8, vd5);
        Triangle trid6 = new Triangle(vd4, vd7, vd8);
        Triangle trid7 = new Triangle(vd5, vd6, vd9);
        Triangle trid8 = new Triangle(vd5, vd8, vd9);

        warpTriangle(bim, warpedbim, tris1, trid1, null, null);
        warpTriangle(bim, warpedbim, tris2, trid2, null, null);
        warpTriangle(bim, warpedbim, tris3, trid3, null, null);
        warpTriangle(bim, warpedbim, tris4, trid4, null, null);
        warpTriangle(bim, warpedbim, tris5, trid5, null, null);
        warpTriangle(bim, warpedbim, tris6, trid6, null, null);
        warpTriangle(bim, warpedbim, tris7, trid7, null, null);
        warpTriangle(bim, warpedbim, tris8, trid8, null, null);
        showWarped = true;
        repaint();
    }

    /*
    This function applies a new intensity to the morphed image.
    The only argument is an integer. This integer will be divided by 1000 to get the appropriate new intensity.
     */
    public void applyIntensity(int intensity)
    {
        float[] intensityMatrix = new float[9];
        intensityMatrix[4] = (float)intensity / 1000.f;
        Kernel kernel = new Kernel(3, 3, intensityMatrix);
        ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

        BufferedImage newbim = new BufferedImage(basebim.getWidth(), basebim.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D big = newbim.createGraphics();
        big.drawImage(basebim, 0, 0, null);

        cop.filter(newbim, bim);

        this.intensity = intensity;
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
