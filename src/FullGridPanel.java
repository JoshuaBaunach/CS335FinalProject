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
    private int gridSize, pointSize;
    private boolean editMode;
    private GridLayout layout;
    private GridPoint[][] points;
    private BufferedImage img;
    private FullGridPanel partnerPanel;

    public FullGridPanel(int gridSize, int pointSize, boolean editMode)
    {
        this.gridSize = gridSize;
        this.pointSize = pointSize;

        layout = new GridLayout(gridSize, gridSize);
        setLayout(layout);

        points = new GridPoint[gridSize][gridSize];

        for (int i = 0; i < gridSize; i++)
        {
            for (int j = 0; j < gridSize; j++)
            {
                points[i][j] = new GridPoint(pointSize);
                add(points[i][j]);
            }
        }
    }

    // Getters/setters
    public GridPoint getPoint(int gridx, int gridy) { return points[gridx][gridy]; }

    public void setPartnerPanel(FullGridPanel partner)
    {
        partnerPanel = partner;
        for (int i = 0; i < gridSize; i++)
        {
            for (int j = 0; j < gridSize; j++)
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
        if (img == null)
        {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, pointSize * gridSize, pointSize * gridSize);
        }

        // Draw a border around the entire object
        g2d.setColor(Color.WHITE);
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(5));
        g2d.drawRect(0, 0, pointSize * gridSize, pointSize * gridSize);
        g2d.setStroke(oldStroke);
    }

    public Dimension getPreferredSize()
    {
        int dim = pointSize * gridSize;
        return new Dimension(dim, dim);
    }
}
