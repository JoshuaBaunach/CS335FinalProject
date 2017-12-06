/*
This class defines a panel that will be used in the preview
for an animation.
 */

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class PreviewGridPanel extends FullGridPanel{

    MorphableImagePair imgPair;
    float stage;
    GridPoint[][] sourcePoints, destPoints;

    // Constructor
    public PreviewGridPanel(int gridWidth, int gridHeight, boolean editMode, BufferedImage b1, BufferedImage b2,
                            GridPoint[][] sourcePoints, GridPoint[][] destPoints)
    {
        super(gridWidth, gridHeight, editMode, b1);
        stage = 0.5f;
        imgPair = new MorphableImagePair(b1, b2, stage);
        this.sourcePoints = sourcePoints;
        this.destPoints = destPoints;
    }

    // Getters/setters
    public MorphableImagePair getImgPair() { return imgPair; }
    public void setStage(float stage) { this.stage = stage; }

    /*
    This function will apply the morph to the two images.
    The source image will gradually morph from source to dest,
    whereas the destination image will gradually "de-morph" from dest to source.
     */
    public void applyMorph()
    {
        // Go through each control point
        for (int i = 0; i < points.length; i++)
        {
            for (int j = 0; j < points[i].length; j++)
            {
                // Derive the bounding box of the control point
                int minX = j * pointWidth;
                int maxX = (j * pointWidth) + pointWidth;
                int minY = i * pointHeight;
                int maxY = (i * pointHeight) + pointHeight;

                // Apply the morph to the source image

                // Determine how far away from the control point the source image should morph in the current stage
                float srcImageSrcX = sourcePoints[i][j].getControlPoint().x + (pointWidth * j);
                float srcImageSrcY = sourcePoints[i][j].getControlPoint().y + (pointHeight * i);
                float srcImageDestX = destPoints[i][j].getControlPoint().x + (pointWidth * j);
                float srcImageDestY = destPoints[i][j].getControlPoint().y + (pointHeight * i);
                float newSrcImageX = srcImageSrcX + ((srcImageDestX - srcImageSrcX) * stage);
                float newSrcImageY = srcImageSrcY + ((srcImageDestY - srcImageSrcY) * stage);
                imgPair.warpBim(new Point((int)srcImageSrcX, (int)srcImageSrcY), new Point((int)newSrcImageX, (int)newSrcImageY), minX, minY, maxX, maxY);
                //imgPair.warpBim(sourcePoints[i][j].getControlPoint(), destPoints[i][j].getControlPoint(), minX, minY, maxX, maxY);

                // Determine how far away from the control point the destination image should morph in the current stage
                float newDestImageX = srcImageDestX + ((srcImageSrcX - srcImageDestX) * (1.f - stage));
                float newDestImageY = srcImageDestY + ((srcImageSrcY - srcImageDestY) * (1.f - stage));
                imgPair.warpOtherBim(new Point((int) srcImageDestX, (int) srcImageDestY), new Point((int)newDestImageX, (int)newDestImageY), minX, minY, maxX, maxY);
            }
        }
    }

    /*
    This paintComponent method will fade the two images together.
     */
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, pointWidth * gridWidth, pointHeight * gridHeight);
        g2d.setComposite(AlphaComposite.SrcOver.derive(1.f - stage));
        g2d.drawImage(imgPair.getBufferedImage(), 0, 0, this);
        g2d.setComposite(AlphaComposite.SrcOver.derive(stage));
        g2d.drawImage(imgPair.getOtherBufferedImage(), 0, 0, this);

        g2d.setComposite(AlphaComposite.SrcOver.derive(1.f));

        // Draw a border around the entire object
        g2d.setColor(Color.WHITE);
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(5));
        g2d.drawRect(0, 0, pointWidth * gridWidth, pointHeight * gridHeight);
        g2d.setStroke(oldStroke);
    }
}
