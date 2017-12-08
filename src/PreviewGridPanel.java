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
                Point vs1, vs2, vs3, vs4, vs5, vs6, vs7, vs8, vs9;
                Point vd1, vd2, vd3, vd4, vd5, vd6, vd7, vd8, vd9;
                // Determine the sixteen points, eight for each

                // Point 1
                if (i == 0 && j == 0) {
                    vs1 = new Point(0, 0);
                    vd1 = new Point(0, 0);
                }
                else if (i == 0) {
                    vs1 = new Point(0, sourcePoints[i][j - 1].getLocation().y - pointHeight);
                    vd1 = new Point(0, destPoints[i][j-1].getLocation().y - pointHeight);
                }
                else if (j == 0) {
                    vs1 = new Point(sourcePoints[i - 1][j].getLocation().x - pointWidth, 0);
                    vd1 = new Point(destPoints[i - 1][j].getLocation().x - pointWidth, 0);
                }
                else {
                    vs1 = sourcePoints[i - 1][j - 1].getLocation();
                    vd1 = destPoints[i - 1][j - 1].getLocation();
                }

                // Point 2
                if (j == 0) {
                    vs2 = new Point(sourcePoints[i][j].getLocation().x, 0);
                    vd2 = new Point(destPoints[i][j].getLocation().x, 0);
                }
                else
                {
                    vs2 = sourcePoints[i][j - 1].getLocation();
                    vd2 = destPoints[i][j-1].getLocation();
                }

                // Point 3
                if (i == gridWidth - 1 && j == 0) {
                    vs3 = new Point(getPanelWidth(), 0);
                    vd3 = new Point(getPanelWidth(), 0);
                }
                else if (i == gridWidth - 1) {
                    vs3 = new Point(getPanelWidth(), sourcePoints[i][j - 1].getLocation().y - pointHeight);
                    vd3 = new Point(getPanelWidth(), destPoints[i][j-1].getLocation().y - pointHeight);
                }
                else if (j == 0) {
                    vs3 = new Point(sourcePoints[i + 1][j].getLocation().x + pointWidth, 0);
                    vd3 = new Point(destPoints[i + 1][j].getLocation().x + pointWidth, 0);
                }
                else
                {
                    vs3 = sourcePoints[i+1][j-1].getLocation();
                    vd3 = destPoints[i+1][j-1].getLocation();
                }

                // Point 4
                if (i == 0) {
                    vs4 = new Point(0, sourcePoints[i][j].getLocation().y);
                    vd4 = new Point(0, destPoints[i][j].getLocation().y);
                }
                else {
                    vs4 = sourcePoints[i - 1][j].getLocation();
                    vd4 = destPoints[i - 1][j].getLocation();
                }

                // Point 5
                vs5 = sourcePoints[i][j].getLocation();
                vd5 = destPoints[i][j].getLocation();

                // Point 6
                if (i == gridWidth - 1) {
                    vs6 = new Point(getPanelWidth(), sourcePoints[i][j].getLocation().y);
                    vd6 = new Point(getPanelWidth(), destPoints[i][j].getLocation().y);
                }
                else {
                    vs6 = sourcePoints[i + 1][j].getLocation();
                    vd6 = destPoints[i + 1][j].getLocation();
                }

                // Point 7
                if (i == 0 && j == gridHeight - 1) {
                    vs7 = new Point(0, getPanelHeight());
                    vd7 = new Point(0, getPanelHeight());
                }
                else if (i == 0) {
                    vs7 = new Point(0, sourcePoints[i][j + 1].getLocation().y + pointHeight);
                    vd7 = new Point(0, destPoints[i][j+1].getLocation().y + pointHeight);
                }
                else if (j == gridHeight - 1) {
                    vs7 = new Point(sourcePoints[i - 1][j].getLocation().x - pointWidth, getPanelHeight());
                    vd7 = new Point(destPoints[i - 1][j].getLocation().x - pointWidth, getPanelHeight());
                }
                else {
                    vs7 = sourcePoints[i - 1][j + 1].getLocation();
                    vd7 = destPoints[i - 1][j + 1].getLocation();
                }

                // Point 8
                if (j == gridHeight - 1) {
                    vs8 = new Point(sourcePoints[i][j].getLocation().y, getPanelHeight());
                    vd8 = new Point(destPoints[i][j].getLocation().y, getPanelHeight());
                }
                else {
                    vs8 = sourcePoints[i][j + 1].getLocation();
                    vd8 = destPoints[i][j + 1].getLocation();
                }

                // Point 9
                if (i == gridWidth - 1 && j == gridHeight - 1) {
                    vs9 = new Point(getPanelWidth(), getPanelHeight());
                    vd9 = new Point(getPanelWidth(), getPanelHeight());
                }
                else if (i == gridWidth - 1) {
                    vs9 = new Point(getPanelWidth(), sourcePoints[i][j + 1].getLocation().y + pointHeight);
                    vd9 = new Point(getPanelWidth(), destPoints[i][j+1].getLocation().y + pointHeight);
                }
                else if (j == gridHeight - 1) {
                    vs9 = new Point(sourcePoints[i + 1][j].getLocation().x + pointWidth, getPanelHeight());
                    vd9 = new Point(destPoints[i + 1][j].getLocation().x + pointWidth, getPanelHeight());
                }
                else {
                    vs9 = sourcePoints[i + 1][j + 1].getLocation();
                    vd9 = destPoints[i + 1][j + 1].getLocation();
                }

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
                Point newvd5 = new Point((int)((float)vs5.x + (((float)vd5.x - (float)vs5.x) * stage)),
                        (int)((float)vs5.y + (((float)vd5.y - (float)vs5.y) * stage)));
                Point newvd6 = new Point((int)((float)vs6.x + (((float)vd6.x - (float)vs6.x) * stage)),
                        (int)((float)vs6.y + (((float)vd6.y - (float)vs6.y) * stage)));
                Point newvd7 = new Point((int)((float)vs7.x + (((float)vd7.x - (float)vs7.x) * stage)),
                        (int)((float)vs7.y + (((float)vd7.y - (float)vs7.y) * stage)));
                Point newvd8 = new Point((int)((float)vs8.x + (((float)vd8.x - (float)vs8.x) * stage)),
                        (int)((float)vs8.y + (((float)vd8.y - (float)vs8.y) * stage)));
                Point newvd9 = new Point((int)((float)vs9.x + (((float)vd9.x - (float)vs9.x) * stage)),
                        (int)((float)vs9.y + (((float)vd9.y - (float)vs9.y) * stage)));
                imgPair.warpBim(vs1, vs2, vs3, vs4, vs5, vs6, vs7, vs8, vs9,
                        newvd1, newvd2, newvd3, newvd4, newvd5, newvd6, newvd7, newvd8, newvd9);

                // Determine how far away from the control point the destination image should morph in the current stage
                Point newvs1 = new Point((int)((float)vd1.x + (((float)vs1.x - (float)vd1.x) * (1.f - stage))),
                        (int)((float)vd1.y + (((float)vs1.y - (float)vd1.y) * (1.f - stage))));
                Point newvs2 = new Point((int)((float)vd2.x + (((float)vs2.x - (float)vd2.x) * (1.f - stage))),
                        (int)((float)vd2.y + (((float)vs2.y - (float)vd2.y) * (1.f - stage))));
                Point newvs3 = new Point((int)((float)vd3.x + (((float)vs3.x - (float)vd3.x) * (1.f - stage))),
                        (int)((float)vd3.y + (((float)vs3.y - (float)vd3.y) * (1.f - stage))));
                Point newvs4 = new Point((int)((float)vd4.x + (((float)vs4.x - (float)vd4.x) * (1.f - stage))),
                        (int)((float)vd4.y + (((float)vs4.y - (float)vd4.y) * (1.f - stage))));
                Point newvs5 = new Point((int)((float)vd5.x + (((float)vs5.x - (float)vd5.x) * (1.f - stage))),
                        (int)((float)vd5.y + (((float)vs5.y - (float)vd5.y) * (1.f - stage))));
                Point newvs6 = new Point((int)((float)vd6.x + (((float)vs6.x - (float)vd6.x) * (1.f - stage))),
                        (int)((float)vd6.y + (((float)vs6.y - (float)vd6.y) * (1.f - stage))));
                Point newvs7 = new Point((int)((float)vd7.x + (((float)vs7.x - (float)vd7.x) * (1.f - stage))),
                        (int)((float)vd7.y + (((float)vs7.y - (float)vd7.y) * (1.f - stage))));
                Point newvs8 = new Point((int)((float)vd8.x + (((float)vs8.x - (float)vd8.x) * (1.f - stage))),
                        (int)((float)vd8.y + (((float)vs8.y - (float)vd8.y) * (1.f - stage))));
                Point newvs9 = new Point((int)((float)vd9.x + (((float)vs9.x - (float)vd9.x) * (1.f - stage))),
                        (int)((float)vd9.y + (((float)vs9.y - (float)vd9.y) * (1.f - stage))));
                imgPair.warpOtherBim(vd1, vd2, vd3, vd4, vd5, vd6, vd7, vd8, vd9,
                        newvs1, newvs2, newvs3, newvs4, newvs5, newvs6, newvs7, newvs8, newvs9);
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
        //g2d.drawRect(0, 0, pointWidth * gridWidth, pointHeight * gridHeight);
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
