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
        for (int i = 0; i < points.length-1; i++)
        {
            for (int j = 0; j < points[i].length-1; j++)
            {
                // Determine the eight points - four in source, four in destination
                Point vs1 = sourcePoints[i][j].getLocation();
                Point vs2 = sourcePoints[i+1][j].getLocation();
                Point vs3 = sourcePoints[i+1][j+1].getLocation();
                Point vs4 = sourcePoints[i][j+1].getLocation();
                Point vd1 = destPoints[i][j].getLocation();
                Point vd2 = destPoints[i+1][j].getLocation();
                Point vd3 = destPoints[i+1][j+1].getLocation();
                Point vd4 = destPoints[i][j+1].getLocation();

                // Apply morph to source image
                // Determine how far away each of the points should morph in the current stage
                Point newvd1 = new Point((int)((float)vs1.x + (((float)vd1.x - (float)vs1.x) * stage)),
                        (int)((float)vs1.y + (((float)vd1.y - (float)vs1.y) * stage)));
                Point newvd2 = new Point((int)((float)vs2.x + (((float)vd2.x - (float)vs2.x) * stage)),
                        (int)((float)vs2.y + (((float)vd2.y - (float)vs2.y) * stage)));
                Point newvd3 = new Point((int)((float)vs3.x + (((float)vd3.x - (float)vs3.x) * stage)),
                        (int)((float)vs3.y + (((float)vd3.y - (float)vs3.y) * stage)));
                Point newvd4 = new Point((int)((float)vs4.x + (((float)vd4.x - (float)vs4.x) * stage)),
                        (int)((float)vs4.y + (((float)vd4.y - (float)vs4.y) * stage)));
                imgPair.warpBim(vs1, vs2, vs3, vs4, newvd1, newvd2, newvd3, newvd4);

                // Determine how far away from the control point the destination image should morph in the current stage
                Point newvs1 = new Point((int)((float)vd1.x + (((float)vs1.x - (float)vd1.x) * (1.f - stage))),
                        (int)((float)vd1.y + (((float)vs1.y - (float)vd1.y) * (1.f - stage))));
                Point newvs2 = new Point((int)((float)vd2.x + (((float)vs2.x - (float)vd2.x) * (1.f - stage))),
                        (int)((float)vd2.y + (((float)vs2.y - (float)vd2.y) * (1.f - stage))));
                Point newvs3 = new Point((int)((float)vd3.x + (((float)vs3.x - (float)vd3.x) * (1.f - stage))),
                        (int)((float)vd3.y + (((float)vs3.y - (float)vd3.y) * (1.f - stage))));
                Point newvs4 = new Point((int)((float)vd4.x + (((float)vs4.x - (float)vd4.x) * (1.f - stage))),
                        (int)((float)vd4.y + (((float)vs4.y - (float)vd4.y) * (1.f - stage))));
                imgPair.warpOtherBim(newvs1, newvs2, newvs3, newvs4, vd1, vd2, vd3, vd4);
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

        // Draw the images on top of each other
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

        // Draw lines between each of the neighbor points

        for (int i = 0; i < gridWidth; i++)
        {
            for (int j = 0; j < gridHeight; j++)
            {
                // Draw blue if this particular point is being dragged
                if (points[i][j].getRubberbanding())
                    g.setColor(Color.BLUE);
                else
                    g.setColor(Color.WHITE);
                if (points[i][j].getEastNeighbor() != null) {
                    // Draw blue if this point's east neighbor is being dragged
                    if (points[i][j].getEastNeighbor().getRubberbanding())
                        g.setColor(Color.BLUE);
                    else if (!points[i][j].getRubberbanding())
                        g.setColor(Color.WHITE);
                    g.drawLine(points[i][j].getLocation().x + GridPoint.POINTRADIUS, points[i][j].getLocation().y + GridPoint.POINTRADIUS,
                            points[i][j].getEastNeighbor().getLocation().x + GridPoint.POINTRADIUS, points[i][j].getEastNeighbor().getLocation().y + GridPoint.POINTRADIUS);
                    if (points[i][j].getEastNeighbor().getSouthNeighbor() != null)
                    {
                        // Draw blue if this point's southeast neighbor is being dragged
                        if (points[i][j].getEastNeighbor().getSouthNeighbor().getRubberbanding())
                            g.setColor(Color.BLUE);
                        else if (!points[i][j].getRubberbanding())
                            g.setColor(Color.WHITE);
                        g.drawLine(points[i][j].getLocation().x + GridPoint.POINTRADIUS, points[i][j].getLocation().y + GridPoint.POINTRADIUS,
                                points[i][j].getEastNeighbor().getSouthNeighbor().getLocation().x + GridPoint.POINTRADIUS, points[i][j].getEastNeighbor().getSouthNeighbor().getLocation().y + GridPoint.POINTRADIUS);
                    }
                }
                if (points[i][j].getSouthNeighbor() != null) {
                    // Draw blue if this point's south neighbor is being dragged
                    if (points[i][j].getSouthNeighbor().getRubberbanding())
                        g.setColor(Color.BLUE);
                    else if (!points[i][j].getRubberbanding())
                        g.setColor(Color.WHITE);
                    g.drawLine(points[i][j].getLocation().x + GridPoint.POINTRADIUS, points[i][j].getLocation().y + GridPoint.POINTRADIUS,
                            points[i][j].getSouthNeighbor().getLocation().x + GridPoint.POINTRADIUS, points[i][j].getSouthNeighbor().getLocation().y + GridPoint.POINTRADIUS);
                }
            }
        }
    }
}
