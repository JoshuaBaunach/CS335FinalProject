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
    public void warpOtherBim(Point vs1, Point vs2, Point vs3, Point vs4, Point vd1, Point vd2, Point vd3, Point vd4)
    {
        Triangle tris1 = new Triangle(vs1, vs3, vs4);
        Triangle tris2 = new Triangle(vs2, vs1, vs3);
        Triangle trid1 = new Triangle(vd1, vd3, vd4);
        Triangle trid2 = new Triangle(vd2, vd1, vd3);

        warpTriangle(otherBim, otherWarpedBim, tris1, trid1, null, null);
        warpTriangle(otherBim, otherWarpedBim, tris2, trid2, null, null);
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
