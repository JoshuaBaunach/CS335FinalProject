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
    public void warpOtherBim(Point vs1, Point vs2, Point vs3, Point vs4, Point vs5, Point vs6, Point vs7, Point vs8, Point vs9,
                        Point vd1, Point vd2, Point vd3, Point vd4, Point vd5, Point vd6, Point vd7, Point vd8, Point vd9)
    {
        Triangle tris1 = new Triangle(vs1, vs2, vs5);
        Triangle tris2 = new Triangle(vs1, vs4, vs5);
        Triangle tris3 = new Triangle(vs2, vs6, vs5);
        Triangle tris4 = new Triangle(vs2, vs3, vs6);
        Triangle tris5 = new Triangle(vs4, vs8, vs5);
        Triangle tris6 = new Triangle(vs4, vs7, vs8);
        Triangle tris7 = new Triangle(vs5, vs6, vs9);
        Triangle tris8 = new Triangle(vs5, vs8, vs9);
        Triangle trid1 = new Triangle(vd1, vd2, vd5);
        Triangle trid2 = new Triangle(vd1, vd4, vd5);
        Triangle trid3 = new Triangle(vd2, vd6, vd5);
        Triangle trid4 = new Triangle(vd2, vd3, vd6);
        Triangle trid5 = new Triangle(vd4, vd8, vd5);
        Triangle trid6 = new Triangle(vd4, vd7, vd8);
        Triangle trid7 = new Triangle(vd5, vd6, vd9);
        Triangle trid8 = new Triangle(vd5, vd8, vd9);

        warpTriangle(otherBim, otherWarpedBim, tris1, trid1, null, null);
        warpTriangle(otherBim, otherWarpedBim, tris2, trid2, null, null);
        warpTriangle(otherBim, otherWarpedBim, tris3, trid3, null, null);
        warpTriangle(otherBim, otherWarpedBim, tris4, trid4, null, null);
        warpTriangle(otherBim, otherWarpedBim, tris5, trid5, null, null);
        warpTriangle(otherBim, otherWarpedBim, tris6, trid6, null, null);
        warpTriangle(otherBim, otherWarpedBim, tris7, trid7, null, null);
        warpTriangle(otherBim, otherWarpedBim, tris8, trid8, null, null);
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
