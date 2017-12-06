/*
This class specifies a pair of morphable images.
It will be used in the PreviewFrame class.
 */

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class MorphableImagePair extends MorphableImage {

    BufferedImage otherBim, otherWarpedBim;
    float stage;

    public MorphableImagePair(BufferedImage b1, BufferedImage b2, float stage)
    {
        super(b1);
        this.otherBim = b2;
        otherWarpedBim = new BufferedImage(b2.getWidth(), b2.getHeight(), BufferedImage.TYPE_INT_RGB);
        showWarped = false;
        this.stage = stage;
        repaint();
    }

    /*
    This function morphs using four different triangles.
    It applies morphing to both the source image using super() and the destination image.
     */
    public void warpOtherBim(Point srcPoint, Point destPoint, int minX, int minY, int maxX, int maxY)
    {
        Triangle ta1 = new Triangle(new Point(minX,minY), srcPoint, new Point(maxX,minY));
        Triangle ta2 = new Triangle(new Point(minX,minY), destPoint, new Point(maxX,minY));
        Triangle tb1 = new Triangle(new Point(minX,maxY), srcPoint, new Point(maxX, maxY));
        Triangle tb2 = new Triangle(new Point(minX,maxY), destPoint, new Point(maxX,maxY));
        Triangle tc1 = new Triangle(new Point(minX,minY), srcPoint, new Point(minX, maxY));
        Triangle tc2 = new Triangle(new Point(minX,minY), destPoint, new Point(minX,maxY));
        Triangle td1 = new Triangle(new Point(maxX,minY), srcPoint, new Point(maxX, maxY));
        Triangle td2 = new Triangle(new Point(maxX,minY), destPoint, new Point(maxX,maxY));

        warpTriangle(otherBim, otherWarpedBim, ta1, ta2, null, null);
        warpTriangle(otherBim, otherWarpedBim, tb1, tb2, null, null);
        warpTriangle(otherBim, otherWarpedBim, tc1, tc2, null, null);
        warpTriangle(otherBim, otherWarpedBim, td1, td2, null, null);
        showWarped = true;
        repaint();
    }

    public BufferedImage getOtherBufferedImage() { return showWarped ? otherWarpedBim : otherBim; }

    public void paintComponent(Graphics g)
    {
        Graphics2D big = (Graphics2D) g;
        super.paintComponent(g);

        // Draw the Buffered Images differently depending on whether we are showing the warped version
        if (showWarped) {
            big.setComposite(AlphaComposite.SrcOver.derive(1.f - stage));
            big.drawImage(warpedbim, 0, 0, this);
            big.setComposite(AlphaComposite.SrcOver.derive(stage));
            big.drawImage(otherWarpedBim, 0, 0, this);
        }
        else {
            big.setComposite(AlphaComposite.SrcOver.derive(1.f - stage));
            big.drawImage(bim, 0, 0, this);
            big.setComposite(AlphaComposite.SrcOver.derive(stage));
            big.drawImage(otherBim, 0, 0, this);
        }
    }
}
