/*
This class defines a panel of points that will be displayed and manipulated by the user.
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Stack;

public class FullGridPanel extends JPanel
{
    // Private variables
    protected int gridWidth, gridHeight, pointWidth, pointHeight;
    protected boolean editMode;
    protected int width, height;
    protected float stage;
    protected GridBagLayout layout;
    protected GridBagConstraints c;
    protected GridPoint[][] points;
    protected FullGridPanel partnerPanel;
    protected MorphableImage image;
    protected Point mousePoint;

    // Default Constructor
    public FullGridPanel()
    {}

    /*
    Welcome to constructor Hell.
    Population: Josh, after being informed of a critical design flaw.
     */

    // Constructor 1: Only image directory is specified
    public FullGridPanel(int gridWidth, int gridHeight, boolean editMode, String imageDir)
    {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;

        image = new MorphableImage(imageDir);
        this.width = image.getBimWidth();
        this.height = image.getBimHeight();
        this.mousePoint = new Point();

        // If image is too big, scale it down
        if (this.width > 750 || this.height > 500)
        {
            int newWidth = this.width > 750 ? 750 : this.width;
            int newHeight = this.height > 500 ? 500 : this.height;
            image  = new MorphableImage(imageDir, newWidth, newHeight);
            this.width = image.getBimWidth();
            this.height = image.getBimHeight();
        }
        pointWidth = width / gridWidth;
        pointHeight = height / gridHeight;

        setLayout(null);

        points = new GridPoint[gridWidth][gridHeight];

        for (int i = 0; i < gridWidth; i++)
        {
            for (int j = 0; j < gridHeight; j++)
            {
                points[i][j] = new GridPoint(pointWidth, pointHeight);

                // Assign neighbor points to this point, if applicable
                if (i > 0)
                {
                    points[i][j].setWestNeighbor(points[i-1][j]);
                    points[i-1][j].setEastNeighbor(points[i][j]);
                }
                if (j > 0)
                {
                    points[i][j].setNorthNeighbor(points[i][j-1]);
                    points[i][j-1].setSouthNeighbor(points[i][j]);
                }

                //add(points[i][j], c);
                add(points[i][j]);
                points[i][j].setSize(new Dimension(GridPoint.POINTRADIUS*2, GridPoint.POINTRADIUS*2));
                points[i][j].setLocation(new Point((pointWidth * i) + (pointWidth / 2) - GridPoint.POINTRADIUS, (pointHeight * j) + (pointHeight / 2) - GridPoint.POINTRADIUS));
                points[i][j].setParentPanel(this);
            }
        }

    }

    // Constructor 2: Image directory and image sizes are specified
    public FullGridPanel(int gridWidth, int gridHeight, boolean editMode, String imageDir, int width, int height) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;

        image = new MorphableImage(imageDir, width, height);
        this.width = image.getBimWidth();
        this.height = image.getBimHeight();
        this.mousePoint = new Point();

        // If image is too big, scale it down
        if (this.width > 750 || this.height > 500) {
            int newWidth = this.width > 750 ? 750 : this.width;
            int newHeight = this.height > 500 ? 500 : this.height;
            image = new MorphableImage(imageDir, newWidth, newHeight);
            this.width = image.getBimWidth();
            this.height = image.getBimHeight();
        }
        pointWidth = width / gridWidth;
        pointHeight = height / gridHeight;

        setLayout(null);

        points = new GridPoint[gridWidth][gridHeight];

        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                points[i][j] = new GridPoint(pointWidth, pointHeight);

                // Assign neighbor points to this point, if applicable
                if (i > 0) {
                    points[i][j].setWestNeighbor(points[i - 1][j]);
                    points[i - 1][j].setEastNeighbor(points[i][j]);
                }
                if (j > 0) {
                    points[i][j].setNorthNeighbor(points[i][j - 1]);
                    points[i][j - 1].setSouthNeighbor(points[i][j]);
                }

                //add(points[i][j], c);
                add(points[i][j]);
                points[i][j].setSize(new Dimension(GridPoint.POINTRADIUS * 2, GridPoint.POINTRADIUS * 2));
                points[i][j].setLocation(new Point((pointWidth * i) + (pointWidth / 2) - GridPoint.POINTRADIUS, (pointHeight * j) + (pointHeight / 2) - GridPoint.POINTRADIUS));
                points[i][j].setParentPanel(this);
            }
        }

    }

    /*
    Constructor 3: A BufferedImage is specified and a stage is specified.
    The stage will be used in the FadeBim method.
     */
    public FullGridPanel(int gridWidth, int gridHeight, boolean editMode, BufferedImage bim)
    {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;

        image = new MorphableImage(bim);
        this.width = image.getBimWidth();
        this.height = image.getBimHeight();
        this.mousePoint = new Point();

        pointWidth = width / gridWidth;
        pointHeight = height / gridHeight;

        setLayout(null);

        points = new GridPoint[gridWidth][gridHeight];

        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                points[i][j] = new GridPoint(pointWidth, pointHeight);

                // Assign neighbor points to this point, if applicable
                if (i > 0) {
                    points[i][j].setWestNeighbor(points[i - 1][j]);
                    points[i - 1][j].setEastNeighbor(points[i][j]);
                }
                if (j > 0) {
                    points[i][j].setNorthNeighbor(points[i][j - 1]);
                    points[i][j - 1].setSouthNeighbor(points[i][j]);
                }

                //add(points[i][j], c);
                add(points[i][j]);
                points[i][j].setSize(new Dimension(GridPoint.POINTRADIUS * 2, GridPoint.POINTRADIUS * 2));
                points[i][j].setLocation(new Point((pointWidth * i) + (pointWidth / 2) - GridPoint.POINTRADIUS, (pointHeight * j) + (pointHeight / 2) - GridPoint.POINTRADIUS));
                points[i][j].setParentPanel(this);
            }
        }

    }


    // Getters/setters
    public GridPoint[][] getPoints()
    {
        GridPoint[][] toReturn = new GridPoint[points.length][];
        for (int i = 0; i < points.length; i++)
        {
            toReturn[i] = new GridPoint[points[i].length];
            for (int j = 0; j < points[i].length; j++)
            {
                toReturn[i][j] = new GridPoint(points[i][j]);
            }
        }

        return toReturn;
    }
    public GridPoint getPoint(int gridx, int gridy) { return points[gridx][gridy]; }
    public int getPanelWidth() { return width; }
    public int getPanelHeight() { return height; }
    public MorphableImage getMorphableImage() { return image; }
    public Point getMousePoint() { return new Point(MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x,
            MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y); }

    public void setPoints(Point[][] points)
    {
        for (int i = 0; i < gridWidth; i++)
        {
            for (int j = 0; j < gridHeight; j++)
            {
                this.points[i][j].setControlPoint(points[i][j]);
            }
        }
    }
    public void setMorphableImage(MorphableImage image) { this.image = image; }


    public void setPartnerPanel(FullGridPanel partner)
    {
        partnerPanel = partner;
        for (int i = 0; i < gridWidth; i++)
        {
            for (int j = 0; j < gridHeight; j++)
            {
                this.getPoint(i, j).setPartnerPoint(partner.getPoint(i, j));
                partner.getPoint(i, j).setPartnerPoint(this.getPoint(i, j));
            }
        }
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // If no image is present, just draw a black background
        if (image == null)
        {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, pointWidth * gridWidth, pointHeight * gridHeight);
        }
        else
        {
            g2d.drawImage(image.getBufferedImage(), 0, 0, this);
        }

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
                    /*if (points[i][j].getSouthNeighbor().getWestNeighbor() != null)
                    {
                        // Draw blue if this point's southwest neighbor is being dragged
                        if (points[i][j].getSouthNeighbor().getWestNeighbor().getRubberbanding())
                            g.setColor(Color.BLUE);
                        else if (!points[i][j].getRubberbanding())
                            g.setColor(Color.WHITE);
                        g.drawLine(points[i][j].getLocation().x + GridPoint.POINTRADIUS, points[i][j].getLocation().y + GridPoint.POINTRADIUS,
                                points[i][j].getSouthNeighbor().getWestNeighbor().getLocation().x + GridPoint.POINTRADIUS, points[i][j].getSouthNeighbor().getWestNeighbor().getLocation().y + GridPoint.POINTRADIUS);
                    }*/
                }
            }
        }

    }

    public Dimension getPreferredSize()
    {
        return new Dimension(pointWidth * gridWidth, pointHeight * gridHeight);
    }
}
