/*
This class defines a panel of points that will be displayed and manipulated by the user.
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;

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

    // Default Constructor
    public FullGridPanel()
    {}

    // Constructor 1: Only image directory is specified
    public FullGridPanel(int gridWidth, int gridHeight, boolean editMode, String imageDir)
    {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;

        image = new MorphableImage(imageDir);
        this.width = image.getBimWidth();
        this.height = image.getBimHeight();

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

        layout = new GridBagLayout();
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        setLayout(layout);

        points = new GridPoint[gridWidth][gridHeight];

        for (int i = 0; i < gridWidth; i++)
        {
            for (int j = 0; j < gridHeight; j++)
            {
                points[i][j] = new GridPoint(pointWidth, pointHeight);
                c.gridx = i;
                c.gridy = j;
                add(points[i][j], c);
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

        pointWidth = width / gridWidth;
        pointHeight = height / gridHeight;
        layout = new GridBagLayout();
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        setLayout(layout);

        points = new GridPoint[gridWidth][gridHeight];

        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                points[i][j] = new GridPoint(pointWidth, pointHeight);
                c.gridx = i;
                c.gridy = j;
                add(points[i][j], c);
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

        this.image = new MorphableImage(bim);
        this.width = image.getBimWidth();
        this.height = image.getBimHeight();

        // If image is too big, scale it down
        if (this.width > 750 || this.height > 500)
        {
            int newWidth = this.width > 750 ? 750 : this.width;
            int newHeight = this.height > 500 ? 500 : this.height;
            image  = new MorphableImage(bim);
            this.width = image.getBimWidth();
            this.height = image.getBimHeight();
        }
        pointWidth = width / gridWidth;
        pointHeight = height / gridHeight;

        layout = new GridBagLayout();
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        setLayout(layout);

        points = new GridPoint[gridWidth][gridHeight];

        for (int i = 0; i < gridWidth; i++)
        {
            for (int j = 0; j < gridHeight; j++)
            {
                points[i][j] = new GridPoint(pointWidth, pointHeight);
                c.gridx = i;
                c.gridy = j;
                add(points[i][j], c);
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
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(pointWidth * gridWidth, pointHeight * gridHeight);
    }
}
